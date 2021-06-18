package com.epam.esm.service.logic.certificate;

import com.epam.esm.persistence.model.entity.GiftCertificate;
import com.epam.esm.persistence.model.entity.Tag;
import com.epam.esm.persistence.model.entity.User;
import com.epam.esm.persistence.repository.data.GiftCertificateRepository;
import com.epam.esm.persistence.repository.data.OrderRepository;
import com.epam.esm.persistence.repository.data.TagRepository;
import com.epam.esm.persistence.repository.data.UserRepository;
import com.epam.esm.service.exception.*;
import com.epam.esm.persistence.model.SortParamsContext;
import com.epam.esm.service.validator.SortParamsContextValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {
    private final GiftCertificateRepository certificateRepository;
    private final TagRepository tagRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SortParamsContextValidator sortParametersValidator;

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateRepository certificateRepository,
                                      TagRepository tagRepository,
                                      OrderRepository orderRepository,
                                      UserRepository userRepository,
                                      SortParamsContextValidator sortParametersValidator) {
        this.certificateRepository = certificateRepository;
        this.tagRepository = tagRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.sortParametersValidator = sortParametersValidator;
    }

    @Override
    @Transactional
    public GiftCertificate create(GiftCertificate giftCertificate) {
        Set<Tag> tagsToPersist = new HashSet<>();
        if (giftCertificate.getTags() != null) {
            for (Tag tag : giftCertificate.getTags()) {
                Optional<Tag> tagOptional = tagRepository.findByName(tag.getName());
                if (tagOptional.isPresent()) {
                    tagsToPersist.add(tagOptional.get());
                } else {
                    tagsToPersist.add(tag);
                }
            }
        }
        giftCertificate.setTags(tagsToPersist);
        return certificateRepository.save(giftCertificate);
    }

    @Override
    public List<GiftCertificate> getAll(int page, int size) {
        Pageable pageRequest;
        try {
            pageRequest = PageRequest.of(page, size);
        } catch (IllegalArgumentException e) {
            throw new InvalidParametersException(ExceptionMessageKey.INVALID_PAGINATION);
        }

        List<GiftCertificate> giftCertificates = certificateRepository
                .findAll(pageRequest).getContent();
        giftCertificates.forEach(giftCertificate -> giftCertificate.setTags(null));
        return giftCertificates;
    }

    @Override
    public GiftCertificate getById(long id) {
        Optional<GiftCertificate> certificateOptional = certificateRepository.findById(id);
        if (!certificateOptional.isPresent()) {
            throw new NoSuchEntityException(ExceptionMessageKey.CERTIFICATE_NOT_FOUND);
        }
        return certificateOptional.get();
    }

    @Override
    public List<GiftCertificate> getAllWithTagsWithFilteringSorting(List<String> tagNames, String partInfo,
                                                                    List<String> sortColumns, List<String> orderTypes,
                                                                    int page, int size) {
        Sort sort = Sort.unsorted();
        if (sortColumns != null) {
            SortParamsContext sortParameters = new SortParamsContext(sortColumns, orderTypes);
            validateSortParams(sortParameters);

            List<String> orderTypesList = sortParameters.getOrderTypes();
            for (int i = 0; i < sortParameters.getSortColumns().size(); i++) {
                String sortColumn = sortParameters.getSortColumns().get(i);
                Sort newSort = Sort.by(sortColumn);
                if (orderTypesList.size() <= i
                        || orderTypesList.get(i).equalsIgnoreCase("asc")) {
                    newSort.ascending();
                } else {
                    newSort.descending();
                }
                sort.and(newSort);
            }
        }
        Pageable pageRequest;
        try {
            pageRequest = PageRequest.of(page, size, sort);
        } catch (IllegalArgumentException e) {
            throw new InvalidParametersException(ExceptionMessageKey.INVALID_PAGINATION);
        }

        if (tagNames == null) {
            tagNames = new ArrayList<>();
        }
        partInfo = "%" + partInfo + "%";
        return certificateRepository.findAllByIdsAndPartInfo(
                null, partInfo, pageRequest);
    }

    @Override
    @Transactional
    public GiftCertificate updateById(long id, GiftCertificate giftCertificate) {
        Optional<GiftCertificate> giftCertificateOptional = certificateRepository.findById(id);

        if (!giftCertificateOptional.isPresent()) {
            throw new NoSuchEntityException(ExceptionMessageKey.CERTIFICATE_NOT_FOUND);
        }
        GiftCertificate sourceCertificate = giftCertificateOptional.get();
        setUpdatedFields(sourceCertificate, giftCertificate);
        if (giftCertificate.getTags() != null) {
            Set<Tag> tags = giftCertificate.getTags();
            sourceCertificate.setTags(saveTags(tags));
        }
        return certificateRepository.save(sourceCertificate);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        Optional<GiftCertificate> certificateOptional = certificateRepository.findById(id);
        if (!certificateOptional.isPresent()) {
            throw new NoSuchEntityException(ExceptionMessageKey.CERTIFICATE_NOT_FOUND);
        }
        GiftCertificate giftCertificate = certificateOptional.get();
        for (Tag tag : giftCertificate.getTags()) {
            tag.getCertificates().remove(giftCertificate);
        }
        orderRepository.findAllByCertificateId(id).forEach(order -> {
            Optional<User> userOptional = userRepository.findById(order.getId());
            userOptional.ifPresent(user -> user.getOrders().remove(order));
            orderRepository.deleteById(order.getId());
        });
        certificateRepository.deleteById(id);
    }

    private void setUpdatedFields(GiftCertificate sourceCertificate,
                                  GiftCertificate certificateDto) {
        String name = certificateDto.getName();
        if (name != null && !sourceCertificate.getName().equals(name)) {
            sourceCertificate.setName(name);
        }
        String description = certificateDto.getDescription();
        if (description != null && !sourceCertificate.getDescription().equals(description)) {
            sourceCertificate.setDescription(description);
        }
        BigDecimal price = certificateDto.getPrice();
        if (price != null && sourceCertificate.getPrice().compareTo(price) != 0) {
            sourceCertificate.setPrice(price);
        }
        int duration = certificateDto.getDuration();
        if (duration != 0 && sourceCertificate.getDuration() != duration) {
            sourceCertificate.setDuration(duration);
        }
    }

    private Set<Tag> saveTags(Set<Tag> tags) {
        Set<Tag> savedTags = new HashSet<>();
        for (Tag tag : tags) {
            Optional<Tag> optionalTag = tagRepository.findByName(tag.getName());
            Tag savedTag = optionalTag.orElseGet(() -> tagRepository.save(tag));
            savedTags.add(savedTag);
        }
        return savedTags;
    }

    private void validateSortParams(SortParamsContext sortParameters) {
        if (!sortParametersValidator.isValid(sortParameters)) {
            throw new InvalidParametersException(ExceptionMessageKey.SORT_PARAMS_INVALID);
        }
    }

}
