package com.danieldev87.demo.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.danieldev87.demo.domain.model.Recipe;
import com.danieldev87.demo.domain.model.TemperatureLog;
import com.danieldev87.demo.domain.model.YogurtBatch;
import com.danieldev87.demo.domain.repository.RecipeRepository;
import com.danieldev87.demo.domain.repository.TemperatureLogRepository;
import com.danieldev87.demo.domain.repository.YogurtBatchRepository;
import com.danieldev87.demo.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class YogurtMakingServiceTest {

    @Mock
    private YogurtBatchRepository batchRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private TemperatureLogRepository temperatureLogRepository;

    @Mock
    private TemperatureControlService temperatureControlService;

    @InjectMocks
    private YogurtMakingService yogurtMakingService;

    @Test
    void startNewBatchRejectsInactiveRecipe() {
        Recipe recipe = Recipe.builder()
            .id(1L)
            .name("Archivada")
            .active(false)
            .build();

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        assertThrows(
            BusinessException.class,
            () -> yogurtMakingService.startNewBatch(1L, null, null)
        );

        verifyNoInteractions(batchRepository, temperatureControlService);
    }

    @Test
    void recordTemperaturePersistsManualTemperatureLog() {
        YogurtBatch batch = YogurtBatch.builder()
            .id(7L)
            .status(YogurtBatch.BatchStatus.INCUBATING)
            .build();

        when(batchRepository.findById(7L)).thenReturn(Optional.of(batch));

        yogurtMakingService.recordTemperature(7L, 43.2, TemperatureLog.LogType.MANUAL);

        ArgumentCaptor<TemperatureLog> captor = ArgumentCaptor.forClass(TemperatureLog.class);
        verify(temperatureLogRepository).save(captor.capture());

        TemperatureLog savedLog = captor.getValue();
        assertEquals(batch, savedLog.getBatch());
        assertEquals(43.2, savedLog.getTemperature());
        assertEquals(TemperatureLog.LogType.MANUAL, savedLog.getType());
    }
}
