package com.coffee.ordertdd.service;

import com.coffee.ordertdd.model.Order;
import com.coffee.ordertdd.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class OrderService {

  @Autowired
  private OrderRepository orderRepository;

  public Iterable<Order> findAll() {
    return orderRepository.findAll();
  }

  public Order findById(Integer id) {

    log.info("Finding order by id:{}", id);
    return orderRepository.findOrderById(id);
  }

  public Order save(Order order) {

    log.info("Saving new order with name:{}", order.getName());
    return orderRepository.save(order);
  }

  public Order update(Order order) {

    log.info("Updating order with id:{}", order.getId());
    Order existingOrder = orderRepository.findOrderById(order.getId());

    if (existingOrder != null) {
      existingOrder.setName(order.getName());
      existingOrder.setDescription(order.getDescription());
      existingOrder.setQuantity(order.getQuantity());
      existingOrder = orderRepository.save(existingOrder);
    } else {
      log.error("Order with id {} could not be updated!", order.getId());
    }

    return existingOrder;
  }

  public void delete(Integer id) {

    log.info("Deleting order with id:{}", id);

    Order existingOrder = orderRepository.findOrderById(id);
    if(existingOrder != null) {
      orderRepository.delete(existingOrder);
    } else {
      log.error("Order with id {} could not be found!", id);
    }
  }
}
