package ru.laurelin.game.models.combat;

import lombok.Data;

@Data
public class Step {
    private Long attacker_id;
    private Zone attack;
    private Zone defense;
}
