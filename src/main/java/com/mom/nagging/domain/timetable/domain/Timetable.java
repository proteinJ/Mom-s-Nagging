package com.mom.nagging.domain.timetable.domain;

import com.mom.nagging.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek; // MON ~ SUN

    private int period;

    private String subject;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    // FK
    // 회원의 시간표임을 명시하기 위한 다대일 매핑 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Timetable(Member member, DayOfWeek dayOfWeek, int period, String subject, LocalTime startTime, LocalTime endTime) {
        this.member = member;
        this.dayOfWeek = dayOfWeek;
        this.period = period;
        this.subject = subject;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}