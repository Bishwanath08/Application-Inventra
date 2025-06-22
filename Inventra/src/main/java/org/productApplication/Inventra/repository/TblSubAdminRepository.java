package org.productApplication.Inventra.repository;

import org.productApplication.Inventra.models.TblUsers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TblSubAdminRepository extends JpaRepository<TblUsers, Long> {

    Optional<TblUsers> findById(@Param("id") Long id);

    TblUsers findByEmail(String email);

    @Query("SELECT e FROM TblUsers e WHERE e.isDeleted = 'false'")
    Page<TblUsers> getAllRecord(Pageable pageable);
}
