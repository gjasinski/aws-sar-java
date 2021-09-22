package com.gjasinski.awssarjava.services;

import com.gjasinski.awssarjava.entity.SarFunctionMain;
import com.gjasinski.awssarjava.entity.SarSubFunction;
import com.gjasinski.awssarjava.entity.SarSubFunctionEventDetected;
import com.gjasinski.awssarjava.repositories.SarFunctionMainRepository;
import com.gjasinski.awssarjava.repositories.SarSubfunctionEventRepository;
import com.gjasinski.awssarjava.repositories.SarSubfunctionRepository;
import com.gjasinski.awssarjava.utils.DetectResultEnum;
import com.gjasinski.awssarjava.utils.DetectionResult;
import com.gjasinski.awssarjava.utils.EventType;
import com.gjasinski.awssarjava.utils.FunctionUtils;
import com.gjasinski.awssarjava.yaml.Resource;
import com.gjasinski.awssarjava.yaml.SamTemplate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TemplateDetectorService {
    private static Logger LOGGER = Logger.getLogger(TemplateDetectorService.class);

    @Autowired
    SarFunctionMainRepository sarFunctionMainRepository;
    @Autowired
    SarSubfunctionRepository sarSubfunctionRepository;
    @Autowired
    FunctionExecutorService functionExecutorService;
    @Autowired
    SarSubfunctionEventRepository sarSubfunctionEventRepository;

    public void detectAllTemplates() throws IOException, InterruptedException {
        if (true) {

            int i = 0;
            List<SarFunctionMain> functions = null;
            try {
                functions = sarFunctionMainRepository.getAllByDeploymentCountIsGreaterThanAndHomePageUrlContainsOrderByIdAsc(-1, "github");
                LOGGER.info("number of functions to detect templates: " + functions.size());
                for (i = 0; i < functions.size(); i++) {
                    SarFunctionMain f = functions.get(i);
                    LOGGER.info("TEMPLATE|DETECT: " + f.getId() + " " + f.getName());
                    DetectResultEnum result;
                    try {
                        result = detect(f);
                        f.setMissingSarTemplate(false);
                        f.setErrorTemplate(false);
                    } catch (Exception ex){
                        LOGGER.info("ERROR!", ex);
                        result = DetectResultEnum.ERROR;
                    }
                    switch (result) {
                        case NO_TEMPLATE:
                            f.setMissingSarTemplate(true);
                            break;
                        case ERROR:
                            f.setErrorTemplate(true);
                            LOGGER.error("ERROR|SHOULD NOT HAPPEN" + functions.get(i));
                            break;
                    }
                    sarFunctionMainRepository.save(f);

                }
            } catch (Exception ex) {
                LOGGER.info("ERROR!", ex);
                LOGGER.info("ERROR! " + i + "/" + functions.size());
            }
            LOGGER.info("PARSED_ALL_FUNCTIONS");
        }
        functionExecutorService.executeAllFunctions();
    }

    private DetectResultEnum detect(SarFunctionMain mainFunction) throws IOException {
        String path = FunctionUtils.getFunctionLocalPath(mainFunction);
        try {
            List<Path> pathsWithTemplates = Files.walk(Paths.get(path))
                    .filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString().equalsIgnoreCase("template.yaml") || f.getFileName().toString().equalsIgnoreCase("template.yml"))
                    .collect(Collectors.toList());
            LOGGER.info("00|" + pathsWithTemplates.size() + pathsWithTemplates);
            if (pathsWithTemplates.size() == 0) {
                return DetectResultEnum.NO_TEMPLATE;
            }
            for (int i = 0; i < pathsWithTemplates.size(); i++) {
                DetectionResult detectionResult = yamlTemplate(pathsWithTemplates.get(i));
                SarSubFunction sarSubFunction = sarSubfunctionRepository.findByPath(pathsWithTemplates.get(i).toString()).orElse(new SarSubFunction());
                sarSubFunction.setExample(pathsWithTemplates.get(i).toString().contains("example") ||
                        (pathsWithTemplates.get(i).toString().contains("hello") && pathsWithTemplates.get(i).toString().contains("world")));
                sarSubFunction.setFunctionMain(mainFunction);
                sarSubFunction.setPath(pathsWithTemplates.get(i).toString());
                sarSubFunction.setFunctionName(detectionResult.getFunctionName());
                sarSubFunction.setCodeUri(detectionResult.getCodeUri());
                switch (detectionResult.getDetectResultEnum()) {
                    case NO_TEMPLATE:
                        break;
                    case NO_FUNCTION_FOUND:
                        sarSubFunction.setNoFunctionFound(true);
                        break;
                    case NODE_JS_6_10:
                        sarSubFunction.setRuntime("nodejs6.10");
                        break;
                    case NODE_JS_8_10:
                        sarSubFunction.setRuntime("nodejs8.10");
                        break;
                    case NODE_JS_10:
                        sarSubFunction.setRuntime("nodejs10.x");
                        break;
                    case NODE_JS_12:
                        sarSubFunction.setRuntime("nodejs12.x");
                        break;
                    case NODE_JS_14:
                        sarSubFunction.setRuntime("nodejs14.x");
                        break;
                    case PYTHON_2_7:
                        sarSubFunction.setRuntime("python2.7");
                        break;
                    case PYTHON_3_6:
                        sarSubFunction.setRuntime("python3.6");
                        break;
                    case PYTHON_3_7:
                        sarSubFunction.setRuntime("python3.7");
                        break;
                    case PYTHON_3_8:
                        sarSubFunction.setRuntime("python3.8");
                        break;
                    case GO_1:
                        sarSubFunction.setRuntime("go1.x");
                        break;
                    case PROVIDED_AL2:
                        sarSubFunction.setRuntime("provided.al2");
                        break;
                    case JAVA8:
                        sarSubFunction.setRuntime("java8");
                        break;
                    case JAVA11:
                        sarSubFunction.setRuntime("java11");
                        break;
                    case RUBY2_5:
                        sarSubFunction.setRuntime("ruby2.7");
                        break;
                    case RUBY2_7:
                        sarSubFunction.setRuntime("ruby2.7");
                        break;
                    case PROVIDED:
                        sarSubFunction.setRuntime("provided");
                        break;
                    case DOT_NET_CORE3_1:
                        sarSubFunction.setRuntime("dotnetcore3.1");
                        break;
                    default:
                        LOGGER.error("NO RUNTIME DETECTED|" + mainFunction);
                }
                sarSubfunctionRepository.save(sarSubFunction);
                detectEvents(sarSubFunction);
            }
            return DetectResultEnum.OK;
        } catch (NoSuchFileException nsf) {
            LOGGER.error("Repository not cloned: " + path);
            return DetectResultEnum.ERROR;
        } catch (IllegalArgumentException ex) {
            LOGGER.error("RUNTIME_NOT_FOUND" + path, ex);
            return DetectResultEnum.ERROR;
        }
    }

    private void detectEvents(SarSubFunction m) throws IOException {
        List<EventType> detectedEvents = new LinkedList<>();
        String text = new String(Files.readAllBytes(Paths.get(m.getPath())), StandardCharsets.UTF_8).toLowerCase();
        if (text.contains("rekognition")) {
            detectedEvents.add(EventType.REKOGNITION);
        }
        if (text.contains("api")) {
            detectedEvents.add(EventType.API_GATEWAY);
        }
        if (text.contains("skill")) {
            detectedEvents.add(EventType.ALEXA_SKILLS_KIT);
        }
        if (text.contains("smart")) {
            detectedEvents.add(EventType.ALEXA_SMART_HOME);
        }
        if (text.contains("batch")) {
            detectedEvents.add(EventType.BATCH);
        }
        if (text.contains("codecommit")) {
            detectedEvents.add(EventType.CODECOMMIT);
        }
        if (text.contains("codepipeline")) {
            detectedEvents.add(EventType.CODEPIPELINE);
        }
        if (text.contains("cloudformation")) {
            detectedEvents.add(EventType.CLOUDFORMATION);
        }
        if (text.contains("cloudwatch")) {
            detectedEvents.add(EventType.CLOUDWATCH);
        }
        if (text.contains("cognito")) {
            detectedEvents.add(EventType.COGNITO);
        }
        if (text.contains("config")) {
            detectedEvents.add(EventType.CONFIG);
        }
        if (text.contains("cloudfront")) {
            detectedEvents.add(EventType.CLOUDFRONT);
        }
        if (text.contains("kinesis")) {
            detectedEvents.add(EventType.KINESIS);
        }
        if (text.contains("lex")) {
            detectedEvents.add(EventType.LEX);
        }
        if (text.contains("ses")) {
            detectedEvents.add(EventType.SES);
        }
        if (text.contains("sqs")) {
            detectedEvents.add(EventType.SQS);
        }
        if (text.contains("sns")) {
            detectedEvents.add(EventType.SNS);
        }
        if (text.contains("step")) {
            detectedEvents.add(EventType.STEPFUNCTION);
        }
        if (text.contains("s3")) {
            detectedEvents.add(EventType.S3);
        }
        if (text.contains("ec2")) {
            detectedEvents.add(EventType.EC2);
        }
        if (text.contains("dynamodb")) {
            detectedEvents.add(EventType.DYNAMODB);
        }
        if (detectedEvents.size() == 0) {
            detectedEvents.add(EventType.UNKNOWN);
        }
        detectedEvents.forEach(e -> {
            SarSubFunctionEventDetected newEventDetected = new SarSubFunctionEventDetected();
            newEventDetected.setSubFunction(m);
            newEventDetected.setEventType(e);
            sarSubfunctionEventRepository.save(newEventDetected);
        });
    }

    private DetectionResult yamlTemplate(Path path) throws IOException {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);

        Yaml yaml = new Yaml(new MoreCustomContructor(SamTemplate.class), representer);
        InputStream stream = Files.newInputStream(path);

        LOGGER.info("01|" + path);
        SamTemplate mailTemplate = yaml.loadAs(stream, SamTemplate.class);


        boolean anyFunctionFound = false;
        String runtimeStr = null;
        LOGGER.info("03|" + path);
        if (mailTemplate.getGlobals() != null &&
                mailTemplate.getGlobals().getFunction() != null
                && mailTemplate.getGlobals().getFunction().getRuntime() != null) {
            LOGGER.info("04a|" + mailTemplate.getGlobals().getFunction().getRuntime());
            runtimeStr = mailTemplate.getGlobals().getFunction().getRuntime();
        } else {
            LOGGER.info("04b|empty");
        }

        if (mailTemplate.getResources() != null) {
            ArrayList<String> resourceList = new ArrayList<>(mailTemplate.getResources().keySet());
            for (int i = 0; i < resourceList.size(); i++) {
                String key = resourceList.get(i);
                Resource resource = mailTemplate.getResources().get(key);
                if (resource.getType().equals("AWS::Serverless::Function")) {
                    String codeUri;
                    if (resource.getProperties().getCodeUri() == null){
                        codeUri = "";
                    }
                    else {
                        try {
                            codeUri = resource.getProperties().getCodeUri().toString();
                            Paths.get(codeUri);
                        } catch (InvalidPathException | NullPointerException ex) {
                            codeUri = "";
                        }
                    }
                    if (codeUri.endsWith(".zip") || codeUri.endsWith(".js") || codeUri.endsWith(".jar") || codeUri.contains("{") || codeUri.startsWith("s3://")){
                        codeUri = "";
                    }
                    if (runtimeStr != null) {
                        return new DetectionResult(DetectResultEnum.fromRuntime(runtimeStr), key, codeUri);
                    }
                    anyFunctionFound = true;
                    if (resource.getProperties().getRuntime() != null) {
                        LOGGER.info("05|" + resource.getProperties().getRuntime());
                        runtimeStr = resource.getProperties().getRuntime().toString();
                        return new DetectionResult(DetectResultEnum.fromRuntime(runtimeStr), key, codeUri);
                    }
                }
            }
        }

        if (!anyFunctionFound) {
            LOGGER.info("no function found|" + path);
            if (mailTemplate.getResources() != null) {
                ArrayList<String> resourceList = new ArrayList<>(mailTemplate.getResources().keySet());
                for (int i = 0; i < resourceList.size(); i++) {
                    String key = resourceList.get(i);
                    Resource resource = mailTemplate.getResources().get(key);
                    LOGGER.info(resource.getType());
                }
            }
            return new DetectionResult(DetectResultEnum.NO_FUNCTION_FOUND, null, null);
        }

        throw new IllegalArgumentException("THis should not happend");
    }

    static class MoreCustomContructor extends Constructor {
        public MoreCustomContructor(Class<?> theRoot) {
            super(theRoot);
            this.yamlConstructors.put(new Tag("!Sub"), new ConstructSub());
            this.yamlConstructors.put(new Tag("!GetAtt"), new ConstructSub());
            this.yamlConstructors.put(new Tag("!Ref"), new ConstructSub());
            this.yamlConstructors.put(new Tag("!Select"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!Split"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!FindInMap"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!Join"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!If"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!Equals"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!Not"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!And"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!Or"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!Condition"), new ConstructSub());
            this.yamlConstructors.put(new Tag("!GetAZs"), new ConstructSub());
            this.yamlConstructors.put(new Tag("!ImportValue"), new ConstructSub());
        }


        private class ConstructSub extends AbstractConstruct {
            public Object construct(Node node) {
                if (node instanceof ScalarNode) {
                    return constructScalar((ScalarNode) node);
                }
                if (node instanceof SequenceNode) {
                    return constructSequence((SequenceNode) node);
                }
                throw new IllegalArgumentException("Unsupported type:" + node);
            }
        }

        private class ConstructSelect extends AbstractConstruct {
            public Object construct(Node node) {
                return constructSequence((SequenceNode) node);
            }
        }
    }

    static class CustomConstructor extends SafeConstructor {
        public CustomConstructor() {
            this.yamlConstructors.put(new Tag("!Sub"), new ConstructSub());
            this.yamlConstructors.put(new Tag("!GetAtt"), new ConstructSub());
            this.yamlConstructors.put(new Tag("!Ref"), new ConstructSub());
            this.yamlConstructors.put(new Tag("!Select"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!Split"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!FindInMap"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!Join"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!If"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!Equals"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!Not"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!And"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!Or"), new ConstructSelect());
            this.yamlConstructors.put(new Tag("!Condition"), new ConstructSub());
            this.yamlConstructors.put(new Tag("!GetAZs"), new ConstructSub());
            this.yamlConstructors.put(new Tag("!ImportValue"), new ConstructSub());
        }

        private class ConstructSub extends AbstractConstruct {
            public Object construct(Node node) {
                if (node instanceof ScalarNode) {
                    return constructScalar((ScalarNode) node);
                }
                if (node instanceof SequenceNode) {
                    return constructSequence((SequenceNode) node);
                }
                throw new IllegalArgumentException("Unsupported type:" + node);
            }
        }

        private class ConstructSelect extends AbstractConstruct {
            public Object construct(Node node) {
                return constructSequence((SequenceNode) node);
            }
        }
    }



}
