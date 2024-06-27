package com.portfolio.demo.project.oauth2;

import com.portfolio.demo.project.dto.social.SocialLoginProvider;
import com.portfolio.demo.project.entity.member.MemberRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private SocialLoginProvider provider;
    private String identifier;
    private MemberRole role;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey,
                            SocialLoginProvider provider, String identifier, MemberRole role) {
        super(authorities, attributes, nameAttributeKey);
        this.provider = provider;
        this.identifier = identifier;
        this.role = role;
    }
}
