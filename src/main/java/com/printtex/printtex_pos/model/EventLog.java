package com.printtex.printtex_pos.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class EventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @CsvBindByName
    private Long id;
    @CsvBindByName
    private String tableName;
    @Column(length = 4000)
    @CsvBindByName
    private String previousValue;
    @Column(length = 4000)
    @CsvBindByName
    private String presentValue;
    @CsvBindByName
    private String rowId;
    @CsvBindByName
    private Integer userId;
    @CsvBindByName
    private String operation;
    @CsvBindByName
    private String ipAddress;
    @CsvBindByName
    private Date eventDate;

    @PrePersist
    public void prePersist() {
        this.eventDate = new Date();
    }

}
