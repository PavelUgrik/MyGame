package ru.laurelin.game.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.laurelin.game.models.combat.Combat;
import ru.laurelin.game.models.combat.CombatStatus;
import ru.laurelin.game.models.user.User;
import ru.laurelin.game.repositories.CombatRepository;

import java.util.List;

@Service
public class CombatService {

    private final CombatRepository combatRepository;

    @Autowired
    public CombatService(CombatRepository combatRepository) {
        this.combatRepository = combatRepository;
    }

    public void save(Combat combat) {
        combatRepository.save(combat);
    }

    public List<Combat> getAll() {
        return combatRepository.findAll();
    }

    public List<Combat> getAllNewCombats() {
        return combatRepository.findAllByStatus(CombatStatus.NEW);
    }

    public boolean isRegBattle(User user) {
        return combatRepository.existsByOwner(user);
    }

    public Combat getCombatFromId(Long id) {
        return combatRepository.getReferenceById(id);
    }

    public void delete(Combat combat) {
        combatRepository.delete(combat);
    }

    public void updateCombat(Combat combat) {
        Combat combatFromDb = combatRepository.getReferenceById(combat.getId());
        combatFromDb.setStatus(combat.getStatus());
        combatFromDb.setWinner(combat.getWinner());
        combatFromDb.setFinished(combat.getFinished());
        combatRepository.save(combatFromDb);
    }
}
