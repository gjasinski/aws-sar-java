package com.gjasinski.awssarjava.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "sar_function_main_labels")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SarFunctionMainLabel implements Serializable {
    @Id
    @Column(name = "label_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "function_id", referencedColumnName = "id")
    private SarFunctionMain functionMain;
    @Column(name = "label")
    private String label;
}
