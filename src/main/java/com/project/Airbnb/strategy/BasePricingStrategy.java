package com.project.Airbnb.strategy;

import com.project.Airbnb.entity.Inventory;

import java.math.BigDecimal;

public class BasePricingStrategy implements PricingStrategy{


    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
         return inventory.getRoom().getBasePrice();
    }
}
