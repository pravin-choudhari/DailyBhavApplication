package com.valuestocks.dailybhav.repository;

import com.valuestocks.dailybhav.model.repository.ReportDateSource;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ReportDateSourceRepository extends CrudRepository<ReportDateSource, Integer> {

  List<ReportDateSource> findAllByOrderByReportDateDesc();

}
