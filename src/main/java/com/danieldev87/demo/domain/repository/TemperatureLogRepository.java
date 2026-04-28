package com.danieldev87.demo.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.danieldev87.demo.domain.model.TemperatureLog;
import com.danieldev87.demo.domain.model.YogurtBatch;

@Repository
public interface TemperatureLogRepository extends JpaRepository<TemperatureLog, Long> {
    
    List<TemperatureLog> findByBatch(YogurtBatch batch);
    
    List<TemperatureLog> findByBatchAndTypeOrderByRecordedAtDesc(Long batchId, TemperatureLog.LogType type);
    
    @Query("SELECT tl FROM TemperatureLog tl WHERE tl.batch.id = :batchId AND tl.recordedAt BETWEEN :startTime AND :endTime")
    List<TemperatureLog> findByBatchAndTimeRange(@Param("batchId") Long batchId, 
                                                  @Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT AVG(tl.temperature) FROM TemperatureLog tl WHERE tl.batch.id = :batchId AND tl.type = :type")
    Double getAverageTemperatureByBatchAndType(@Param("batchId") Long batchId, @Param("type") TemperatureLog.LogType type);
    
    @Query("SELECT MAX(tl.temperature) FROM TemperatureLog tl WHERE tl.batch.id = :batchId")
    Double getMaxTemperatureByBatch(@Param("batchId") Long batchId);
    
    @Query("SELECT MIN(tl.temperature) FROM TemperatureLog tl WHERE tl.batch.id = :batchId")
    Double getMinTemperatureByBatch(@Param("batchId") Long batchId);
}