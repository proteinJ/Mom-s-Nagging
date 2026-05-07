package com.mom.nagging.domain.schedule.domain;

import com.mom.nagging.domain.member.domain.Member;
import com.mom.nagging.domain.routine.domain.RoutineItem;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

// 매일매일 사용자가 실제로 수행해야 하는 '하루 단위의 일정(실행 로그)'을 기록하는 엔티티입니다.
@Entity
public class Schedule {

    // 이 일정 테이블의 고유 식별자(PK)입니다. 데이터가 생성될 때마다 1씩 자동으로 증가합니다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    // 이 일정이 수행되어야 하는 목표 날짜입니다. (예: 2026-05-05)
    @Column(name = "target_date")
    private Date targetDate;

    // 현재 사용자의 상태를 나타냅니다. EnumType.STRING을 사용하여 DB에 숫자 대신 문자로 안전하게 저장합니다.
    @Enumerated(EnumType.STRING)
    private CurStatus status;

    // 현재 '엄마의 잔소리' 강도를 숫자로 나타냅니다. 지각이 가까워질수록 수치가 올라가는 핵심 로직에 쓰이겠네요.
    @Column(name = "current_nagging_level")
    private Integer currentNaggingLevel;

    // 사용자가 실제로 일어난 시간을 기록합니다.
    @Column(name = "actual_wake_up_time")
    private LocalDateTime actualWakeUpTime;

    // 사용자가 실제로 출발한 시간을 기록합니다.
    @Column(name = "actual_depart_time")
    private LocalDateTime actualDepartTime;

    // 다대일(N:1) 관계 매핑입니다. 하나의 루틴(반복되는 기본 정보)을 바탕으로 매일매일의 일정이 여러 개 생성될 수 있습니다.
    // 지연 로딩(LAZY)을 사용하여 불필요한 DB 조회를 막고 성능을 최적화했습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_item_id")
    private RoutineItem routineItem;

    // 다대일(N:1) 관계 매핑입니다. 한 명의 사용자(Member)가 여러 개의 일정(Schedule)을 가집니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}