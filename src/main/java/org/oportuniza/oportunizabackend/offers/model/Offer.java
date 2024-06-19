package org.oportuniza.oportunizabackend.offers.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.oportuniza.oportunizabackend.applications.model.Application;
import org.oportuniza.oportunizabackend.users.model.User;

import java.net.URL;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "offers")
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"applications", "user"})
public abstract class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    private String district;

    private String county;

    @Column(name = "image_url")
    private URL imageUrl;

    @Column(name = "image_file_name")
    private String imageFileName;

    @Column(nullable = false)
    private boolean negotiable;

    @OneToMany(mappedBy = "offer")
    private List<Application> applications;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    public void addApplication(Application application) {
        applications.add(application);
    }

    public void removeApplication(Application application) {
        applications.remove(application);
    }
}
