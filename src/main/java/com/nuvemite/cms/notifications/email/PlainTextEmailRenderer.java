package com.nuvemite.cms.notifications.email;

public final class PlainTextEmailRenderer {

    private PlainTextEmailRenderer() {}

    public static String toPlainText(String html) {
        if (html == null) {
            return "";
        }
        return html.replaceAll("<br\\s*/?>", "\n")
                .replaceAll("</p>", "\n\n")
                .replaceAll("<[^>]+>", "")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }
}
