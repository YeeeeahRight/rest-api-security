package com.epam.esm.persistence.repository.data;

import com.epam.esm.persistence.model.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Role
 */
@Repository
public interface RoleRepository
        extends org.springframework.data.repository.Repository<Role, Long> {

    /**
     * Finds role by name.
     *
     * @param name the name to find
     * @return the optional role
     */
    Optional<Role> findByName(String name);
}
