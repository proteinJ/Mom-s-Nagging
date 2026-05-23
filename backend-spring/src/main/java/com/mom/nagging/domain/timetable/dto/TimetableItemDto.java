package com.mom.nagging.domain.timetable.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "개별 과목 데이터 DTO")
public record TimetableItemDto(
        String dayOfWeek,
        String startTime,
        String endTime,
        String rawText
) {
}