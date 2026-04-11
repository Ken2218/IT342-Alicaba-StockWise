package edu.cit.alicaba.stockwise.repository;

import edu.cit.alicaba.stockwise.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    boolean existsBySku(String sku);
}