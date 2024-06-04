package org.oportuniza.oportunizabackend.users.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.oportuniza.oportunizabackend.applications.model.Application;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number" ,nullable = false)
    private String phoneNumber;

    private String district;

    private String county;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<Application> applications;

    @OneToMany(mappedBy = "user")
    private List<Offer> offers;

    @ManyToMany
    @JoinTable(
            name = "favorites_offers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "offer_id"))
    private List<Offer> favoritesOffers = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "favorite_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "favorite_user_id"))
    private List<User> favoriteUsers;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "resume_url")
    private String resumeUrl;

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void addFavoriteUser(User user) {
        this.favoriteUsers.add(user);
    }

    public void removeFavoriteUser(User user) {
        this.favoriteUsers.remove(user);
    }

    public void addOffer(Offer offer) {
        this.offers.add(offer);
    }

    public void removeOffer(Offer offer) {
        this.offers.remove(offer);
    }

    public void addFavoriteOffer(Offer offer) {
        this.favoritesOffers.add(offer);
    }

    public void removeFavoriteOffer(Offer offer) {
        this.favoritesOffers.remove(offer);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
