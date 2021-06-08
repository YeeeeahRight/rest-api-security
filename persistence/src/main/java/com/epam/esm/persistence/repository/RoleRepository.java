package com.epam.esm.persistence.repository;

import com.epam.esm.persistence.model.entity.GiftCertificate;
import com.epam.esm.persistence.model.entity.Role;

import java.util.Optional;

/**
 * Repository interface for Role
 */
public interface RoleRepository extends EntityRepository<Role> {

    /**
     * Find by name optional.
     *
     * @param name the name to find
     * @return the optional role
     */
    Optional<Role> findByName(String name);
}
