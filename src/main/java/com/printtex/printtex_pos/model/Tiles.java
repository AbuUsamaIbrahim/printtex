package com.printtex.printtex_pos.model;

import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Tiles {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String tilesName;
    @NaturalId
    private String tileId;

}
