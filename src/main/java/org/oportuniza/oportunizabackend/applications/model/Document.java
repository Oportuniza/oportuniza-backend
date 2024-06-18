package org.oportuniza.oportunizabackend.applications.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private URL url;

    @Column(name = "name_in_bucket")
    private String nameInBucket;

    @Column(name = "file_name")
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;
}
