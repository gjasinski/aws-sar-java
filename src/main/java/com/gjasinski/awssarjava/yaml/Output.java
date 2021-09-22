package com.gjasinski.awssarjava.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Output {
    public String Description;
    public Object Value;
    public Object Export;
    public Object Condition;
}
