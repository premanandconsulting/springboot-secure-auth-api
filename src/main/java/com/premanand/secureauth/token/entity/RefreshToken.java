package com.premanand.secureauth.token.entity;


import com.premanand.secureauth.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Column(nullable = false, unique = true)
    private String token;

    @Setter
    @Getter
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @Getter
    @Column(nullable = false)
    private Instant expiryDate;

    @Setter
    @Getter
    @Column(nullable = false)
    private boolean revoked = false;

    // getters and setters
}

