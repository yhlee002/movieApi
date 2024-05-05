package com.portfolio.demo.project.entity;

import lombok.*;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "persistent_logins")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RememberMeToken {

    @Id
    private String series;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "TOKEN", nullable = false)
    private String token;

    @Column(name = "LAST_USED", nullable = false)
    private Date lastUsed;

    public void updateToken(String token) {
        this.token = token;
    }

    public void updateLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

    public RememberMeToken(PersistentRememberMeToken token) {
        this.series = token.getSeries();
        this.username = token.getUsername();
        this.token = token.getTokenValue();
        this.lastUsed = token.getDate();
    }
}
