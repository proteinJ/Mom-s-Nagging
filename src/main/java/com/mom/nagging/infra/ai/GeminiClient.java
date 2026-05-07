package com.mom.nagging.infra.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mom.nagging.domain.routine.dto.RoutineItemDto;
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
    private final ObjectMapper objectMapper;

    @Value("${ai.google.api-key}")
    private String apiKey;

    @Value("${ai.google.gemini-url}")
    private String geminiUrl;

    // ---------------------------------------------------------
    // 1. 기존에 학생이 만들어둔 메서드 (루틴 분석용)
    // ---------------------------------------------------------
    public List<RoutineItemDto> parseTimetableToDtoList(String extractedText) {
        String prompt = String.format("""
            너는 시간표 분석 전문가야. 아래 텍스트에서 [요일(dayOfWeek), 도착시간(arrivalTime), 장소(destinationName)]을 추출해.
            - 요일 형식: MON, TUE, WED, THU, FRI, SAT, SUN
            - 시간 형식: HH:mm:ss
            - 응답은 오직 JSON 배열 포맷으로만 해줘.
            
            [텍스트]
            %s
            """, extractedText);

        GeminiDto.Response response = aiRestClient.post()
                .uri(geminiUrl + "?key=" + apiKey)
                .body(new GeminiDto.Request(prompt))
                .retrieve()
                .body(GeminiDto.Response.class);

        String rawJson = response.extractText();

        try {
            String cleanedJson = rawJson.replaceAll("```json|```", "").trim();
            return objectMapper.readValue(cleanedJson, new TypeReference<List<RoutineItemDto>>() {});
        } catch (Exception e) {
            log.error("Gemini JSON 파싱 실패: {}", e.getMessage());
            throw new RuntimeException("시간표 데이터 변환 중 오류가 발생했습니다.");
        }
    }

    // ---------------------------------------------------------
    // 2. 💡 새로 추가한 메서드 (시간표 등 범용 프롬프트용)
    // 총괄 매니저(TimetableRegistrationService)가 이 메서드를 찾고 있었습니다!
    // ---------------------------------------------------------
    public String askSimplePrompt(String prompt) {
        GeminiDto.Response response = aiRestClient.post()
                .uri(geminiUrl + "?key=" + apiKey)
                .body(new GeminiDto.Request(prompt))
                .retrieve()
                .body(GeminiDto.Response.class);

        String rawJson = response.extractText();

        // 제미나이가 가끔 친절하게 마크다운(```json)을 붙여주는 것을 걷어내고 순수 텍스트만 반환합니다.
        return rawJson.replaceAll("```json|```", "").trim();
    }
}