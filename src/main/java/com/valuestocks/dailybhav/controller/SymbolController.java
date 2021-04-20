package com.valuestocks.dailybhav.controller;

import com.valuestocks.dailybhav.model.repository.Symbol;
import com.valuestocks.dailybhav.service.SymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/symbol")
public class SymbolController {
  @Autowired
  final SymbolService symbolService;

  public SymbolController(SymbolService symbolService) {
    this.symbolService = symbolService;
  }

  @RequestMapping(value = "/get")
  Symbol getByName(@RequestParam("symbol") String symbol, @RequestParam("series") String series) {
    return symbolService.getSymbol(symbol,series);
  }
}
