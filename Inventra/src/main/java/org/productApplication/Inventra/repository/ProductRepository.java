package org.productApplication.Inventra.repository;

import org.productApplication.Inventra.models.TblProducts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface ProductRepository extends JpaRepository<TblProducts, Long> {
    Page<TblProducts> findAll (Pageable pageable);

}
