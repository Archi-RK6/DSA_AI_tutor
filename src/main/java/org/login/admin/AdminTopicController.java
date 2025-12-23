package org.login.admin;

import org.login.common.model.Content;
import org.login.content.ContentService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class AdminTopicController {

    private static final Map<String, String> TOPIC_TO_PDF = Map.of(
            "oop", "oop.pdf",
            "ds", "ds.pdf",
            "algo", "algo.pdf"
    );

    private static final Set<String> ALLOWED = Set.copyOf(TOPIC_TO_PDF.values());

    private final ContentService contentService;

    public AdminTopicController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/admin_home/topic/oop")
    public String oopPageAdmin(ModelMap m) {
        m.addAttribute("topicName", "Object-oriented programming");
        m.addAttribute("pdfPath", "/admin_home/topic/pdf/" + TOPIC_TO_PDF.get("oop"));

        List<Content> chapters = contentService.getContentsForTopic(1);
        m.put("chapters", chapters);
        m.put("Topic", 1);

        return "admin_content";
    }

    @GetMapping("/admin_home/topic/ds")
    public String dsPageAdmin(ModelMap m) {
        m.addAttribute("topicName", "Data Structures");
        m.addAttribute("pdfPath", "/admin_home/topic/pdf/" + TOPIC_TO_PDF.get("ds"));

        List<Content> chapters = contentService.getContentsForTopic(2);
        m.put("chapters", chapters);
        m.put("Topic", 2);

        return "admin_content";
    }

    @GetMapping("/admin_home/topic/algo")
    public String algoPageAdmin(ModelMap m) {
        m.addAttribute("topicName", "Algorithms");
        m.addAttribute("pdfPath", "/admin_home/topic/pdf/" + TOPIC_TO_PDF.get("algo"));

        List<Content> chapters = contentService.getContentsForTopic(3);
        m.put("chapters", chapters);
        m.put("Topic", 3);

        return "admin_content";
    }

    @GetMapping("/admin_home/topic/visual")
    public String visualAdminPage() {
        return "redirect:/admin";
    }

    @GetMapping("/admin_home/topic/pdf/{fileName:.+}")
    public ResponseEntity<Resource> servePdf(@PathVariable String fileName) {
        if (fileName == null || !ALLOWED.contains(fileName)) {
            return ResponseEntity.notFound().build();
        }

        ClassPathResource res = new ClassPathResource("rag/" + fileName);
        if (!res.exists()) {
            return ResponseEntity.notFound().build();
        }

        String safeName = fileName.replace("\"", "");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + safeName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .cacheControl(CacheControl.noCache())
                .body(res);
    }

    @GetMapping("/admin_home/manage_content")
    public String manageContent(ModelMap m, @RequestParam("topicId") int topicId) {
        m.addAttribute("topicId", topicId);
        m.addAttribute("chapters", contentService.getContentsForTopic(topicId));
        m.addAttribute("newTopic", new Content());
        return "admin_content_manage";
    }

    @PostMapping("/admin_home/topic")
    public String addChapter(@RequestParam Integer topicId,
                             @ModelAttribute("newTopic") Content content) {
        content.setTopicId(topicId);
        contentService.save(content);
        return "redirect:/admin_home";
    }

    @PostMapping("/admin_home/topic/delete")
    public String deleteChapter(@RequestParam("id") Integer id) {
        contentService.deleteById(id);
        return "redirect:/admin_home";
    }
}
