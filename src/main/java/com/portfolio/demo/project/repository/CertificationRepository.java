package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.controller.member.certkey.CertificationType;
import com.portfolio.demo.project.entity.CertificationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificationRepository extends JpaRepository<CertificationData, Long> {

    CertificationData findByCertificationId(String certificationId);

    CertificationData findByCertificationIdAndType(String certificationId, CertificationType type);
}
