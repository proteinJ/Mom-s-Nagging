package com.mom.nagging.domain.timetable.domain;

import com.mom.nagging.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) // @Builder가 내부적으로 사용할 전체 생성자
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙을 만족하면서 외부 생성을 막음
public class TimeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_id")
    private Long id;

    @Column(name = "timetable_title")
    private String title;

    // FK
    // 회원의 시간표임을 명시하기 위한 다대일 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 여러 TimeData(하나의 강의) 매핑
    @OneToMany(mappedBy = "timeTable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeData> timeDataList = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addTimeDataList(List<TimeData> dataList) {
        for (TimeData data : dataList) {
            this.timeDataList.add(data);
            data.setTimeTable(this);
        }
    }
}
