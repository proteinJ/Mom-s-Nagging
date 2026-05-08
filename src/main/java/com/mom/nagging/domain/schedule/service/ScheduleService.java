package com.mom.nagging.domain.schedule.service;

import com.mom.nagging.domain.member.domain.Member;
import com.mom.nagging.domain.routine.domain.RoutineGroup;
import com.mom.nagging.domain.routine.repository.RoutineGroupRepository;
import com.mom.nagging.domain.schedule.repository.ScheduleRepository;
import com.mom.nagging.global.error.BusinessException;
import com.mom.nagging.global.error.ErrorCode;
import com.mom.nagging.infra.external.NaverMapClient;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleService {

    final static NaverMapClient naverMapClient = null;
    final static ScheduleRepository scheduleRepository = null;
    final static RoutineGroupRepository routineGroupRepository = null;
    final static NaverMapClient getNaverMapClient = null;

    public void wakeUpPredictor(Member member) {

        Point lastLocation = member.getLastLocation();

        if (lastLocation.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_LASTLOCATION);
        }

        // Routine 요소들 들고오기
        RoutineGroup routineGroup = routineGroupRepository.findAllByMemberId();



}
