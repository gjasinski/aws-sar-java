package com.gjasinski.awssarjava.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SamTemplate {
    public String AWSTemplateFormatVersion;
    public Object Transform;
    public Globals Globals;
    public String Description;
    public Object Metadata;
    public Map<String, Parameter> Parameters;
    public Object Mappings;
    public Object Conditions;
    public Map<String, Resource> Resources;
    public Map<String, Output> Outputs;
}
