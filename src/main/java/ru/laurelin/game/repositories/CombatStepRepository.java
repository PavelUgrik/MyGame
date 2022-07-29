package ru.laurelin.game.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.laurelin.game.models.combat.CombatStep;

@Repository
public interface CombatStepRepository extends JpaRepository<CombatStep, Long> {
}
