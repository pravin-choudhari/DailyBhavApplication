package com.valuestocks.dailybhav.repository;

import com.valuestocks.dailybhav.model.repository.Symbol;
import org.springframework.data.repository.CrudRepository;

public interface SymbolRepository extends CrudRepository<Symbol, Symbol.PK> {

  Symbol findBySymbolAndSeries(String symbol, String series);
}
