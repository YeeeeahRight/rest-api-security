package com.epam.esm.service.logic.user;

import com.epam.esm.persistence.model.entity.Role;
import com.epam.esm.persistence.model.entity.User;
import com.epam.esm.persistence.repository.RoleRepository;
import com.epam.esm.persistence.repository.UserRepository;
import com.epam.esm.service.exception.DuplicateEntityException;
import com.epam.esm.service.exception.ExceptionMessageKey;
import com.epam.esm.service.exception.InvalidParametersException;
import com.epam.esm.service.exception.NoSuchEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final String USER_ROLE_NAME = "USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(@Qualifier("bcryptPasswordEncoder") PasswordEncoder passwordEncoder,
                           UserRepository userRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public User create(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEntityException(ExceptionMessageKey.USER_EMAIL_EXIST);
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateEntityException(ExceptionMessageKey.USERNAME_EXIST);
        }
        Optional<Role> defaultRoleOptional = roleRepository.findByName(USER_ROLE_NAME);
        if (!defaultRoleOptional.isPresent()) {
            throw new NoSuchEntityException(ExceptionMessageKey.ROLE_NOT_FOUND);
        }
        Role defaultRole = defaultRoleOptional.get();
        user.setRoles(Collections.singleton(defaultRole));
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll(int page, int size) {
        Pageable pageRequest;
        try {
            pageRequest = PageRequest.of(page, size);
        } catch (IllegalArgumentException e) {
            throw new InvalidParametersException(ExceptionMessageKey.INVALID_PAGINATION);
        }

        return userRepository.findAll(pageRequest).getContent();
    }

    @Override
    public User getById(long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            throw new NoSuchEntityException(ExceptionMessageKey.USER_NOT_FOUND);
        }
        return userOptional.get();
    }

    @Override
    public User getByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new NoSuchEntityException(ExceptionMessageKey.USER_NOT_FOUND);
        }
        return userOptional.get();
    }

    @Override
    @Transactional
    public User changePassword(String username, String currentPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new NoSuchEntityException(ExceptionMessageKey.USER_NOT_FOUND);
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidParametersException(ExceptionMessageKey.PASSWORDS_NOT_MATCH);
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return user;
    }
}
