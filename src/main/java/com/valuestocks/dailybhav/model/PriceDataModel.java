package com.valuestocks.dailybhav.model;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class PriceDataModel {

  private final String exchange;
  private ObjectArrayList<Price> dailyPriceData = new ObjectArrayList<>();


  public PriceDataModel(String exchange) {
    this.exchange = exchange;
  }


  public String getExchange() {
    return exchange;
  }


  public void addPrice(final Price price) {
    dailyPriceData.add(price);
  }

  public ObjectArrayList<Price> getAllPrices() {
    return dailyPriceData;
  }
}
