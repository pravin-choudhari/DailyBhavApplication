package com.valuestocks.dailybhav.aggrdata.model;

import com.valuestocks.dailybhav.model.Price;
import com.valuestocks.dailybhav.model.repository.MonthlyPriceData;
import com.valuestocks.dailybhav.model.repository.PriceData;
import com.valuestocks.dailybhav.model.repository.Symbol;
import com.valuestocks.dailybhav.model.repository.WeeklyPriceData;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StockSummary {
  private final Symbol symbol;
  private final Object2ObjectAVLTreeMap<LocalDate, MonthlyPriceData> monthToMonthlyPriceData = new Object2ObjectAVLTreeMap<>();
  private final Object2ObjectAVLTreeMap<LocalDate, WeeklyPriceData> weekToWeeklyPriceData = new Object2ObjectAVLTreeMap<>();
  private final List<WeeklyPriceData> weeklyPriceData = new ArrayList<>();
  private int totalHighMonths;
  private int consecutiveHighMonths;
  private int totalHighWeeks;
  private LocalDate updatedTillDate;
  private Object2BooleanArrayMap<LocalDate> monthToChangeMarker = new Object2BooleanArrayMap<>();
  private Object2BooleanArrayMap<LocalDate> weekToChangeMarker = new Object2BooleanArrayMap<>();

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

    updateHighMonth(data, month);
    monthToMonthlyPriceData.put(month, data);
  }

  private void updateHighMonth(final MonthlyPriceData data, final LocalDate month) {
    if (data.getPrevClosePrice() < data.getClosePrice() ) {
      final LocalDate currentDate = LocalDate.now();
      final LocalDate currentMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(),1);

      if (!month.equals(currentMonth)) {
        totalHighMonths++;
      }
    }
  }

  public void updateHighMonth(final MonthlyPriceData data) {
    final LocalDate dataDate = data.getMonth();
    final LocalDate month = LocalDate.of(dataDate.getYear(), dataDate.getMonth(), 1);

    updateHighMonth(data,month);
  }


  public List<MonthlyPriceData> getAllUpdatedMonthlyData() {
    List<MonthlyPriceData> updatedData =  new ArrayList<>();

    for (LocalDate month: monthToChangeMarker.keySet()) {
      updatedData.add(monthToMonthlyPriceData.get(month));
    }

    monthToChangeMarker.clear();
    return updatedData;
  }

  public List<WeeklyPriceData> getAllUpdatedWeeklyData() {
    List<WeeklyPriceData> updatedData =  new ArrayList<>();

    for (LocalDate week: weekToChangeMarker.keySet()) {
      updatedData.add(weekToWeeklyPriceData.get(week));
    }

    weekToChangeMarker.clear();
    return updatedData;
  }

  public MonthlyPriceData getMonthlyPriceDataForMonth(LocalDate date) {
    return monthToMonthlyPriceData.get(date);
  }

  public void updateWeeklyData(PriceData priceData) {
    final LocalDate dataDate = priceData.getDate();
    final LocalDate currentDate = LocalDate.now();
    final LocalDate weekStartDate = dataDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    final LocalDate weekEndDate = dataDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

    if (weekEndDate.isAfter(currentDate)) {
      //this is ongoing week, don't need to update db
      WeeklyPriceData weeklyPriceData = weekToWeeklyPriceData.get(weekStartDate);

      if (weeklyPriceData == null) {
        weeklyPriceData = new WeeklyPriceData();
        weeklyPriceData.setPrevClosePrice(priceData.getPrevClosePrice());
        weeklyPriceData.setWeekStartDate(weekStartDate);
        weeklyPriceData.setWeekEndDate(dataDate);
        weeklyPriceData.setClosePrice(priceData.getClosePrice());
        updateHighDays(priceData,weeklyPriceData);
        weekToWeeklyPriceData.put(weekStartDate, weeklyPriceData);
      } else {
        if (dataDate.isAfter(weeklyPriceData.getWeekEndDate())) {
          weeklyPriceData.setClosePrice(priceData.getClosePrice());
          weeklyPriceData.setWeekEndDate(dataDate);
          updateHighDays(priceData,weeklyPriceData);
        }
      }
    } else {
      // This is prev week, would need a db update too
      WeeklyPriceData weeklyPriceData = weekToWeeklyPriceData.get(weekStartDate);

      if (weeklyPriceData == null) {
        weeklyPriceData = new WeeklyPriceData();
        weeklyPriceData.setPrevClosePrice(priceData.getPrevClosePrice());
        weeklyPriceData.setWeekStartDate(weekStartDate);
        weeklyPriceData.setWeekEndDate(dataDate);
        weeklyPriceData.setClosePrice(priceData.getClosePrice());
        updateHighDays(priceData,weeklyPriceData);
        weekToWeeklyPriceData.put(weekStartDate, weeklyPriceData);
        weekToChangeMarker.put(weekStartDate,true);
      } else {
        if (weekToChangeMarker.getBoolean(weekStartDate)) {
              if (dataDate.isAfter(weeklyPriceData.getWeekEndDate())) {
                weeklyPriceData.setClosePrice(priceData.getClosePrice());
                weeklyPriceData.setWeekEndDate(dataDate);
              } else if (dataDate.isBefore(weeklyPriceData.getWeekEndDate())) {
                weeklyPriceData.setPrevClosePrice(priceData.getPrevClosePrice());
              }

          updateHighDays(priceData,weeklyPriceData);
        }
      }
    }
  }

  private void updateHighDays(final PriceData priceData, WeeklyPriceData weeklyPriceData) {
    if (priceData.getPrevClosePrice() <  priceData.getClosePrice()) {
      weeklyPriceData.setHighDays(weeklyPriceData.getHighDays() + 1);
    }
  }

  public void updateMonthlyData(PriceData priceData) {
    final LocalDate dataDate = priceData.getDate();
    final LocalDate dataMonth = LocalDate.of(dataDate.getYear(), dataDate.getMonth(), 1);
    final LocalDate currentDate = LocalDate.now();
    final LocalDate currentMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(),1);


    // If data is for the current month, update the summary but do not update change marker
    // as we do not want to update db
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
        if (monthToChangeMarker.getBoolean(dataMonth)) {
          // this means that this is a new entry and should read all entries
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
}
