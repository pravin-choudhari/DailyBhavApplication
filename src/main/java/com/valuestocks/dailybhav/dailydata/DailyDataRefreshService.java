package com.valuestocks.dailybhav.dailydata;

import com.valuestocks.dailybhav.model.repository.PriceData;
import com.valuestocks.dailybhav.model.repository.Symbol;
import com.valuestocks.dailybhav.repository.PriceDataRepository;
import com.valuestocks.dailybhav.repository.SymbolRepository;
import com.valuestocks.dailybhav.utils.ValidationUtil;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@EnableScheduling
public class DailyDataRefreshService {

  private static final Logger l = LoggerFactory.getLogger(DailyDataRefreshService.class);

  @Autowired
  private final DailyData dailyData;
  @Autowired
  private final PriceDataRepository priceDataRep;
  @Autowired
  private final ReportDateService reportDateService;
  @Autowired
  private final SymbolRepository symbolRep;
  @Autowired
  private final ReportDateCalculator reportDateCalculator;


  public DailyDataRefreshService(final DailyData dailyData,
      final PriceDataRepository priceDataRep,
      final ReportDateService reportDateService,
      final SymbolRepository symbolRep,
      final ReportDateCalculator reportDateCalculator
  ) {
    this.dailyData = dailyData;
    this.priceDataRep = priceDataRep;
    this.reportDateService = reportDateService;
    this.symbolRep = symbolRep;
    this.reportDateCalculator = reportDateCalculator;
  }

  private static Float getFloat(final String num) {
    try {
      if (num != null && num.length() > 0) {
        return Float.parseFloat(num);
      }
    } catch (NumberFormatException ex) {
      l.info("Error occured while parsing: {} , {}", num, ex.getCause());
    }

    return null;
  }

  private static Long getLong(final String num) {
    try {
      if (num != null && num.length() > 0) {
        return Long.parseLong(num);
      }
    } catch (NumberFormatException ex) {
      l.info("Error occured while parsing: {} , {}", num, ex.getCause());
    }

    return null;
  }

  @Scheduled(fixedDelay = 30000)
  public void run() {
    //LocalDate date = LocalDate.of(2020, 12, 24);
    final LocalDate date = reportDateCalculator
        .apply(reportDateService.findAllOrderByReportDateDesc());

    if (date.isBefore(LocalDate.now())) {
      List<String[]> data = dailyData.getDailyData(date);

      if (data != null && data.size() > 1000) {
        reportDateService.startReportUpdate(date);
        updatePriceData(data, date);
        reportDateService.completeReportUpdate(date);
        l.info("Updated price data with records: {} for date: {}", data.size(), date);
      }
    } else {
      l.info("Did not run the daily data update as only pending date is today's date");
    }
  }

  private void updatePriceData(List<String[]> data, LocalDate date) {
    data.forEach(record -> {
      final PriceData stockData = new PriceData();
      final Symbol symbol = new Symbol();
      final String sym = record[2];

      symbol.setSeries(record[1]);

      if (!ValidationUtil.notNullOrEmpty(sym)) {
        return;
      }

      stockData.setDate(date);
      symbol.setSymbol(record[2]);
      symbol.setSecurity(record[3]);
      stockData.setPrevClosePrice(getFloat(record[4]));
      stockData.setOpenPrice(getFloat(record[5]));
      stockData.setHighPrice(getFloat(record[6]));
      stockData.setLowPrice(getFloat(record[7]));
      stockData.setClosePrice(getFloat(record[8]));
      stockData.setTradedValue(getFloat(record[9]));
      stockData.setTradedQty(getLong(record[10]));
      symbol.setSecurityInd(record[11]);
      symbol.setCorpInd(record[12]);
      stockData.setNoOfTrades(getLong(record[13]));
      stockData.setYearlyHighPrice(getFloat(record[14]));
      stockData.setYearlyLowPrice(getFloat(record[15]));
      //final Symbol existingSymbol = symbolRep.findBySymbolAndSeries(symbol.getSymbol(), symbol.getSeries());
      final Symbol existingSymbol = symbolRep.findById(new Symbol.PK(symbol.getSymbol(),
          symbol.getSeries())).orElseGet(() -> {
        return symbolRep.save(symbol);
      });

      if (existingSymbol != null) {
        stockData.setSymbol(existingSymbol);
      }

      priceDataRep.save(stockData);
    });
  }
}



