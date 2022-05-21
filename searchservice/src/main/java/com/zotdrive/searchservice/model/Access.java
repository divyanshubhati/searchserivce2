package com.zotdrive.searchservice.model;

import javax.persistence.*;

@Entity // this was commented
@Table(name = "Access")
public class Access {

    @Id
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "object_id")
    FileObject file;
}
