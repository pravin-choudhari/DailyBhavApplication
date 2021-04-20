package com.valuestocks.dailybhav.model;

public class Security {

  private final String mktType;

  private final SERIES series;
  private final String symbol;
  private final String name;
  private final boolean ind_sec;
  private final String corp_sec;
  public Security(String mktType, SERIES series, String symbol, String name, boolean ind_sec,
      String corp_sec) {
    super();
    this.mktType = mktType;
    this.series = series;
    this.symbol = symbol;
    this.name = name;
    this.ind_sec = ind_sec;
    this.corp_sec = corp_sec;
  }

  /**
   * @return the mktType
   */
  public String getMktType() {
    return mktType;
  }

  /**
   * @return the series
   */
  public SERIES getSeries() {
    return series;
  }

  /**
   * @return the symbol
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the ind_sec
   */
  public boolean isInd_sec() {
    return ind_sec;
  }

  /**
   * @return the corp_sec
   */
  public String getCorp_sec() {
    return corp_sec;
  }

  enum SERIES {
    EQ,
    BE,
  }

}
