package com.nuvemite.cms.notifications.messaging;

public final class EventTypes {

    public static final String CONSUMER_GROUP = "cms-notifications";

    public static final String PERMIT_SUBMITTED = "cms.permit.submitted.v1";
    public static final String PERMIT_APPROVED = "cms.permit.approved.v1";
    public static final String LICENSE_GRANTED = "cms.license.granted.v1";
    public static final String LICENSE_INSPECTION_SCHEDULED = "cms.license.inspection.scheduled.v1";
    public static final String BATCH_CREATED = "cms.batch.created.v1";
    public static final String MOVEMENT_COMPLETED = "cms.movement.completed.v1";
    public static final String PAYMENT_COMPLETED = "cms.payment.completed.v1";
    public static final String VISIT_SCHEDULED = "cms.visit.scheduled.v1";

    private EventTypes() {}
}
