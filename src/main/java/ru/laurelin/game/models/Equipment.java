package ru.laurelin.game.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.laurelin.game.models.user.Slot;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "equipment")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;
    @Enumerated(value = EnumType.STRING)
    private ItemType itemType;
    @Enumerated(value = EnumType.STRING)
    private Slot slot;
    private Integer armor;
    private Integer damage;
}
