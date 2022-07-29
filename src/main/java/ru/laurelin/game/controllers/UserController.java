package ru.laurelin.game.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.laurelin.game.models.user.Role;
import ru.laurelin.game.models.user.User;
import ru.laurelin.game.services.UserService;

@Controller
@RequestMapping("/ap/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUsersPage(Model model) {
        model.addAttribute("userList", userService.getAllUsers());
        return "ap/usersPage";
    }

    @GetMapping("/{id}/edit")
    public String getItemEditPage(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("rolesList", Role.values());
        return "ap/editUser";
    }

    @PostMapping("/{id}/update")
    public String updateUser(@ModelAttribute User user, @PathVariable Long id, Model model) {
        if (userService.updateUserInfo(user, id))
            return "redirect:../../users";
        else {
            model.addAttribute("user", user);
            return "ap/editItem";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:../../users";
    }
}
