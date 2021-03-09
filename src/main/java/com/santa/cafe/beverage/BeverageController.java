package com.santa.cafe.beverage;

import com.santa.cafe.beverage.model.Beverage;
import com.santa.cafe.beverage.model.BeverageSize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RequestMapping(value = "/api/v1/beverages")
@RestController
public class BeverageController {
    private final BeverageService beverageService;

    public BeverageController(BeverageService beverageService) {
        this.beverageService = beverageService;
    }

    @GetMapping
    public List<Beverage> getBeverages() {
        return beverageService.getBeverages();
    }

    @PostMapping
    public List<Beverage> createBeverages(@RequestBody List<Beverage> beverages) {
        return beverageService.createBeverages(beverages);
    }
}
