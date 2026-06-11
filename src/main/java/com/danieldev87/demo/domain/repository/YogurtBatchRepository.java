package com.danieldev87.demo.domain.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.danieldev87.demo.domain.model.YogurtBatch;
import com.danieldev87.demo.domain.model.YogurtBatch.BatchStatus;

@Repository
public interface YogurtBatchRepository extends JpaRepository<YogurtBatch, Long> {
    
    List<YogurtBatch> findByStatus(BatchStatus status);

    List<YogurtBatch> findByStatusIn(Collection<BatchStatus> statuses);

    long countByStatus(BatchStatus status);

    long countByStatusIn(Collection<BatchStatus> statuses);

    long countByStatusAndCreatedAtBetween(BatchStatus status, LocalDateTime startDate, LocalDateTime endDate);
    
}
