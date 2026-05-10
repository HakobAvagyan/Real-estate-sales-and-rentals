package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.example.exception.BusinessException;
import org.example.service.Property360Service;
import org.example.service.security.SpringUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class Property360Controller {

    private final Property360Service property360Service;

    @PostMapping("/property/{id}/view360")
    public String addOrUpdate(@PathVariable int id,
                              @RequestParam String viewUrl,
                              @AuthenticationPrincipal SpringUser userPrincipal,
                              RedirectAttributes redirectAttributes) {
        try {
            property360Service.addOrUpdate(id, viewUrl, userPrincipal.getUser().getId());
            redirectAttributes.addFlashAttribute("view360Success", true);
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("view360Error", ex.getMessage());
        }
        return "redirect:/property/details?propertyId=" + id;
    }

    @PostMapping("/property/{id}/view360/delete")
    public String delete(@PathVariable int id,
                         @AuthenticationPrincipal SpringUser userPrincipal,
                         RedirectAttributes redirectAttributes) {
        try {
            property360Service.delete(id, userPrincipal.getUser().getId());
            redirectAttributes.addFlashAttribute("view360Deleted", true);
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("view360Error", ex.getMessage());
        }
        return "redirect:/property/details?propertyId=" + id;
    }
}