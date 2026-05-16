package com.project.Airbnb.strategy;

import com.project.Airbnb.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@RequiredArgsConstructor
public class OccupancyPricingStrategy implements PricingStrategy {
    private final PricingStrategy wrapped;
    //@REQARGSCONS WILL GENERATE PARAMETRIZED CONSTRUCTOR WITH PARAMETER pricing Strategy wrap object
    // each param passed inside PricingService class will store calling method parameter

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price=wrapped.calculatePrice(inventory);
        double occupancyRate=(double) inventory.getBookedCount()/inventory.getTotalCount();
        if(occupancyRate > 0.8) {
            price = price.multiply(BigDecimal.valueOf(1.2));
        }
        return price;

    }
}
