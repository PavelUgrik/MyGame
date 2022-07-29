package ru.laurelin.game.models.combat;

import lombok.*;
import ru.laurelin.game.models.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "combats")
public class Combat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private User owner;
    @OneToOne
    private User opponent;
    @Enumerated(value = EnumType.STRING)
    private CombatStatus status;
    @Enumerated(value = EnumType.STRING)
    private CombatType type;
    private LocalDateTime created;
    private LocalDateTime combatStart;
    private LocalDateTime finished;
    @OneToOne
    private CombatStep currentStep;
    @OneToMany
    @JoinColumn(name = "combat_id")
    private List<CombatStep> stepList;
    @OneToOne
    private User winner;

}
