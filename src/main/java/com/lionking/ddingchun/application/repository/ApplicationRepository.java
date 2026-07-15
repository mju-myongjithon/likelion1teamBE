package com.lionking.ddingchun.application.repository;

import com.lionking.ddingchun.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    boolean existsByPost_IdAndApplicant_Id(
            Long postId,
            Long applicantId
    );

    /*
     * 마이페이지에 노출할, 내가 신청한 모집글 목록
     */
    List<Application> findByApplicant_IdOrderByAppliedAtDesc(
            Long applicantId
    );

    /*
     * 신청자 목록 조회 (작성자 전용)
     */
    List<Application> findByPost_IdOrderByAppliedAtDesc(
            Long postId
    );
}