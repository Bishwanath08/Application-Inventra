package org.productApplication.Inventra.repository;

import org.productApplication.Inventra.models.TblGroupPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TblGroupPermissionRepository extends JpaRepository<TblGroupPermission, Long> {

//    List<TblPermissions> findByNameStartingWith(String prefix);
    List<TblGroupPermission> findByGroupId(Long groupId);
}

