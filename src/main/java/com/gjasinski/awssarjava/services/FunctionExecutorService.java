package com.gjasinski.awssarjava.services;

import com.gjasinski.awssarjava.entity.ExecutionResult;
import com.gjasinski.awssarjava.entity.SarFunctionMain;
import com.gjasinski.awssarjava.entity.SarSubFunction;
import com.gjasinski.awssarjava.entity.SarSubFunctionEventDetected;
import com.gjasinski.awssarjava.entity.TestExecution;
import com.gjasinski.awssarjava.repositories.ExecutionResultRepository;
import com.gjasinski.awssarjava.repositories.SarFunctionMainRepository;
import com.gjasinski.awssarjava.repositories.SarSubfunctionEventRepository;
import com.gjasinski.awssarjava.repositories.SarSubfunctionRepository;
import com.gjasinski.awssarjava.repositories.TestExecutionRepository;
import com.gjasinski.awssarjava.utils.DetectResultEnum;
import com.gjasinski.awssarjava.utils.EventType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class FunctionExecutorService {
    private static Logger LOGGER = Logger.getLogger(FunctionCloneService.class);
    private TestExecution testExecution;
    @Autowired
    SarFunctionMainRepository sarFunctionMainRepository;
    @Autowired
    SarSubfunctionRepository sarSubfunctionRepository;
    @Autowired
    SarSubfunctionEventRepository sarSubfunctionEventRepository;
    @Autowired
    ExecutionResultRepository executionResultRepository;
    @Autowired
    TestExecutionRepository testExecutionRepository;

    //    @PostConstruct
    public void executeAllFunctions() throws IOException, InterruptedException {
        LOGGER.info("INIT AWS");
        initSqs();
        initDynamoDb();
        initS3();
        LOGGER.info("EXECUTE!");
        testExecution = new TestExecution();
        testExecution.setStartDate(new Date());
        testExecution = testExecutionRepository.save(testExecution);
        try {
            LOGGER.info("IN_TRY!");
            LOGGER.info("after export!");
            List<SarFunctionMain> deployedFunctions = sarFunctionMainRepository.getAllByDeploymentCountIsGreaterThanAndHomePageUrlContainsOrderByIdAsc(-1, "github.com");
            for (int i = 0; i < deployedFunctions.size(); i++) {
                LOGGER.info(deployedFunctions.get(i).getId());
                SarFunctionMain function = deployedFunctions.get(i);
                if (function.getId() == 945) {
                    try {
                        LOGGER.info("START|" + function);
                        testFunction(function);
                        LOGGER.info("FINISHED|" + function);
                    } catch (Exception ex) {
                        testExecution.setEndDate(new Date());
                        testExecution.setSuccess(false);
                        testExecutionRepository.save(testExecution);
                        LOGGER.error("ERROR|SHUTDOWN PROCEED", ex);
                        System.exit(-1);
                    }
                }
            }
            LOGGER.info("FINISHED ALL");
            testExecution.setEndDate(new Date());
            testExecution.setSuccess(true);
            testExecutionRepository.save(testExecution);
        } catch (Throwable ex) {
            testExecution.setEndDate(new Date());
            testExecution.setSuccess(false);
            testExecutionRepository.save(testExecution);
        }
    }

    private void initSqs() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("aws", "sqs", "create-queue", "--queue-name", "MyQueue", "--no-verify-ssl");
        File executionStdout = new File("/home/ubuntu/sar-java-logs/initSqs.log");
        processBuilder.redirectOutput(executionStdout);
        processBuilder.redirectError(executionStdout);
        Process start = processBuilder.start();
        start.waitFor(20, TimeUnit.SECONDS);

        processBuilder = new ProcessBuilder("aws", "sqs", "send-message", "--queue-url", "https://queue.amazonaws.com/000000000000/MyQueue", "--message-body", "test", "--no-verify-ssl");
        executionStdout = new File("/home/ubuntu/sar-java-logs/initSqs1.log");
        processBuilder.redirectOutput(executionStdout);
        processBuilder.redirectError(executionStdout);
        start = processBuilder.start();
        start.waitFor(20, TimeUnit.SECONDS);
    }

    private void initDynamoDb() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("aws", "dynamodb", "create-table",
                "--table-name", "exampleTableName", "--attribute-definitions", "AttributeName=pk,AttributeType=S",
                "AttributeName=sk,AttributeType=S", "--key-schema", "AttributeName=pk,KeyType=HASH", "AttributeName=sk,KeyType=RANGE",
                "--provisioned-throughput", "ReadCapacityUnits=10,WriteCapacityUnits=5", "--no-verify-ssl");
        File executionStdout = new File("/home/ubuntu/sar-java-logs/initDynamoDb.log");
        processBuilder.redirectOutput(executionStdout);
        processBuilder.redirectError(executionStdout);
        Process start = processBuilder.start();
        start.waitFor(20, TimeUnit.SECONDS);
    }

    private void initS3() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("aws", "s3", "mb", "s3://example-bucket", "--no-verify-ssl");
        File executionStdout = new File("/home/ubuntu/sar-java-logs/initS31.log");
        processBuilder.redirectOutput(executionStdout);
        processBuilder.redirectError(executionStdout);
        Process start = processBuilder.start();
        start.waitFor(20, TimeUnit.SECONDS);

        processBuilder = new ProcessBuilder("aws", "s3", "cp", "/home/ubuntu/fileToS3.txt", "s3://example-bucket/test/key", "--no-verify-ssl");
        executionStdout = new File("/home/ubuntu/sar-java-logs/initS32.log");
        processBuilder.redirectOutput(executionStdout);
        processBuilder.redirectError(executionStdout);
        start = processBuilder.start();
        start.waitFor(20, TimeUnit.SECONDS);

        processBuilder = new ProcessBuilder("aws", "s3", "cp", "/home/ubuntu/image.jpg", "s3://example-bucket/test/image.jpg", "--no-verify-ssl");
        executionStdout = new File("/home/ubuntu/sar-java-logs/initS33.log");
        processBuilder.redirectOutput(executionStdout);
        processBuilder.redirectError(executionStdout);
        start = processBuilder.start();
        start.waitFor(20, TimeUnit.SECONDS);

        processBuilder = new ProcessBuilder("aws", "s3", "cp", "/home/ubuntu/sound.wav", "s3://example-bucket/test/sound.wav", "--no-verify-ssl");
        executionStdout = new File("/home/ubuntu/sar-java-logs/initS34.log");
        processBuilder.redirectOutput(executionStdout);
        processBuilder.redirectError(executionStdout);
        start = processBuilder.start();
        start.waitFor(20, TimeUnit.SECONDS);
    }

    private void testFunction(SarFunctionMain f) {
        List<SarSubFunction> byFunctionMain = sarSubfunctionRepository.findByFunctionMain(f);
        for (SarSubFunction sarSubFunction : byFunctionMain) {
            try {
                testSubFunction(sarSubFunction);
                if (sarSubFunction.getRuntime().toLowerCase().contains("node")) {
                    removeCopiedNodeModules(sarSubFunction);
                }
            } catch (Exception e) {
                LOGGER.error("ERRROR", e);
            }
        }
    }

    private void testSubFunction(SarSubFunction m) throws InterruptedException, IOException {
        if (m.getFunctionName() == null) {
            LOGGER.info(m.getId() + "|no function");
            return;
        }
        DetectResultEnum runtime = DetectResultEnum.fromRuntime(m.getRuntime());
        if (runtime.equals(DetectResultEnum.NODE_JS_6_10) ||
                runtime.equals(DetectResultEnum.NODE_JS_8_10)) {
            ExecutionResult executionResult = new ExecutionResult();
            executionResult.setTestExecution(testExecution);
            executionResult.setSubFunction(m);
            executionResult.setEventType(null);
            executionResult.setExecutionSslException(false);
            executionResult.setTimeout(false);
            executionResult.setInvalidLayerArn(false);
            executionResult.setHandlerNotFound(false);
            executionResult.setCannotFindModule(false);
            executionResult.setUnsupportedRuntime(true);
            executionResultRepository.save(executionResult);
            return;
        }
        if (runtime.toString().contains("NODE")) {
            removeCopiedNodeModules(m);
            copyNodeModules(m);
        }
        boolean anySuccess = false;
        List<SarSubFunctionEventDetected> bySubFunction = sarSubfunctionEventRepository.findBySubFunction(m);
        for (int i = 0; i < bySubFunction.size(); i++) {
            SarSubFunctionEventDetected sarSubFunctionEventDetected = bySubFunction.get(i);
            switch (bySubFunction.get(i).getEventType()) {
                case ALEXA_SKILLS_KIT:
                    anySuccess = anySuccess || runStandardProcess("alexa-skills-kit end-session", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("alexa-skills-kit intent-answer", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("alexa-skills-kit intent-getnewfact", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("alexa-skills-kit intent-mycoloris", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("alexa-skills-kit intent-recipe", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("alexa-skills-kit start-session", m, sarSubFunctionEventDetected);
                    break;
                case ALEXA_SMART_HOME:
                    anySuccess = anySuccess || runStandardProcess("alexa-smart-home smart-home-control-turn-off-request", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("alexa-smart-home smart-home-control-turn-on-request", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("alexa-smart-home smart-home-discovery-request", m, sarSubFunctionEventDetected);
                    break;
                case API_GATEWAY:
                    anySuccess = anySuccess || runStandardProcess("apigateway authorizer", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("apigateway aws-proxy --method GET", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("apigateway aws-proxy --method PUT", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("apigateway aws-proxy --method POST", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("apigateway aws-proxy --method DELETE", m, sarSubFunctionEventDetected);
                    runCustomProcess("/sar/example_events/aws_proxy_get_1.json", m, sarSubFunctionEventDetected);

                    break;
                case BATCH:
                    anySuccess = anySuccess || runStandardProcess("batch get-job", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("batch submit-job", m, sarSubFunctionEventDetected);
                    break;
                case CLOUDFORMATION:
                    anySuccess = anySuccess || runStandardProcess("cloudformation create-request", m, sarSubFunctionEventDetected);
                    break;
                case CLOUDFRONT:
                    anySuccess = anySuccess || runStandardProcess("cloudfront ab-test", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("cloudfront access-request-in-response", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("cloudfront http-redirect", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("cloudfront modify-querystring", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("cloudfront modify-response-header", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("cloudfront multiple-remote-calls-aggregate-response", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("cloudfront normalize-querystring-to-improve-cache-hit", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("cloudfront redirect-on-viewer-country", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("cloudfront redirect-unauthenticated-users", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("cloudfront response-generation", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("cloudfront serve-object-on-viewer-device", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("cloudfront simple-remote-call", m, sarSubFunctionEventDetected);
                    break;
                case CLOUDWATCH:
                    anySuccess = anySuccess || runStandardProcess("cloudwatch logs", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("cloudwatch scheduled-event", m, sarSubFunctionEventDetected);
                    break;
                case CODECOMMIT:
                    anySuccess = anySuccess || runStandardProcess("codecommit repository", m, sarSubFunctionEventDetected);
                    break;
                case CODEPIPELINE:
                    anySuccess = anySuccess || runStandardProcess("codepipeline job", m, sarSubFunctionEventDetected);
                    break;
                case COGNITO:
                    anySuccess = anySuccess || runStandardProcess("cognito sync-trigger", m, sarSubFunctionEventDetected);
                    break;
                case CONFIG:
                    anySuccess = anySuccess || runStandardProcess("config item-change-notification", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("config oversized-item-change-notification", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("config periodic-rule", m, sarSubFunctionEventDetected);
                    break;
                case DYNAMODB:
                    anySuccess = anySuccess || runStandardProcess("dynamodb update", m, sarSubFunctionEventDetected);
                    break;
                case KINESIS:
                    anySuccess = anySuccess || runStandardProcess("kinesis analytics", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("kinesis analytics-compressed", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("kinesis analytics-dynamodb", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("kinesis analytics-kpl", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("kinesis apachelog", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("kinesis cloudwatch-logs-processor", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("kinesis get-records", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("kinesis kinesis-firehose", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("kinesis streams-as-source", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("kinesis syslog", m, sarSubFunctionEventDetected);
                    break;
                case LEX:
                    anySuccess = anySuccess || runStandardProcess("lex book-car", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("lex book-hotel", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("lex make-appointment", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("lex order-flowers", m, sarSubFunctionEventDetected);
                    break;
                case REKOGNITION:
                    anySuccess = anySuccess || runStandardProcess("rekognition s3-request", m, sarSubFunctionEventDetected);
                    break;
                case S3:
                    anySuccess = anySuccess || runStandardProcess("s3 put", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("s3 delete", m, sarSubFunctionEventDetected);
                    break;
                case SES:
                case SNS:
                    anySuccess = anySuccess || runStandardProcess("ses email-receiving", m, sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runStandardProcess("sns notification", m, sarSubFunctionEventDetected);
                    runCustomProcess("/sar/example_events/sns_custom_1.json", m, sarSubFunctionEventDetected);
                    break;
                case SQS:
                    anySuccess = anySuccess || runStandardProcess("sqs receive-message", m, sarSubFunctionEventDetected);
                    break;
                case STEPFUNCTION:
                    anySuccess = anySuccess || runStandardProcess("stepfunctions error", m, sarSubFunctionEventDetected);
                    break;
                case EC2:
                    break;
            }
        }
        String directory = m.getPath().substring(m.getPath().lastIndexOf("/"));
        String[] files = new File(directory).list();
        if (files != null) {
            List<String> customEvents = Arrays.stream(files)
                    .filter(f -> f.contains("event") && f.contains(".json"))
                    .collect(Collectors.toList());
            for (int i = 0; i < customEvents.size(); i++) {
                String customEventName = customEvents.get(i);
                try {
                    String filePath = directory + customEventName;
                    SarSubFunctionEventDetected sarSubFunctionEventDetected = new SarSubFunctionEventDetected();
                    sarSubFunctionEventDetected.setEventType(EventType.CUSTOM_EVENT_DETECTED);
                    sarSubFunctionEventDetected.setSubFunction(m);
                    sarSubfunctionEventRepository.save(sarSubFunctionEventDetected);
                    anySuccess = anySuccess || runCustomProcess(filePath, m, sarSubFunctionEventDetected);
                } catch (IOException | InterruptedException e) {
                    LOGGER.error("ERROR", e);
                }
            }
        }
        if (!anySuccess) {
            if (runCustomEvent(m, "/sar/example_events/hello_world1.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event1.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event2.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event3.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event4.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event5.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event6.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event7.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event8.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event9.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event10.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event11.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event12.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event13.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event14.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event15.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event16.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event18.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event19.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event20.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/custom_event21.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/sns_custom_1.json")) {
                return;
            }
            if (runCustomEvent(m, "/sar/example_events/sns_custom_2.json")) {
                return;
            }
            runCustomEvent(m, "/sar/example_events/aws_proxy_get_1.json");
        }
    }

    private void copyNodeModules(SarSubFunction m) throws IOException, InterruptedException {
        String path = m.getPath().substring(0, m.getPath().lastIndexOf("/"));
        LOGGER.info("cp -r /home/ubuntu/node_modules/ " + path + "/" + m.getCodeUri());
        ProcessBuilder processBuilder = new ProcessBuilder("cp", "-r", "/home/ubuntu/node_modules/", path + "/" + m.getCodeUri());
        Process start = processBuilder.start();
        start.waitFor(60, TimeUnit.SECONDS);
        LOGGER.info("DONE|cp -r /home/ubuntu/node_modules/ " + path + "/" + m.getCodeUri());
    }

    private void removeCopiedNodeModules(SarSubFunction m) throws IOException, InterruptedException {
        String path = m.getPath().substring(0, m.getPath().lastIndexOf("/"));
        ProcessBuilder processBuilder = new ProcessBuilder("rm", "-rf", path + "/node_modules/");
        Process start = processBuilder.start();
        start.waitFor(60, TimeUnit.SECONDS);
        processBuilder = new ProcessBuilder("rm", "-rf", path + "/build/");
        start = processBuilder.start();
        start.waitFor(60, TimeUnit.SECONDS);

        path = path + "/" + m.getCodeUri();
        processBuilder = new ProcessBuilder("rm", "-rf", path + "/node_modules/");
        start = processBuilder.start();
        start.waitFor(60, TimeUnit.SECONDS);
        processBuilder = new ProcessBuilder("rm", "-rf", path + "/build/");
        start = processBuilder.start();
        start.waitFor(60, TimeUnit.SECONDS);
    }

    private boolean runCustomEvent(SarSubFunction m, String s) throws IOException, InterruptedException {
        String filePath;
        SarSubFunctionEventDetected sarSubFunctionEventDetected;
        filePath = s;
        sarSubFunctionEventDetected = new SarSubFunctionEventDetected();
        sarSubFunctionEventDetected.setEventType(EventType.CUSTOM_EVENT_GLOBAL);
        sarSubFunctionEventDetected.setSubFunction(m);
        sarSubFunctionEventDetected = sarSubfunctionEventRepository.save(sarSubFunctionEventDetected);
        return runCustomProcess(filePath, m, sarSubFunctionEventDetected);
    }

    private boolean runStandardProcess(String event, SarSubFunction m, SarSubFunctionEventDetected eventDetected) throws IOException, InterruptedException {
        boolean yaml = m.getPath().contains("yaml");
        String executionStdoutPath = "/home/ubuntu/sar-java-logs/execution/" + eventDetected.getId() + "_" + m.getId() + "_" + m.getFunctionName() + "_" + eventDetected.getEventType() + new Date().getTime() + ".log";

        String filePath = createScriptFile(event, m.getPath(), m.getFunctionName());
        boolean result = runProcess(createCommand(event, m.getPath(), m.getFunctionName()), m, eventDetected, filePath, executionStdoutPath);

        if (result) {
            return true;
        }

        String originalPath = m.getPath().substring(0, m.getPath().lastIndexOf("/"));
        String lastDirName = originalPath.substring(originalPath.lastIndexOf("/"));
        if (m.getRuntime().contains("nodejs")) {
            LOGGER.info(m.getId() + "|START install nodejs");
            removeCopiedNodeModules(m);
            copyNodeModules(m);
            String tmpPath = copyToTmp(m);
            if (npmInstall(tmpPath + "/" + lastDirName, executionStdoutPath, m)) {
                if (yaml) {
                    filePath = createScriptFile(event, tmpPath + "/" + lastDirName + "/template.yaml", m.getFunctionName());
                    result = runProcess(createCommand(event, tmpPath + "/" + lastDirName + "/template.yaml", m.getFunctionName()), m, eventDetected, filePath, executionStdoutPath);
                } else {
                    filePath = createScriptFile(event, tmpPath + "/" + lastDirName + "/template.yml", m.getFunctionName());
                    result = runProcess(createCommand(event, tmpPath + "/" + lastDirName + "/template.yml", m.getFunctionName()), m, eventDetected, filePath, executionStdoutPath);
                }
                LOGGER.info(m.getId() + "|FINISHED install nodejs|" + result);
            } else {
                LOGGER.info(m.getId() + "|CANNOT install nodejs");
            }
            cleanTmpDirectory(tmpPath);
            if (result) {
                return true;
            }
        }


        LOGGER.info(m.getId() + "|START SAM BUILD");
        //copy to tmp build and run
        String tmpPath = copyToTmp(m);
        if (buildLambda(tmpPath + "/" + lastDirName, m.getFunctionName(), executionStdoutPath)) {
            if (yaml) {
                filePath = createScriptFile(event, tmpPath + "/" + lastDirName + "/.aws-sam/build/template.yaml", m.getFunctionName());
                result = runProcess(createCommand(event, tmpPath + "/" + lastDirName + "/.aws-sam/build/template.yaml", m.getFunctionName()), m, eventDetected, filePath, executionStdoutPath);
            } else {
                filePath = createScriptFile(event, tmpPath + "/" + lastDirName + "/.aws-sam/build/template.yml", m.getFunctionName());
                result = runProcess(createCommand(event, tmpPath + "/" + lastDirName + "/.aws-sam/build/template.yml", m.getFunctionName()), m, eventDetected, filePath, executionStdoutPath);
            }
        } else {
            LOGGER.info(m.getId() + "|SAM BUILD FAILED!");
        }
        cleanTmpDirectory(tmpPath);
        LOGGER.info(m.getId() + "|FINISH COPY|" + result);
        return result;
    }

    private boolean npmInstall(String path, String logStdFile, SarSubFunction sarSubFunction) throws IOException, InterruptedException {
        boolean res1 = npmInstallInPath(path, logStdFile);
        boolean res2 = npmInstallInPath(path + "/" + sarSubFunction.getCodeUri(), logStdFile);
        return res1 || res2;
    }

    private boolean npmInstallInPath(String path, String logStdFile) throws IOException, InterruptedException {
        LOGGER.info("RUNIING IN: " + path);
        File executionStdout = new File(logStdFile + "_npm_0.log");
        ProcessBuilder pbClean = new ProcessBuilder("rm", "-rf", "build");
        pbClean.redirectOutput(executionStdout);
        pbClean.redirectError(executionStdout);
        pbClean.directory(new File(path));
        Process startInstall = pbClean.start();
        startInstall.waitFor(60, TimeUnit.SECONDS);
        LOGGER.info(Arrays.stream(new File(path).list()).reduce("", (a, b) -> a + ", " + b));

        executionStdout = new File(logStdFile + "_npm_1.log");

        ProcessBuilder pbInstall = new ProcessBuilder("npm", "install");
        pbInstall.redirectOutput(executionStdout);
        pbInstall.redirectError(executionStdout);
        pbInstall.directory(new File(path));
        startInstall = pbInstall.start();
        startInstall.waitFor(60, TimeUnit.SECONDS);

        executionStdout = new File(logStdFile + "_npm_2.log");
        ProcessBuilder pbRun = new ProcessBuilder("npm", "run", "build");
        pbRun.redirectOutput(executionStdout);
        pbRun.redirectError(executionStdout);
        pbRun.directory(new File(path));
        Process startRun = pbRun.start();
        startRun.waitFor(120, TimeUnit.SECONDS);

        return startInstall.exitValue() == 0 || startRun.exitValue() == 0;
    }

    private boolean runCustomProcess(String eventPath, SarSubFunction m, SarSubFunctionEventDetected eventDetected) throws IOException, InterruptedException {
        boolean yaml = m.getPath().contains("yaml");
        String executionStdoutPath = "/home/ubuntu/sar-java-logs/execution/" + eventDetected.getId() + "_" + m.getId() + "_" + m.getFunctionName() + "_" + eventDetected.getEventType() + new Date().getTime() + ".log";

        String filePath = createCustomScriptFile(eventPath, m.getPath(), m.getFunctionName());
        boolean result = runProcess(createCustomCommand(eventPath, m.getPath(), m.getFunctionName()), m, eventDetected, filePath, executionStdoutPath);
        if (result) {
            return true;
        }

        String originalPath = m.getPath().substring(0, m.getPath().lastIndexOf("/"));
        String lastDirName = originalPath.substring(originalPath.lastIndexOf("/"));

        if (m.getRuntime().contains("nodejs")) {
            LOGGER.info(m.getId() + "|START install nodejs");
            String tmpPath = copyToTmp(m);
            if (npmInstall(tmpPath + "/" + lastDirName, executionStdoutPath, m)) {
                if (yaml) {
                    filePath = createCustomScriptFile(eventPath, tmpPath + "/" + lastDirName + "/template.yaml", m.getFunctionName());
                    result = runProcess(createCommand(eventPath, tmpPath + "/" + lastDirName + "/template.yaml", m.getFunctionName()), m, eventDetected, filePath, executionStdoutPath);
                } else {
                    filePath = createCustomScriptFile(eventPath, tmpPath + "/" + lastDirName + "/template.yml", m.getFunctionName());
                    result = runProcess(createCommand(eventPath, tmpPath + "/" + lastDirName + "/template.yml", m.getFunctionName()), m, eventDetected, filePath, executionStdoutPath);
                }
                LOGGER.info(m.getId() + "|FINISHED install nodejs");
            } else {
                LOGGER.info(m.getId() + "|CANNOT install nodejs");
            }
            cleanTmpDirectory(tmpPath);
        }


        LOGGER.info(m.getId() + "|START COPY");
        //copy to tmp build and run
        LOGGER.info(lastDirName);
        String tmpPath = copyToTmp(m);
        if (buildLambda(tmpPath + "/" + lastDirName, m.getFunctionName(), executionStdoutPath)) {
            if (yaml) {
                filePath = createCustomScriptFile(eventPath, tmpPath + "/" + lastDirName + "/.aws-sam/build/template.yaml", m.getFunctionName());
                result = runProcess(createCommand(eventPath, tmpPath + "/" + lastDirName + "/.aws-sam/build/template.yaml", m.getFunctionName()), m, eventDetected, filePath, executionStdoutPath);
            } else {
                filePath = createCustomScriptFile(eventPath, tmpPath + "/" + lastDirName + "/.aws-sam/build/template.yml", m.getFunctionName());
                result = runProcess(createCommand(eventPath, tmpPath + "/" + lastDirName + "/.aws-sam/build/template.yml", m.getFunctionName()), m, eventDetected, filePath, executionStdoutPath);
            }
        } else {
            LOGGER.info(m.getId() + "|SAM BUILD FAILED!");
        }
        cleanTmpDirectory(tmpPath);
        LOGGER.info(m.getId() + "|FINISH COPY|" + result);
        return result;
    }

    private String createScriptFile(String event, String path, String functionName) throws IOException, InterruptedException {
        String filePath = System.getProperty("java.io.tmpdir") + "/script_" + functionName + "_" + (new Date()).getTime() + ".sh";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write("#!/bin/bash\n");
        writer.write("cat " + filePath + "\n");
        String command = createCommand(event, path, functionName);
        writer.write(command);
        writer.close();

        ProcessBuilder pbAddX = new ProcessBuilder("chmod", "+x", filePath);
        Process addX = pbAddX.start();
        addX.waitFor(10, TimeUnit.SECONDS);
        return filePath;
    }

    private String createCommand(String event, String path, String functionName) {
        return "sam local generate-event " + event + " | sam local invoke -t " + path + " -e - " + functionName + " --skip-pull-image -n /home/ubuntu/env.json";
    }

    private String createCustomScriptFile(String eventPath, String path, String functionName) throws IOException, InterruptedException {
        String filePath = System.getProperty("java.io.tmpdir") + "/script_" + functionName + "_" + (new Date()).getTime() + ".sh";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write("#!/bin/bash\n");
        String command = createCustomCommand(eventPath, path, functionName);
        writer.write(command);
        writer.close();

        ProcessBuilder pbAddX = new ProcessBuilder("chmod", "+x", filePath);
        Process addX = pbAddX.start();
        addX.waitFor(10, TimeUnit.SECONDS);
        return filePath;
    }

    private String createCustomCommand(String eventPath, String path, String functionName) {
        return "sam local invoke -t " + path + " -e " + eventPath + " " + functionName + " --skip-pull-image -n /home/ubuntu/env.json";
    }

    private boolean runProcess(String executionScript, SarSubFunction m, SarSubFunctionEventDetected eventDetected, String filePath, String executionStdoutPath) throws IOException, InterruptedException {
        Process cpuDetection = startCpuDetection();
        Process networkDetection = startNetDetection();
        executionStdoutPath = executionStdoutPath + "_" + new Date().getTime() + ".log";
        ProcessBuilder pbRun = new ProcessBuilder(filePath);
        File executionStdout = new File(executionStdoutPath);
        pbRun.redirectOutput(executionStdout);
        pbRun.redirectError(executionStdout);
        LOGGER.info(eventDetected.getId() + "_" + pbRun.command());
        Process start = pbRun.start();
        start.waitFor(600, TimeUnit.SECONDS);

        LOGGER.info("EXIT_VALUE|" + start.exitValue());
        removeScriptFile(filePath);

        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setTestExecution(testExecution);
        executionResult.setSubFunction(eventDetected.getSubFunction());
        executionResult.setEventType(eventDetected.getEventType());
        List<String> stdoutLines = Files.readAllLines(Paths.get(executionStdoutPath), StandardCharsets.UTF_8);
        String stdout = stdoutLines.stream().reduce("", (a, b) -> a + "\n" + b).replace((char) 0x00, '\n');
        executionResult.setExecutionStdout(stdout);
        Optional<String> isDetailedError = stdoutLines.stream().filter(s -> s.contains("Invoke Error")).findFirst();
        executionResult.setExecutionResult(true);
        if (isDetailedError.isPresent()) {
            String s = isDetailedError.get().replace((char) 0x00, '\n');
            String errorStr = s.substring(s.indexOf("Invoke Error") + 14);
            executionResult.setErrorDescription(errorStr);
            executionResult.setExecutionResult(false);
        }
        Optional<String> isNotDetailedError = stdoutLines.stream()
                .filter(s -> s.contains("Runtime exited with error")
                        || s.contains("stackTrace")
                        || s.contains("exception"))
                .findFirst();
        executionResult.setExecutionSslException(false);
        executionResult.setTimeout(false);
        executionResult.setInvalidLayerArn(false);
        executionResult.setHandlerNotFound(false);
        executionResult.setCannotFindModule(false);
        executionResult.setUnsupportedRuntime(false);
        executionResult.setTemplateNotFound(false);
        executionResult.setInitDuration(null);
        executionResult.setDuration(null);
        executionResult.setBilledDuration(null);
        executionResult.setMemorySize(null);
        executionResult.setMaxMemoryUsed(null);
        if (isNotDetailedError.isPresent()) {
            executionResult.setErrorDescription(stdout);
            executionResult.setExecutionResult(false);
        }
        Optional<String> functionFinishedNormallyButWithError = stdoutLines.stream().filter(s -> s.contains("errorType")).findFirst();
        if (functionFinishedNormallyButWithError.isPresent()) {
            executionResult.setErrorDescription(functionFinishedNormallyButWithError.get());
            executionResult.setExecutionResult(false);
        }
        Optional<String> functionTimeout = stdoutLines.stream().filter(s -> s.contains("Task timed out after")).findFirst();
        if (functionTimeout.isPresent()) {
            executionResult.setErrorDescription(functionTimeout.get());
            executionResult.setExecutionResult(false);
        }
        Optional<String> sslException = stdoutLines.stream().filter(s -> s.contains("CERTIFICATE_VERIFY_FAILED")
                || s.contains("certificate verify failed")
                || s.contains("self signed certificate")
                || s.contains("certificate is not yet valid")
                || s.contains("TLSSocket.onConnectSecure")
                || s.contains("unable to get local issuer certificate"))
                .findFirst();
        if (sslException.isPresent()) {
            executionResult.setErrorDescription(sslException.get());
            executionResult.setExecutionResult(false);
            executionResult.setExecutionSslException(true);
        }
        Optional<String> invalidLayerArn = stdoutLines.stream()
                .filter(s -> s.contains("Invalid Layer Arn")
                        || s.contains("layers/arn")
                        || s.contains("Missing or empty lambdaARN"))
                .findFirst();
        if (invalidLayerArn.isPresent()) {
            executionResult.setErrorDescription(invalidLayerArn.get());
            executionResult.setExecutionResult(false);
            executionResult.setInvalidLayerArn(true);
        }
        Optional<String> timeout = stdoutLines.stream().filter(s -> s.contains("timed out after")).findFirst();
        if (timeout.isPresent()) {
            executionResult.setErrorDescription(timeout.get());
            executionResult.setExecutionResult(false);
            executionResult.setTimeout(true);
        }
        if (!executionResult.getExecutionStdout().contains("Init Duration") && !executionResult.getExecutionStdout().contains("Billed Duration")) {
            executionResult.setErrorDescription(executionResult.getExecutionStdout());
            executionResult.setExecutionResult(false);
        } else {
            try {
                //Init Duration: 0.62 ms  Duration: 847.62 ms     Billed Duration: 900 ms Memory Size: 128 MB     Max Memory Used: 128 MB
                String stdoutStr = executionResult.getExecutionStdout();

                String initDurationStr = stdoutStr.substring(stdoutStr.indexOf("Init Duration"));
                String initDuration = initDurationStr.substring(14, initDurationStr.indexOf("ms"));

                String durationStr = initDurationStr.substring(17).substring(initDurationStr.indexOf("Duration:"));
                String duration = durationStr.substring(11, durationStr.indexOf("ms"));

                String billedDurationStr = durationStr.substring(durationStr.indexOf("Billed Duration"));
                String billedDuration = billedDurationStr.substring(16, billedDurationStr.indexOf("ms"));

                String memorySizeStr = billedDurationStr.substring(billedDurationStr.indexOf("Memory Size:"));
                String memorySize = memorySizeStr.substring(12, memorySizeStr.indexOf("MB"));

                String maxMemoryUsedStr = memorySizeStr.substring(memorySizeStr.indexOf("Max Memory Used:"));
                String maxMemoryUsed = maxMemoryUsedStr.substring(16, maxMemoryUsedStr.indexOf("MB"));

                executionResult.setInitDuration(Double.valueOf(initDuration));
                executionResult.setDuration(Double.valueOf(duration));
                executionResult.setBilledDuration(Double.valueOf(billedDuration));
                executionResult.setMemorySize(Double.valueOf(memorySize));
                executionResult.setMaxMemoryUsed(Double.valueOf(maxMemoryUsed));
            } catch (Exception ex) {
                LOGGER.error("Error during getting sam results to double", ex);
            }
        }
        Optional<String> cannotFindModule = stdoutLines.stream()
                .filter(s -> s.contains("Cannot find modul")
                        || s.contains("Unable to import module")
                        || s.contains("No module named"))
                .findFirst();
        if (cannotFindModule.isPresent()) {
            executionResult.setErrorDescription(executionResult.getExecutionStdout());
            executionResult.setCannotFindModule(true);
            executionResult.setExecutionResult(false);
        }
        Optional<String> handlerNotFound = stdoutLines.stream().filter(s -> s.contains("HandlerNotFound")).findFirst();
        if (handlerNotFound.isPresent()) {
            executionResult.setErrorDescription(executionResult.getExecutionStdout());
            executionResult.setHandlerNotFound(true);
            executionResult.setExecutionResult(false);
        }
        Optional<String> templateNotFound = stdoutLines.stream().filter(s -> s.contains("Template file not found")).findFirst();
        if (templateNotFound.isPresent()) {
            executionResult.setErrorDescription(executionResult.getExecutionStdout());
            executionResult.setTemplateNotFound(true);
            executionResult.setExecutionResult(false);
        }
        Optional<String> inlineCodeNotSupported = stdoutLines.stream().filter(s -> s.contains("expected str, bytes or os.PathLike object, not NoneType")).findFirst();
        if (inlineCodeNotSupported.isPresent()) {
            executionResult.setErrorDescription(executionResult.getExecutionStdout());
            executionResult.setNotSupportedInlineCode(true);
            executionResult.setExecutionResult(false);
        }
        Optional<String> s3CodeUriNotSupported = stdoutLines.stream().filter(s -> s.contains("has specified S3 location for CodeUri")).findFirst();
        if (s3CodeUriNotSupported.isPresent()) {
            executionResult.setErrorDescription(executionResult.getExecutionStdout());
            executionResult.setS3CodeUriNotSupported(true);
            executionResult.setExecutionResult(false);
        }
        Optional<String> notSupportedWithoutCodeUri = stdoutLines.stream().filter(s -> s.contains("no such file or directory: PathError")).findFirst();
        if (notSupportedWithoutCodeUri.isPresent()) {
            executionResult.setErrorDescription(executionResult.getExecutionStdout());
            executionResult.setNotSupportedWithoutCodeUri(true);
            executionResult.setExecutionResult(false);
        }
        Optional<String> missingRequiredKey = stdoutLines.stream().filter(s -> s.contains("statusCode\":\"4") || s.contains("statusCode\":\"5")).findFirst();
        if (missingRequiredKey.isPresent()) {
            executionResult.setErrorDescription(executionResult.getExecutionStdout());
            executionResult.setExecutionNot200Response(true);
            executionResult.setExecutionResult(false);
        }

        executionResult.setExecutionScript(executionScript);
        executionResultRepository.save(executionResult);
        try {
            cpuDetection.destroyForcibly();
            networkDetection.destroyForcibly();
        } catch (Exception ex) {
            LOGGER.error("cannot destroy measuring process", ex);
        }
        try {
            computeAvgCpu();
            String readedCpu = readFileContent("/home/ubuntu/stats/proc_aggregated.log");
            LOGGER.info("READ_CPU_RESULT|" + readedCpu);
            Double procUsage = Double.valueOf(readedCpu);
            executionResult.setCpuResult(procUsage);
            executionResultRepository.save(executionResult);
        } catch (Exception ex) {
            LOGGER.error("cannot calc proc usage", ex);
        }
        try {
            computeNetworkUsage();
            String upNetwork = readFileContent("/home/ubuntu/stats/net_results_up.log");
            LOGGER.info("up_network|" + upNetwork);
            Double upload = Double.valueOf(upNetwork.substring(upNetwork.indexOf(" ")));
            executionResult.setUploadNetwork(upload);

            String downNetwork = readFileContent("/home/ubuntu/stats/net_results_down.log");
            LOGGER.info("net_results_down|" + downNetwork);
            Double download = Double.valueOf(downNetwork.substring(downNetwork.indexOf(" ")));
            executionResult.setDownloadNetwork(download);

            executionResultRepository.save(executionResult);
        } catch (Exception ex) {
            LOGGER.error("cannot calc proc usage", ex);
        }
        new File("/home/ubuntu/stats/proc_aggregated.log").delete();
        new File("/home/ubuntu/stats/proc_results.log").delete();
        new File("/home/ubuntu/stats/network_results.log").delete();
        try {
            killLeftProcesses();
        } catch (Exception ex) {
            LOGGER.error("cannot kill left processes", ex);
        }
        return executionResult.getExecutionResult();
    }

    private String readFileContent(String file) throws FileNotFoundException {
        File cpuResult = new File(file);
        Scanner myReader = new Scanner(cpuResult);
        return myReader.nextLine();
    }

    private void removeScriptFile(String filePath) throws IOException, InterruptedException {
        ProcessBuilder removePb = new ProcessBuilder("rm", filePath);
        Process removeProcess = removePb.start();
        removeProcess.waitFor(10, TimeUnit.SECONDS);
    }

    private String copyToTmp(SarSubFunction m) throws IOException, InterruptedException {
        String eventDirectory = m.getPath().substring(0, m.getPath().lastIndexOf("/"));
        LOGGER.info("COPY FROM " + eventDirectory);
        String tmpDirFile = System.getProperty("java.io.tmpdir") + "/copy_dir_" + m.getFunctionName() + "_" + (new Date()).getTime() + "";
        LOGGER.info("COPY TO " + tmpDirFile);
        boolean mkdir = new File(tmpDirFile).mkdir();

        ProcessBuilder pbAddX = new ProcessBuilder("cp", "-r", eventDirectory, tmpDirFile);
        LOGGER.info(pbAddX.command());
        Process addX = pbAddX.start();
        addX.waitFor(10, TimeUnit.SECONDS);
        return tmpDirFile;
    }

    private boolean buildLambda(String path, String functionName, String executionStdoutPath) throws IOException, InterruptedException {
        File executionStdout = new File(executionStdoutPath);

        ProcessBuilder processBuilder = new ProcessBuilder("sam", "build", functionName);
        processBuilder.directory(new File(path));
        processBuilder.redirectOutput(executionStdout);
        processBuilder.redirectError(executionStdout);
        Process buildProcess = processBuilder.start();
        buildProcess.waitFor(60, TimeUnit.SECONDS);
        return buildProcess.exitValue() == 0;
    }

    private Process startCpuDetection() throws IOException {
        File executionStdout = new File("/home/ubuntu/stats/proc_results.log");

        ProcessBuilder processBuilder = new ProcessBuilder("top", "-b", "-d", "0");
        processBuilder.redirectOutput(executionStdout);
        processBuilder.redirectError(executionStdout);
        return processBuilder.start();
    }

    private Process startNetDetection() throws IOException {
        File executionStdout = new File("/home/ubuntu/stats/network_results.log");

        ProcessBuilder processBuilder = new ProcessBuilder("nethogs", "-t", "-d", "0");
        processBuilder.redirectOutput(executionStdout);
        processBuilder.redirectError(executionStdout);
        return processBuilder.start();
    }

    private void computeAvgCpu() throws IOException, InterruptedException {
        File executionStdout = new File("/home/ubuntu/stats/result.log");

        ProcessBuilder processBuilder = new ProcessBuilder("/home/ubuntu/calCpuAvg.sh");
        processBuilder.redirectOutput(executionStdout);
        processBuilder.redirectError(executionStdout);
        Process start = processBuilder.start();
        start.waitFor(10, TimeUnit.SECONDS);
    }

    private void computeNetworkUsage() throws InterruptedException, IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("/home/ubuntu/calNetworkAvg.sh");
        Process start = processBuilder.start();
        start.waitFor(10, TimeUnit.SECONDS);
    }

    private void killLeftProcesses() throws InterruptedException, IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("/home/ubuntu/killLeftProcesses.sh");
        Process start = processBuilder.start();
        start.waitFor(10, TimeUnit.SECONDS);
    }

    private String cleanTmpDirectory(String eventDirectory) throws InterruptedException, IOException {
        ProcessBuilder pbAddX = new ProcessBuilder("rm", "-rf", eventDirectory);
        Process addX = pbAddX.start();
        addX.waitFor(60, TimeUnit.SECONDS);

        return eventDirectory;
    }
}
