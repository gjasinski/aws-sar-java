package com.gjasinski.awssarjava.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resource {
    public String Type;
    public Properties Properties;
    public Object DependsOn;
    public Object Condition;
    public Object DeletionPolicy;

    /*
AWSTemplateFormatVersion: '2010-09-09'
Transform: 'AWS::Serverless-2016-10-31'
Description: 'Example of Multiple-Origin CORS using API Gateway and Lambda'
Resources:
  ExampleRoot:
    Type: 'AWS::Serverless::Function'
    Properties:
      CodeUri: '.'
      Handler: 'routes/root.handler'
      Runtime: 'nodejs12.x'
      Events:
        Get:
          Type: 'Api'
          Properties:
            Path: '/'
            Method: 'get'
  ExampleTest:
    Type: 'AWS::Serverless::Function'
    Properties:
      CodeUri: '.'
      Handler: 'routes/test.handler'
      Runtime: 'nodejs12.x'
      Events:
        Delete:
          Type: 'Api'
          Properties:
            Path: '/test'
            Method: 'delete'
        Options:
          Type: 'Api'
          Properties:
            Path: '/test'
            Method: 'options'

Outputs:
  ExampleApi:
    Description: "API Gateway endpoint URL for Prod stage for API Gateway Multi-Origin CORS function"
    Value: !Sub "https://${ServerlessRestApi}.execute-api.${AWS::Region}.amazonaws.com/Prod/"
  ExampleRoot:
    Description: "API Gateway Multi-Origin CORS Lambda Function (Root) ARN"
    Value: !GetAtt ExampleRoot.Arn
  ExampleRootIamRole:
    Description: "Implicit IAM Role created for API Gateway Multi-Origin CORS function (Root)"
    Value: !GetAtt ExampleRootRole.Arn
  ExampleTest:
    Description: "API Gateway Multi-Origin CORS Lambda Function (Test) ARN"
    Value: !GetAtt ExampleTest.Arn
  ExampleTestIamRole:
    Description: "Implicit IAM Role created for API Gateway Multi-Origin CORS function (Test)"
    Value: !GetAtt ExampleTestRole.Arn
*/

}
