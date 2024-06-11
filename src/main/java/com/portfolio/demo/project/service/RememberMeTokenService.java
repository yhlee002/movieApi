package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.RememberMeToken;
import com.portfolio.demo.project.repository.RememberMeTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RememberMeTokenService implements PersistentTokenRepository {

    private final RememberMeTokenRepository rememberMeTokenRepository;

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        RememberMeToken rmT = new RememberMeToken(token);
        rememberMeTokenRepository.save(rmT);
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        RememberMeToken rmT = rememberMeTokenRepository.findBySeries(series);
        if(rmT != null){
            rmT.updateToken(tokenValue);
            rmT.updateLastUsed(lastUsed);
            rememberMeTokenRepository.save(rmT);
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        RememberMeToken rmT = rememberMeTokenRepository.findBySeries(seriesId);
        return new PersistentRememberMeToken(rmT.getUsername(), rmT.getSeries(), rmT.getToken(), rmT.getLastUsed());
    }

    @Override
    public void removeUserTokens(String username) {
        List<RememberMeToken> rmTs = rememberMeTokenRepository.findByUsername(username); // List인 이유는? 토큰이 여러개 생기는지 확인 필요
        this.rememberMeTokenRepository.deleteAll(rmTs);
    }
}
