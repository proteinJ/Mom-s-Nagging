package com.mom.nagging.domain.timetable.dto;

import com.mom.nagging.domain.timetable.domain.DayOfWeek;
import java.time.LocalTime;

public record TimetableDto(

        DayOfWeek dayOfWeek, // 요일
        int period,
        String subject, // 과목
        LocalTime startTime, //
        LocalTime endTime
) {
}