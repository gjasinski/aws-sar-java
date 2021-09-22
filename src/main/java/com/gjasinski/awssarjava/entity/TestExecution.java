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
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "test_execution")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestExecution {
    @Id
    @Column(name = "test_execution_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_execution_start_date")
    private Date startDate;

    @Column(name = "test_execution_end_date")
    private Date endDate;

    @Column(name = "test_execution_sucess")
    private Boolean success;
}
