package com.mom.nagging.infra.ai;

import com.mom.nagging.domain.routine.dto.RoutineItemDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GeminiClientTest {

    @Autowired
    private GeminiClient geminiClient;

    @Test
    @DisplayName("OCR 텍스트가 들어오면 Gemini가 루틴 리스트로 변환해야 한다")
    void parseRoutineTest() {
        // given (준비: 어떤 상황이 주어졌을 때)
        String rawOCRText = "월 09:00 데이터베이스 공학관 302호";
    
        // when (실행: 무엇을 하면)
        List<RoutineItemDto.AnalysisList> result = 
    
        // then (검증: 어떤 결과가 나와야 하는가)
        
    }
}