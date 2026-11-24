package org.example.student_university_helpdesk.controller.Resources;

import org.example.student_university_helpdesk.model.ResourceManagement.Resources;
import org.example.student_university_helpdesk.service.ResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/resources")
public class AdminResourceController {

    private final ResourcesService resourcesService;

    @Autowired
    public AdminResourceController(ResourcesService resourcesService) {
        this.resourcesService = resourcesService;
    }

    @PostMapping({"/add", "/create"})
    public String addResource(@RequestParam String resourceType,
                              @RequestParam String location,
                              @RequestParam(required = false, defaultValue = "true") Boolean availability,
                              @RequestParam(required = false) String returnUrl,
                              RedirectAttributes redirectAttributes) {
        try {
            boolean isAvail = (availability == null) || availability;
            Resources resource = new Resources(resourceType.trim(), location.trim(), isAvail);
            resourcesService.addResource(resource);
            redirectAttributes.addFlashAttribute("successMessage", "New resource added: " + resourceType.trim() + " at " + location.trim());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding resource: " + e.getMessage());
        }
        return (returnUrl != null && !returnUrl.isBlank()) ? "redirect:" + returnUrl : "redirect:/admin/bookings";
    }

    @PostMapping("/update")
    public String updateResource(@RequestParam Long resourceId,
                                 @RequestParam(required = false) String resourceType,
                                 @RequestParam(required = false) String location,
                                 @RequestParam(required = false) Boolean availability,
                                 @RequestParam(required = false) String returnUrl,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (resourceType != null && !resourceType.isBlank() && location != null && !location.isBlank()) {
                Resources updated = new Resources();
                updated.setResourceType(resourceType.trim());
                updated.setLocation(location.trim());
                resourcesService.updateResource(resourceId, updated);
            }
            if (availability != null) {
                resourcesService.setResourceAvailability(resourceId, availability);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Resource updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating resource: " + e.getMessage());
        }
        return (returnUrl != null && !returnUrl.isBlank()) ? "redirect:" + returnUrl : "redirect:/admin/bookings";
    }

    @PostMapping({"/delete", "/disable"})
    public String deleteResource(@RequestParam Long resourceId,
                                 @RequestParam(required = false) String returnUrl,
                                 RedirectAttributes redirectAttributes) {
        try {
            resourcesService.setResourceAvailability(resourceId, false);
            redirectAttributes.addFlashAttribute("successMessage", "Resource disabled successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not disable resource: " + e.getMessage());
        }
        return (returnUrl != null && !returnUrl.isBlank()) ? "redirect:" + returnUrl : "redirect:/admin/bookings";
    }
}
