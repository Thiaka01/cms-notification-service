package com.nuvemite.cms.notifications.security;

public record CmsUserPrincipal(String subject, String platformRole) {

    public boolean isRegulator() {
        return "REGULATOR".equals(platformRole);
    }

    public static CmsUserPrincipal fromClaims(String subject, String platformRole) {
        return new CmsUserPrincipal(subject, platformRole);
    }
}
