package com.epam.esm.persistence.repository;

import com.epam.esm.persistence.model.entity.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * Repository interface for User
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    /**
     * Finds role by username.
     *
     * @param username the username to find
     * @return the optional role
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds role by email.
     *
     * @param email the email to find
     * @return the optional role
     */
    Optional<User> findByEmail(String email);
}
