package com.app.web;

import com.app.service.DataStructureService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    private final DataStructureService dataStructureService;

    public AdminController(DataStructureService dataStructureService) {
        this.dataStructureService = dataStructureService;
    }

    @GetMapping("/admin")
    public String adminRoot() {
        return "redirect:/admin/visualization";
    }

    @GetMapping("/admin/visualization")
    public String adminVisualization() {
        return "visual";
    }

    @GetMapping("/admin/visualize")
    public String adminVisualize(@RequestParam(value = "type", required = false) String type,
                                 HttpSession session,
                                 Model model) {

        if (type != null && !type.isBlank()) {
            dataStructureService.select(session, type);
        } else if (dataStructureService.selectedType(session) == null) {
            dataStructureService.select(session, "ARRAY_LIST");
        }

        model.addAttribute("selectedType", dataStructureService.selectedType(session));
        return "visualize";
    }
}