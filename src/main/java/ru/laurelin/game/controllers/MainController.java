package ru.laurelin.game.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.laurelin.game.models.user.User;
import ru.laurelin.game.services.UserService;

@Controller
public class MainController {
    private final UserService userService;

    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String getProfilePage(Model model) {
        User user = userService.getUserById(1L);
//        user.setHp(2);
        userService.refreshUser(user);
        Integer percent = user.getHp() * 100 / user.getMaxHp();
        model.addAttribute("user", user);
        model.addAttribute("percent", percent);
        return "profile";
    }

    @GetMapping
    public String getMainPage(Model model) {
        User currentUser = getCurrentUser();
        userService.refreshUser(currentUser);
        Integer percent = currentUser.getHp() * 100 / currentUser.getMaxHp();
        model.addAttribute("user", currentUser);
        model.addAttribute("percent", percent);
        return "index";
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) userService.loadUserByUsername(auth.getName());
    }
}
