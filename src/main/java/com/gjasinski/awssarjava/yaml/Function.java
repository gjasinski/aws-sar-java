package com.gjasinski.awssarjava.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Function {
    public String Handler;
    public String Runtime;
    public String CodeUri;
    public String DeadLetterQueue;
    public String Description;
    public String MemorySize;
    public String Timeout;
    public String VpcConfig;
    public Map<String, Object> Environment;
    public String Tags;
    public String Tracing;
    public String KmsKeyArn;
    public Object Layers;
    public String AutoPublishAlias;
    public String DeploymentPreference;
    public String ReservedConcurrentExecutions;
    public String ProvisionedConcurrencyConfig;
    public String AssumeRolePolicyDocument;
    public String EventInvokeConfig;
}
