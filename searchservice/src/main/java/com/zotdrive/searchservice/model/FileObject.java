package com.zotdrive.searchservice.model;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;
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
    @Column(name = "id", columnDefinition = "VARCHAR(255)")
    private UUID object_id;

    private String name;

    private String tags;

    private String createdBy;

    @ManyToOne
    private User parentId;

    private String path; // s3 path

    private boolean deleted; // true if deleted

    private boolean folder; // true if folder

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdOn;

    @OneToMany(mappedBy = "file")
    Set<Access> access;


}
