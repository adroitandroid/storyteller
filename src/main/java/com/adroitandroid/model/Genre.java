package com.adroitandroid.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by pv on 30/11/16.
 */
@Entity
@Table(name = "genre")
public class Genre {
    @Id
    private Integer id;

    @Column(name = "genre_name")
    private String name;
}
