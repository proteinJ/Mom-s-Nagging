package com.mom.nagging.domain.timetable.dto;

import com.mom.nagging.domain.timetable.domain.DayOfWeek;
import com.mom.nagging.domain.timetable.domain.TimeData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
@Schema(description = "개별 강의 정보 DTO")
public class TimeItemResponse {

    @Schema(description = "DB 시간표 ID", example = "1")
    private Long id;

    @Schema(description = "요일", example = "MON")
    private DayOfWeek dayOfWeek;

    @Schema(description = "교시", example = "1")
    private int period;

    @Schema(description = "과목명", example = "수치해석")
    private String subject;

    @Schema(description = "시작 시간", example = "09:00")
    private LocalTime startTime;

    @Schema(description = "종료 시간", example = "10:30")
    private LocalTime endTime;

    @Schema(description = "교수", example = "옥수열")
    private String professor;

    @Schema(description = "장소", example = "S06-601")
    private String location;

    // 정적 팩토리 메서드 (컨벤션 유지)
    public static TimeItemResponse of(TimeData timeData) {
        return TimeItemResponse.builder()
                .id(timeData.getId())
                .dayOfWeek(timeData.getDayOfWeek())
                .period(timeData.getPeriod())
                .subject(timeData.getSubjectName())
                .startTime(timeData.getStartTime())
                .endTime(timeData.getEndTime())
                .professor(timeData.getProfessor())
                .location(timeData.getLocation())
                .build();
    }
}