package com.valuestocks.dailybhav.model;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import java.util.Date;

public class PriceTimeSeries {

  private final String symbol;
  private final Object2FloatArrayMap<Date> dateToPrice = new Object2FloatArrayMap<>();


  /**
   * Constructor.
   *
   * @param symbol
   */
  public PriceTimeSeries(String symbol) {
    this.symbol = symbol;
  }

  public void addPrice(final Date date, final float price) {
    dateToPrice.putIfAbsent(date, price);
  }

  public Object2FloatArrayMap<Date> getAllPrice() {
    return dateToPrice;
  }

  public float getPrice(final Date date) {
    return dateToPrice.getFloat(date);
  }
}
