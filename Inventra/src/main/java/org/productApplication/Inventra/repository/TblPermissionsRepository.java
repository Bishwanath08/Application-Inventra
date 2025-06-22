package org.productApplication.Inventra.repository;

import org.productApplication.Inventra.models.TblPermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TblPermissionsRepository extends JpaRepository<TblPermissions, Long> {
    TblPermissions findByName(String name);
    Optional<TblPermissions> findById(Long id);


}
