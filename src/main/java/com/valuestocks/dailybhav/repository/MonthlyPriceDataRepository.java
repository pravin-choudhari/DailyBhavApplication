package com.valuestocks.dailybhav.repository;

import com.valuestocks.dailybhav.model.repository.MonthlyPriceData;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MonthlyPriceDataRepository  extends CrudRepository<MonthlyPriceData, Integer> {
 // @Modifying
  /*@Query("UPDATE monthly_price_data m SET m.close_price = :close_price , m.high_days = :high_days"
      + " , m.month = :new_month , m.prev_close_price = :prev_close_price where m.symbol = :symbol and "
      + "m.series = :series and m.month = :month")
  @Query("UPDATE MonthlyPriceData m SET m.closePrice = :close_price , m.highDays = :high_days"
      + " , m.month = :new_month , m.prevClosePrice = :prev_close_price where m.symbol = :symbol and "
      + "m.series = :series and m.month = :month")*/
  /*@Query("UPDATE monthly_price_data m SET m.close_price = :close_price  where m.symbol = :symbol and "
      + "m.series = :series and m.month = :month")
  int updateData(@Param("close_price") Float closePrice,
      @Param("symbol") String symbol, @Param("series") String series,
      @Param("month") LocalDate currentDate);*/
  /*int updateData(@Param("close_price") Float closePrice, @Param("prev_close_price") Float prevClosePrice,
      @Param("high_days")int highDays, @Param("symbol") String symbol, @Param("series") String series,
      @Param("new_month")LocalDate newDate, @Param("month") LocalDate currentDate);*/
}
