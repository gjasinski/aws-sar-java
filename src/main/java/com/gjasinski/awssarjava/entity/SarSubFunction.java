package com.gjasinski.awssarjava.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sar_sub_functions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"functionMain"})
public class SarSubFunction {
    @Id
    @Column(name = "sub_function_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mian_function_id", referencedColumnName = "id")
    private SarFunctionMain functionMain;

    @Column(name = "path")
    private String path;

    @Column(name = "runtime")
    private String runtime;

    @Column(name = "example")
    private boolean example = false;

    @Column(name = "noFunctionFound")
    private boolean noFunctionFound = false;

    @Column(name = "function_name")
    private String functionName;

    @Column(name = "code_uri")
    private String codeUri;
}
