package com.epam.esm.service.logic.tag;

import static org.mockito.Mockito.*;

import com.epam.esm.persistence.model.entity.Tag;
import com.epam.esm.persistence.repository.TagRepository;
import com.epam.esm.persistence.repository.UserRepository;
import com.epam.esm.service.exception.DuplicateEntityException;
import com.epam.esm.service.exception.InvalidParametersException;
import com.epam.esm.service.exception.NoSuchEntityException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TagServiceImpl.class})
public class TagServiceImplTest {
    private static final long ID = 1;
    private static final String NAME = "tag";
    private static final Tag TAG = new Tag(ID,NAME);

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 50;

    @MockBean
    private TagRepository tagRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private TagServiceImpl tagService;

    @Test
    public void testCreateShouldCreateWhenNotExist() {
        when(tagRepository.findByName(NAME)).thenReturn(Optional.empty());
        tagService.create(TAG);
        verify(tagRepository).save(TAG);
    }

    @Test(expected = DuplicateEntityException.class)
    public void testCreateShouldThrowsDuplicateEntityExceptionWhenExist() {
        when(tagRepository.findByName(NAME)).thenReturn(Optional.of(TAG));
        tagService.create(TAG);
    }

    @Test
    public void testGetAllShouldGetAll() {
        when(tagRepository.findAll((Pageable) any())).thenReturn(Page.empty());
        tagService.getAll(DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
        verify(tagRepository).findAll((Pageable) any());
    }

    @Test(expected = InvalidParametersException.class)
    public void testGetAllShouldThrowsInvalidParametersExceptionWhenParamsInvalid() {
        tagService.getAll(-3, -2);
    }

    @Test
    public void testGetByIdShouldGetWhenFound() {
        when(tagRepository.findById(ID)).thenReturn(Optional.of(TAG));
        tagService.getById(ID);
        verify(tagRepository).findById(ID);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testGetByIdShouldThrowsNoSuchEntityExceptionWhenNotFound() {
        when(tagRepository.findById(ID)).thenReturn(Optional.empty());
        tagService.getById(ID);
    }

    @Test
    public void testDeleteByIdShouldDeleteWhenFound() {
        when(tagRepository.findById(ID)).thenReturn(Optional.of(TAG));
        tagService.deleteById(ID);
        verify(tagRepository).deleteById(ID);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testDeleteByIdShouldThrowsNoSuchEntityExceptionWhenNotFound() {
        when(tagRepository.findById(ID)).thenReturn(Optional.empty());
        tagService.deleteById(ID);
    }

    public void testGetUserMostWidelyUsedTagWithHighestOrderCostShouldGetWhenFound() {
        when(tagRepository.findById(ID)).thenReturn(Optional.of(TAG));
        tagService.getUserMostWidelyUsedTagWithHighestOrderCost(ID);
        verify(tagRepository).findUserMostWidelyUsedTagWithHighestOrderCost(eq(ID));
    }

    @Test(expected = NoSuchEntityException.class)
    public void testGetUserMostWidelyUsedTagWithHighestOrderCostShouldThrowsWhenNotFound() {
        when(tagRepository.findById(ID)).thenReturn(Optional.of(TAG));
        tagService.getUserMostWidelyUsedTagWithHighestOrderCost(ID);
    }
}
