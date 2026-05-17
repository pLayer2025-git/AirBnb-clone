package com.project.Airbnb.service;

import com.project.Airbnb.entity.Hotel;
import com.project.Airbnb.entity.HotelMinPrice;
import com.project.Airbnb.entity.Inventory;
import com.project.Airbnb.repository.HotelMinPriceRepository;
import com.project.Airbnb.repository.HotelRepository;
import com.project.Airbnb.repository.InventoryRepository;
import com.project.Airbnb.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateService {
    //scheduler to update the inventory and HotelMinPrice tables every hour
    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;

    private final PricingService pricingService;
    //@Scheduled(cron = "*/9 * * * * *")
    @Scheduled(cron = "0 0 * * * *")// every hour at 0 min and 0 sec update this. learn from cron expression cron hub .
    public void updatePrices() {
    int page=0;
    int batchSize=100;
    while (true)
    {
        Page<Hotel> hotelPage=hotelRepository.findAll(PageRequest.of(page,batchSize));
        if (hotelPage.isEmpty()){
            break;
        }
        hotelPage.getContent().forEach(this::updateHotelPrices);
        page++;
    }
    }

    private void updateHotelPrices(Hotel hotel) {
        LocalDate startDate=LocalDate.now();
        LocalDate endDate=LocalDate.now().plusYears(1);

        List<Inventory> inventoryList=inventoryRepository.findByHotelAndDateBetween(hotel,startDate,endDate);

        updateInventoryPrices(inventoryList);

        updateHotelMinPrices(hotel,inventoryList,startDate,endDate);

    }

    private void updateInventoryPrices(List<Inventory> inventoryList) {
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice=pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAll(inventoryList);
    }

    private void updateHotelMinPrices(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {
        // Compute minimum price per day for the hotel
        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().orElse(BigDecimal.ZERO)));

        // Prepare HotelPrice entities in bulk
        List<HotelMinPrice> hotelPrices = new ArrayList<>();
        dailyMinPrices.forEach((date, price) -> {
            HotelMinPrice hotelPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date)
                    .orElse(new HotelMinPrice(hotel, date));
            hotelPrice.setPrice(price);
            hotelPrices.add(hotelPrice);
        });

        // Save all HotelPrice entities in bulk
        hotelMinPriceRepository.saveAll(hotelPrices);
    }
}
