# 📢 엄마의 잔소리 (Mom's Nagging)
> **"재현아, 늦었다 빨리 뛰어라!"**
> AI 비전 기술과 실시간 길찾기를 결합한 지능형 등교/출근 도우미 서비스

<br/>

## 🚀 프로젝트 소개
사용자가 에브리타임 등의 시간표 이미지를 업로드하면 **Google Vision AI**가 텍스트를 추출하고, **Gemini AI**가 분석하여 자동으로 일정을 등록합니다. 등록된 일정을 바탕으로 **네이버 길찾기 API**를 통해 이동 시간을 계산하며, 출발 전 최적의 타이밍(이동 시간의 0.8배수 전 여유 시간)에 맞춰 AI가 생성한 맞춤형 잔소리 알림을 발송합니다.

<br/>

## 🛠 Tech Stack
**Backend (Spring Boot)**
- **Framework:** Spring Boot 3.4.2
- **Language:** Java 17
- **Database:** MySQL / Spring Data JPA (QueryDSL)
- **Security:** Spring Security & JWT
- **Documentation:** Swagger (Springdoc-openapi 2.8.0)

**AI & OCR (FastAPI)**
- **Framework:** FastAPI
- **Language:** Python 3.11+
- **AI Model:** Google Cloud Vision AI (OCR), Gemini API 2.5 Flash (Data Refinement), YOLO (Table Object Detection)

<br/>

## 📂 Project Structure
본 프로젝트는 하나의 레포지토리에서 백엔드와 AI 서버를 함께 관리하는 모노레포 구조를 가집니다.
```text
Mom-s-Nagging/
├── backend-spring/      # Spring Boot 메인 비즈니스 서버
├── ai-fastapi/          # AI 비전 처리 및 데이터 정제 서버
└── docker-compose.yml   # 전체 인프라(DB, Spring, FastAPI) 일괄 구동 설정

## ⚙️ 시작하기 (Getting Started)

### 1. 환경 변수 설정 (Environment Variables)

각 서버를 실행하기 전, API 통신 및 보안을 위해 필요한 키 파일들을 설정해야 합니다.

**🔹 AI 서버 (`ai-fastapi/.env`)**
`ai-fastapi/` 폴더 최상단에 `.env` 파일을 생성하고 아래 내용을 입력하세요. (구글 비전 키 파일인 `.json` 파일도 같은 경로에 위치해야 합니다.)

```env
GOOGLE_APPLICATION_CREDENTIALS="google-vision-key.json"
GEMINI_API_KEY="your_gemini_api_key_here"

```

**🔹 백엔드 서버 (`backend-spring/src/main/resources/application.yml`)**
데이터베이스, JWT, 네이버 길찾기 API 관련 환경 변수를 시스템 또는 IDE에 맞게 세팅해 주세요.

```yaml
spring:
  datasource:
    url: jdbc:mysql://db_host:3306/momsnagging
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
# 기타 JWT_SECRET, NAVER_CLIENT_ID 등 필요

```

### 2. 서버 실행 방법 (How to Run)

본 프로젝트는 **Docker Compose**를 사용하여 클릭 한 번에 모든 인프라를 구축할 수 있습니다.

✅ **초간편 Docker 실행 (권장)**
최상위 루트 폴더(`Mom-s-Nagging/`)에서 아래 명령어를 실행하세요.

```bash
# 전체 컨테이너 백그라운드 빌드 및 실행
docker-compose up -d --build

```

*(참고) 로컬 환경에서 개별 실행이 필요한 경우:*

* **FastAPI:** `cd ai-fastapi` ➡️ `source .venv/bin/activate` ➡️ `uvicorn main:app --reload --port 8000`
* **Spring Boot:** `cd backend-spring` ➡️ `./gradlew bootRun`

## 🧪 API Documentation

서버가 정상적으로 실행된 후, 아래 주소에서 API 명세서 및 테스트 환경을 확인할 수 있습니다.

* **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui/index.html)

## 💡 주요 기능 로직 (Business Logic)

1. **TimeTable Registration (시간표 자동 등록):** - YOLO를 활용한 시간표 영역 감지 및 Google Vision AI로 텍스트를 추출.
* 단일 배치(Batch) 프롬프트로 Gemini AI를 호출하여 빠르고 정확하게 데이터를 정제 및 DB 엔티티로 저장.


2. **Nagging Trigger (지능형 알림 스케줄러):** - Spring의 `@Scheduled`를 활용해 1분 단위로 스케줄러 작동.
* `TimeCalculator`를 통해 `(이동 시간 * 0.8)` 배수 시점을 역산하여, 출발 최적의 타이밍에 잔소리 API를 자동 호출.



## 👨‍💻 Developer

**Park Jae-hyun** (@Dong-A University, Computer Science)

* **Role:** Backend Developer (Spring Boot, Infra, AI Integration)
