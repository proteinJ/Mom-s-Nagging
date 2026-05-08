package com.mom.nagging.domain.schedule.domain;

// 사용자의 현재 행동 상태를 관리하는 열거형(Enum) 클래스입니다.
// DB에 문자열로 저장되어 '엄마의 잔소리'가 언제 발동해야 하는지 상태 변화를 추적하는 데 사용됩니다.
public enum CurStatus {
    PENDING,   // 대기 중 (아직 일정이 시작되지 않음)
    AWAKE,     // 기상 완료 (일어남)
    DEPARTED,  // 출발 완료 (집에서 나감)
    MOVING,    // 이동 중 (학교/직장으로 가는 중)
    ARRIVED,   // 도착 완료 (목적지 도착)
    LATE       // 지각 (목표 시간 초과)
}