package com.zazabeyligisf.zazacord.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zazabeyligisf.zazacord.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByUsername(String username);

    // ignore case
    @Query("SELECT s FROM User s WHERE LOWER(s.username) LIKE ?1%")
    List<User> findByNameStartingWithIgnoreCase(String namePattern);

    default void update(User user, JsonObject payload) {
        findById(user.getId()).or(() -> {
            save(user);
            return Optional.of(user);
        }).orElseThrow();
        save(new Gson().fromJson(payload, User.class));

    }
}
