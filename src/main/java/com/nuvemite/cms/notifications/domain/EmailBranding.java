package com.nuvemite.cms.notifications.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

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

    protected EmailBranding() {}

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

    public UUID getId() {
        return id;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public String getHeaderSubtitle() {
        return headerSubtitle;
    }

    public String getHeaderHtml() {
        return headerHtml;
    }

    public String getFooterHtml() {
        return footerHtml;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }
}
