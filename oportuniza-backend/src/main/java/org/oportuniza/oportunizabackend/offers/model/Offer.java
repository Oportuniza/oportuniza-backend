package org.oportuniza.oportunizabackend.offers.model;

import jakarta.persistence.*;
import lombok.Data;
import org.oportuniza.oportunizabackend.applications.model.Application;
import org.oportuniza.oportunizabackend.users.model.User;

import java.util.List;

@Data
@Entity
@Table(name = "offers")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private boolean negotiable;

    @OneToMany(mappedBy = "offer")
    private List<Application> applications;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void addApplication(Application application) {
        applications.add(application);
    }

    public void removeApplication(Application application) {
        applications.remove(application);
    }
}
