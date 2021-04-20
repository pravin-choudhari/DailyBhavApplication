package com.valuestocks.dailybhav.repository;

import com.valuestocks.dailybhav.model.repository.PriceData;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PriceDataRepository extends CrudRepository<PriceData, Integer> {

  @Query(value = "SELECT * FROM price_data p WHERE p.series = :series and p.symbol = :symbol order by date asc",
      nativeQuery = true)
  List<PriceData> find(@Param("series") String series, @Param("symbol") String symbol);
}
