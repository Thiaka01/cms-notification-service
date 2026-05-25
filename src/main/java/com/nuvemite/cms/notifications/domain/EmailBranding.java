package com.nuvemite.cms.notifications.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "email_branding")
public class EmailBranding {

    @Id
    private UUID id;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "header_title", nullable = false)
    private String headerTitle;

    @Column(name = "header_subtitle")
    private String headerSubtitle;

    @Column(name = "header_html")
    private String headerHtml;

    @Column(name = "footer_html", nullable = false)
    private String footerHtml;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;


    public static EmailBranding create(
            String logoUrl,
            String headerTitle,
            String headerSubtitle,
            String headerHtml,
            String footerHtml,
            String updatedBy) {
        EmailBranding branding = new EmailBranding();
        branding.id = UUID.randomUUID();
        branding.logoUrl = logoUrl;
        branding.headerTitle = headerTitle;
        branding.headerSubtitle = headerSubtitle;
        branding.headerHtml = headerHtml;
        branding.footerHtml = footerHtml;
        branding.active = true;
        branding.updatedAt = Instant.now();
        branding.updatedBy = updatedBy;
        return branding;
    }

    public void deactivate() {
        this.active = false;
    }







    public boolean isActive() {
        return active;
    }


}
