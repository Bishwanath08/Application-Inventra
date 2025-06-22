package org.productApplication.Inventra.service;

import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.repository.TblUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TblUsersRepository tblUsersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        TblUsers users = tblUsersRepository.findByEmail(email);
        if (users == null) {
            throw new UsernameNotFoundException("Admin not found with email: " + email);
        }

        return new CustomUserDetails(
                users.getEmail(),
                users.getPassword(),
                AuthorityUtils.createAuthorityList("ROLE_ADMIN"),
                users.getAvatarUrl(),
                users.getRole()

        );
    }
}
