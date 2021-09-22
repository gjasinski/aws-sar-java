package com.gjasinski.awssarjava.services;

import com.gjasinski.awssarjava.entity.SarFunctionMain;
import com.gjasinski.awssarjava.repositories.ExecutionResultRepository;
import com.gjasinski.awssarjava.repositories.SarFunctionMainRepository;
import com.gjasinski.awssarjava.repositories.SarSubfunctionRepository;
import com.gjasinski.awssarjava.repositories.TestExecutionRepository;
import com.gjasinski.awssarjava.utils.FunctionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SonarScannerRunner {
    @Autowired
    private SarFunctionMainRepository mainRepository;


    private static Logger LOGGER = Logger.getLogger(FunctionCloneService.class);

//    @PostConstruct
    public void scanRepos() throws IOException, InterruptedException {
        List<SarFunctionMain> all = mainRepository.findAll();
        for (int i = 0; i < all.size(); i++) {
            SarFunctionMain f = all.get(i);
            if (f.getErrorClonePull() || f.getHomePageUrl() == null) {
                LOGGER.info("No code, skip:" + f.getId());
                continue;
            }

            String functionLocalPath = FunctionUtils.getFunctionLocalPath(f);
            File file = new File(functionLocalPath);
            if (!file.exists()) {
                LOGGER.info("No directory, skip:" + f.getId());
                continue;
            }
            File executionStdout = new File("/home/ubuntu/sar-java-logs/sonar" + f.getId() + ".log");
            LOGGER.info("SONAR: " + functionLocalPath);
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "/home/ubuntu/sonar-scanner-4.6.2.2472-linux/bin/sonar-scanner",
                    "-Dsonar.projectKey=" + f.getId() + "_" + f.getName(),
                    "-Dsonar.source=.",
                    "-Dsonar.host.url=http://192.168.18.150:9000",
                    "-Dsonar.login=put_here_sonar_login");
            LOGGER.info(processBuilder.command());
            processBuilder.directory(new File(functionLocalPath));
            processBuilder.redirectError(executionStdout);
            processBuilder.redirectOutput(executionStdout);
            Process start = processBuilder.start();
            start.waitFor(300, TimeUnit.SECONDS);
        }


        //java
        //todo dynamic check if Java runtime then run maven
        List<Integer> toRunWithMaven = new LinkedList<>();
        toRunWithMaven.add(9);
        toRunWithMaven.add(14);
        toRunWithMaven.add(33);
        toRunWithMaven.add(67);
        toRunWithMaven.add(73);
        toRunWithMaven.add(74);
        toRunWithMaven.add(88);
        toRunWithMaven.add(106);
        toRunWithMaven.add(118);
        toRunWithMaven.add(123);
        toRunWithMaven.add(128);
        toRunWithMaven.add(132);
        toRunWithMaven.add(133);
        toRunWithMaven.add(155);
        toRunWithMaven.add(170);
        toRunWithMaven.add(191);
        toRunWithMaven.add(195);
        toRunWithMaven.add(216);
        toRunWithMaven.add(217);
        toRunWithMaven.add(232);
        toRunWithMaven.add(238);
        toRunWithMaven.add(240);
        toRunWithMaven.add(241);
        toRunWithMaven.add(242);
        toRunWithMaven.add(244);
        toRunWithMaven.add(245);
        toRunWithMaven.add(251);
        toRunWithMaven.add(260);
        toRunWithMaven.add(277);
        toRunWithMaven.add(293);
        toRunWithMaven.add(313);
        toRunWithMaven.add(326);
        toRunWithMaven.add(328);
        toRunWithMaven.add(333);
        toRunWithMaven.add(335);
        toRunWithMaven.add(350);
        toRunWithMaven.add(351);
        toRunWithMaven.add(364);
        toRunWithMaven.add(425);
        toRunWithMaven.add(432);
        toRunWithMaven.add(433);
        toRunWithMaven.add(453);
        toRunWithMaven.add(522);
        toRunWithMaven.add(567);
        toRunWithMaven.add(599);
        toRunWithMaven.add(606);
        toRunWithMaven.add(693);
        toRunWithMaven.add(725);
        toRunWithMaven.add(758);
        toRunWithMaven.add(769);
        toRunWithMaven.add(855);
        toRunWithMaven.add(863);
        toRunWithMaven.add(875);
        toRunWithMaven.add(909);
        toRunWithMaven.add(912);
        toRunWithMaven.add(986);
        toRunWithMaven.add(1038);
        toRunWithMaven.add(1142);
        toRunWithMaven.add(1164);
        toRunWithMaven.add(1184);
        toRunWithMaven.add(1226);
        toRunWithMaven.add(1230);
        toRunWithMaven.add(1265);
        toRunWithMaven.add(1297);
        toRunWithMaven.add(1343);

        for (int i = 0; i < toRunWithMaven.size(); i++) {
            SarFunctionMain f = mainRepository.findById(toRunWithMaven.get(i)).get();
            String functionLocalPath = FunctionUtils.getFunctionLocalPath(f);
            File file = new File(functionLocalPath);
            if (!file.exists()) {
                LOGGER.info("No directory, skip:" + f.getId());
                continue;
            }
            File executionStdout = new File("/home/ubuntu/sar-java-logs/sonar" + f.getId() + ".log");
            LOGGER.info("SONAR: " + functionLocalPath);
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "mvn",
                    "clean",
                    "install",
                    "-DskipTests",
                    "-Dsonar.login=put_here_sonar_login");
            LOGGER.info(processBuilder.command());
            processBuilder.directory(new File(functionLocalPath));
            processBuilder.redirectError(executionStdout);
            processBuilder.redirectOutput(executionStdout);
            Process start = processBuilder.start();
            start.waitFor(600, TimeUnit.SECONDS);

            executionStdout = new File("/home/ubuntu/sar-java-logs/sonar" + f.getId() + "_2.log");
            LOGGER.info("SONAR: " + functionLocalPath);
            processBuilder = new ProcessBuilder(
                    "mvn",
                    "sonar:sonar",
                    "-Dsonar.projectKey=" + f.getId() + "_" + f.getName(),
                    "-Dsonar.host.url=http://192.168.18.150:9000",
                    "-Dsonar.login=put_here_sonar_login");
            LOGGER.info(processBuilder.command());
            processBuilder.directory(new File(functionLocalPath));
            processBuilder.redirectError(executionStdout);
            processBuilder.redirectOutput(executionStdout);
            start = processBuilder.start();
            start.waitFor(600, TimeUnit.SECONDS);
        }
    }

}
