package com.epam.esm.persistence.repository.data;

import com.epam.esm.persistence.model.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Order
 */
@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

    /**
     * Gets all Orders by User id
     *
     * @param userId User id to search
     * @param pageable object with pagination info(page number, page size)
     * @return founded orders
     */
    List<Order> findAllByUserId(long userId, Pageable pageable);
}
