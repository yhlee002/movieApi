package com.portfolio.demo.project.entity.rememberMe;


import lombok.*;
import jakarta.persistence.*;

@Table(name = "persistant_logins")
@Entity
@Builder
@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class PersistantLogins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String series;

    @Column(name = "username")
    private String username;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "LAST_USED")
    private String lastUsed;
}
