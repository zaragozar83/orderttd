package com.coffee.ordertdd;

import java.util.Date;

public class Checkout extends Cart{

  private Float paymentAmount = 0F;
  private Float paymentDue = 0F;

  private Date paymentDate;

  public enum PaymentStatus{
    DUE, DONE
  }

  private PaymentStatus paymentStatus;

  public Float getPaymentDue() {
    return paymentDue;
  }

  public PaymentStatus getPaymentStatus() {
    return paymentStatus;
  }

  public void pay(Float payment) {
    paymentAmount = payment;
    paymentDue = getTotalAmount() - paymentAmount;
    paymentDate = new Date();
  }

  public void confirmOrder() {
    if (paymentDue == 0.0F) {
      System.out.println("Payment Successful! Thank you for your order.");
      paymentStatus = PaymentStatus.DONE;
    } else if (paymentDue > 0) {
      System.out.printf("Payment Failed! Remaining $%f needs to be paid.", paymentDue);
      paymentStatus = PaymentStatus.DUE;
    }
  }

  public void complete() {
    printCartDetails();
    confirmOrder();
  }

}
