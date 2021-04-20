package com.valuestocks.dailybhav.aggrdata.model;

import com.valuestocks.dailybhav.model.repository.MonthlyPriceData;
import com.valuestocks.dailybhav.model.repository.PriceData;
import com.valuestocks.dailybhav.model.repository.Symbol;
import com.valuestocks.dailybhav.model.repository.WeeklyPriceData;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockSummary {
  private final Symbol symbol;
  private final Object2ObjectAVLTreeMap<LocalDate, MonthlyPriceData> monthToMonthlyPriceData = new Object2ObjectAVLTreeMap<>();
  private final List<WeeklyPriceData> weeklyPriceData = new ArrayList<>();
  private int totalHighMonths;
  private int consecutiveHighMonths;
  private int totalHighWeeks;
  private LocalDate updatedTillDate;
  private Object2BooleanArrayMap<LocalDate> monthToChangeMarker = new Object2BooleanArrayMap<>();

  public StockSummary(Symbol symbol) {
    this.symbol = symbol;
  }

  public Symbol getSymbol() {
    return symbol;
  }

  public List<WeeklyPriceData> getWeeklyPriceData() {
    return weeklyPriceData;
  }

  public int getTotalHighMonths() {
    return totalHighMonths;
  }

  public void setTotalHighMonths(int totalHighMonths) {
    this.totalHighMonths = totalHighMonths;
  }

  public int getTotalHighWeeks() {
    return totalHighWeeks;
  }

  public void setTotalHighWeeks(int totalHighWeeks) {
    this.totalHighWeeks = totalHighWeeks;
  }

  public void addMonthlyPriceData(final MonthlyPriceData data) {
    final LocalDate dataDate = data.getMonth();
    final LocalDate month = LocalDate.of(dataDate.getYear(), dataDate.getMonth(), 1);

    if (data.getPrevClosePrice() < data.getClosePrice() ) {
      final LocalDate currentDate = LocalDate.now();
      final LocalDate currentMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(),1);

      if (!month.equals(currentMonth)) {
        totalHighMonths++;
      }
    }

    monthToMonthlyPriceData.put(month, data);
  }

  public List<MonthlyPriceData> getAllUpdatedMonthlyData() {
    List<MonthlyPriceData> updatedData =  new ArrayList<>();

    for (LocalDate month: monthToChangeMarker.keySet()) {
      updatedData.add(monthToMonthlyPriceData.get(month));
    }

    monthToChangeMarker.clear();
    return updatedData;
  }

  public MonthlyPriceData getMonthlyPriceDataForMonth(LocalDate date) {
    return monthToMonthlyPriceData.get(date);
  }

  public void updateMonthlyData(PriceData priceData) {
    final LocalDate dataDate = priceData.getDate();
    final LocalDate dataMonth = LocalDate.of(dataDate.getYear(), dataDate.getMonth(), 1);
    final LocalDate currentDate = LocalDate.now();
    final LocalDate currentMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(),1);
    //final MonthlyPriceData monthlyPriceData = monthToMonthlyPriceData
     //   .computeIfAbsent(dataMonth, data -> new MonthlyPriceData());

    if (currentMonth.equals(dataMonth)) {
      MonthlyPriceData currentMonthlyPriceData =  monthToMonthlyPriceData.get(dataMonth);

      if (currentMonthlyPriceData == null) {
        currentMonthlyPriceData = new MonthlyPriceData();
        //currentMonthlyPriceData.setSymbol(existingSymbol);
        currentMonthlyPriceData.setPrevClosePrice(priceData.getPrevClosePrice());
        currentMonthlyPriceData.setMonth(dataDate);
        currentMonthlyPriceData.setClosePrice(priceData.getClosePrice());
        monthToMonthlyPriceData.put(dataMonth,currentMonthlyPriceData);
      } else {
        if (currentMonthlyPriceData.getMonth().isBefore(dataDate)) {
          currentMonthlyPriceData.setClosePrice(priceData.getClosePrice());
        }
      }
    } else {
      MonthlyPriceData monthlyPriceData =  monthToMonthlyPriceData.get(dataMonth);

      if (monthlyPriceData == null) {
        monthlyPriceData = new MonthlyPriceData();
        monthlyPriceData.setMonth(dataDate);
        monthlyPriceData.setPrevClosePrice(priceData.getPrevClosePrice());
        monthlyPriceData.setClosePrice(priceData.getClosePrice());
        monthToMonthlyPriceData.put(dataMonth,monthlyPriceData);
        monthToChangeMarker.put(dataMonth,true);
      } else {
        final LocalDate prevDate = monthlyPriceData.getMonth();

        if (prevDate.isBefore(dataDate)) {
          monthlyPriceData.setMonth(dataDate);
          monthlyPriceData.setClosePrice(priceData.getClosePrice());
          monthToChangeMarker.put(dataMonth,true);
        }

        if (prevDate.isAfter(dataDate)) {
          monthlyPriceData.setPrevClosePrice(priceData.getPrevClosePrice());
          monthToChangeMarker.put(dataMonth,true);
        }
      }
    }
  }
}
