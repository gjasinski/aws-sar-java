package com.gjasinski.awssarjava.dtos;

import com.gjasinski.awssarjava.entity.SarFunctionMain;
import com.gjasinski.awssarjava.entity.SarFunctionMainLabel;
import com.gjasinski.awssarjava.entity.SarFunctionMainRequiredCapabilities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SarFunctionMainDto {
    private String id;
    private String name;
    private String description;
    private String publisherAlias;
    private String homePageUrl;
    private Integer deploymentCount;
    private Boolean isVerifiedAuthor;
    private String verifiedAuthorUrl;
    private List<String> labels;
    private List<String> requiredCapabilitiesForLatestVersion;

    public SarFunctionMain mainFunctionFromDto(){
        SarFunctionMain s = new SarFunctionMain();
        s.setExternalId(id);
        s.setName(name);
        s.setDescription(description);
        s.setPublisherAlias(publisherAlias);
        s.setHomePageUrl(homePageUrl);
        s.setDeploymentCount(deploymentCount);
        s.setVerifiedAuthorUrl(verifiedAuthorUrl);
        s.setIsVerifiedAuthor(isVerifiedAuthor);
        return s;
    }

    public List<SarFunctionMainLabel> labelsFromDto(SarFunctionMain sarFunctionMain){
        List<SarFunctionMainLabel> entityLabels = new LinkedList<>();
        for (String label : labels) {
            label = label.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
            SarFunctionMainLabel l = new SarFunctionMainLabel();
            l.setLabel(label);
            l.setFunctionMain(sarFunctionMain);
            entityLabels.add(l);
        }
        return entityLabels;
    }

    public List<SarFunctionMainRequiredCapabilities> capabilitiesFromDto(SarFunctionMain sarFunctionMain){
        List<SarFunctionMainRequiredCapabilities> entityCapabilities = new LinkedList<>();
        for (String capability : requiredCapabilitiesForLatestVersion) {
            SarFunctionMainRequiredCapabilities c = new SarFunctionMainRequiredCapabilities();
            c.setCapability(capability);
            c.setFunctionMain(sarFunctionMain);
            entityCapabilities.add(c);
        }
        return entityCapabilities;
    }
}
