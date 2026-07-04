package online.armanportfolio.sms.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Exposes runtime config so the UI can adapt (e.g. hide destructive actions
 * that are disabled in the public demo).
 */
@RestController
@RequestMapping("/api/meta")
public class MetaController {

    private final boolean allowClearAll;

    public MetaController(@Value("${app.allow-clear-all:true}") boolean allowClearAll) {
        this.allowClearAll = allowClearAll;
    }

    @GetMapping
    public Map<String, Object> meta() {
        return Map.of("clearAllEnabled", allowClearAll, "version", "1.0.0");
    }
}
