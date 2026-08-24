package com.arvin.filmrec.security;

import com.arvin.filmrec.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    /** Returns the authenticated User, or null if the request is unauthenticated (e.g. public browsing). */
    public User getCurrentUserOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof User)) {
            return null;
        }
        return (User) auth.getPrincipal();
    }

    public User getRequiredCurrentUser() {
        User user = getCurrentUserOrNull();
        if (user == null) {
            throw new IllegalStateException("No authenticated user in security context");
        }
        return user;
    }
}
