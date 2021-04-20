package com.valuestocks.dailybhav.model.repository;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Data
@NoArgsConstructor
@Entity
@IdClass(Symbol.PK.class)
public class Symbol implements Persistable<Symbol.PK> {

  @Id
  private String symbol;
  @Id
  private String series;
  private String security;
  private String securityInd;
  private String corpInd;

  public PK getId() {
    return new PK(symbol, series);
  }

  public String getSeries() {
    return series;
  }

  public void setSeries(String series) {
    this.series = series;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String getSecurity() {
    return security;
  }

  public void setSecurity(String security) {
    this.security = security;
  }

  public String getSecurityInd() {
    return securityInd;
  }

  public void setSecurityInd(String securityInd) {
    this.securityInd = securityInd;
  }

  public String getCorpInd() {
    return corpInd;
  }

  public void setCorpInd(String corpInd) {
    this.corpInd = corpInd;
  }

  @Override
  public boolean isNew() {
    return true;
  }

  @Data
  @NoArgsConstructor
  public static class PK implements Serializable {

    private static final long serialVersionUID = -2796668349486710992L;
    private String symbol;
    private String series;


    public PK(String symbol, String series) {
      this.symbol = symbol;
      this.series = series;
    }
  }
}
