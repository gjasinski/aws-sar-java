package com.gjasinski.awssarjava.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "sar_function_main")
@Data
@ToString(exclude = {"labels", "capabilities", "subFunctions"})
public class SarFunctionMain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "external_id", unique = true)
    private String externalId;
    @Column(name = "name")
    private String name;
    @Column(name = "description", length = 1000)
    private String description;
    @Column(name = "publisher_alias")
    private String publisherAlias;
    @Column(name = "home_page_url")
    private String homePageUrl;
    @Column(name = "deployment_count")
    private Integer deploymentCount;
    @Column(name = "is_verified_author")
    private Boolean isVerifiedAuthor;
    @Column(name = "verified_author_url")
    private String verifiedAuthorUrl;
    @Column(name = "licence")
    private String licence;
    @Column(name = "error_clone_pull")
    private Boolean errorClonePull = false;
    @Column(name = "missing_sar_template")
    private Boolean missingSarTemplate = false;
    @Column(name = "error_template")
    private Boolean errorTemplate = false;

    @OneToMany(
            mappedBy = "functionMain",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SarFunctionMainLabel> labels;
    @OneToMany(
            mappedBy = "functionMain",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SarFunctionMainRequiredCapabilities> capabilities;

    @OneToMany(
            mappedBy = "functionMain",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SarSubFunction> subFunctions;
}
