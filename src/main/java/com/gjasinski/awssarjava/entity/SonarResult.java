package com.gjasinski.awssarjava.entity;

import com.gjasinski.awssarjava.utils.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sonar_result")
@Data()
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"functionMain"})
public class SonarResult {
    @Id
    @Column(name = "sonar_result_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_function_id", referencedColumnName = "id")
    private SarFunctionMain functionMain;

    @Column(name = "metric")
    private String metric;
    @Column(name = "value")
    private Integer value;


}
