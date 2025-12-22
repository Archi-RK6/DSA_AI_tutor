package org.login.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.login.common.model.Profile;
import org.login.common.repository.ProfileRepository;
import org.login.topic.TopicService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    private final ProfileRepository profileRepository;
    private final TopicService topicService;

    public RegistrationController(ProfileRepository profileRepository,
                                  TopicService topicService) {
        this.profileRepository = profileRepository;
        this.topicService = topicService;
    }

    @GetMapping("/register")
    public String registerForm() {
        // /WEB-INF/jsp/register.jsp
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("name") String name,
                           @RequestParam("password") String password,
                           HttpServletRequest request,
                           ModelMap model) {

        if (name == null || name.isBlank() || password == null || password.isBlank()) {
            model.put("errorMessage", "Имя и пароль не должны быть пустыми");
            return "register";
        }

        if (profileRepository.findByName(name).isPresent()) {
            model.put("errorMessage", "Пользователь с таким именем уже существует");
            return "register";
        }

        Profile profile = new Profile();
        profile.setName(name);
        // В учебном проекте можно оставить так, но в реальности тут должен быть хэш
        profile.setPassword(password);
        profileRepository.save(profile);

        // Автоматический логин после регистрации
        HttpSession session = request.getSession(true);
        session.setAttribute("AUTH_NAME", name);

        model.put("name", name);
        model.put("topics", topicService.findAll());

        return "user_home";
    }
}
