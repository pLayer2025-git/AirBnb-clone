package com.project.Airbnb.strategy;

import com.project.Airbnb.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
//DECORATOR DESIGN PATTERN
@Service
public class PricingService {
    public BigDecimal calculateDynamicPricing(Inventory inventory)
    {
        PricingStrategy pricingStrategy=new BasePricingStrategy();
        pricingStrategy=new SurgePricingStrategy(pricingStrategy);
        pricingStrategy=new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy=new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy=new HolidayPricingStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);
    }
}
