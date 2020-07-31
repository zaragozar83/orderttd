package com.coffee.ordertdd;

import java.util.ArrayList;
import java.util.List;

public class Cart {

  private Float totalAmount = 0F;

  private List<Product> items = new ArrayList<Product>();

  public void addToCart(Product p) {
    items.add(p);
    totalAmount = totalAmount + p.getPrice();
  }

  public void removeFromCart(Product p) {
    items.remove(p);
    totalAmount = totalAmount - p.getPrice();
  }

  public void emptyCart() {
    items.clear();
  }

  public List<Product> getItems() {
    return items;
  }

  public Float getTotalAmount () {
    return totalAmount;
  }

  public void printCartDetails() {

    System.out.println("Here are the items in your shopping cart:");
    for (Product p : items) {
      System.out.println("===================");
      System.out.println("ID: " + p.getId());
      System.out.println("Name: " + p.getName());
      System.out.println("Description: " + p.getDescription());
      System.out.println("Price: " + p.getPrice());
      System.out.println("===================");
    }
    System.out.println("Shopping Cart Total: $" + totalAmount);
  }

}
