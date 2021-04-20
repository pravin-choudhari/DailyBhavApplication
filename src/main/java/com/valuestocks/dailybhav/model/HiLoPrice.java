package com.valuestocks.dailybhav.model;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import java.util.Date;

public class HiLoPrice {

  private final Object2FloatArrayMap<Date> dateToPrice = new Object2FloatArrayMap<>();
  private final String symbol;

  public HiLoPrice(String symbol) {
    this.symbol = symbol;
  }


}
