package com.gjasinski.awssarjava.services;

import com.gjasinski.awssarjava.dtos.MeasureDto;
import com.gjasinski.awssarjava.dtos.SonarComponentDto;
import com.gjasinski.awssarjava.dtos.SonarMetricsDto;
import com.gjasinski.awssarjava.entity.SarFunctionMain;
import com.gjasinski.awssarjava.entity.SonarResult;
import com.gjasinski.awssarjava.repositories.SarFunctionMainRepository;
import com.gjasinski.awssarjava.repositories.SonarResultRepository;
import com.gjasinski.awssarjava.utils.FunctionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SonarScraper {
    @Autowired
    private SarFunctionMainRepository mainRepository;
    @Autowired
    private SonarResultRepository sonarResultRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private static Logger LOGGER = Logger.getLogger(FunctionCloneService.class);

//    @PostConstruct
    public void scanRepos() throws IOException, InterruptedException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("Cookie", "XSRF-TOKEN=PUT_HERE_TOKEN_XSRF; JWT-SESSION=PUT_HERE_SONAR_JWT_SESSION");
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        List<SarFunctionMain> all = mainRepository.findAll();
        for (int i = 0; i < all.size(); i++) {
            SarFunctionMain f = all.get(i);
            if (f.getErrorClonePull() || f.getHomePageUrl() == null) {
                LOGGER.info("No code, skip:" + f.getId());
                continue;
            }

            try {
                String sonarKey = f.getId() + "_" + f.getName();
                String url = "http://192.168.18.150:9000/api/measures/component?additionalFields=metrics&component=" + sonarKey + "&metricKeys=bugs,vulnerabilities,code_smells,security_hotspots";
                ResponseEntity<SonarMetricsDto> response = restTemplate.exchange(url,
                        HttpMethod.GET,
                        httpEntity,
                        SonarMetricsDto.class);
                SonarMetricsDto body = response.getBody();
                List<MeasureDto> measures = body.getComponent().getMeasures();
                measures.forEach(m ->{
                    SonarResult sonarResult = new SonarResult();
                    sonarResult.setMetric(m.getMetric());
                    sonarResult.setValue(m.getValue());
                    sonarResult.setFunctionMain(f);
                    sonarResultRepository.save(sonarResult);
                });
            }
            catch (Exception ex){
                LOGGER.error("ERROR", ex);
            }
        }


    }

}
