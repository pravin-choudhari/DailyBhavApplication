package com.valuestocks.dailybhav.service;

import com.valuestocks.dailybhav.model.repository.Symbol;
import com.valuestocks.dailybhav.repository.SymbolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SymbolService {
  @Autowired
  private final SymbolRepository symbolRep;

  public SymbolService(SymbolRepository symbolRep) {
    this.symbolRep = symbolRep;
  }

  public Symbol getSymbol(final String symbol, final String series) {
    final Symbol s =  symbolRep.findBySymbolAndSeries(symbol,series);
    return s;
  }
}
