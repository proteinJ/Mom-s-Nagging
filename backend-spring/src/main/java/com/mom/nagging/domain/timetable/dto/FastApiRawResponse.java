package com.mom.nagging.domain.timetable.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mom.nagging.domain.timetable.domain.DayOfWeek;

import java.time.LocalTime;
import java.util.List;

public record FastApiRawResponse(
        @JsonProperty("data")
        List<CellInfo> cells
) {
    public record CellInfo(

            String day,          // 예: "월"

            @JsonProperty("start_time")
            String startTime,    // 예: "09:00"

            @JsonProperty("end_time")
            String endTime,      // 예: "10:30"

            @JsonProperty("raw_text")
            String rawText       // 예: "운영체제\n옥수열\n공학관 402" (추출된 거친 텍스트)
    ) {}
}