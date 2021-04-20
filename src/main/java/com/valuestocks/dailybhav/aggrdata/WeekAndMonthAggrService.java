package com.valuestocks.dailybhav.aggrdata;

import com.valuestocks.dailybhav.aggrdata.model.StockSummary;
import com.valuestocks.dailybhav.model.repository.MonthlyPriceData;
import com.valuestocks.dailybhav.model.repository.PriceData;
import com.valuestocks.dailybhav.model.repository.Symbol;
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

  void persistUpdateMonthlyData() {
    for (Entry<Symbol, StockSummary> entry : symbolToStockSummary.entrySet()) {
      final List<MonthlyPriceData> updatedMonthlyData;

      final Symbol symbol = entry.getKey();
      final StockSummary summary = entry.getValue();

      final Symbol existingSymbol = symbolRep.findById(new Symbol.PK(symbol.getSymbol(),
          symbol.getSeries())).orElseGet(() -> {
        return symbolRep.save(symbol);
      });

      updatedMonthlyData = summary.getAllUpdatedMonthlyData();

      for (MonthlyPriceData data : updatedMonthlyData) {
        data.setSymbol(existingSymbol);
        l.info("Updating data in DB for {} , for month: {}", existingSymbol.getSymbol(), data.getMonth());
        monthlyPriceDataRepository.save(data);
      }
    }
  }

  @Scheduled(fixedDelay = 180000)
  public void run() {
    final List<Symbol> updatedSymbols = getAllSymbols();
    boolean temp = true;

    if (temp) {
      l.info("Aggregation service is not running");
      return;
    }
    l.info("Going to update weekly/monthly data for {} symbols", updatedSymbols.size());

    loadStockSummary();
    for (Symbol symbol : updatedSymbols) {
      final List<PriceData> data = priceDataRep.find(symbol.getSeries(), symbol.getSymbol());
      final StockSummary summary = symbolToStockSummary
          .computeIfAbsent(symbol, sum -> new StockSummary(symbol));
      LocalDate prevPriceDataMonth = null;
      MonthlyPriceData currentMonthlyPriceData = null;

      for (PriceData priceData : data) {
        summary.updateMonthlyData(priceData);
        // updateMonthlyData(priceData, summary);
        /*final LocalDate dataDate = priceData.getDate();
        final LocalDate dataMonth = LocalDate.of(dataDate.getYear(), dataDate.getMonth(), 1);
        final LocalDate currentDate = LocalDate.now();
        final LocalDate currentMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(),1);

          if (currentMonth.equals(dataMonth)) {
            // This is for current month
            final Symbol existingSymbol = symbolRep.findById(new Symbol.PK(symbol.getSymbol(),
                symbol.getSeries())).orElseGet(() -> {
              return symbolRep.save(symbol);
            });
            currentMonthlyPriceData =  summary.getMonthlyPriceDataForMonth(dataMonth);

            if (currentMonthlyPriceData == null) {
              currentMonthlyPriceData = new MonthlyPriceData();
              currentMonthlyPriceData.setSymbol(existingSymbol);
              currentMonthlyPriceData.setPrevClosePrice(priceData.getPrevClosePrice());
              currentMonthlyPriceData.setMonth(dataDate);
              currentMonthlyPriceData.setClosePrice(priceData.getClosePrice());
            } else {
              currentMonthlyPriceData.setClosePrice(priceData.getClosePrice());
            }

            summary.addMonthlyPriceData(currentMonthlyPriceData);
          } else {
            // this is for previous months
            MonthlyPriceData monthlyPriceData =  summary.getMonthlyPriceDataForMonth(dataMonth);

            if (monthlyPriceData != null) {
                continue;
            } else {
              final Symbol existingSymbol = symbolRep.findById(new Symbol.PK(symbol.getSymbol(),
                  symbol.getSeries())).orElseGet(() -> {
                return symbolRep.save(symbol);
              });

              if (newMonthlyPriceData == null) {
                newMonthlyPriceData = new MonthlyPriceData();
              } else {
                if (!newMonthlyPriceData.getSymbol().equals(existingSymbol)) {
                  monthlyPriceDataRepository.save(newMonthlyPriceData);
                  summary.addMonthlyPriceData(newMonthlyPriceData);
                  newMonthlyPriceData =  new MonthlyPriceData();
                }
              }

              if (dataMonth.equals(prevPriceDataMonth)) {

                newMonthlyPriceData.setSymbol(existingSymbol);
                newMonthlyPriceData.setMonth(dataDate);
                newMonthlyPriceData.setClosePrice(priceData.getClosePrice());
                prevPriceDataMonth = dataMonth;
              } else if (prevPriceDataMonth == null) {
                newMonthlyPriceData.setSymbol(existingSymbol);
                newMonthlyPriceData.setPrevClosePrice(priceData.getPrevClosePrice());
                newMonthlyPriceData.setMonth(dataDate);
                newMonthlyPriceData.setClosePrice(priceData.getClosePrice());
                prevPriceDataMonth = dataMonth;
              } else if (prevPriceDataMonth.isBefore(dataMonth)) {
                monthlyPriceDataRepository.save(newMonthlyPriceData);
                summary.addMonthlyPriceData(newMonthlyPriceData);
                newMonthlyPriceData = null;
              }
            }
          }*/
      }
    }
    l.info("Updated weekly/monthly data successfully in summary, going to update data now");
    persistUpdateMonthlyData();
    l.info("Updated monthly data successfully in DB");
  }

 /* @Transactional
  public void updateMonthlyData(PriceData priceData, final StockSummary summary) {
    final LocalDate dataDate = priceData.getDate();
    final LocalDate dataMonth = LocalDate.of(dataDate.getYear(), dataDate.getMonth(), 1);
    final LocalDate currentDate = LocalDate.now();
    final LocalDate currentMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(),1);

    MonthlyPriceData monthlyPriceData = summary.getMonthlyPriceDataForMonth(dataMonth);
    final Symbol symbol = summary.getSymbol();
    final LocalDate prevDate = monthlyPriceData.getMonth();

    final Symbol existingSymbol = symbolRep.findById(new Symbol.PK(symbol.getSymbol(),
        symbol.getSeries())).orElseGet(() -> {
      return symbolRep.save(symbol);
    });

    monthlyPriceData.setSymbol(existingSymbol);

    if (dataMonth.equals(currentMonth)) {

    }
    if (prevDate == null) {
      monthlyPriceData.setMonth(dataDate);
      monthlyPriceData.setPrevClosePrice(priceData.getPrevClosePrice());
      monthlyPriceData.setClosePrice(priceData.getClosePrice());
      monthlyPriceDataRepository.save(monthlyPriceData);
    } else if (prevDate.isBefore(dataDate)) {
      monthlyPriceData.setMonth(dataDate);
      monthlyPriceData.setClosePrice(priceData.getClosePrice());
    }
  }*/

  private void loadStockSummary() {
    Iterable<MonthlyPriceData> monthlyPriceData = monthlyPriceDataRepository.findAll();
    //Iterable<WeeklyPriceData> weeklyPriceData = weeklyPriceDataRepository.findAll();

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
}
