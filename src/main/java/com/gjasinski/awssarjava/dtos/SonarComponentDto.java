package com.gjasinski.awssarjava.dtos;

import lombok.Data;

import java.util.List;

@Data
public class SonarComponentDto {
    private String key;
    private String name;
    private String qualifier;
    private List<MeasureDto> measures;
}
