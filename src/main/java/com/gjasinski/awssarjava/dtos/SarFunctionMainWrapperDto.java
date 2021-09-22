package com.gjasinski.awssarjava.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SarFunctionMainWrapperDto {
    private Integer approximateResultCount;
    private List<SarFunctionMainDto> applications;
}
