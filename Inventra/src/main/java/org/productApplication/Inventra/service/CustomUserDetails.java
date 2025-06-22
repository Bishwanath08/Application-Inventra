package org.productApplication.Inventra.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private String avatarUrl;
    private String role;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String avatarUrl, String role) {
        super(username, password, authorities);
        this.avatarUrl = avatarUrl;
        this.role = role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getRole() {
        return role;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
