package com.portfolio.demo.project.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "persistent_logins")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class RememberMeToken {

    @Id
    private String series;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "TOKEN", nullable = false)
    private String token;

    @Column(name = "LAST_USED", nullable = false)
    private Date lastUsed;

    public RememberMeToken(PersistentRememberMeToken token) {
        this.series = token.getSeries();
        this.username = token.getUsername();
        this.token = token.getTokenValue();
        this.lastUsed = token.getDate();
    }
}
