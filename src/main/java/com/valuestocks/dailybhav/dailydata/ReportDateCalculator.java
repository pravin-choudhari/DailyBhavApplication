package com.valuestocks.dailybhav.dailydata;

import com.valuestocks.dailybhav.model.repository.ReportDateSource;
import com.valuestocks.dailybhav.utils.ReportStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class ReportDateCalculator implements Function<List<ReportDateSource>, LocalDate> {

  private static final Logger l = LoggerFactory.getLogger(ReportDateCalculator.class);

  @Override
  public LocalDate apply(List<ReportDateSource> reportDates) {
    for (ReportDateSource reportDate : reportDates) {
      if (reportDate.getReportStatus().contentEquals(ReportStatus.COMPLETED)) {
        final LocalDate date = reportDate.getReportDate().plusDays(1);

        l.info("Going to report next date as: {}", date.toString());
        return date;
      }
    }

    l.info("Initial run, going back 1 year and starting from there");
    return LocalDate.now().minusDays(366);
  }
}
