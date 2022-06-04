package com.zotdrive.searchservice.model;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Nullable
    private String type;

    private long size = 0;


    @ManyToOne
    private User createdBy;


    @ManyToOne
    @JoinColumn(name = "parent_id")
    private FileObject parent;
//
//	    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
//	    private List<FileObject> children = new ArrayList<>();

    private boolean deleted; // true if deleted

//	    @Column(name = "deleted_at")
//	    private Time deletedAt;

    @Column(name = "deleted_on")
    private Date deletedOn;

    private boolean folder; // true if folder

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private Date createdOn;

    @Nullable
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "file", orphanRemoval = true)
    private List<Access> userList = new ArrayList<>();

}
