package com.epam.esm.service.logic.user;

import com.epam.esm.persistence.model.entity.Role;
import com.epam.esm.persistence.model.entity.User;
import com.epam.esm.persistence.repository.RoleRepository;
import com.epam.esm.persistence.repository.UserRepository;
import com.epam.esm.service.exception.InvalidParametersException;
import com.epam.esm.service.exception.NoSuchEntityException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UserServiceImpl.class})
public class UserServiceImplTest {
    private static final long ID = 1;
    private static final String NAME = "user";
    private static final User USER = new User(ID, NAME);

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 50;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    @Qualifier("bcryptPasswordEncoder")
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserServiceImpl userService;

    @Test
    public void testCreateShouldCreate() {
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(new Role("USER")));
        userService.create(USER);
        verify(userRepository).save(USER);
    }

    @Test
    public void testGetAllShouldGetAll() {
        when(userRepository.findAll((Pageable) any())).thenReturn(Page.empty());
        userService.getAll(DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
        verify(userRepository).findAll((Pageable) any());
    }

    @Test(expected = InvalidParametersException.class)
    public void testGetAllShouldThrowsInvalidParametersExceptionWhenParamsInvalid() {
        userService.getAll(-3, -2);
    }

    @Test
    public void testGetByIdShouldGetWhenFound() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(USER));
        userService.getById(ID);
        verify(userRepository).findById(ID);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testGetByIdShouldThrowsNoSuchEntityExceptionWhenNotFound() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());
        userService.getById(ID);
    }

}
