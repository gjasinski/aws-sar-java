package com.gjasinski.awssarjava.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Policy {
    public com.gjasinski.awssarjava.yaml.policies.DynamoDBCrudPolicy DynamoDBCrudPolicy;
    public com.gjasinski.awssarjava.yaml.policies.LambdaInvokePolicy LambdaInvokePolicy;
    public com.gjasinski.awssarjava.yaml.policies.KMSDecryptPolicy KMSDecryptPolicy;
    public com.gjasinski.awssarjava.yaml.policies.SNSPublishMessagePolicy SNSPublishMessagePolicy;
    public com.gjasinski.awssarjava.yaml.policies.S3CrudPolicy S3CrudPolicy;


}
