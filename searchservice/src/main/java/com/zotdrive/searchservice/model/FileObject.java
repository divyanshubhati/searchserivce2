package com.zotdrive.searchservice.model;
import lombok.Data;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;
import java.sql.Time;
import java.util.UUID;

@Data
@Entity
@Table(name = "Object")
public class FileObject {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "object_id", nullable=false)
    private UUID objectid;

    private String name;

    private String tags;

    @ManyToOne
    private User createdBy;


    @ManyToOne
    @JoinColumn(name = "parent_id")
    private FileObject parent;
//
//	    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
//	    private List<FileObject> children = new ArrayList<>();

    private boolean deleted; // true if deleted

    @Column(name = "deleted_at")
    private Time deletedAt;

    private boolean folder; // true if folder

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private Date createdOn;



}
