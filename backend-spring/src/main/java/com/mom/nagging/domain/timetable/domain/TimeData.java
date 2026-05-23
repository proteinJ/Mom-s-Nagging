package com.mom.nagging.domain.timetable.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) // @Builder가 내부적으로 사용할 전체 생성자
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙을 만족하면서 외부 생성을 막음
public class TimeData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timedata_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek; // MON ~ SUN

    private int period;

    private String subjectName;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "details")
    private String details;

    private String professor;

    private String location;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id")
    private TimeTable timeTable;
}