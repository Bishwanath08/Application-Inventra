package org.productApplication.Inventra.repository;


import org.productApplication.Inventra.models.TblUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
    public interface TblUsersRepository extends JpaRepository<TblUsers, Long> {

    TblUsers findByEmail(String email);

    @Query("SELECT u FROM TblUsers u WHERE u.name LIKE %:name%")
    List<TblUsers> findByNameLike(@Param("name") String name);
}
