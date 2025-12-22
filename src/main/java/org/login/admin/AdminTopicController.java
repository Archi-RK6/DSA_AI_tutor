package org.login.admin;

import org.login.content.ContentService;
import org.login.common.model.Content;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminTopicController {

    private final ContentService contentService;

    public AdminTopicController(ContentService contentService) {
        this.contentService = contentService;
    }

    @RequestMapping(value = "/admin_home/topic/oop", method = RequestMethod.GET)
    public String oopPageAdmin(ModelMap m) {
        m.addAttribute("topicName", "Object-oriented programming");
        List<Content> chapters = contentService.getContentsForTopic(1);
        m.put("chapters", chapters);
        m.put("Topic", 1);
        return "admin_content";
    }

    @RequestMapping(value = "/admin_home/topic/ds", method = RequestMethod.GET)
    public String dsPageAdmin(ModelMap m) {
        m.addAttribute("topicName", "Data structures");
        List<Content> chapters = contentService.getContentsForTopic(2);
        m.put("chapters", chapters);
        m.put("Topic", 2);
        return "admin_content";
    }

    @RequestMapping(value = "/admin_home/topic/algo", method = RequestMethod.GET)
    public String algoPageAdmin(ModelMap m) {
        m.addAttribute("topicName", "Algorithms");
        List<Content> chapters = contentService.getContentsForTopic(3);
        m.put("chapters", chapters);
        m.put("Topic", 3);
        return "admin_content";
    }

    @GetMapping("/admin_home/topic/visual")
    public String visualAdminPage() {
        // Route admins to the new DS selection page
        return "redirect:/admin";
    }

    @RequestMapping(value = "/admin_home/manage_content", method = RequestMethod.GET)
    public String manageContent(ModelMap m, @RequestParam("topicId") int topicId) {
        m.addAttribute("topicId", topicId);
        m.addAttribute("chapters", contentService.getContentsForTopic(topicId));
        m.addAttribute("newTopic", new Content());
        return "admin_content_manage";
    }

    @RequestMapping(value = "/admin_home/topic", method = RequestMethod.POST)
    public String addChapter(@RequestParam Integer topicId, @ModelAttribute("newTopic") Content content){
        content.setTopicId(topicId);
        contentService.save(content);
        return "redirect:/admin_home";
    }

    @RequestMapping(value = "/admin_home/topic/delete", method = RequestMethod.POST)
    public String deleteChapter(@RequestParam("id") Integer id){
        contentService.deleteById(id);
        return "redirect:/admin_home";
    }
}
