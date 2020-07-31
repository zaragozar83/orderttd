package com.coffee.ordertdd;

public class Shopping {

  public static void main(String[] args) {

    Product firstProduct = Product.builder()
                                  .id(1)
                                  .name("USB Drive")
                                  .description("128 GB USB Drive")
                                  .price(19.9F)
                                  .build();
    Product secondProduct = Product.builder()
                                  .id(2)
                                  .name("External Hard Drive")
                                  .description("1 TB External Drive")
                                  .price(79.9F)
                                  .build();

    Checkout checkout = new Checkout();
    checkout.addToCart(firstProduct);
    checkout.addToCart(secondProduct);

    checkout.pay(90F);
    checkout.complete();
  }
}
