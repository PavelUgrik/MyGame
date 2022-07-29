package ru.laurelin.game.models.combat;

public enum CombatType {
    ARM("кулачный"), WEAPON("с оружием");

    private final String type;

    CombatType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
