package com.mom.nagging.domain.routine.domain;

import com.mom.nagging.domain.member.domain.Member;
import jakarta.persistence.*;

@Entity
@Table(name = "routine_group")
public class RoutineGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routine_group_id")
    private Long id;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "current_travel_time")
    private Integer currentTravelTime;


    /**
     * FK
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_item_id")
    private RoutineItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
