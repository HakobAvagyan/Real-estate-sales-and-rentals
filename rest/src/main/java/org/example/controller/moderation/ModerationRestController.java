package org.example.controller.moderation;

import lombok.RequiredArgsConstructor;
import org.example.dto.property.PropertyResponseDto;
import org.example.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moderation/properties")
@RequiredArgsConstructor
public class ModerationRestController {

    private final PropertyService propertyService;

    @GetMapping("/pending")
    public List<PropertyResponseDto> getPendingListings() {
        return propertyService.findPendingModeration();
    }

    @PostMapping("/{propertyId}/approve")
    public ResponseEntity<Void> approveListing(@PathVariable int propertyId) {
        propertyService.approveListing(propertyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{propertyId}/reject")
    public ResponseEntity<Void> rejectListing(
            @PathVariable int propertyId,
            @RequestParam(required = false) String reason) {
        propertyService.rejectListing(propertyId, reason);
        return ResponseEntity.ok().build();
    }
}
