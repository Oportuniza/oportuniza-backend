package org.oportuniza.oportunizabackend.applications.model;

import jakarta.persistence.*;
import lombok.Data;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.users.model.User;

import java.util.List;

@Data
@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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

    private String message;

    @Column(name = "resume_url")
    private String resumeUrl;

    @OneToMany(mappedBy = "application", fetch = FetchType.EAGER)
    private List<Document> documents;

    private String status;

    public void addDocument(Document document) {
        documents.add(document);
    }
}
