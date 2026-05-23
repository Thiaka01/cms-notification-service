package com.nuvemite.cms.notifications.email;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class TemplateRendererTest {

    @Test
    void replacesPlaceholders() {
        String result = TemplateRenderer.render(
                "Hello {{recipientName}}, permit {{permitNumber}}",
                Map.of("recipientName", "Jane", "permitNumber", "P-42"));
        assertThat(result).isEqualTo("Hello Jane, permit P-42");
    }
}
