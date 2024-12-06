package com.inventory.server.configuration.security;

import com.inventory.server.user.User;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import java.util.Map;
import java.util.function.Supplier;

@Component
public class UserRequestAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final UriTemplate AUTH_URI_TEMPLATE = new UriTemplate("/users/{userId}");

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier,
                                       RequestAuthorizationContext object) {

        Map<String, String> uriVariables = AUTH_URI_TEMPLATE.match(object.getRequest().getRequestURI());
        String userIdFromUri = uriVariables.get("userId");

        Authentication authentication = authenticationSupplier.get();
        String userIdFromPrincipal = String.valueOf(((User) authentication.getPrincipal()).getId());

        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));

        boolean userIdMatch = userIdFromUri != null && userIdFromUri.equals(userIdFromPrincipal);

        return new AuthorizationDecision(hasAdminRole || userIdMatch);
    }
}
