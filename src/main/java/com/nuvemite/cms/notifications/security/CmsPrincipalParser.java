package com.nuvemite.cms.notifications.security;

import org.springframework.security.oauth2.jwt.Jwt;

public final class CmsPrincipalParser {

    private CmsPrincipalParser() {}

    public static CmsUserPrincipal fromJwt(Jwt jwt) {
        String platformRole = jwt.getClaimAsString("platform_role");
        return CmsUserPrincipal.fromClaims(jwt.getSubject(), platformRole);
    }
}
