package com.gjasinski.awssarjava.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Properties {
    public Object AssumeRolePolicyDocument;
    public Object AttributeDefinitions;
    public Object Action;
    public Object AutoPublishAlias;
    public Object AutoPublishCodeSha256;
    public Object CodeSigningConfigArn;
    public Object CodeUri;
    public Object ContentUri;
    public List<String> CompatibleRuntimes;
    public Map<String, Object> DeadLetterQueue;
    public Object DeploymentPreference;
    public Object DestZipsBucket;
    public Object Description;
    public Environment Environment;
    public Object EventInvokeConfig;
    public Map<String, Event> Events;
    public Map<String, Object> Variables;
    public Object FileSystemConfigs;
    public Object FunctionName;
    public Object Handler;
    public Object ImageConfig;
    public Object ImageUri;
    public Object InlineCode;
    public Object KmsKeyArn;
    public Object KeySchema;
    public Object ProvisionedThroughput;
    public Object Layers;
    public Object LayerName;
    public Object LicenseInfo;
    public Object LogGroupName;
    public Object MemorySize;
    public Object PackageType;
    public Object ProvisionedConcurrencyConfig;
    public Object Principal;
    public Object ReservedConcurrentExecutions;
    public Object Role;
    public Object RetentionPolicy;
    public Object RetentionInDays;
    public Object Runtime;
    public Object SourceZipUrl;
    public Object SourceAccount;
    public Object SecretString;
    public Object StreamSpecification;
    public Object ServiceToken;
    public Object ShardCount;
    public Object SourceArn;
    public Object BucketEncryption;
    public Object Bucket;
    public Object BucketName;
    public Object PublicAccessBlockConfiguration;
    public Object PolicyDocument;
    public Object TableName;
    public Object Timeout;
    public Object Tracing;
    public Object VersionDescription;
    public Object VpcConfig;
    public Object DefinitionBody;
    public Object StageName;
    public Object NotificationConfiguration;
    public Object GlobalSecondaryIndexes;
    public Object Subscription;
    public Object Path;
}