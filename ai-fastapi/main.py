import os
from dotenv import load_dotenv
import cv2
import numpy as np
from fastapi import FastAPI, File, UploadFile
from fastapi.responses import JSONResponse
from ultralytics import YOLO
from google.cloud import vision


# .env 파일의 환경 변수 로드
load_dotenv()

# 키 가져오기
client = vision.ImageAnnotatorClient()

app = FastAPI(title="Everytime Timetable Extractor API")

# 서버 시작 시 모델 & Vision API 한 번만 로드 (속도 최적화)
model = YOLO('weights/best.pt')
vision_client = vision.ImageAnnotatorClient()

# [전처리] 1080x1920 에 이미지 붙이기
def pad_image(img):
    h, w = img.shape[:2]
    target_w, target_h = 1080, 1920
    canvas = np.ones((target_h, target_w, 3), dtype=np.uint8) * 255

    scale = target_w / w if w > target_w else 1.0
    new_w, new_h = int(w * scale), int(h * scale)
    resized_img = cv2.resize(img, (new_w, new_h))

    canvas[:new_h, :new_w] = resized_img
    return canvas

# [역산] YOLO 좌표로 요일/시간 알아내기
def get_time_info(x1, y1, y2):
    START_X = 69
    REAL_START_Y = 61 # 테스트하며 본인 스크린샷에 맞게 조절하세요.
    
    COL_WIDTH = 178
    HALF_ROW_HEIGHT = 180.5 / 2  # 30분당 픽셀(약 90.25)

    # 요일 계산
    day_idx = round((x1 - START_X) / COL_WIDTH)
    days = ["월", "화", "수", "목", "금"]
    day_str = days[day_idx] if 0 <= day_idx <= 4 else "알 수 없음"
    
    # 실수 연산 오차를 막기 위해 '반 칸(30분)'을 1개의 정수 블록으로 계산
    # 0블록 = 9:00 / 1블록 = 9:30 / 2블록 = 10:00 ...
    start_blocks = round((y1 - REAL_START_Y) / HALF_ROW_HEIGHT)
    start_hour = 9 + (start_blocks // 2)
    start_minute = "30" if start_blocks % 2 != 0 else "00"

    duration_blocks = round((y2 - y1) / HALF_ROW_HEIGHT)
    end_blocks = start_blocks + duration_blocks
    end_hour = 9 + (end_blocks // 2)
    end_minute = "30" if end_blocks % 2 != 0 else "00"

    return day_str, f"{start_hour:02d}:{start_minute}", f"{end_hour:02d}:{end_minute}"

# [OCR] 구글 비전 API 호출 함수
def extract_text_from_vision(crop_img):
    # OpenCV 이미지를 Google Vision API가 처리할 수 있는 Byte형식으로 변환
    success, encoded_image = cv2.imencode('.jpg', crop_img)
    content = encoded_image.tobytes()

    image = vision.Image(content=content)
    # text_detection이 한글+영어 섞인 문서를 제일 잘 읽는다.
    response = vision_client.text_detection(image=image)
    texts = response.text_annotations

    if texts:
        # 첫 번째 요소(texts[0])가 전체 문장을 줄바꿈 포함해서 줌
        return texts[0].description.strip()
    return ""

# ==========================================
#   실제 API 엔드포인트
# ==========================================
@app.post("/api/v1/vision/extract")
async def extract_timetable(file: UploadFile = File(...)):
    try:
        # 스프링부트/클라이언트에서 보낸 이미지 읽기
        contents = await file.read()
        nparr = np.frombuffer(contents, np.uint8)
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

        # 전처리 (1080x1920 도화지 패딩)
        canvas = pad_image(img)

        # YOLO 예측
        results = model.predict(canvas, conf=0.8)

        parsed_data = []

        for box in results[0].boxes:
            x1, y1, x2, y2 = map(int, box.xyxy[0])
            
            # 수학으로 시간/요일 계산
            day_str, start_time, end_time = get_time_info(x1, y1, y2)
            
            # 박스 영역 Crop(자르기) (여백을 2~3px 줘서 OCR 정확도 향상)
            crop_img = canvas[max(0, y1+2):y2-2, max(0, x1+2):x2-2]

            # Google Vision OCR 처리
            raw_text = extract_text_from_vision(crop_img)

            # 줄바꿈을 공백으로 합쳐서 원본 그대로 보냄
            combined_text = raw_text.replace('\n', ' ').strip()

            parsed_data.append({
                "day": day_str,
                "start_time": start_time,
                "end_time": end_time,
                "raw_text": combined_text
            })

        return JSONResponse(content={"status": "success", "data": parsed_data})
    
    except Exception as e:
        return JSONResponse(status_code=500, content={"status": "error", "message": str(e)})