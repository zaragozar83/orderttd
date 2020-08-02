package com.coffee.ordertdd.controller;

import com.coffee.ordertdd.model.Order;
import com.coffee.ordertdd.service.OrderService;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/orders")
public class OrdersController {

  @Autowired
  private OrderService orderService;

  /**
   * Gets all the orders in repository
   * @return Iterable list of all Orders
   */
  @GetMapping
  public Iterable<Order> getAllProducts() {
    return orderService.findAll();
  }

  /**
   * Get the Order with specified ID
   * @param id ID of the Order to get
   * @return ResponseEntity with the found Order
   *         or NOT_FOUND if no Order found
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getOrder(@PathVariable Integer id) {

    Order order = orderService.findById(id);
    if (order != null) {
      try {
        return ResponseEntity
            .ok()
            .eTag(Integer.toString(order.getId()))
            .location(new URI("/orders/" + order.getId()))
            .body(order);
      } catch (URISyntaxException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Saves a new Order
   * @param order Order to save
   * @return ResponseEntity with the saved Order
   */
  @PostMapping
  public ResponseEntity<?> saveOrder(@RequestBody Order order) {
    log.info("Adding new order with name {}", order.getName());
    Order newOrder = orderService.save(order);
    try {
      return ResponseEntity
              .created(new URI("/orders/" + newOrder.getId()))
              .eTag(Integer.toString(newOrder.getId()))
              .body(newOrder);
    } catch (URISyntaxException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Update an existing Order
   * @param order Order to update
   * @param ifMatch eTag version of the Order to update
   * @return ResponseEntity with the updated Order
   *         or CONFLICT if eTag versions do not match
   */
  @PutMapping("/{id}")
  public ResponseEntity<?> updateOrder(@PathVariable Integer id,
                                         @RequestBody Order order,
                                         @RequestHeader("If-Match") Integer ifMatch) {
    Order existingOrder = orderService.findById(id);
    if(existingOrder == null) {
      return ResponseEntity.notFound().build();
    } else {
      if(!existingOrder.getVersion().equals(ifMatch)) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
      } else {
        log.info("Updating order with name:{}", existingOrder.getName());

        existingOrder.setName(order.getName());
        existingOrder.setDescription(order.getDescription());
        existingOrder.setQuantity(order.getQuantity());
        existingOrder.setVersion(existingOrder.getVersion() + 1);

        try{
          existingOrder = orderService.update(existingOrder);
          return ResponseEntity
              .ok()
              .eTag(Integer.toString(existingOrder.getVersion()))
              .location(new URI("/orders/" + existingOrder.getId()))
              .body(existingOrder);
        } catch (URISyntaxException e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
      }
    }
  }

  /**
   * Delete an existing Product with given id
   * @param id Product id to delete
   * @return ResponseEntity with HTTP status
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {

    log.info("Deleting product with id:{}", id);

    Order existingOrder = orderService.findById(id);
    if(existingOrder != null) {
      orderService.delete(id);
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

}
