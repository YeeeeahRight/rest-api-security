package com.epam.esm.service.logic.order;

import com.epam.esm.persistence.model.entity.GiftCertificate;
import com.epam.esm.persistence.model.entity.Order;
import com.epam.esm.persistence.model.entity.User;
import com.epam.esm.persistence.repository.data.GiftCertificateRepository;
import com.epam.esm.persistence.repository.data.OrderRepository;
import com.epam.esm.persistence.repository.data.UserRepository;
import com.epam.esm.service.exception.InvalidParametersException;
import com.epam.esm.service.exception.NoSuchEntityException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OrderServiceImpl.class})
public class OrderServiceImplTest {
    private static final long ID = 1;
    private static final String NAME = "order";
    private static final User USER = new User(ID, NAME);
    private static final GiftCertificate GIFT_CERTIFICATE = new GiftCertificate();
    private static final Order ORDER = new Order(ID, ZonedDateTime.now(),
            BigDecimal.ONE, USER, GIFT_CERTIFICATE);

    static {
        USER.setOrders(Collections.singletonList(ORDER));
    }

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 50;

    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private GiftCertificateRepository certificateRepository;
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private OrderServiceImpl orderService;

    @Test
    public void testCreateShouldCreateWhenNotExist() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(USER));
        when(certificateRepository.findById(ID)).thenReturn(Optional.of(GIFT_CERTIFICATE));
        orderService.create(ID, ID);
        verify(orderRepository).save(any());
    }

    @Test(expected = NoSuchEntityException.class)
    public void testCreateShouldThrowsNoSuchEntityExceptionWhenUserNotFound() {
        when(certificateRepository.findById(ID)).thenReturn(Optional.of(GIFT_CERTIFICATE));
        orderService.create(ID, ID);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testCreateShouldThrowsNoSuchEntityExceptionWhenCertificateNotFound() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(USER));
        orderService.create(ID, ID);
    }

    @Test
    public void testGetAllByUserIdIdShouldGetAll() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(USER));
        orderService.getAllByUserId(ID, DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
        verify(orderRepository).findAllByUserId(eq(ID), any());
    }

    @Test(expected = InvalidParametersException.class)
    public void testGetAllShouldThrowsInvalidParametersExceptionWhenPaginationInvalid() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(USER));
        orderService.getAllByUserId(ID, -3, -4);
    }

    @Test
    public void testGetByUserIdShouldGet() {
        when(orderRepository.findById(ID)).thenReturn(Optional.of(ORDER));
        when(userRepository.findById(ID)).thenReturn(Optional.of(USER));
        orderService.getByUserId(ID, ID);
        verify(orderRepository).findById(eq(ID));
        verify(userRepository).findById(eq(ID));
    }

    @Test(expected = NoSuchEntityException.class)
    public void testGetByUserIdShouldThrowsInvalidParametersExceptionWhenUserNotFound() {
        when(orderRepository.findById(ID)).thenReturn(Optional.of(ORDER));
        when(userRepository.findById(ID)).thenReturn(Optional.empty());
        orderService.getByUserId(ID, ID);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testGetByUserIdShouldThrowsInvalidParametersExceptionWhenOrderNotFound() {
        when(orderRepository.findById(ID)).thenReturn(Optional.empty());
        orderService.getByUserId(ID, ID);
    }
}
