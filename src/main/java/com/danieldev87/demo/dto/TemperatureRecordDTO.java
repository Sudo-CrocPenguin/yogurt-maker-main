package com.danieldev87.demo.dto;

import com.danieldev87.demo.domain.model.TemperatureLog;

import lombok.Data;

@Data
public class TemperatureRecordDTO {
    private Double temperature;
    private TemperatureLog.LogType type;
}

