package com.portfolio.demo.project.entity.rememberMe;


import lombok.*;
import jakarta.persistence.*;

@Table(name = "persistant_logins")
@Entity
@Setter
@Getter
@ToString
@EqualsAndHashCode
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

    @Builder
    public PersistantLogins(String series, String username, String token, String lastUsed) {
        this.series = series;
        this.username = username;
        this.token = token;
        this.lastUsed = lastUsed;
    }
}
