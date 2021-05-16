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
public class WeeklyPriceData implements Persistable<Integer> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private Float closePrice;
  private Float prevClosePrice;
  private int highDays;
  private Symbol symbol;
  private LocalDate weekStartDate;
  private LocalDate weekEndDate;



  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  @JoinColumns(
      value = {@JoinColumn(name = "series"), @JoinColumn(name = "symbol")},
      foreignKey = @ForeignKey(name = "symbolsWeekly")
  )

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

  public LocalDate getWeekStartDate() {
    return weekStartDate;
  }

  public void setWeekStartDate(LocalDate weekStartDate) {
    this.weekStartDate = weekStartDate;
  }

  public Symbol getSymbol() {
    return symbol;
  }

  public void setSymbol(Symbol symbol) {
    this.symbol = symbol;
  }

  public LocalDate getWeekEndDate() {
    return weekEndDate;
  }

  public void setWeekEndDate(LocalDate weekEndDate) {
    this.weekEndDate = weekEndDate;
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
