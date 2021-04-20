package com.valuestocks.dailybhav.repository;

import com.valuestocks.dailybhav.model.repository.MonthlyPriceData;
import com.valuestocks.dailybhav.model.repository.WeeklyPriceData;
import org.springframework.data.repository.CrudRepository;

public interface WeeklyPriceDataRepository extends CrudRepository<WeeklyPriceData, Integer> {
}
