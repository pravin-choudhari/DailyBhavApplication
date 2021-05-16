package com.valuestocks.dailybhav.aggrdata;

import com.valuestocks.dailybhav.aggrdata.model.StockSummary;
import com.valuestocks.dailybhav.model.repository.MonthlyPriceData;
import com.valuestocks.dailybhav.model.repository.PriceData;
import com.valuestocks.dailybhav.model.repository.Symbol;
import com.valuestocks.dailybhav.model.repository.WeeklyPriceData;
import com.valuestocks.dailybhav.repository.MonthlyPriceDataRepository;
import com.valuestocks.dailybhav.repository.PriceDataRepository;
import com.valuestocks.dailybhav.repository.SymbolRepository;
import com.valuestocks.dailybhav.repository.WeeklyPriceDataRepository;
import com.valuestocks.dailybhav.utils.SymbolSeries;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class WeekAndMonthAggrService {

  private static final Logger l = LoggerFactory.getLogger(WeekAndMonthAggrService.class);
  private final ObjectArrayList<Symbol> symbols = new ObjectArrayList<>();
  private final Object2ObjectLinkedOpenHashMap<Symbol, StockSummary>
      symbolToStockSummary = new Object2ObjectLinkedOpenHashMap<>();
  @Autowired
  private final SymbolRepository symbolRep;
  @Autowired
  private final PriceDataRepository priceDataRep;
  @Autowired
  private final MonthlyPriceDataRepository monthlyPriceDataRepository;
  @Autowired
  private final WeeklyPriceDataRepository weeklyPriceDataRepository;
  MonthlyPriceData newMonthlyPriceData = null;
  boolean initialRun = true;

  /**
   * @param symbolRep
   */
  public WeekAndMonthAggrService(SymbolRepository symbolRep,
      PriceDataRepository priceDataRep,
      MonthlyPriceDataRepository monthlyPriceDataRepository,
      WeeklyPriceDataRepository weeklyPriceDataRepository) {
    this.symbolRep = symbolRep;
    this.priceDataRep = priceDataRep;
    this.monthlyPriceDataRepository = monthlyPriceDataRepository;
    this.weeklyPriceDataRepository = weeklyPriceDataRepository;
  }

  void persistAggregationData() {
    for (Entry<Symbol, StockSummary> entry : symbolToStockSummary.entrySet()) {
      final List<MonthlyPriceData> updatedMonthlyData;
      final List<WeeklyPriceData> updatedWeeklyData;


      final Symbol symbol = entry.getKey();
      final StockSummary summary = entry.getValue();

      final Symbol existingSymbol = symbolRep.findById(new Symbol.PK(symbol.getSymbol(),
          symbol.getSeries())).orElseGet(() -> {
        return symbolRep.save(symbol);
      });

      updatedMonthlyData = summary.getAllUpdatedMonthlyData();

      for (MonthlyPriceData data : updatedMonthlyData) {
        data.setSymbol(existingSymbol);
        summary.updateHighMonth(data);
        l.info("Updating data in DB for {} , for month: {}", existingSymbol.getSymbol(), data.getMonth());
        try {
          monthlyPriceDataRepository.save(data);
        } catch (Exception e) {
          l.warn("Failed to update monthly data: {} , Cause: {} Exception: {}", data, e.getCause(), e);
        }
      }

      updatedWeeklyData =  summary.getAllUpdatedWeeklyData();

      for (WeeklyPriceData data: updatedWeeklyData) {
        data.setSymbol(existingSymbol);
        l.info("Updating data in DB for {} , for week: {}", existingSymbol.getSymbol(), data.getWeekStartDate());
        try {
          weeklyPriceDataRepository.save(data);
        } catch (Exception e) {
          l.warn("Failed to update weekly data: {} , Cause: {} Exception: {}", data, e.getCause(), e);
        }
      }
    }
  }

  @Scheduled(fixedDelay = 180000)
  public void run() {
    final List<Symbol> updatedSymbols = getAllSymbols();
    /*boolean temp = true;

    if (temp) {
      l.info("Aggregation service is not running");
      return;
    }*/
    l.info("Going to update weekly/monthly data for {} symbols", updatedSymbols.size());

    if(initialRun) {
      loadStockSummary();
      initialRun =false;
    }

    for (Symbol symbol : updatedSymbols) {
      final List<PriceData> data = priceDataRep.find(symbol.getSeries(), symbol.getSymbol());
      final StockSummary summary = symbolToStockSummary
          .computeIfAbsent(symbol, sum -> new StockSummary(symbol));
      LocalDate prevPriceDataMonth = null;
      MonthlyPriceData currentMonthlyPriceData = null;

      for (PriceData priceData : data) {
        summary.updateMonthlyData(priceData);
        summary.updateWeeklyData(priceData);
      }

      l.info("Updated summary data for symbol: {}", symbol.getSymbol() );
    }

    l.info("Updated weekly/monthly data successfully in summary, going to update data now");
    persistAggregationData();
    l.info("Updated monthly data successfully in DB");
  }

  private void loadStockSummary() {
    Iterable<MonthlyPriceData> monthlyPriceData = monthlyPriceDataRepository.findAll();

    monthlyPriceData.forEach(data -> {
      final Symbol symbol = data.getSymbol();
      final StockSummary summary = symbolToStockSummary
          .computeIfAbsent(symbol, sum -> new StockSummary(symbol));
      summary.addMonthlyPriceData(data);
    });
  }

  private List<Symbol> getAllSymbols() {
    final Iterator<Symbol> symbolsIterator = symbolRep.findAll().iterator();

    while (symbolsIterator.hasNext()) {
      final Symbol symbol = symbolsIterator.next();

      if (symbol.getSeries().equals(SymbolSeries.EQ) && !symbols.contains(symbol)) {
        symbols.add(symbol);
      }
    }

    return symbols;
  }

  public StockSummary getDataBySymbol(final String symbol, final String series) {
    Symbol symbol1 = symbols.stream().filter(s -> s.getSymbol().equals(symbol) && s.getSeries().equals(series))
        .findFirst().orElse(null);
;    return symbolToStockSummary.get(symbol1);
  }

  public List<StockSummary> monthlyDataByHighMonthsGreaterOrEqualTo(Integer months) {
    List<StockSummary> stockSummaries = new ObjectArrayList<>();

    for ( Entry<Symbol, StockSummary> entry : symbolToStockSummary.entrySet()) {
      final StockSummary summary = entry.getValue();

      if (summary.getTotalHighMonths() >= months) {
        stockSummaries.add(summary);
      }
    }

    return stockSummaries;
  }

  public List<StockSummary> monthlyDataByRecentHIghMonths(final Integer noOfMonths) {
    List<StockSummary> stockSummaries = new ObjectArrayList<>();
    List<LocalDate> months = new ObjectArrayList<>();
    LocalDate tempMonth = null;
    final LocalDate currentDate = LocalDate.now();
    final LocalDate currentMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(),1);
    int totalMonths = noOfMonths;

    months.add(currentMonth);
    totalMonths --;
    tempMonth=currentMonth;

    while(totalMonths  > 0) {
      tempMonth = tempMonth.minusMonths(1);
      months.add(tempMonth);
      totalMonths--;
    }

    for ( Entry<Symbol, StockSummary> entry : symbolToStockSummary.entrySet()) {
      final StockSummary summary = entry.getValue();
      int highMonths =0;

      for (LocalDate month : months) {
        MonthlyPriceData priceData = summary.getMonthlyPriceDataForMonth(month);

        if (priceData != null &&  priceData.getClosePrice() > priceData.getPrevClosePrice()) {
          highMonths++;
        }
      }

      if (highMonths == noOfMonths) {
        stockSummaries.add(summary);
      }
    }

    return stockSummaries;
  }
}
