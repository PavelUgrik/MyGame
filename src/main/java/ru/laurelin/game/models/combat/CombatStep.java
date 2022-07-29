package ru.laurelin.game.models.combat;

import lombok.Getter;
import lombok.Setter;
import ru.laurelin.game.models.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Setter
@Getter
@Entity
@Table(name = "steps")
public class CombatStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Combat combat;

    private LocalDateTime startStep;

    @OneToOne
    private User fighter1;
    @Enumerated(value = EnumType.STRING)
    private Zone attack1;
    @Enumerated(value = EnumType.STRING)
    private Zone defense1;
    private boolean isMakeStepFighter1;

    @OneToOne
    private User fighter2;
    @Enumerated(value = EnumType.STRING)
    private Zone attack2;
    @Enumerated(value = EnumType.STRING)
    private Zone defense2;
    private boolean isMakeStepFighter2;
}
