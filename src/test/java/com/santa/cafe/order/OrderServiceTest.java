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

    public static final int CUSTOMER_ID = 24264;
    @InjectMocks
    private OrderService subject;

    @Mock
    private CustomerService mockCustomerService;

    @Mock
    private OrderRepository mockOrderRepository;

    @Mock
    private MileageApiService mockMileageApiService;

    @Mock
    private BeverageRepository mockBeverageRepository;

    @Mock
    private OrderItemRepository mockOrderItemRepository;

    @Captor
    private ArgumentCaptor<Mileage> mileageCaptor;

    @Before
    public void setUp() {
        when(mockBeverageRepository.getOne(1)).thenReturn(new Beverage(1, "americano", 1000, BeverageSize.SMALL));
    }

    @Test
    public void 주문을하면_OrderItem들의_가격을_합한_TotalCost를_계산한다() {
        //given

        //when
        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("beverageId", 1);
        orderItem.put("count", 2);
        Order result = subject.create(CUSTOMER_ID, Collections.singletonList(orderItem), 1);

        //then
        assertThat(result.getTotalCost(), is(2000.0));
    }

    @Test
    public void 등록되지않은_음료가_OrderItem에_포함되면_해당금액은_TotalCost에서_제외한다() {
        //given
        when(mockBeverageRepository.getOne(2)).thenReturn(null);

        //when
        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("beverageId", 1);
        orderItem.put("count", 2);

        Map<String, Object> notValidOrderItem = new HashMap<>();
        notValidOrderItem.put("beverageId", 2);
        notValidOrderItem.put("count", 3);

        Order result = subject.create(CUSTOMER_ID, Arrays.asList(orderItem, notValidOrderItem), 1);

        //then
        assertThat(result.getTotalCost(), is(2000.0));
    }

    @Test
    public void 매월_마지막날에_주문하면_TotalCost에서_10퍼센트가_할인된다() {
    }

    @Test
    public void 현금으로_결제시_TotalCost의_10퍼센트를_마일리지로_적립한다() {
        //given

        //when
        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("beverageId", 1);
        orderItem.put("count", 2);

        int PAYMENT_CASH = 1;
        Order result = subject.create(CUSTOMER_ID, Collections.singletonList(orderItem), PAYMENT_CASH);

        //then
        assertThat(result.getMileagePoint(), is(200.0));
    }

    @Test
    public void 카드로_결제시_TotalCost의_5퍼센트를_마일리지로_적립한다() {
        //given

        //when
        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("beverageId", 1);
        orderItem.put("count", 2);

        int PAYMENT_CARD = 2;
        Order result = subject.create(CUSTOMER_ID, Collections.singletonList(orderItem), PAYMENT_CARD);

        //then
        assertThat(result.getMileagePoint(), is(100.0));
    }

    @Test
    public void 마일리지로_결제하는경우_고객의마일리지가_TotalCost보다_크거나같으면_마일리지를_차감한다() {
        //given
        when(mockMileageApiService.getMileages(CUSTOMER_ID)).thenReturn(3000);

        //when
        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("beverageId", 1);
        orderItem.put("count", 2);

        int PAYMENT_MILEAGE = 3;
        Order result = subject.create(CUSTOMER_ID, Collections.singletonList(orderItem), PAYMENT_MILEAGE);

        //then
        verify(mockMileageApiService, times(1)).minusMileages(eq(CUSTOMER_ID), mileageCaptor.capture());

        Mileage appliedMileage = mileageCaptor.getValue();
        assertThat(appliedMileage.getValue(), is(2000.0));
        assertThat(result.getMileagePoint(), is(0.0));
    }

    @Test(expected = BizException.class)
    public void 마일리지로_결제하는경우_고객의마일리지가_TotalCost보다_적으면_예외를_발생한다() {
        //given
        when(mockMileageApiService.getMileages(CUSTOMER_ID)).thenReturn(1000);

        //when
        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("beverageId", 1);
        orderItem.put("count", 2);

        int PAYMENT_MILEAGE = 3;
        subject.create(CUSTOMER_ID, Collections.singletonList(orderItem), PAYMENT_MILEAGE);

        //then
        verify(mockMileageApiService, never()).minusMileages(anyInt(), any());
    }

    @Test
    public void 마일리지로_결제하는경우_마일리지를_적립하지_않는다() {
        //given
        when(mockMileageApiService.getMileages(CUSTOMER_ID)).thenReturn(3000);

        //when
        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("beverageId", 1);
        orderItem.put("count", 2);

        int PAYMENT_MILEAGE = 3;
        subject.create(CUSTOMER_ID, Collections.singletonList(orderItem), PAYMENT_MILEAGE);

        //then
        verify(mockMileageApiService, never()).saveMileages(anyInt(), any());
    }

    @Test
    public void 마일리지로_결제하지않는경우_마일리지를_적립한다() {
        //given

        //when
        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("beverageId", 1);
        orderItem.put("count", 2);

        int PAYMENT_MILEAGE = 1;
        Order result = subject.create(CUSTOMER_ID, Collections.singletonList(orderItem), PAYMENT_MILEAGE);

        //then
        verify(mockMileageApiService, times(1)).saveMileages(eq(CUSTOMER_ID), mileageCaptor.capture());

        Mileage appliedMileage = mileageCaptor.getValue();
        assertThat(appliedMileage.getValue(), is(200.0));
    }
}