package com.valuestocks.dailybhav.model.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ReportDateSource {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private LocalDate reportDate;
  private LocalDateTime dateLoaded;
  private String reportStatus;

  public LocalDate getReportDate() {
    return reportDate;
  }

  public void setReportDate(LocalDate reportDate) {
    this.reportDate = reportDate;
  }

  public LocalDateTime getDateLoaded() {
    return dateLoaded;
  }

  public void setDateLoaded(LocalDateTime dateLoaded) {
    this.dateLoaded = dateLoaded;
  }

  public String getReportStatus() {
    return reportStatus;
  }

  public void setReportStatus(String reportStatus) {
    this.reportStatus = reportStatus;
  }


}
