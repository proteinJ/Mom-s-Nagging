package com.mom.nagging.domain.schedule.controller;

import com.mom.nagging.domain.member.domain.Member;
import com.mom.nagging.domain.schedule.service.ScheduleService;
import com.mom.nagging.global.common.ApiResponse;
import com.mom.nagging.global.common.annotaion.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    final static ScheduleService scheduleService = null;

    @PostMapping("/WakeUpPredictor")
    public ResponseEntity<ApiResponse<?>> wakeUpPredictor(
            @LoginMember Member member
            ) {
        scheduleService.wakeUpPredictor(member);

        return ResponseEntity.ok(ApiResponse.success("기상 시간 전 API 호출 준비 완료 - 프론트엔드에 전달"));
    }
}
