package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.RememberMeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, String> {

    RememberMeToken findBySeries(String series);

    List<RememberMeToken> findByUsername(String username);
}
