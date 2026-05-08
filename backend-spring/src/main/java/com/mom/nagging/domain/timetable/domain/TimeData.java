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

    private String subject;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "details")
    private String details;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id")
    private TimeTable timeTable;

    @Builder
    public TimeData(DayOfWeek dayOfWeek, int period, String subject, LocalTime startTime, LocalTime endTime, String details) {
        this.dayOfWeek = dayOfWeek;
        this.period = period;
        this.subject = subject;
        this.startTime = startTime;
        this.endTime = endTime;
        this.details = details;
    }

}