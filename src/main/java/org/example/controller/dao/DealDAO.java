package org.example.controller.dao;
import org.example.models.Deal;

import java.util.List;

public interface DealDAO {
    int registerDeal(Deal deal);
    List<Deal> getAllDeals();
}