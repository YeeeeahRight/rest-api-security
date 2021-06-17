package com.epam.esm.persistence.repository.data;

import com.epam.esm.persistence.model.BestUserTag;
import com.epam.esm.persistence.model.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Tag
 */
@Repository
public interface TagRepository extends PagingAndSortingRepository<Tag, Long> {

    /**
     * Finds role by name.
     *
     * @param name the name to find
     * @return the optional role
     */
    Optional<Tag> findByName(String name);

    /**
     * Gets User most widely used Tag with highest Order cost
     *
     * @param userId User id to search
     * @return Optional BestUserTag - BestUserTag if founded or Empty if not
     */
    @Query(name = "findUserMostWidelyUsedTagWithHighestOrderCost", nativeQuery = true)
    Optional<BestUserTag> findUserMostWidelyUsedTagWithHighestOrderCost(long userId);
}
