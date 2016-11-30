package com.adroitandroid.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by pv on 30/11/16.
 */
@Entity
@Table(name = "chapter_detail")
public class ChapterDetail implements Serializable {
    @Id
    @Column(name = "chapter_id")
    private Long id;

    private String content;
}
