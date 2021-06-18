package com.epam.esm.persistence.repository.data;

import com.epam.esm.persistence.model.entity.GiftCertificate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

/**
 * Repository interface for Certificate
 */
public interface GiftCertificateRepository
        extends PagingAndSortingRepository<GiftCertificate, Long> {

    /**
     * Gets all Certificates with sorting and filtering by part info of description or name.
     *
     * @param tagNames Tag names to filter
     * @param partInfo part info of name/description of Certificate to filter
     * @param sort     sort parameters
     * @param pageable object with pagination info(page number, page size)
     * @return List of filtered and sorted Certificates
     */
    @Query(value = "SELECT *\n" +
            "from certificates\n" +
            "WHERE certificates.name like ?1\n" +
            "   or certificates.description like ?1", nativeQuery = true)
    List<GiftCertificate> findAllByPartInfo(String partInfo, Pageable pageable);

    /**
     * Gets all Certificates with sorting and filtering.
     *
     * @param tagNames Tag names to filter
     * @param partInfo part info of name/description of Certificate to filter
     * @param sort     sort parameters
     * @param pageable object with pagination info(page number, page size)
     * @return List of filtered and sorted Certificates
     */
    @Query(value = "SELECT *\n" +
            "from certificates\n" +
            "WHERE (certificates.id IN ?1)\n" +
            "    AND (certificates.name like ?2\n" +
            "   or certificates.description like ?2)", nativeQuery = true)
    List<GiftCertificate> findAllByIdsAndPartInfo(
            Set<Long> certificateIds, String partInfo, Pageable pageable);
}
