package com.zotdrive.searchservice.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table( name = "Access", uniqueConstraints= @UniqueConstraint(columnNames={"user_id", "object_id"}))
public class Access {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "object_id")
    FileObject file;
}
