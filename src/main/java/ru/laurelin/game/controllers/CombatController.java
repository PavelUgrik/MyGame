package ru.laurelin.game.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.laurelin.game.models.combat.*;
import ru.laurelin.game.models.user.User;
import ru.laurelin.game.services.CombatService;
import ru.laurelin.game.services.CombatStepService;
import ru.laurelin.game.services.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
public class CombatController {

    private final CombatService combatService;
    private final UserService userService;
    private final CombatStepService combatStepService;

    @Autowired
    public CombatController(CombatService combatService, UserService userService, CombatStepService combatStepService) {
        this.combatService = combatService;
        this.userService = userService;
        this.combatStepService = combatStepService;
    }

    @GetMapping("/combats")
    public String getCombatsPage(Model model) {
        User user = getCurrentUser();
        if (user.getCurrentCombat() != null && user.getCurrentCombat().getStatus() == CombatStatus.ACTIVE) {
            return String.format("redirect:/combats/%d", user.getCurrentCombat().getId());
        } else {
            model.addAttribute("combatTypes", CombatType.values());
            model.addAttribute("newCombat", new Combat());
            model.addAttribute("combatsList", combatService.getAllNewCombats());
            model.addAttribute("isRegCombat", combatService.isRegBattle(user));
            model.addAttribute("user", user);
            return "combats";
        }
    }

    @PostMapping("/combats/new")
    public String CreateNewCombat(@ModelAttribute Combat combat) {
        User user = getCurrentUser();
        combat.setCreated(LocalDateTime.now());
        combat.setOwner(user);
        combat.setStepList(new ArrayList<>());
        combat.setStatus(CombatStatus.NEW);
        if (combat.getType() == null) {
            combat.setType(CombatType.ARM);
        }
        combatService.save(combat);
        user.setCurrentCombat(combat);
        userService.updateUser(user);
        return "redirect:../combats";
    }

    @PostMapping("/combats/{id}/join")
    public String joinToCombat(@PathVariable Long id) {
        User user = getCurrentUser();
        Combat combat = combatService.getCombatFromId(id);
        if (combat != null) {
            if (combat.getOwner() == user) {
                user.setCurrentCombat(null);
                combatService.delete(combat);
                userService.updateUser(user);
            } else {
                if (combat.getOpponent() == null) {
                    combat.setOpponent(user);
                    combat.setStatus(CombatStatus.ACTIVE);
                    combat.setCombatStart(LocalDateTime.now());
                    combatStepService.nextStep(combat);
                    user.setCurrentCombat(combat);
                    userService.updateUser(user);
                    return String.format("redirect:../../combats/%d", combat.getId());
                }
            }
        }
        return "redirect:../../combats";
    }

    @GetMapping("/combats/{id}")
    public String getCombatPage(@PathVariable Long id, Model model) {
        User owner, opponent;
        Combat combat = combatService.getCombatFromId(id);
        User user = getCurrentUser();
        if (combat.getOwner() == user) {
            owner = combat.getOwner();
            opponent = combat.getOpponent();
        } else {
            owner = combat.getOpponent();
            opponent = combat.getOwner();
        }
        Integer ownerPercent = owner.getHp() * 100 / owner.getMaxHp();
        Integer opponentPercent = opponent.getHp() * 100 / opponent.getMaxHp();
        Step step = new Step();
        boolean isDoStep;
        CombatStep combatStep = combatStepService.getCurrentCombatStep(combat);
        if (combatStep.getFighter1().equals(owner)) {
            isDoStep = combatStep.isMakeStepFighter1();
        } else {
            isDoStep = combatStep.isMakeStepFighter2();
        }
        model.addAttribute("step", step);
        model.addAttribute("combat", combat);
        model.addAttribute("owner", owner);
        model.addAttribute("ownerPercent", ownerPercent);
        model.addAttribute("opponent", opponent);
        model.addAttribute("opponentPercent", opponentPercent);
        model.addAttribute("isDoStep", isDoStep);
        return "combatPage";
    }

    @PostMapping("/combats/{id}")
    public String doStep(@ModelAttribute Step step, @PathVariable Long id) {
        Combat combat = combatService.getCombatFromId(id);
        CombatStep combatStep = combatStepService.getCurrentCombatStep(combat);
        combatStepService.addStepToCombatStep(combatStep, step);
        combatService.save(combat);
        return String.format("redirect:../combats/%d", id);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) userService.loadUserByUsername(auth.getName());
    }


}
