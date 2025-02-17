package com.microsoft.weather_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.weather_service.entities.ColdWaveDTO;
import com.microsoft.weather_service.entities.DateRangeDTO;
import com.microsoft.weather_service.entities.TemperatureDTO;
import com.microsoft.weather_service.entities.WeatherDay;
import com.microsoft.weather_service.services.WeatherService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/weather")
public class WeatherController {

	private final WeatherService weatherService;

	public WeatherController(WeatherService weatherService) {
		this.weatherService = weatherService;
	}

	@GetMapping("/get/{city}")
	public List<WeatherDay> fetchWeatherData(@PathVariable String city) {
		return weatherService.fetchWeatherData(city);
	}

	@GetMapping("/get/cold-waves")
	public List<ColdWaveDTO> findColdWaves() {
		return this.weatherService.findColdWaves();
	}

	@GetMapping("/get/min-temperature")
	public List<TemperatureDTO> findMinTemperaturePerCity() {
		return this.weatherService.findMinTemperaturePerCity();
	}

	@GetMapping("/get/max-temperature")
	public List<TemperatureDTO> findMaxTemperaturePerCity() {
		return this.weatherService.findMaxTemperaturePerCity();
	}

	@PostMapping("/get/avg-temperature")
	public List<TemperatureDTO> findAvgTemperaturePerCityInDateRange(@RequestBody DateRangeDTO dateRange) {
		return this.weatherService.findAvgTemperaturePerCityInDateRange(dateRange);
	}
}
