package com.valuestocks.dailybhav;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class DailybhavApplicationTests {

  @Test
  void contextLoads() {
    final LocalDate dataDate = LocalDate.of(2021,1,6);
    WeekFields wf = WeekFields.of(Locale.getDefault()) ;                    // Use week fields appropriate to your locale. People in different places define a week and week-number differently, such as starting on a Monday or a Sunday, and so on.
    TemporalField weekNum = wf.weekOfYear();
    //TemporalField weekNum = wf.weekOfWeekBasedYear();
    int week = Integer.parseInt(String.format("%02d",dataDate.get(weekNum)));

    //System.out.println(week);

    LocalDate startDate = LocalDate.now()
                 .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

    //System.out.println(startDate);

    LocalDate now = LocalDate.of(2021,5,4);

    System.out.println(now);
    System.out.println(now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
    System.out.println(now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));

  }

}
