package com.gjasinski.awssarjava.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpApi {
    private String auth;
    private String accessLogSettings;
    private String stageVariables;
    private String tags;
}
