package org.example.app.controller.manager;

import lombok.RequiredArgsConstructor;
import org.example.dto.property.PropertyResponseDto;
import org.example.exception.BusinessException;
import org.example.service.PropertyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ManagerPropertyModerationController {

    private final PropertyService propertyService;

    @GetMapping("/manager/properties/pending")
    public String pendingListings(ModelMap modelMap) {
        List<PropertyResponseDto> pending = propertyService.findPendingModeration();
        modelMap.addAttribute("pendingProperties", pending);
        return "manager/pendingProperties";
    }

    @PostMapping("/manager/property/approve")
    public String approve(@RequestParam int propertyId, RedirectAttributes redirectAttributes) {
        try {
            propertyService.approveListing(propertyId);
            redirectAttributes.addFlashAttribute("moderationSuccess", "Listing approved.");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("moderationError", ex.getMessage());
        }
        return "redirect:/manager/properties/pending";
    }

    @PostMapping("/manager/property/reject")
    public String reject(@RequestParam int propertyId,
                         @RequestParam(required = false) String reason,
                         RedirectAttributes redirectAttributes) {
        try {
            propertyService.rejectListing(propertyId, reason);
            redirectAttributes.addFlashAttribute("moderationSuccess", "Listing rejected; the owner was notified.");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("moderationError", ex.getMessage());
        }
        return "redirect:/manager/properties/pending";
    }
}
