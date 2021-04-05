package com.santa.cafe.order;

import com.santa.cafe.api.mileage.Mileage;
import com.santa.cafe.api.mileage.MileageApiService;
import com.santa.cafe.beverage.BeverageRepository;
import com.santa.cafe.beverage.model.Beverage;
import com.santa.cafe.beverage.model.BeverageSize;
import com.santa.cafe.customer.CustomerService;
import com.santa.cafe.exception.BizException;
import com.santa.cafe.order.model.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    private final int CUSTOMER_ID = 12345;
    private final int PAYMENT_CASH = 1;
    private final int AMERICANO_ID = 1;
    private final int NOT_EXIST_BEVERAGE_ID = 2;

    private TestableOrderService subject;

    @Mock
    private OrderRepository mockOrderRepository;
    @Mock
    private MileageApiService mockMileageApiService;
    @Mock
    private CustomerService mockCustomerService;
    @Mock
    private BeverageRepository mockBeverageRepository;
    @Mock
    private OrderItemRepository mockOrderItemRepository;

    private boolean isLastDayOfMonthToday = false;

    @Before
    public void setUp() throws Exception {
        subject = new TestableOrderService(mockOrderRepository, mockMileageApiService, mockCustomerService, mockBeverageRepository, mockOrderItemRepository);
        when(mockBeverageRepository.getOne(AMERICANO_ID)).thenReturn(new Beverage(AMERICANO_ID, "americano", 1000, BeverageSize.SMALL));
    }

    @Test
    public void 주문을하면_OrderItem들의_가격을_합한_TotalCost를_계산한다() {
        //given

        //when
        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("beverageId", AMERICANO_ID);
        orderItem.put("count", 2);
        Order result = subject.create(CUSTOMER_ID, Collections.singletonList(orderItem), PAYMENT_CASH);

        //then
        assertThat(result.getTotalCost(), is(2000.0));
    }

    @Test
    public void 등록되지않은_음료가_OrderItem에_포함되면_해당금액은_TotalCost에서_제외한다() {
        //given
        when(mockBeverageRepository.getOne(NOT_EXIST_BEVERAGE_ID)).thenReturn(null);

        //when
        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("beverageId", AMERICANO_ID);
        orderItem.put("count", 2);

        Map<String, Object> notValidOrderItem = new HashMap<>();
        notValidOrderItem.put("beverageId", NOT_EXIST_BEVERAGE_ID);
        notValidOrderItem.put("count", 2);

        Order result = subject.create(CUSTOMER_ID, Arrays.asList(orderItem, notValidOrderItem), PAYMENT_CASH);

        assertThat(result.getTotalCost(), is(2000.0));
    }

    @Test
    public void 매월_마지막날에_주문하면_TotalCost에서_10퍼센트가_할인된다() {
        //given
        isLastDayOfMonthToday = true;

        //when
        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("beverageId", AMERICANO_ID);
        orderItem.put("count", 2);

        Order result = subject.create(CUSTOMER_ID, Collections.singletonList(orderItem), PAYMENT_CASH);

        //then
        assertThat(result.getTotalCost(), is(1800.0));

        isLastDayOfMonthToday = false;
    }

    @Test
    public void 현금으로_결제시_TotalCost의_10퍼센트를_마일리지로_적립한다() {
    }

    @Test
    public void 카드로_결제시_TotalCost의_5퍼센트를_마일리지로_적립한다() {
    }

    @Test
    public void 마일리지로_결제하는경우_고객의마일리지가_TotalCost보다_크거나같으면_마일리지API를_호출하여_마일리지를_차감한다() {
    }

    @Test
    public void 마일리지로_결제하는경우_고객의마일리지가_TotalCost보다_적으면_예외를_발생한다() {
    }

    @Test
    public void 마일리지로_결제하는경우_마일리지API를_호출하여_마일리지를_적립하지_않는다() {
    }

    @Test
    public void 마일리지로_결제하지않는경우_마일리지API를_호출하여_마일리지를_적립한다() {
    }

    class TestableOrderService extends OrderService {

        public TestableOrderService(OrderRepository orderRepository, MileageApiService mileageApiService, CustomerService customerService, BeverageRepository beverageRepository, OrderItemRepository orderItemRepository) {
            super(orderRepository, mileageApiService, customerService, beverageRepository, orderItemRepository);
        }

        @Override
        protected boolean isLastDayOfMonth() {
            return isLastDayOfMonthToday;
        }
    }
}