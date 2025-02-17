package com.microsoft.weather_service.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.microsoft.weather_service.entities.ColdWaveDTO;
import com.microsoft.weather_service.entities.WeatherDay;

@Repository
public interface WeatherDayRepository extends CrudRepository<WeatherDay, Long> {

	@Query(value = "WITH daily_avg_temp AS (" + "    SELECT " + "        w.city, "
			+ "        w.timestamp::date AS day_date, " + "        AVG((wh.temp - 32) * 5 / 9) AS avg_temp "
			+ "    FROM " + "        weather_day w " + "    JOIN "
			+ "        weather_hour wh ON w.id = wh.weather_day_id " + "    GROUP BY "
			+ "        w.city, w.timestamp::date " + "), " + "coldwave_groups AS (" + "    SELECT " + "        city, "
			+ "        day_date, " + "        avg_temp, " + "        SUM(CASE WHEN avg_temp < 10 THEN 1 ELSE 0 END) "
			+ "        OVER (PARTITION BY city ORDER BY day_date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) "
			+ "        AS coldwave_group, " + "        ROW_NUMBER() OVER (PARTITION BY city ORDER BY day_date) - "
			+ "        ROW_NUMBER() OVER (PARTITION BY city, CASE WHEN avg_temp < 10 THEN 1 ELSE 0 END ORDER BY day_date) AS group_id "
			+ "    FROM daily_avg_temp " + ") " + "SELECT " + "    city, " + "    group_id, "
			+ "    MIN(day_date) AS start_date, " + "    MAX(day_date) AS end_date, "
			+ "    COUNT(*) AS coldwave_length " + "FROM " + "    coldwave_groups " + "WHERE " + "    avg_temp < 10 "
			+ "GROUP BY " + "    city, group_id " + "ORDER BY " + "    city, start_date", nativeQuery = true)
	List<Object[]> findColdWavesRaw();

	@Query(value = "SELECT weather_day.city, MIN(weather_day.min_temp) AS min_temperature " + "FROM weather_day "
			+ "GROUP BY weather_day.city", nativeQuery = true)
	List<Object[]> findMinTemperaturePerCity();

	@Query(value = "SELECT weather_day.city, MAX(weather_day.max_temp) AS max_temperature " + "FROM weather_day "
			+ "GROUP BY weather_day.city", nativeQuery = true)
	List<Object[]> findMaxTemperaturePerCity();

	@Query(value = "SELECT " + "weather_day.city, " + "AVG(weather_day.min_temp) AS avg_min_temp, "
			+ "AVG(weather_day.max_temp) AS avg_max_temp " + "FROM weather_day "
			+ "WHERE weather_day.timestamp BETWEEN :startDate AND :endDate " + "GROUP BY weather_day.city "
			+ "ORDER BY weather_day.city", nativeQuery = true)
	List<Object[]> findAvgTemperaturePerCityInDateRange(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

}
