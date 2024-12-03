package com.inventory.server.utils;

import com.inventory.server.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserIdGetter {

    public static Long getUserIdFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return ((User) authentication.getPrincipal()).getId();
    }
}
