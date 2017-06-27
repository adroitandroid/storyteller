package com.adroitandroid.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by pv on 02/12/16.
 */
@Entity
@Table(name = "version")
public class Version {
    @Id
    private Integer id;
    private String platform;

    @Column(name = "version_latest")
    private Long versionLatest;

    @Column(name = "version_min_supported")
    private Long versionMinSupported;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public Long getVersionLatest() {
        return versionLatest;
    }

    public Long getVersionMinSupported() {
        return versionMinSupported;
    }
}
