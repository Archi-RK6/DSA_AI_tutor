package org.login.admin;

import org.login.topic.TopicService;
import org.login.common.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminPanelControl {

    @Autowired
    TopicService topicService;

    @GetMapping("/admin_home/topics")
    public String listTopics(Model model) {
        model.addAttribute("topics", topicService.findAll());
        model.addAttribute("newTopic", new Topic());
        return "admin_topics";
    }

    @PostMapping("/admin_home/topics")
    public String addTopic(@ModelAttribute("newTopic") Topic topic) {
        topicService.save(topic);
        return "redirect:/admin_home/topics";
    }

    @PostMapping("/admin_home/topics/delete")
    public String deleteTopic(@RequestParam("id") Integer id) {
        topicService.deleteById(id);
        return "redirect:/admin_home/topics";
    }
}
