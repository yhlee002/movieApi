package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.loginlog.LoginLog;
import com.portfolio.demo.project.entity.loginlog.LoginResult;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {

    @Query(value = "select l from LoginLog l join fetch l.member")
    Page<LoginLog> findAll(Pageable pageable);

    @Query(value = "select l from LoginLog l join fetch l.member m where l.member.memNo = :id")
    Page<LoginLog> findByMemNo(@Param("id") Long id, Pageable pageable);

    Page<LoginLog> findByIp(String ip, Pageable pageable);

    Page<LoginLog> findByResult(LoginResult result, Pageable pageable);
}
