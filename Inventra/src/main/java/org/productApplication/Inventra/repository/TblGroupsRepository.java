package org.productApplication.Inventra.repository;

import org.productApplication.Inventra.models.TblGroups;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface TblGroupsRepository  extends JpaRepository<TblGroups, Long> {

    Optional<TblGroups> findById(@Param("id") Long id);

    @Query("SELECT e FROM TblGroups e WHERE e.isDeleted = 'false'")
    Page<TblGroups> getAllRecord(Pageable pageable);

    @Query("SELECT e FROM TblGroups e WHERE e.isDeleted = 'false'")
    List<TblGroups> getAllRecordNonDeleted();

    TblGroups findByName(String name);
}
