package com.mom.nagging.domain.timetable.dto;

import com.mom.nagging.domain.timetable.domain.DayOfWeek;
import com.mom.nagging.domain.timetable.domain.Timetable;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class TimetableResponse {
    // DB에 저장된 시간표의 고유 식별자(PK)를 프론트엔드에 전달하기 위한 필드입니다.
    private Long id;

    // 추출된 시간표의 요일 데이터를 담는 필드입니다.
    private DayOfWeek dayOfWeek;

    // 추출된 시간표의 몇 교시인지 나타내는 정수형 필드입니다.
    private int period;

    // 추출된 시간표의 과목명
    private String subject;

    // 해당 교시의 시작 시간
    private LocalTime startTime;

    // 해당 교시의 종료 시간을 나타내는 필드입니다.
    private LocalTime endTime;

    // Timetable 엔티티 객체를 매개변수로 받아 TimetableResponse DTO 객체로 변환하는 정적 팩토리 메서드입니다.
    // 친구가 작성한 LoginResponse 코드의 네이밍 컨벤션과 동일하게 메서드명을 of로 지정했습니다.
    public static TimetableResponse of(Timetable timetable) {
        // Builder 패턴을 사용하여 전달받은 엔티티의 필드 값들을 DTO의 필드에 각각 매핑한 후 객체를 생성하여 반환합니다.
        return TimetableResponse.builder()
                .id(timetable.getId())
                .dayOfWeek(timetable.getDayOfWeek())
                .period(timetable.getPeriod())
                .subject(timetable.getSubject())
                .startTime(timetable.getStartTime())
                .endTime(timetable.getEndTime())
                .build();
    }
}