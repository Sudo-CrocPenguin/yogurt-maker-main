package com.danieldev87.demo.domain.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.danieldev87.demo.domain.model.YogurtBatch;
import com.danieldev87.demo.domain.repository.TemperatureLogRepository;
import com.danieldev87.demo.domain.repository.YogurtBatchRepository;
import com.danieldev87.demo.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class MonitoringServiceTest {

    @Mock
    private YogurtBatchRepository batchRepository;

    @Mock
    private TemperatureLogRepository temperatureLogRepository;

    @Mock
    private TemperatureControlService temperatureControlService;

    @InjectMocks
    private MonitoringService monitoringService;

    @Test
    void getTemperatureLogsRejectsPartialDateRange() {
        YogurtBatch batch = YogurtBatch.builder().id(3L).build();
        when(batchRepository.findById(3L)).thenReturn(Optional.of(batch));

        assertThrows(
            BusinessException.class,
            () -> monitoringService.getTemperatureLogs(3L, LocalDateTime.now(), null)
        );

        verifyNoInteractions(temperatureLogRepository);
    }
}
