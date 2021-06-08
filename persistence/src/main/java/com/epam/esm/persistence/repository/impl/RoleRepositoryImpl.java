package com.epam.esm.persistence.repository.impl;

import com.epam.esm.persistence.model.entity.Role;
import com.epam.esm.persistence.repository.AbstractRepository;
import com.epam.esm.persistence.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
@Transactional
public class RoleRepositoryImpl extends AbstractRepository<Role> implements RoleRepository {

    @Autowired
    public RoleRepositoryImpl(EntityManager entityManager) {
        super(entityManager, Role.class);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return findByField("name", name);
    }
}
