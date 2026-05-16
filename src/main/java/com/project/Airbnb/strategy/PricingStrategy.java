package com.project.Airbnb.strategy;

import com.project.Airbnb.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy  {
    BigDecimal calculatePrice(Inventory inventory);
}
