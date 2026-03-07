package edu.cit.alicaba.stockwise.repository;

import edu.cit.alicaba.stockwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    // Add this line right here!
    Optional<User> findByEmailOrName(String email, String name);
}