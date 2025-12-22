package com.app.web;

import com.app.service.DataStructureService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final DataStructureService dataStructureService;

    public UserController(DataStructureService dataStructureService) {
        this.dataStructureService = dataStructureService;
    }

    @GetMapping("/user")
    public String userRoot() {
        return "redirect:/user/visualization";
    }

    @GetMapping("/user/visualization")
    public String userVisualization() {
        return "visual";
    }

    @GetMapping("/user/visualize")
    public String userVisualize(@RequestParam(value = "type", required = false) String type,
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
