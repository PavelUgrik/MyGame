package ru.laurelin.game.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.laurelin.game.models.combat.Combat;
import ru.laurelin.game.models.combat.CombatStatus;
import ru.laurelin.game.models.user.User;

import java.util.List;

@Repository
public interface CombatRepository extends JpaRepository<Combat, Long> {
    List<Combat> findAllByStatus(CombatStatus status);

    boolean existsByOwner(User user);
}
