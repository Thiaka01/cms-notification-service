package com.nuvemite.cms.notifications.support;

import java.time.Instant;
import org.springframework.security.oauth2.jwt.Jwt;

public final class TestJwt {

    private TestJwt() {}

    public static Jwt regulator() {
        return Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .subject("regulator-sub")
                .claim("platform_role", "REGULATOR")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    public static Jwt companyUser() {
        return Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .subject("user-sub")
                .claim("platform_role", "COMPANY_USER")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}
