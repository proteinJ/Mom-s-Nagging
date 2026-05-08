package com.mom.nagging.domain.timetable.repository;

import com.mom.nagging.domain.timetable.domain.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;

// 시간표를 DB에 저장하고, 수정하고, 삭제하는 쿼리가 자동으로 생성됩니다.
public interface TimetableRepository extends JpaRepository<TimeTable, Long> {
}