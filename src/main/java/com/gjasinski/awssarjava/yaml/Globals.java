package com.gjasinski.awssarjava.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Globals {
    public Function Function;
    public Api Api;
    public HttpApi HttpApi;
    public String SSESpecification;

}
