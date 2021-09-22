package com.gjasinski.awssarjava.services;

import com.gjasinski.awssarjava.dtos.SarFunctionMainDto;
import com.gjasinski.awssarjava.dtos.SarFunctionMainWrapperDto;
import com.gjasinski.awssarjava.entity.SarFunctionMain;
import com.gjasinski.awssarjava.entity.SarFunctionMainLabel;
import com.gjasinski.awssarjava.entity.SarFunctionMainRequiredCapabilities;
import com.gjasinski.awssarjava.repositories.SarFunctionMailLabelRepository;
import com.gjasinski.awssarjava.repositories.SarFunctionMainRepository;
import com.gjasinski.awssarjava.repositories.SarFunctionMainRequiredCapabilitiesRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class FunctionListService {
    private static Logger LOGGER = Logger.getLogger(FunctionCloneService.class);
    private static RestTemplate restTemplate = new RestTemplate();
    private final SarFunctionMainRepository mainRepository;
    private final SarFunctionMailLabelRepository labelRepository;
    private final SarFunctionMainRequiredCapabilitiesRepository capabilitiesRepository;
    private final FunctionCloneService functionCloneService;

    @Autowired
    public FunctionListService(SarFunctionMainRepository mainRepository,
                               SarFunctionMailLabelRepository labelRepository,
                               SarFunctionMainRequiredCapabilitiesRepository capabilitiesRepository, FunctionCloneService functionCloneService) {
        this.mainRepository = mainRepository;
        this.labelRepository = labelRepository;
        this.capabilitiesRepository = capabilitiesRepository;
        this.functionCloneService = functionCloneService;
    }

//    @PostConstruct
    public void scrapFunctions() throws IOException, InterruptedException {
        int pageNumber = 1;
        LOGGER.info("LISTING");

        boolean run = false;
        while (run) {
            try {
                ResponseEntity<SarFunctionMainWrapperDto> result = restTemplate.getForEntity(
                        createUrl(pageNumber),
                        SarFunctionMainWrapperDto.class);
                List<SarFunctionMainDto> applications = result.getBody().getApplications();
                if (result.getStatusCode().is2xxSuccessful() && applications.size() > 0) {
                    for (SarFunctionMainDto application : applications) {
                        LOGGER.info(application);
                        Optional<SarFunctionMain> byExternalId = mainRepository.findByExternalId(application.getId());
                        if (byExternalId.isPresent()){
                            SarFunctionMain sarFunctionMain = byExternalId.get();
                            sarFunctionMain.setDeploymentCount(application.getDeploymentCount());
                            mainRepository.save(sarFunctionMain);

                            LOGGER.info(application.getName() + "|" + application.getId() + "|already in database");
                            continue;
                        }
                        else {
                            SarFunctionMain sarFunctionMain = application.mainFunctionFromDto();
                            sarFunctionMain = mainRepository.save(sarFunctionMain);

                            List<SarFunctionMainRequiredCapabilities> sarFunctionMainRequiredCapabilities = application.capabilitiesFromDto(sarFunctionMain);
                            List<SarFunctionMainLabel> sarFunctionMainLabels = application.labelsFromDto(sarFunctionMain);
                            capabilitiesRepository.saveAll(sarFunctionMainRequiredCapabilities);
                            labelRepository.saveAll(sarFunctionMainLabels);
                        }
                    }
                } else {
                    run = false;
                }
                pageNumber++;
                LOGGER.info(pageNumber);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        LOGGER.info("LISTING_FINISHED");
        functionCloneService.cloneAllFunctions();
    }

    private String createUrl(int pageNumber) {
        return "https://shr32taah3.execute-api.us-east-1.amazonaws.com/Prod/applications/browse?pageSize=12&pageNumber="
                + pageNumber
                + "&searchText=&category=&runtime=&verified=&includeAppsWithCapabilities=CAPABILITY_IAM%2CCAPABILITY_NAMED_IAM%2CCAPABILITY_RESOURCE_POLICY%2CCAPABILITY_AUTO_EXPAND";
    }
}
