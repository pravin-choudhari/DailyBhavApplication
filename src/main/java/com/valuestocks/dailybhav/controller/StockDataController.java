package com.valuestocks.dailybhav.controller;

import com.valuestocks.dailybhav.aggrdata.WeekAndMonthAggrService;
import com.valuestocks.dailybhav.aggrdata.model.StockSummary;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stock")
public class StockDataController {
  @Autowired
  final WeekAndMonthAggrService weekAndMonthAggrService;

  public StockDataController(WeekAndMonthAggrService weekAndMonthAggrService) {
    this.weekAndMonthAggrService = weekAndMonthAggrService;
  }

  @GetMapping(value = "/monthly")
  StockSummary getByName(@RequestParam("symbol") String symbol, @RequestParam("series") String series) {
    return weekAndMonthAggrService.getDataBySymbol(symbol,series);
  }

  @GetMapping(value = "/monthlyByHighMonthsGreaterOrEqualTo")
  List<StockSummary> monthlyByHighMonthsGreaterOrEqualToß(@RequestParam("months") Integer months ) {
    return weekAndMonthAggrService.monthlyDataByHighMonthsGreaterOrEqualTo(months);
  }


  @GetMapping(value = "/monthlyByRecentAllMonthsHigh")
  List<StockSummary> monthlyByRecentAllMonthsHigh(@RequestParam("months") Integer months ) {
    return weekAndMonthAggrService.monthlyDataByRecentHIghMonths(months);
  }
}
