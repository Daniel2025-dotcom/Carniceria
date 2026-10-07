package org.example.controller.dao;
import org.example.models.Product;
import java.util.List;

public interface ProductDAO {
    void create(Product product);
    void update(Product product);
    void delete(String code);
    Product getByCode(String code);
    List<Product> getAll();
    void updateStock(String code, float quantitySold);
}