package org.example.app.i18n;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class LocaleController {

    private static final List<String> ALLOWED = List.of("en", "ru", "hy");

    private final LocaleResolver localeResolver;

    @GetMapping("/locale")
    public String changeLocale(@RequestParam("lang") String lang,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        String code = ALLOWED.contains(lang) ? lang : "en";
        Locale locale = Locale.forLanguageTag(code);
        localeResolver.setLocale(request, response, locale);
        return "redirect:" + safeRedirectPath(request);
    }

    private static String safeRedirectPath(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return "/home";
        }
        try {
            URI uri = new URI(referer);
            if (!request.getServerName().equalsIgnoreCase(uri.getHost())) {
                return "/home";
            }
            String ctx = request.getContextPath();
            String path = uri.getPath();
            if (ctx != null && !ctx.isEmpty() && path != null && path.startsWith(ctx)) {
                path = path.substring(ctx.length());
            }
            if (path == null || path.isEmpty()) {
                path = "/home";
            }
            String query = uri.getRawQuery();
            if (query == null || query.isBlank()) {
                return path;
            }
            String cleaned = removeLangParam(query);
            return cleaned.isEmpty() ? path : path + "?" + cleaned;
        } catch (URISyntaxException e) {
            return "/home";
        }
    }

    private static String removeLangParam(String query) {
        String[] parts = query.split("&");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.startsWith("lang=")) {
                continue;
            }
            if (!sb.isEmpty()) {
                sb.append('&');
            }
            sb.append(part);
        }
        return sb.toString();
    }
}
