package com.gjasinski.awssarjava.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Api {
    private String auth;
    private String name;
    private String definitionUri;
    private String cacheClusterEnabled;
    private String cacheClusterSize;
    private String variables;
    private String endpointConfiguration;
    private String methodSettings;
    private String BinaryMediaTypes;
    private String minimumCompressionSize;
    private String cors;
    private String gatewayResponses;
    private String accessLogSetting;
    private String canarySetting;
    private String tracingEnabled;
    private String openApiVersion;
    private String domain;
}
