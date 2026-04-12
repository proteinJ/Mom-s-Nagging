package com.mom.nagging.domain.routine.dto;

import java.util.List;

public class RoutineItemDto {

    // 1. Gemini 분석 결과 (OCR -> JSON 변환용)
    public record AnalysisResponse(
        String dayOfWeeks,
        String arrivalTime,
        String destinationName
    ) {}

    // 2. 분석 결과 리스트 (여러 과목/일정을 한꺼번에 받을 때)
    public record AnalysisList (
        List<AnalysisResponse> routines
    ) {}

    // 3. 실제 DB 저장 후 사용자에게 보여줄 상세 정보
    public record Response(
        Long id,
        String dayOfWeeks,
        String arrivalTime,
        String destinationName,
        boolean isActive
    ) {}
}
