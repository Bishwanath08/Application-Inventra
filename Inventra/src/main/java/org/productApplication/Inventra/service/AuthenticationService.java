package org.productApplication.Inventra.service;

import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.repository.TblUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AuthenticationService {

    @Autowired
    private TblUsersRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public boolean authenticate(String email, String password) {
        TblUsers user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }

        return passwordEncoder.matches(password, user.getPassword());
    }
}
