package com.gjasinski.awssarjava.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Environment {
    public Map<String, Object> Variables;
}
