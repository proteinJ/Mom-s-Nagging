package com.mom.nagging.domain.routine.domain;

import com.mom.nagging.domain.schedule.domain.Schedule;
import jakarta.persistence.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routine_item")
public class RoutineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routine_item_id")
    private Long id;

    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    private Week dayOfWeek;

    @Column(name = "arrival_time")
    private Time arrivalTime;

    @Column(name = "destination_name")
    private String destinationName;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "prep_time_minutes")
    private Integer prepTimeMinutes;

    @Column(name = "standard_travel_time")
    private Integer standardTravelTime;

    @Column(name = "latitude")
    private Double lat;

    @Column(name = "longitude")
    private Double lon;


    /**
     * FK
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_group_id")
    private RoutineGroup routineGroup;

    @OneToMany(mappedBy = "routineItem", cascade = CascadeType.ALL)
    private List<Schedule> schedules = new ArrayList<>();
}
