package com.nuvemite.cms.notifications.messaging;

import com.nuvemite.cms.notifications.domain.NotificationChannel;
import java.util.List;
import java.util.Set;

public final class EventTypes {

    public static final String CONSUMER_GROUP = "cms-notifications";
    public static final String CMS_EVENT_TOPIC_PATTERN = "(cms\\..*\\.v\\d+|[A-Z][A-Z0-9_]+)";

    public static final String PERMIT_SUBMITTED = "cms.permit.submitted.v1";
    public static final String PERMIT_APPROVED = "cms.permit.approved.v1";
    public static final String CMS_COMPANY_CREATED = "cms.company.created.v1";
    public static final String PREMISE_CREATED = "cms.premise.created.v1";
    public static final String COMPANY_SUSPENDED = "cms.company.suspended.v1";
    public static final String LICENSE_GRANTED = "cms.license.granted.v1";
    public static final String LICENSE_REVOKED = "cms.license.revoked.v1";
    public static final String LICENSE_APPLICATION_SUBMITTED = "cms.license.application.submitted.v1";
    public static final String LICENSE_ANNUAL_INSPECTION_DUE = "cms.license.annual-inspection.due.v1";
    public static final String INSPECTION_DATE_CONFIRMED = "cms.inspection.date.confirmed.v1";
    public static final String LICENSE_INSPECTION_PROPOSED = "cms.license.inspection.proposed.v1";
    public static final String LICENSE_INSPECTION_SCHEDULED = "cms.license.inspection.scheduled.v1";
    public static final String BATCH_CREATED = "cms.batch.created.v1";
    public static final String MOVEMENT_COMPLETED = "cms.movement.completed.v1";
    public static final String LEDGER_UPDATED = "cms.ledger.updated.v1";
    public static final String PAYMENT_COMPLETED = "cms.payment.completed.v1";
    public static final String VISIT_SCHEDULED = "cms.visit.scheduled.v1";
    public static final String COMPLAINT_INSPECTION_REQUESTED = "cms.complaint.inspection.requested.v1";
    public static final String COMPLAINT_SUBMITTED = "cms.complaint.submitted.v1";
    public static final String COMPLAINT_RESOLVED = "cms.complaint.resolved.v1";
    public static final String TEMPLATE_PUBLISHED = "cms.template.published.v1";
    public static final String INSPECTION_DATE_PROPOSED = "cms.inspection.date.proposed.v1";

    public static final String USER_CREATED = "USER_CREATED";
    public static final String USER_UPDATED = "USER_UPDATED";
    public static final String PAYROLL_CREATED = "PAYROLL_CREATED";
    public static final String PAYROLL_UPDATED = "PAYROLL_UPDATED";
    public static final String PAYROLL_APPROVED = "PAYROLL_APPROVED";
    public static final String PAYROLL_REJECTED = "PAYROLL_REJECTED";
    public static final String PAYROLL_PROCESSED = "PAYROLL_PROCESSED";
    public static final String PAYROLL_COMPLETED = "PAYROLL_COMPLETED";
    public static final String EMPLOYEE_CREATED = "EMPLOYEE_CREATED";
    public static final String EMPLOYEE_UPDATED = "EMPLOYEE_UPDATED";
    public static final String COMPANY_CREATED = "COMPANY_CREATED";
    public static final String COMPANY_UPDATED = "COMPANY_UPDATED";

    private static final Set<NotificationChannel> EMAIL_SMS =
            Set.of(NotificationChannel.EMAIL, NotificationChannel.SMS);
    private static final Set<NotificationChannel> EMAIL_SMS_IN_APP =
            Set.of(NotificationChannel.EMAIL, NotificationChannel.SMS, NotificationChannel.IN_APP);

    private static final List<NotificationEventDefinition> SYSTEM_DEFINITIONS = List.of(
            event(LICENSE_GRANTED, "License granted", "Sent when a chemical license is granted.", EMAIL_SMS_IN_APP,
                    template(NotificationChannel.EMAIL, "Your license has been granted",
                            "<p>Hello {{recipientName}},</p><p>Your license <strong>{{licenseNumber}}</strong> for {{chemicalName}} has been granted.</p>{{detailHtml}}"),
                    template(NotificationChannel.SMS, "License granted",
                            "Your license {{licenseNumber}} for {{chemicalName}} has been granted.")),
            event(PERMIT_APPROVED, "Permit approved", "Sent when a permit is approved.", EMAIL_SMS_IN_APP,
                    template(NotificationChannel.EMAIL, "Your permit has been approved",
                            "<p>Hello {{recipientName}},</p><p>Permit <strong>{{permitNumber}}</strong> has been approved.</p>{{detailHtml}}"),
                    template(NotificationChannel.SMS, "Permit approved",
                            "Permit {{permitNumber}} has been approved.")),
            event(PAYMENT_COMPLETED, "Payment completed", "Sent when a payment is completed.", EMAIL_SMS_IN_APP,
                    template(NotificationChannel.EMAIL, "Payment received",
                            "<p>Hello {{recipientName}},</p><p>Payment {{reference}} of {{amount}} was completed.</p>{{detailHtml}}"),
                    template(NotificationChannel.SMS, "Payment completed",
                            "Payment {{reference}} of {{amount}} was completed.")),
            event(PERMIT_SUBMITTED, "Permit submitted", "Sent when a permit is submitted.", EMAIL_SMS_IN_APP,
                    genericEmailTemplate(),
                    genericSmsTemplate()),
            event(LICENSE_INSPECTION_SCHEDULED, "License inspection scheduled",
                    "Sent when a license inspection is scheduled.", EMAIL_SMS_IN_APP,
                    template(NotificationChannel.EMAIL, "License inspection scheduled",
                            "<p>Hello {{recipientName}},</p><p>Your license inspection is scheduled for <strong>{{inspectionDate}}</strong> with {{inspectorName}}.</p>{{detailHtml}}"),
                    template(NotificationChannel.SMS, "Inspection scheduled",
                            "Your license inspection is scheduled for {{inspectionDate}} with {{inspectorName}}.")),
            event(BATCH_CREATED, "Batch created", "Sent when a chemical batch is created.", EMAIL_SMS_IN_APP,
                    genericEmailTemplate(),
                    genericSmsTemplate()),
            event(MOVEMENT_COMPLETED, "Movement completed", "Sent when a movement is completed.", EMAIL_SMS_IN_APP,
                    genericEmailTemplate(),
                    genericSmsTemplate()),
            event(VISIT_SCHEDULED, "Visit scheduled", "Sent when a visit is scheduled.", EMAIL_SMS_IN_APP,
                    template(NotificationChannel.EMAIL, "Visit scheduled",
                            "<p>Hello {{recipientName}},</p><p>A visit is scheduled for <strong>{{visitDate}}</strong> with {{inspectorName}}.</p>{{detailHtml}}"),
                    template(NotificationChannel.SMS, "Visit scheduled",
                            "A visit is scheduled for {{visitDate}} with {{inspectorName}}.")),
            cmsEvent(CMS_COMPANY_CREATED, "Sent when a company is created."),
            cmsEvent(PREMISE_CREATED, "Sent when a premise is created."),
            cmsEvent(COMPANY_SUSPENDED, "Sent when a company is suspended."),
            cmsEvent(LICENSE_REVOKED, "Sent when a license is revoked."),
            cmsEvent(LICENSE_APPLICATION_SUBMITTED, "Sent when a license application is submitted."),
            cmsEvent(LICENSE_ANNUAL_INSPECTION_DUE, "Sent when a license annual inspection is due."),
            cmsEvent(INSPECTION_DATE_CONFIRMED, "Sent when an inspection date is confirmed."),
            cmsEvent(LICENSE_INSPECTION_PROPOSED, "Sent when license inspection dates are proposed."),
            cmsEvent(LEDGER_UPDATED, "Sent when a chemical ledger is updated."),
            cmsEvent(COMPLAINT_INSPECTION_REQUESTED, "Sent when a complaint inspection is requested."),
            cmsEvent(COMPLAINT_SUBMITTED, "Sent when a complaint is submitted."),
            cmsEvent(COMPLAINT_RESOLVED, "Sent when a complaint is resolved."),
            cmsEvent(TEMPLATE_PUBLISHED, "Sent when a form template is published."),
            cmsEvent(INSPECTION_DATE_PROPOSED, "Sent when an inspection date is proposed."),
            event(USER_CREATED, "User created", "Sent when a user account is created.", EMAIL_SMS,
                    template(NotificationChannel.EMAIL, "Welcome to CMS",
                            "<p>Hello {{recipientName}},</p><p>Your CMS user account has been created.</p><p>Username: <strong>{{username}}</strong></p>"),
                    template(NotificationChannel.SMS, "CMS account created",
                            "Hello {{recipientName}}, your CMS account has been created. Username: {{username}}.")),
            event(USER_UPDATED, "User updated", "Sent when a user account is updated.", EMAIL_SMS,
                    template(NotificationChannel.EMAIL, "Your CMS profile was updated",
                            "<p>Hello {{recipientName}},</p><p>Your CMS user profile was updated.</p>{{detailHtml}}"),
                    template(NotificationChannel.SMS, "CMS profile updated",
                            "Hello {{recipientName}}, your CMS user profile was updated.")),
            payroll(PAYROLL_CREATED, "Payroll created", "created"),
            payroll(PAYROLL_UPDATED, "Payroll updated", "updated"),
            payroll(PAYROLL_APPROVED, "Payroll approved", "approved"),
            payroll(PAYROLL_REJECTED, "Payroll rejected", "rejected"),
            payroll(PAYROLL_PROCESSED, "Payroll processed", "processed"),
            payroll(PAYROLL_COMPLETED, "Payroll completed", "completed"),
            event(EMPLOYEE_CREATED, "Employee created", "Sent when an employee record is created.", EMAIL_SMS,
                    template(NotificationChannel.EMAIL, "Employee record created",
                            "<p>Hello {{recipientName}},</p><p>Employee <strong>{{employeeName}}</strong> has been created.</p>"),
                    template(NotificationChannel.SMS, "Employee created",
                            "Employee {{employeeName}} has been created in CMS.")),
            event(EMPLOYEE_UPDATED, "Employee updated", "Sent when an employee record is updated.", EMAIL_SMS,
                    template(NotificationChannel.EMAIL, "Employee record updated",
                            "<p>Hello {{recipientName}},</p><p>Employee <strong>{{employeeName}}</strong> has been updated.</p>"),
                    template(NotificationChannel.SMS, "Employee updated",
                            "Employee {{employeeName}} has been updated in CMS.")),
            event(COMPANY_CREATED, "Company created", "Sent when a company record is created.", Set.of(NotificationChannel.EMAIL),
                    template(NotificationChannel.EMAIL, "Company record created",
                            "<p>Hello {{recipientName}},</p><p>Company <strong>{{companyName}}</strong> has been created.</p>")),
            event(COMPANY_UPDATED, "Company updated", "Sent when a company record is updated.", Set.of(NotificationChannel.EMAIL),
                    template(NotificationChannel.EMAIL, "Company record updated",
                            "<p>Hello {{recipientName}},</p><p>Company <strong>{{companyName}}</strong> has been updated.</p>")));

    private EventTypes() {}

    public static List<NotificationEventDefinition> systemDefinitions() {
        return SYSTEM_DEFINITIONS;
    }

    private static NotificationEventDefinition cmsEvent(String eventCode, String description) {
        return event(eventCode, eventName(eventCode), description, EMAIL_SMS, genericEmailTemplate(), genericSmsTemplate());
    }

    private static NotificationEventDefinition payroll(String eventCode, String name, String action) {
        return event(eventCode, name, "Sent when payroll is " + action + ".", EMAIL_SMS,
                template(NotificationChannel.EMAIL, "Payroll {{payrollMonth}} " + action,
                        "<p>Hello {{employeeName}},</p><p>Your payroll for <strong>{{payrollMonth}}</strong> has been "
                + action + ".</p><p>Regards,<br>CMS Team</p>"),
                template(NotificationChannel.SMS, "Payroll " + action,
                        "Hello {{employeeName}}, your payroll for {{payrollMonth}} has been " + action + "."));
    }

    private static TemplateDefinition genericEmailTemplate() {
        return template(NotificationChannel.EMAIL, "{{eventName}}",
                "<p>Hello {{recipientName}},</p><p>{{eventName}} was recorded in CMS.</p>{{detailHtml}}");
    }

    private static TemplateDefinition genericSmsTemplate() {
        return template(NotificationChannel.SMS, "{{eventName}}", "{{eventName}} was recorded in CMS.");
    }

    private static NotificationEventDefinition event(
            String eventCode,
            String name,
            String description,
            Set<NotificationChannel> channels,
            TemplateDefinition... templates) {
        return new NotificationEventDefinition(eventCode, name, description, channels, List.of(templates));
    }

    private static TemplateDefinition template(NotificationChannel channel, String subject, String content) {
        return new TemplateDefinition(channel, subject, content);
    }

    private static String eventName(String eventCode) {
        String normalized = eventCode;
        if (normalized.startsWith("cms.")) {
            normalized = normalized.substring(4);
        }
        normalized = normalized.replaceAll("\\.v\\d+$", "").replace('.', ' ').replace('_', ' ');
        String[] words = normalized.split("\\s+");
        StringBuilder title = new StringBuilder();
        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            if (!title.isEmpty()) {
                title.append(' ');
            }
            title.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                title.append(word.substring(1));
            }
        }
        return !title.isEmpty() ? title.toString() : "Notification";
    }

    public record NotificationEventDefinition(
            String eventCode,
            String name,
            String description,
            Set<NotificationChannel> supportedChannels,
            List<TemplateDefinition> templates) {}

    public record TemplateDefinition(
            NotificationChannel channel,
            String subject,
            String templateContent) {}
}
