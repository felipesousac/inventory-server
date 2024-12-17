package com.inventory.server.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class MyUserPrincipal implements UserDetails {

    private final User inventoryUser;

    public MyUserPrincipal(User inventoryUser) {
        this.inventoryUser = inventoryUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.inventoryUser.getRoles();
    }

    @Override
    public String getPassword() {
        return this.inventoryUser.getPassword();
    }

    @Override
    public String getUsername() {
        return this.inventoryUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.inventoryUser.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.inventoryUser.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.inventoryUser.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.inventoryUser.isEnabled();
    }

    public User getInventoryUser() {
        return inventoryUser;
    }
}
