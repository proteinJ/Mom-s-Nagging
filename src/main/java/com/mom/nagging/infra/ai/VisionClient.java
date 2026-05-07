package com.mom.nagging.infra.ai;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class VisionClient {

    /**
     * 이미지 파일의 바이트 데이터를 받아 구글 비전 API를 호출하고 텍스트를 추출합니다.
     * @param imageBytes 사용자가 업로드한 이미지의 바이트 배열
     * @return 추출된 날것의 텍스트 문자열
     */
    public String extractTextFromImage(byte[] imageBytes) {
        try {
            // 1. 보안 키 로드 (resources 폴더의 vision-key.json)
            ClassPathResource resource = new ClassPathResource("vision-key.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());
            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            // 2. 이미지 데이터 세팅
            ByteString imgBytes = ByteString.copyFrom(imageBytes);
            Image img = Image.newBuilder().setContent(imgBytes).build();

            // 3. 텍스트 추출(TEXT_DETECTION) 요청 객체 생성
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);

            // 4. 구글 서버와 통신 및 결과 반환
            try (ImageAnnotatorClient client = ImageAnnotatorClient.create(settings)) {
                BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
                AnnotateImageResponse res = response.getResponsesList().get(0);

                if (res.hasError()) {
                    log.error("Vision API 호출 에러: {}", res.getError().getMessage());
                    throw new RuntimeException("이미지 분석 중 구글 서버 에러가 발생했습니다.");
                }

                // 추출된 전체 텍스트 반환
                String extractedText = res.getTextAnnotationsList().get(0).getDescription();
                log.info("Vision API 텍스트 추출 성공 (길이: {})", extractedText.length());
                return extractedText;
            }

        } catch (IOException e) {
            log.error("Vision API 인증 키 파일을 찾을 수 없거나 읽을 수 없습니다.", e);
            throw new RuntimeException("비전 API 설정 오류입니다.");
        } catch (Exception e) {
            log.error("Vision API 처리 중 알 수 없는 에러 발생", e);
            throw new RuntimeException("텍스트 추출 실패");
        }
    }
}