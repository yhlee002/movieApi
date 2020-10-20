//package com.portfolio.demo.project.service;
//
//import com.portfolio.demo.project.entity.token.RememberMeToken;
//import com.portfolio.demo.project.repository.RememberMeTokenRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
//import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//import java.util.Date;
//import java.util.List;
//
//@Service
//@Transactional
//public class RememberMeTokenService implements PersistentTokenRepository {
//
//    @Autowired
//    private RememberMeTokenRepository rememberMeTokenRepository;
//
//    @Override
//    public void createNewToken(PersistentRememberMeToken token) {
//        RememberMeToken rmT = new RememberMeToken(token);
//        this.rememberMeTokenRepository.save(rmT);
//    }
//
//    @Override
//    public void updateToken(String series, String tokenValue, Date lastUsed) {
//        RememberMeToken rmT = this.rememberMeTokenRepository.findBySeries(series);
//        if(rmT != null){
//            rmT.setToken(tokenValue);
//            rmT.setLastUsed(lastUsed);
//            this.rememberMeTokenRepository.save(rmT); // 기존에 중복된 값이 있으면 그냥 save만 해도 업데이트가 됨?
//        }
//    }
//
//    @Override
//    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
//        RememberMeToken rmT = this.rememberMeTokenRepository.findBySeries(seriesId);
//        return new PersistentRememberMeToken(rmT.getEmail(), rmT.getSeries(), rmT.getToken(), rmT.getLastUsed());
//    }
//
//    @Override
//    public void removeUserTokens(String email) {
//        List<RememberMeToken> rmTs = this.rememberMeTokenRepository.findByEmail(email); // List인 이유는? 토큰이 여러개 생기는지 확인 필요
//        this.rememberMeTokenRepository.deleteAll(rmTs);
//    }
//}
