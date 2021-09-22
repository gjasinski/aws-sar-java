package com.gjasinski.awssarjava.yaml.policies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamoDBCrudPolicy {
    public String TableName;

}
