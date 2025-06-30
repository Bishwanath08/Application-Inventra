package org.productApplication.Inventra.repository;

import org.productApplication.Inventra.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {

    Page<Users> findAll (Pageable pageable);
}
