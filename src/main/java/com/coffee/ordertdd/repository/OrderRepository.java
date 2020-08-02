package com.coffee.ordertdd.repository;

import com.coffee.ordertdd.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Integer> {

  Order findOrderById(Integer id);
}
