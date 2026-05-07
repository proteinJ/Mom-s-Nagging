package com.mom.nagging.domain.timetable.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mom.nagging.domain.member.domain.Member;
import com.mom.nagging.domain.timetable.domain.Timetable;
import com.mom.nagging.domain.timetable.dto.TimetableDto;
import com.mom.nagging.domain.timetable.repository.TimetableRepository;
import com.mom.nagging.infra.ai.VisionClient;
import com.mom.nagging.infra.ai.GeminiClient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimetableRegistrationService {

    private final VisionClient visionClient;
    private final GeminiClient geminiClient;
    private final TimetableRepository timetableRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public List<Timetable> processAndSaveTimetable(byte[] imageBytes, Member member) {
        try {
            String rawText = visionClient.extractTextFromImage(imageBytes);

            String prompt = "다음은 초등학교 시간표 이미지에서 추출한 텍스트야. " +
                    "이 텍스트를 분석해서 아래의 JSON 배열 형식으로만 대답해줘. 다른 부가 설명은 절대 하지마.\n" +
                    "형식: [{\"dayOfWeek\": \"MON\", \"period\": 1, \"subject\": \"국어\", \"startTime\": \"09:10\", \"endTime\": \"09:50\"}]\n" +
                    "추출된 텍스트:\n" + rawText;

            String jsonResult = geminiClient.askSimplePrompt(prompt);

            List<TimetableDto> dtoList = objectMapper.readValue(jsonResult, new TypeReference<List<TimetableDto>>() {});

            List<Timetable> timetables = dtoList.stream()
                    .map(dto -> Timetable.builder()
                            .member(member)
                            .dayOfWeek(dto.dayOfWeek())
                            .period(dto.period())
                            .subject(dto.subject())
                            .startTime(dto.startTime())
                            .endTime(dto.endTime())
                            .build())
                    .collect(Collectors.toList());

            return timetableRepository.saveAll(timetables);

        } catch (Exception e) {
            throw new RuntimeException("시간표 자동 등록에 실패했습니다.", e);
        }
    }
}