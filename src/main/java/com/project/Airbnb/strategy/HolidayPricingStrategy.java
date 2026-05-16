package com.project.Airbnb.strategy;

import com.project.Airbnb.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy {
    private final PricingStrategy wrapped;
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        boolean isTodayHoliday=true; //call an API or check with local date
        if (isTodayHoliday)
        {
            price=price.multiply(BigDecimal.valueOf(1.25));
        }
        return price;
    }
}
