package com.coffee.ordertdd.service;

import com.coffee.ordertdd.model.Order;
import com.coffee.ordertdd.repository.OrderRepository;
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.AssertionErrors;

@SpringBootTest
public class OrderServiceTest {

  @Autowired
  private OrderService service;

  @MockBean
  private OrderRepository repository;

  @Test
  @DisplayName("Find order by id successfully")
  void testFindOrderById() {
    Order mockOrder = Order.builder().id(1).name("Order").description("Description").quantity(10).version(1).build();

    Mockito.doReturn(mockOrder).when(repository).findOrderById(1);

    Order foundOrder = repository.findOrderById(1);

    Assertions.assertNotNull(foundOrder);
    Assertions.assertEquals("Order", foundOrder.getName());
  }


  @Test
  @DisplayName("Fail to find Order with id")
  void testFailToFindOrderById() {

    Mockito.doReturn(null).when(repository).findOrderById(1);

    Order foundOrder = repository.findOrderById(1);

    Assertions.assertNull(foundOrder);
  }

  @Test
  @DisplayName("Find all Orders")
  void findAllOrders() {
    Order firstOrder = Order.builder()
        .id(1)
        .name("First Order")
        .description("First Order description")
        .quantity(8)
        .version(1)
        .build();

    Order secondOrder = Order.builder()
        .id(2)
        .name("Second Order")
        .description("Second Order description")
        .quantity(10)
        .version(1)
        .build();

    Mockito.doReturn(Arrays.asList(firstOrder, secondOrder)).when(repository).findAll();

    Iterable<Order> allOrders = repository.findAll();

    Assertions.assertEquals(2, ((Collection<?>)allOrders).size());

  }

  @Test
  @DisplayName("Save new Order seccessfully")
  void saveOrder() {

    Order mockOrder = Order.builder().id(1).name("Order").description("Description").quantity(10).version(1).build();

    Mockito.doReturn(mockOrder).when(repository).save(ArgumentMatchers.any());

    Order orderSaved = repository.save(mockOrder);

    AssertionErrors.assertNotNull("Order should not be null", orderSaved);
    Assertions.assertEquals("Order", orderSaved.getName());
    Assertions.assertEquals(1, orderSaved.getId());
  }

  @Test
  @DisplayName("Update an existing Order successfully")
  public void testUpdatingOrderSuccessfully(){
    Order existingOrder = Order.builder().id(1).name("Order").description("Description").quantity(10).version(1).build();
    Order updatedOrder = Order.builder().id(1).name("New Name").description("Description").quantity(20).version(2).build();

    Mockito.doReturn(existingOrder).when(repository).findOrderById(1);
    Mockito.doReturn(updatedOrder).when(repository).save(existingOrder);

    Order updateOrder = service.update(existingOrder);

    Assertions.assertEquals("New Name", updateOrder.getName());
  }

  @Test
  @DisplayName("Fail to update an existing Order")
  public void testFailToUpdateExistingOrder(){
    Order mockOrder = Order.builder().id(1).name("Order").description("Description").quantity(10).version(1).build();

    Mockito.doReturn(null).when(repository).findOrderById(1);

    Order updatedOrder = service.update(mockOrder);

    AssertionErrors.assertNull("Order should be null", updatedOrder);
  }
}
