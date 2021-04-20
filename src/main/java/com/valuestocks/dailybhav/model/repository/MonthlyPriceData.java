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
public class MonthlyPriceData implements Persistable<Integer> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private Float closePrice;
  private Float prevClosePrice;
  private int highDays;
  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  @JoinColumns(
      value = {@JoinColumn(name = "series"), @JoinColumn(name = "symbol")},
      foreignKey = @ForeignKey(name = "symbolsMonthly")
  )
  private Symbol symbol;
  private LocalDate month;

  public Float getClosePrice() {
    return closePrice;
  }

  public void setClosePrice(Float closePrice) {
    this.closePrice = closePrice;
  }

  public Float getPrevClosePrice() {
    return prevClosePrice;
  }

  public void setPrevClosePrice(Float prevClosePrice) {
    this.prevClosePrice = prevClosePrice;
  }

  public int getHighDays() {
    return highDays;
  }

  public void setHighDays(int highDays) {
    this.highDays = highDays;
  }

  public LocalDate getMonth() {
    return month;
  }

  public void setMonth(LocalDate month) {
    this.month = month;
  }

  public Symbol getSymbol() {
    return symbol;
  }

  public void setSymbol(Symbol symbol) {
    this.symbol = symbol;
  }

  @Override
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Override
  public boolean isNew() {
    return true;
  }

}
