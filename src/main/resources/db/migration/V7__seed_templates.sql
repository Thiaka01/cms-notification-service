INSERT INTO email_branding (
  id, logo_url, header_title, header_subtitle, header_html, footer_html, active, updated_by
) VALUES (
  '00000000-0000-0000-0000-000000000001',
  'https://via.placeholder.com/180x48/0d4f4f/ffffff?text=CMS',
  'Chemical Management System',
  'Regulatory notifications',
  NULL,
  '<p style="margin:0;font-size:12px;color:#64748b;line-height:1.5;">This is an automated message from the Chemical Management System. Please do not reply directly to this email.</p><p style="margin:8px 0 0;font-size:12px;color:#94a3b8;">&copy; Chemical Management System</p>',
  true,
  'system'
);

INSERT INTO notification_template (id, event_type, channel, subject, body_template, active) VALUES
  ('10000000-0000-0000-0000-000000000001', 'cms.license.granted.v1', 'EMAIL', 'Your license has been granted',
   '<p>Hello {{recipientName}},</p><p>Your license <strong>{{licenseNumber}}</strong> for {{chemicalName}} at {{premiseName}} has been granted.</p>{{detailHtml}}', true),
  ('10000000-0000-0000-0000-000000000002', 'cms.license.granted.v1', 'IN_APP', 'License granted',
   'Your license {{licenseNumber}} for {{chemicalName}} has been granted.', true),
  ('10000000-0000-0000-0000-000000000003', 'cms.permit.approved.v1', 'EMAIL', 'Your permit has been approved',
   '<p>Hello {{recipientName}},</p><p>Permit <strong>{{permitNumber}}</strong> has been approved.</p>{{detailHtml}}', true),
  ('10000000-0000-0000-0000-000000000004', 'cms.permit.approved.v1', 'IN_APP', 'Permit approved',
   'Permit {{permitNumber}} has been approved.', true),
  ('10000000-0000-0000-0000-000000000005', 'cms.payment.completed.v1', 'EMAIL', 'Payment received',
   '<p>Hello {{recipientName}},</p><p>We have received your payment of <strong>{{amount}}</strong> (reference {{reference}}).</p>{{detailHtml}}', true),
  ('10000000-0000-0000-0000-000000000006', 'cms.payment.completed.v1', 'IN_APP', 'Payment received',
   'Payment {{reference}} of {{amount}} was completed.', true);
