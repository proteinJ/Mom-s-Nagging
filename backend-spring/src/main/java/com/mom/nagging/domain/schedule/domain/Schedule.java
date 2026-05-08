package com.mom.nagging.domain.schedule.domain;

import com.mom.nagging.domain.member.domain.Member;
import com.mom.nagging.domain.routine.domain.RoutineItem;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @Column(name = "target_date")
    private Date targetDate;

    @Enumerated(EnumType.STRING)
    private CurStatus status; // PENDING, AWAKE, DEPARTED, MOVING, ARRIVED, LATE

    // 현재 엄마 짜증 레벨
    @Column(name = "current_nagging_level")
    private Integer currentNaggingLevel;

    // 실제 기상시간
    @Column(name = "actual_wake_up_time")
    private LocalDateTime actualWakeUpTime;

    // 실제 도착시간
    @Column(name = "actual_depart_time")
    private LocalDateTime actualDepartTime;

    /**
     * FK
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_item_id")
    private RoutineItem routineItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
