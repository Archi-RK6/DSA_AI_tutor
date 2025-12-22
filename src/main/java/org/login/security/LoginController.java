package org.login.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.login.topic.TopicService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final LoginService loginService;
    private final TopicService topicService;

    public LoginController(LoginService loginService, TopicService topicService) {
        this.loginService = loginService;
        this.topicService = topicService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam("name") String name,
                          @RequestParam("password") String password,
                          HttpServletRequest request,
                          ModelMap model) {
        if (!loginService.validateUser(name, password)) {
            model.put("errorMessage", "Invalid username or password");
            return "login";
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("AUTH_NAME", name);

        if ("admin".equalsIgnoreCase(name)) {
            return "redirect:/admin_home";
        } else {
            return "redirect:/user_home";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }

    @GetMapping("/admin_home")
    public String adminHome(ModelMap model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String name = session != null ? (String) session.getAttribute("AUTH_NAME") : null;
        if (name == null) {
            return "redirect:/login";
        }
        model.put("name", name);
        model.put("topics", topicService.findAll());
        return "admin_home";
    }

    @GetMapping("/user_home")
    public String userHome(ModelMap model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String name = session != null ? (String) session.getAttribute("AUTH_NAME") : null;
        if (name == null) {
            return "redirect:/login";
        }
        model.put("name", name);
        model.put("topics", topicService.findAll());
        return "user_home";
    }
}
