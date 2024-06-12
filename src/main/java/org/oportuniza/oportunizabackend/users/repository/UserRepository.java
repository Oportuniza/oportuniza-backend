package org.oportuniza.oportunizabackend.users.repository;

import org.oportuniza.oportunizabackend.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u.favoriteUsers FROM User u WHERE u.id = :userId")
    Page<User> findFavoriteUsersByUserId(@Param("userId") Long userId, Pageable pageable);
}