package com.gjasinski.awssarjava.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetectionResult {
    private DetectResultEnum detectResultEnum;
    private String functionName;
    private String codeUri;
}
