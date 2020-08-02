package com.coffee.ordertdd.repository;

import com.coffee.ordertdd.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OrderRepositoryTest {

  @Autowired
  private OrderRepository repository;

  private static final File DATA_JSON = Paths.get("src","test", "resources","orders.json").toFile();

  @BeforeEach
  public void setup() throws IOException {
    // Deserialize orders from JSON file to Order array
    Order[] orders = new ObjectMapper().readValue(DATA_JSON, Order[].class);

    // Save each order to database
    Arrays.stream(orders).forEach(repository::save);
  }

  @AfterEach
  public void cleanup() {
    repository.deleteAll();
  }

  @Test
  @DisplayName("Test order not found with non-existing id")
  public void testOrderNotFoundForNonExistingId(){
    // Given two orders in the database

    // When
    Order retrievedOrder = repository.findOrderById(100);

    // Then
    Assertions.assertNull(retrievedOrder, "Order with id 100 should not exist");
  }

  @Test
  @DisplayName("Test order saved successfully")
  public void testProductSavedSuccessfully(){
    // Prepare mock order
    Order newOrder = Order.builder().name("New Order").description("New Order Description").quantity(8).build();

    // When
    Order savedOrder = repository.save(newOrder);

    // Then
    Assertions.assertNotNull(savedOrder, "Order should be saved");
    Assertions.assertNotNull(savedOrder.getId(), "Order should have an id when saved");
    Assertions.assertEquals(newOrder.getName(), savedOrder.getName());
  }

  @Test
  @DisplayName("Test order updated successfully")
  public void testOrderUpdatedSuccessfully(){
    // Prepare the order
    Order orderToUpdate = Order.builder().id(1).name("Updated Order").description("New Order Description").quantity(20).version(2).build();

    // When
    Order updatedOrder = repository.save(orderToUpdate);

    // Then
    Assertions.assertEquals(orderToUpdate.getName(), updatedOrder.getName());
    Assertions.assertEquals(2, updatedOrder.getVersion());
    Assertions.assertEquals(20, updatedOrder.getQuantity());
  }

  @Test
  @DisplayName("Test order deleted successfully")
  public void testOrderDeletedSuccessfully(){
    // Given two orders in the database


    // When
    repository.deleteById(1);

    // Then
    Assertions.assertEquals(1L, repository.count());
  }

}
