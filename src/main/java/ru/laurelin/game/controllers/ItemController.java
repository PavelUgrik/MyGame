package ru.laurelin.game.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.laurelin.game.models.Equipment;
import ru.laurelin.game.models.ItemType;
import ru.laurelin.game.models.user.Slot;
import ru.laurelin.game.services.EquipmentService;

@Controller
@RequestMapping("/ap/items")
public class ItemController {

    private final EquipmentService equipmentService;

    @Autowired
    public ItemController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping()
    public String getItemsPage(Model model) {
        model.addAttribute("itemsList", equipmentService.getAllEquipments());
        return "ap/itemPage";
    }

    @GetMapping("/new")
    public String getNewEquipmentPage(Model model) {
        model.addAttribute("gameItem", new Equipment());
        model.addAttribute("slotsList", Slot.values());
        model.addAttribute("itemTypesList", ItemType.values());
        return "ap/newItem";
    }

    @PostMapping("/new")
    public String addNewEquipment(@ModelAttribute Equipment gameItem, Model model) {
        equipmentService.saveEquipment(gameItem);
        return "redirect:../items";
    }

    @GetMapping("/{id}/edit")
    public String getItemEditPage(@PathVariable Long id, Model model) {
        Equipment equipment = equipmentService.getEquipmentById(id);
        model.addAttribute("gameItem", equipment);
        model.addAttribute("slotsList", Slot.values());
        model.addAttribute("itemTypesList", ItemType.values());
        return "ap/editItem";
    }

    @PostMapping("/{id}/update")
    public String editItem(@ModelAttribute Equipment gameItem, @PathVariable Long id, Model model) {
        if (equipmentService.updateEquipment(gameItem, id))
            return "redirect:../../items";
        else {
            model.addAttribute("gameItem", gameItem);
            model.addAttribute("slotsList", Slot.values());
            model.addAttribute("itemTypesList", ItemType.values());
            return "ap/editItem";
        }

    }

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id) {
        equipmentService.deleteById(id);
        return "redirect:../../items";
    }

}
