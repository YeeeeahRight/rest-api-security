package com.epam.esm.service.logic.certificate;

import static org.mockito.Mockito.*;

import com.epam.esm.persistence.model.entity.Tag;
import com.epam.esm.persistence.repository.GiftCertificateRepository;
import com.epam.esm.persistence.repository.OrderRepository;
import com.epam.esm.persistence.repository.TagRepository;
import com.epam.esm.persistence.model.entity.GiftCertificate;
import com.epam.esm.persistence.repository.UserRepository;
import com.epam.esm.service.exception.InvalidParametersException;
import com.epam.esm.service.exception.NoSuchEntityException;
import com.epam.esm.service.validator.SortParamsContextValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GiftCertificateServiceImpl.class)
public class GiftCertificateServiceImplTest {
    private static final long ID = 1;
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final BigDecimal PRICE = BigDecimal.TEN;
    private static final ZonedDateTime UPDATE_TIME = ZonedDateTime.now();
    private static final ZonedDateTime CREATE_TIME = ZonedDateTime.now();
    private static final int DURATION = 5;
    private static final GiftCertificate GIFT_CERTIFICATE = new GiftCertificate(
            ID, NAME, DESCRIPTION, PRICE, CREATE_TIME, UPDATE_TIME, DURATION
    );
    private static final Tag TAG = new Tag(ID, "new");
    private static final GiftCertificate GIFT_CERTIFICATE_WITH_TAGS = new GiftCertificate(
            ID, NAME, DESCRIPTION, PRICE, CREATE_TIME, UPDATE_TIME, DURATION
    );

    static {
        GIFT_CERTIFICATE_WITH_TAGS.setTags(Collections.singleton(TAG));
    }

    private static final String PART_INFO = "z";
    private static final List<String> SORTING_COLUMN = Collections.singletonList("name");

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 50;

    @MockBean
    private GiftCertificateRepository certificateRepository;
    @MockBean
    private TagRepository tagRepository;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private SortParamsContextValidator sortParamsContextValidator;
    @Autowired
    private GiftCertificateServiceImpl giftCertificateService;


    @Test
    public void testCreateShouldCreateWhenNotExist() {
        giftCertificateService.create(GIFT_CERTIFICATE);
        verify(certificateRepository).save(GIFT_CERTIFICATE);
    }

    @Test
    public void testGetAllShouldGetAll() {
        when(certificateRepository.findAll((Pageable) any())).thenReturn(Page.empty());
        giftCertificateService.getAll(DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
        verify(certificateRepository).findAll((Pageable) any());
    }

    @Test(expected = InvalidParametersException.class)
    public void testGetAllShouldThrowsInvalidParametersExceptionWhenParamsInvalid() {
        giftCertificateService.getAll(-3, -2);
    }

    @Test(expected = InvalidParametersException.class)
    public void getAllWithTagsShouldThrowsInvalidParametersExceptionWhenParamsInvalid() {
        giftCertificateService.getAllWithTagsWithFilteringSorting(null, null,
                null, null, -3, -2);
    }

    @Test
    public void testGetByIdShouldGetWhenFound() {
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.of(GIFT_CERTIFICATE));
        giftCertificateService.getById(ID);
        verify(certificateRepository).findById(ID);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testGetByIdShouldThrowsNoSuchEntityExceptionWhenNotFound() {
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.empty());
        giftCertificateService.getById(ID);
    }

    @Test
    public void getAllWithTagsShouldGetWhenFilteringAndSortingNotExist() {
        when(certificateRepository.findAll((Pageable) any())).thenReturn(Page.empty());
        giftCertificateService.getAllWithTagsWithFilteringSorting(null, null,
                null, null, DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
        verify(certificateRepository).findAll((Pageable) any());
    }

    @Test
    public void getAllWithTagsShouldGetWithFilteringWhenFilteringExist() {
        when(certificateRepository.findAllByPartInfo(eq(PART_INFO), any())).thenReturn(Collections.emptyList());
        giftCertificateService.getAllWithTagsWithFilteringSorting(null, PART_INFO, null,
                null, DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
        verify(certificateRepository).findAllByPartInfo(any(), any());
    }

    @Test(expected = InvalidParametersException.class)
    public void getAllWithTagsShouldThrowsInvalidParametersExceptionWhenSortParamsInvalid() {
        when(sortParamsContextValidator.isValid(any())).thenReturn(false);
        giftCertificateService.getAllWithTagsWithFilteringSorting(null, null,
                SORTING_COLUMN, null, DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
    }

    @Test
    public void testUpdateByIdShouldUpdateWhenFound() {
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.of(GIFT_CERTIFICATE));
        giftCertificateService.updateById(ID, GIFT_CERTIFICATE);
        verify(certificateRepository).save(GIFT_CERTIFICATE);
    }

    @Test
    public void testUpdateByIdShouldCreateTagWhenNewTagPassed() {
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.of(GIFT_CERTIFICATE_WITH_TAGS));
        when(tagRepository.findByName(any())).thenReturn(Optional.empty());
        giftCertificateService.updateById(ID, GIFT_CERTIFICATE_WITH_TAGS);
        verify(tagRepository).save(TAG);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testUpdateByIdShouldThrowsNoSuchEntityExceptionWhenNotFound() {
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.empty());
        giftCertificateService.updateById(ID, GIFT_CERTIFICATE);
    }

    @Test
    public void testDeleteByIdShouldDeleteWhenFound() {
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.of(GIFT_CERTIFICATE));
        giftCertificateService.deleteById(ID);
        verify(certificateRepository).deleteById(ID);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testDeleteByIdShouldThrowsNoSuchEntityExceptionWhenNotFound() {
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.empty());
        giftCertificateService.deleteById(ID);
    }
}
