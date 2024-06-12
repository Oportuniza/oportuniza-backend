package org.oportuniza.oportunizabackend.users.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @ManyToOne
    @JoinColumn(name = "reviewed_id", nullable = false)
    private User reviewed;

    @Column(nullable = false)
    private int rating; // 1 to 5
}
