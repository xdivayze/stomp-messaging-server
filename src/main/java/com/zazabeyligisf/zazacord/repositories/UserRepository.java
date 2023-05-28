package com.zazabeyligisf.zazacord.repositories;

import com.google.gson.JsonObject;
import com.zazabeyligisf.zazacord.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByUsername(String username);

    //ignore case
    @Query("SELECT s FROM User s WHERE LOWER(s.username) LIKE ?1%")
    List<User> findByNameStartingWithIgnoreCase(String namePattern);

    default void update(User user, JsonObject payload) {
        User foundUser = findById(user.getId()).or(() -> {
            save(user);
            return Optional.of(user);
        }).orElseThrow();
        foundUser.setFriends(new LinkedList<>());
        foundUser.setPassword(payload.get("password").getAsString());
        foundUser.setUsername(payload.get("username").getAsString());

        save(foundUser);

    }
}
