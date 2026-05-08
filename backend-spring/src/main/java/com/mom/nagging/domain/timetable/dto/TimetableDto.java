package com.mom.nagging.domain.timetable.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "시간표 추출 전체 결과 응답 DTO")
public record TimetableDto(

        @Schema(description = "상태", example = "success") String status,
        @Schema(description = "추출된 강의 목록") List<TimetableItemDto> data
) {
}