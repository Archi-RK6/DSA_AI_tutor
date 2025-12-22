package org.login.user;

import org.springframework.cache.CacheManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.Set;

@Controller
public class UserTopicController {

    private static final Map<String, String> TOPIC_TO_PDF = Map.of(
            "oop", "oop.pdf",
            "ds", "ds.pdf",
            "algo", "algo.pdf"
    );

    private static final Set<String> ALLOWED = Set.copyOf(TOPIC_TO_PDF.values());

    @GetMapping("/user_home/topic/oop")
    public String oopUserPage(ModelMap m) {
        m.addAttribute("topicName", "Object-oriented programming");
        m.addAttribute("pdfPath", "/user_home/topic/pdf/" + TOPIC_TO_PDF.get("oop"));
        return "user_content";
    }

    @GetMapping("/user_home/topic/ds")
    public String dsUserPage(ModelMap m) {
        m.addAttribute("topicName", "Data Structures");
        m.addAttribute("pdfPath", "/user_home/topic/pdf/" + TOPIC_TO_PDF.get("ds"));
        return "user_content";
    }

    @GetMapping("/user_home/topic/algo")
    public String algoUserPage(ModelMap m) {
        m.addAttribute("topicName", "Algorithms");
        m.addAttribute("pdfPath", "/user_home/topic/pdf/" + TOPIC_TO_PDF.get("algo"));
        return "user_content";
    }

    @GetMapping("/user_home/topic/visual")
    public String visualUserPage() {
        return "redirect:/visual";
    }

    @GetMapping("/user_home/topic/pdf/{fileName:.+}")
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
}
