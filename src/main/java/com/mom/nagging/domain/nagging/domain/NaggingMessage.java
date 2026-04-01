package com.mom.nagging.domain.nagging.domain;

import jakarta.persistence.*;

@Entity
public class NaggingMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nagging_message_id")
    private Long id;

    @Column(name = "dialect_type")
    @Enumerated(EnumType.STRING)
    private DIALECTTYPE dialectType; // MOM, DAD, BROTHER, GRAND

    @Column(name = "anger_level")
    private Integer angerLevel;

    @Column(name = "situation_tag")
    @Enumerated(EnumType.STRING)
    private SituationTag situationTag; // WAKEUP, BEFORE_DEPART, RUN, TAXI, LATE

    private String content;

}

