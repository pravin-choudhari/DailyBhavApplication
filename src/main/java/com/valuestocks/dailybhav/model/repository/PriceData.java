package com.valuestocks.dailybhav.model.repository;

import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import org.springframework.data.domain.Persistable;

@Entity
public class PriceData implements Persistable<Integer> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private Float openPrice;
  private Float highPrice;
  private Float prevClosePrice;
  private Float lowPrice;
  private Float closePrice;
  private Float tradedValue;
  private Long tradedQty;
  private Long noOfTrades;
  private Float yearlyLowPrice;
  private Float yearlyHighPrice;
  private LocalDate date;
  //@ManyToOne (cascade=CascadeType.ALL)
  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
//@JoinColumn(name="symbol_id", nullable=false , referencedColumnName="id",unique=true)
  @JoinColumns(
      value = {@JoinColumn(name = "series"), @JoinColumn(name = "symbol")},
      foreignKey = @ForeignKey(name = "symbols")
  )
  private Symbol symbol;

  public Symbol getSymbol() {
    return symbol;
  }

  public void setSymbol(Symbol symbol) {
    this.symbol = symbol;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public Float getPrevClosePrice() {
    return prevClosePrice;
  }

  public void setPrevClosePrice(Float prevClosePrice) {
    this.prevClosePrice = prevClosePrice;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Float getOpenPrice() {
    return openPrice;
  }

  public void setOpenPrice(Float openPrice) {
    this.openPrice = openPrice;
  }

  public Float getHighPrice() {
    return highPrice;
  }

  public void setHighPrice(Float highPrice) {
    this.highPrice = highPrice;
  }

  public Float getLowPrice() {
    return lowPrice;
  }

  public void setLowPrice(Float lowPrice) {
    this.lowPrice = lowPrice;
  }

  public Float getClosePrice() {
    return closePrice;
  }

  public void setClosePrice(Float closePrice) {
    this.closePrice = closePrice;
  }

  public Float getTradedValue() {
    return tradedValue;
  }

  public void setTradedValue(Float tradedValue) {
    this.tradedValue = tradedValue;
  }

  public Long getTradedQty() {
    return tradedQty;
  }

  public void setTradedQty(Long tradedQty) {
    this.tradedQty = tradedQty;
  }

  public Long getNoOfTrades() {
    return noOfTrades;
  }

  public void setNoOfTrades(Long noOfTrades) {
    this.noOfTrades = noOfTrades;
  }

  public Float getYearlyLowPrice() {
    return yearlyLowPrice;
  }

  public void setYearlyLowPrice(Float yearlyLowPrice) {
    this.yearlyLowPrice = yearlyLowPrice;
  }

  public Float getYearlyHighPrice() {
    return yearlyHighPrice;
  }

  public void setYearlyHighPrice(Float yearlyHighPrice) {
    this.yearlyHighPrice = yearlyHighPrice;
  }

  @Override
  public boolean isNew() {
    return true;
  }
}