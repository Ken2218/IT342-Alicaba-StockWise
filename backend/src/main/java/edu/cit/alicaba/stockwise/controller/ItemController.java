package edu.cit.alicaba.stockwise.controller;

import edu.cit.alicaba.stockwise.entity.Item;
import edu.cit.alicaba.stockwise.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addItem(@RequestBody Item item) {
        // Removed the SKU duplicate check. Just save the item!
        Item savedItem = itemRepository.save(item);
        return ResponseEntity.ok(savedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        if (!itemRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found.");
        }
        itemRepository.deleteById(id);
        return ResponseEntity.ok("Item deleted successfully.");
    }
}