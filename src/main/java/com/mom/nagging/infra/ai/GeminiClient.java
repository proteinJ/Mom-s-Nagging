package com.mom.nagging.infra.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper; // JSON 파싱용
import com.mom.nagging.domain.routine.dto.RoutineItemDto; // 우리가 쓸 DTO
import com.mom.nagging.infra.ai.dto.GeminiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final RestClient aiRestClient;
    private final ObjectMapper objectMapper; // Spring이 주입해주는 Jackson Mapper

    @Value("${ai.google.api-key}")
    private String apiKey;
    @Value("${ai.google.gemini-url}")
    private String geminiUrl;

    public List<RoutineItemDto> parseTimetableToDtoList(String extractedText) {
        // JSON 형식 강제
        String prompt = String.format("""
            너는 시간표 분석 전문가야. 아래 텍스트에서 [요일(dayOfWeek), 도착시간(arrivalTime), 장소(destinationName)]을 추출해.
            - 요일 형식: MON, TUE, WED, THU, FRI, SAT, SUN
            - 시간 형식: HH:mm:ss
            - 응답은 오직 JSON 배열 포맷으로만 해줘.
            
            [텍스트]
            %s
            """, extractedText);

        // 2. API 호출
        GeminiDto.Response response = aiRestClient.post()
                .uri(geminiUrl + "?key=" + apiKey)
                .body(new GeminiDto.Request(prompt))
                .retrieve()
                .body(GeminiDto.Response.class);

        String rawJson = response.extractText();

        // 마크다운 제거 및 객체 변환
        try {
            String cleanedJson = rawJson.replaceAll("```json|```", "").trim();
            return objectMapper.readValue(cleanedJson, new TypeReference<List<RoutineItemDto>>() {});
        } catch (Exception e) {
            log.error("Gemini JSON 파싱 실패: {}", e.getMessage());
            throw new RuntimeException("시간표 데이터 변환 중 오류가 발생했습니다.");
        }
    }
}