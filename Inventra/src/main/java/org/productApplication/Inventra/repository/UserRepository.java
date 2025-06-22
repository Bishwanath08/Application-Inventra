package org.productApplication.Inventra.repository;

import org.productApplication.Inventra.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
