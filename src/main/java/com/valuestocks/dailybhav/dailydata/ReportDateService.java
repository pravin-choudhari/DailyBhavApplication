package com.valuestocks.dailybhav.dailydata;

import com.valuestocks.dailybhav.model.repository.ReportDateSource;
import com.valuestocks.dailybhav.repository.ReportDateSourceRepository;
import com.valuestocks.dailybhav.utils.ReportStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportDateService {

  @Autowired
  private final ReportDateSourceRepository reportDateRep;

  public ReportDateService(final ReportDateSourceRepository reportDateRep) {
    this.reportDateRep = reportDateRep;
  }

  public void startReportUpdate(final LocalDate date) {
    logDataSourceForDate(date, ReportStatus.INITIATED);

  }

  public void completeReportUpdate(final LocalDate date) {
    logDataSourceForDate(date, ReportStatus.COMPLETED);
  }

  public void abortReportUpdate(final LocalDate date) {
    logDataSourceForDate(date, ReportStatus.FAILED);
  }

  List<ReportDateSource> findAllOrderByReportDateDesc() {
    final List<ReportDateSource> reportDates = reportDateRep.findAllByOrderByReportDateDesc();

    if (reportDates != null && reportDates.size() > 0) {
      return reportDates;
    } else {
      return new ArrayList<ReportDateSource>();
    }
  }

  private void logDataSourceForDate(final LocalDate date, final String state) {
    ReportDateSource reportDateSource = new ReportDateSource();

    reportDateSource.setReportDate(date);
    reportDateSource.setDateLoaded(LocalDateTime.now());
    reportDateSource.setReportStatus(state);
    reportDateRep.save(reportDateSource);
  }

}
