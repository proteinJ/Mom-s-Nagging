package com.mom.nagging.domain.timetable.service;

import com.mom.nagging.domain.member.domain.Member;
import com.mom.nagging.domain.timetable.domain.DayOfWeek;
import com.mom.nagging.domain.timetable.domain.TimeData;
import com.mom.nagging.domain.timetable.domain.TimeTable;
import com.mom.nagging.domain.timetable.dto.FastApiRawResponse;
import com.mom.nagging.domain.timetable.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TimetableRegistrationService {

    private final TimetableRepository timetableRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.fastapi.url}")
    private String fastApiUrl;

    @Value("${ai.google.gemini-url}")
    private String geminiUrl;

    @Value("${ai.google.api-key}")
    private String geminiApiKey;

    @Transactional
    public List<TimeData> processAndSaveTimetable(byte[] imageBytes, Member member) {
        // 도커 내부망을 통해 FastAPI(YOLO + Vision OCR) 호출
        FastApiRawResponse rawResponse = callFastApiServer(imageBytes);

        TimeTable timeTable = TimeTable.builder()
                .member(member)
                .title("내 시간표")
                .build();

        List<FastApiRawResponse.CellInfo> cells = rawResponse.cells();
        if (cells == null || cells.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("다음은 시간표의 여러 칸에서 추출된 거친 텍스트들의 목록이야.\n");
        promptBuilder.append("각 항목을 분석해서 과목명, 교수명, 강의실을 찾고, 반드시 '인덱스: 과목명/교수명/강의실' 형태로 줄바꿈해서 답변해줘.\n");
        promptBuilder.append("정보가 없으면 빈칸으로 두고 슬래시(/) 구분을 유지해. 부가 설명은 절대 금지.\n");
        promptBuilder.append("예시:\n0: 운영체제/옥수열/공학관 402\n1: 컴퓨터네트워크/홍길동/공8311\n\n데이터:\n");

        for (int i = 0; i<cells.size(); i++) {
            String safeText = cells.get(i).rawText().replace("\n", " ").trim();
            promptBuilder.append(i).append(": ").append(safeText).append("\n");
        }

        String batchResult = callGeminiApi(promptBuilder.toString());

        Map<Integer, String> parsedMap = new HashMap<>();
        String[] lines = batchResult.split("\n");

        for (String line : lines) {
            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                try {
                    int index = Integer.parseInt(parts[0].trim());
                    parsedMap.put(index, parts[1].trim());
                } catch (NumberFormatException ignored) {
                    // 파싱 실패한 줄은 무시
                }
            }
        }

        List<TimeData> timeDataList = new ArrayList<>();

        //  원본 Cell 배열과 정제된 Map을 매칭하여 엔티티 대량 조립
        for (int i = 0; i < cells.size(); i++) {
            FastApiRawResponse.CellInfo cell = cells.get(i);

            // Map에서 정제된 결과 가져오기 (만약 AI가 누락했다면 기본값 세팅)
            String refinedText = parsedMap.getOrDefault(i, "알 수 없는 과목/ / ");
            String[] parsed = refinedText.split("/", -1);

            String subjectName = parsed.length > 0 && !parsed[0].trim().isEmpty() ? parsed[0].trim() : "알 수 없는 과목";
            String professor = parsed.length > 1 ? parsed[1].trim() : "";
            String location = parsed.length > 2 ? parsed[2].trim() : "";

            DayOfWeek dayOfWeek = convertDay(cell.day());

            TimeData timeData = TimeData.builder()
                    .timeTable(timeTable)
                    .dayOfWeek(dayOfWeek)
                    .startTime(LocalTime.parse(cell.startTime()))
                    .endTime(LocalTime.parse(cell.endTime()))
                    .subjectName(subjectName)
                    .professor(professor)
                    .location(location)
                    .build();

            timeDataList.add(timeData);
        }

        timeTable.addTimeDataList(timeDataList);

        TimeTable savedTable = timetableRepository.save(timeTable);

        return savedTable.getTimeDataList();
    }

    private DayOfWeek convertDay(String day) {
        if (day == null) {
            return DayOfWeek.MON; // 기본값 방어
        }

        // 앞뒤 공백 제거하고 소문자든 대문자든 안전하게 처리
        return switch (day.trim().toUpperCase()) {
            case "월", "월요일", "MON", "MONDAY" -> DayOfWeek.MON;
            case "화", "화요일", "TUE", "TUESDAY" -> DayOfWeek.TUE;
            case "수", "수요일", "WED", "WEDNESDAY" -> DayOfWeek.WED;
            case "목", "목요일", "THU", "THURSDAY" -> DayOfWeek.THU;
            case "금", "금요일", "FRI", "FRIDAY" -> DayOfWeek.FRI;
            case "토", "토요일", "SAT", "SATURDAY" -> DayOfWeek.SAT;
            case "일", "일요일", "SUN", "SUNDAY" -> DayOfWeek.SUN;
            default -> DayOfWeek.MON; // 일치하는 게 없으면 월요일로 방어
        };
    }

    // FastAPI 호출 메서드
    private FastApiRawResponse callFastApiServer(byte[] imageBytes) {
        String url = fastApiUrl + "/api/v1/vision/extract";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource imageResource = new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() { return "timetable.png"; }
        };
        body.add("file", imageResource);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(url, entity, FastApiRawResponse.class);
    }

    // Gemini 1.5 Flash 호출 메서드
    private String callGeminiApi(String prompt) {
        String url = geminiUrl + "?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Gemini 1.5 공식 JSON 규격 바디 설계
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        // 응답 JSON 구조에서 파싱해서 텍스트 긁어오기
        try {
            Map resultBody = response.getBody();
            List candidates = (List) resultBody.get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            Map firstPart = (Map) parts.get(0);
            return (String) firstPart.get("text");
        } catch (Exception e) {
            return "미정/ / "; // 실패 시 기본 파싱 포맷 리턴
        }
    }
}