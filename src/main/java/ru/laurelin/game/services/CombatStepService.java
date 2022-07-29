package ru.laurelin.game.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.laurelin.game.models.combat.Combat;
import ru.laurelin.game.models.combat.CombatStatus;
import ru.laurelin.game.models.combat.CombatStep;
import ru.laurelin.game.models.combat.Step;
import ru.laurelin.game.models.user.User;
import ru.laurelin.game.repositories.CombatStepRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class CombatStepService {

    private final static Long COMBAT_STEP_TIME = 60L;

    private final CombatStepRepository combatStepRepository;
    private final CombatService combatService;
    private final UserService userService;

    @Autowired
    public CombatStepService(CombatStepRepository stepRepository, CombatService combatService, UserService userService) {
        this.combatStepRepository = stepRepository;
        this.combatService = combatService;
        this.userService = userService;
    }

    public void addStepToCombatStep(CombatStep combatStep, Step step) {
        User user = userService.getUserById(step.getAttacker_id());
        if (combatStep.getFighter1().equals(user)) {
            if (!combatStep.isMakeStepFighter1()) {
                combatStep.setAttack1(step.getAttack());
                combatStep.setDefense1(step.getDefense());
                combatStep.setMakeStepFighter1(true);
            }
        } else {
            if (!combatStep.isMakeStepFighter2()) {
                combatStep.setAttack2(step.getAttack());
                combatStep.setDefense2(step.getDefense());
                combatStep.setMakeStepFighter2(true);
            }
        }
        updateCombatStep(combatStep);
    }

    private void doCombatStep(CombatStep combatStep) {
        User fighter1 = combatStep.getFighter1();
        User fighter2 = combatStep.getFighter2();
        Combat combat = combatStep.getCombat();
        int modHp;

        if (combatStep.getAttack1() != null && !combatStep.getAttack1().equals(combatStep.getDefense2())) {
            int str1 = fighter1.getStrength();
            int hp2 = fighter2.getHp();
            modHp = Math.min(str1, hp2);
            fighter2.setHp(hp2 - modHp);
            fighter2.setLastHit(LocalTime.now());
        }
        if (combatStep.getAttack2() != null && !combatStep.getAttack2().equals(combatStep.getDefense1())) {
            int str2 = fighter2.getStrength();
            int hp1 = fighter1.getHp();
            modHp = Math.min(str2, hp1);
            fighter1.setHp(hp1 - modHp);
            fighter1.setLastHit(LocalTime.now());
        }

        if (fighter1.getHp() < 1 || fighter2.getHp() < 1) {
            combat.setStatus(CombatStatus.FINISHED);
            combat.setFinished(LocalDateTime.now());
            if (fighter1.getHp() < 1 && fighter2.getHp() > 0) {
                combat.setWinner(fighter2);
            }
            if (fighter2.getHp() < 1 && fighter1.getHp() > 0) {
                combat.setWinner(fighter1);
            }
        }
        combatService.updateCombat(combat);
    }

    public void saveCombatStep(CombatStep combatStep) {
        combatStepRepository.save(combatStep);
    }

    public void updateCombatStep(CombatStep combatStep) {
        CombatStep stepFromDB = combatStepRepository.getReferenceById(combatStep.getId());
        stepFromDB.setAttack1(combatStep.getAttack1());
        stepFromDB.setDefense1(combatStep.getDefense1());
        stepFromDB.setMakeStepFighter1(combatStep.isMakeStepFighter1());
        stepFromDB.setAttack2(combatStep.getAttack2());
        stepFromDB.setDefense2(combatStep.getDefense2());
        stepFromDB.setMakeStepFighter2(combatStep.isMakeStepFighter2());
        combatStepRepository.save(stepFromDB);
    }

    public CombatStep nextStep(Combat combat) {
        CombatStep step = new CombatStep();
        step.setStartStep(LocalDateTime.now());
        step.setCombat(combat);
        step.setFighter1(combat.getOwner());
        step.setFighter2(combat.getOpponent());
        step.setMakeStepFighter1(false);
        step.setMakeStepFighter2(false);
        combatStepRepository.save(step);
        combat.setCurrentStep(step);
        combatService.save(combat);
        return step;
    }

    public CombatStep getCurrentCombatStep(Combat combat) {
        CombatStep combatStep = combat.getCurrentStep();
        if (LocalDateTime.now().isAfter(combatStep.getStartStep().plusSeconds(COMBAT_STEP_TIME)) || (combatStep.isMakeStepFighter1() && combatStep.isMakeStepFighter2())) {
            doCombatStep(combatStep);
            combatStep = nextStep(combat);
        }
        return combatStep;
    }
}
