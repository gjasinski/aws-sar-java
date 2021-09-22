package com.gjasinski.awssarjava.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parameter {
    public String Type;
    public String Default;
    public Boolean NoEcho;
    public String AllowedPattern;
    public String Description;
    public String ConstraintDescription;
    public Integer MinValue;
    public Integer MaxValue;
    public List<String> AllowedValues;
}
