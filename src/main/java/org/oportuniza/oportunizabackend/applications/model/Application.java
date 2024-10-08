package org.oportuniza.oportunizabackend.applications.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.users.model.User;

import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "applications")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user","offer"})
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private Offer offer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "phone_number")
    private String phoneNumber;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @Column(name = "resume_url")
    private URL resumeUrl;

    @Column(name = "resume_name_in_bucket")
    private String resumeNameInBucket;

    @Column(name = "resume_file_name")
    private String resumeFileName;

    @OneToMany(mappedBy = "application", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Document> documents = new HashSet<>();

    private String status;

    public void addDocument(Document document) {
        documents.add(document);
    }
}
