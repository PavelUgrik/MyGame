package ru.laurelin.game.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.laurelin.game.models.Equipment;
import ru.laurelin.game.repositories.EquipmentRepository;

import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository repository;

    @Autowired
    public EquipmentService(EquipmentRepository repository) {
        this.repository = repository;
    }


    public List<Equipment> getAllEquipments() {
        return repository.findAllByOrderByIdAsc();
    }

    public void saveEquipment(Equipment equipment) {
        repository.save(equipment);
    }

    public Equipment getEquipmentById(Long id) {
        return repository.getReferenceById(id);
    }

    public boolean updateEquipment(Equipment equipment, Long id) {
        Equipment equipmentFromDb = getEquipmentById(id);
        if (equipmentFromDb != null) {
            equipmentFromDb.setSlot(equipment.getSlot());
            equipmentFromDb.setName(equipment.getName());
            equipmentFromDb.setArmor(equipment.getArmor());
            equipmentFromDb.setDamage(equipment.getDamage());
            equipmentFromDb.setPrice(equipment.getPrice());
            repository.save(equipmentFromDb);
            return true;
        }
        return false;
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
