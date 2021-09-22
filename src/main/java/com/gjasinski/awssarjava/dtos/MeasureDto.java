package com.gjasinski.awssarjava.dtos;

import lombok.Data;

@Data
public class MeasureDto {
    private String metric;
    private Integer value;
    private String bestValue;
}
