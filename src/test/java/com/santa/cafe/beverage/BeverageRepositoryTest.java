package com.santa.cafe.beverage;

import com.santa.cafe.beverage.model.Beverage;
import com.santa.cafe.beverage.model.BeverageSize;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BeverageRepositoryTest {
    @Autowired
    private BeverageRepository beverageRepository;

    private Beverage americano;
    private Beverage latte;
    private Beverage coldBrew;

    @Before
    public void setUp() {
       // createBeverages();
    }

    @Test
    public void whenFindByCostGreaterThan_thenReturnBeveragesGT100() {
        List<Beverage> onlyColdBrew = beverageRepository.findByCostGreaterThan(100);

        assertThat(onlyColdBrew.size()).isEqualTo(1);
        assertThat(onlyColdBrew.get(0).getName()).isEqualTo("cold brew");
    }

    @Test
    public void whenSaveBeverages_thenReturnCreatedBeverages() {
        List<Beverage> newBeverages = beverageRepository.findAll();

        assertThat(newBeverages.size()).isEqualTo(3);
        assertThat(newBeverages.get(0).getName()).isEqualTo("americano");
        assertThat(newBeverages.get(1).getName()).isEqualTo("latte");
        assertThat(newBeverages.get(2).getName()).isEqualTo("cold brew");
    }

    private List<Beverage> createBeverages() {
        americano = new Beverage("americano", 50, BeverageSize.SMALL);
        latte = new Beverage("latte", 100, BeverageSize.REGULAR);
        coldBrew = new Beverage("cold brew", 200, BeverageSize.REGULAR);

        return beverageRepository.saveAll(Lists.newArrayList(americano, latte, coldBrew));
    }
}