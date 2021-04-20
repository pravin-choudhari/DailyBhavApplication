package com.valuestocks.dailybhav.dailydata;

import java.time.LocalDate;
import java.util.List;

public interface DailyData {

  List<String[]> getDailyData(LocalDate date);

}
