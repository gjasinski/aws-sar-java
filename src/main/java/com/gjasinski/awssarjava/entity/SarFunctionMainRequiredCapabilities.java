package com.gjasinski.awssarjava.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "sar_function_main_required_capabilities")
@Data
public class SarFunctionMainRequiredCapabilities implements Serializable {
    @Id
    @Column(name = "capability_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "function_id", referencedColumnName = "id")
    private SarFunctionMain functionMain;
    @Column(name = "capability")
    private String capability;

}
