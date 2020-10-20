package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.token.RememberMeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, String> {
    List<RememberMeToken> findAll();
    RememberMeToken findBySeries(String series);
    List<RememberMeToken> findByEmail(String email);
}
