package com.valuestocks.dailybhav.model;

import java.util.Date;

public class Price {

  private final String symbol;
  private final Date tradeDate;
  private final float openPrice;
  private final float highPrce;
  private final float lowPrice;
  private final float closePrice;
  private final float netTradeVal;
  private final long netTrdeQty;
  private final long noOfTrades;


  public Price(String symbol, Date tradeDate, float openPrice, float highPrce, float lowPrice,
      float closePrice,
      float netTradeVal, long netTrdeQty, long noOfTrades) {
    super();
    this.symbol = symbol;
    this.tradeDate = tradeDate;
    this.openPrice = openPrice;
    this.highPrce = highPrce;
    this.lowPrice = lowPrice;
    this.closePrice = closePrice;
    this.netTradeVal = netTradeVal;
    this.netTrdeQty = netTrdeQty;
    this.noOfTrades = noOfTrades;
  }


  public String getSymbol() {
    return symbol;
  }


  public Date getTradeDate() {
    return tradeDate;
  }


  public float getOpenPrice() {
    return openPrice;
  }


  public float getHighPrce() {
    return highPrce;
  }


  public float getLowPrice() {
    return lowPrice;
  }


  public float getNetTradeVal() {
    return netTradeVal;
  }


  public long getNetTrdeQty() {
    return netTrdeQty;
  }


  public long getNoOfTrades() {
    return noOfTrades;
  }
}

