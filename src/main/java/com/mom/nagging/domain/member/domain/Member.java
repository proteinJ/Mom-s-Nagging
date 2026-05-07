package com.mom.nagging.domain.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String realname;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false)
    private int totalPoint;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point lastLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public void updateProfile(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) { this.password = password; }

}
