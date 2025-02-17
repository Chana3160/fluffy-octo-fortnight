package com.microsoft.weather_service.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.microsoft.weather_service.entities.ColdWaveDTO;
import com.microsoft.weather_service.entities.DateRangeDTO;
import com.microsoft.weather_service.entities.TemperatureDTO;
import com.microsoft.weather_service.entities.WeatherDay;

public interface WeatherService {
	public List<WeatherDay> fetchWeatherData(String city);
	public List<ColdWaveDTO> findColdWaves();
	public List<TemperatureDTO> findAvgTemperaturePerCityInDateRange(DateRangeDTO dateRange);
	public List<TemperatureDTO> findMaxTemperaturePerCity();
	public List<TemperatureDTO> findMinTemperaturePerCity();
}

