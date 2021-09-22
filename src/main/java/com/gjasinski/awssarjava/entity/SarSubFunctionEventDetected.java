package com.gjasinski.awssarjava.entity;

import com.gjasinski.awssarjava.utils.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Table(name = "sar_sub_functions_event")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SarSubFunctionEventDetected {
    @Id
    @Column(name = "sub_function_event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_function_id", referencedColumnName = "sub_function_id")
    private SarSubFunction subFunction;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;

}
