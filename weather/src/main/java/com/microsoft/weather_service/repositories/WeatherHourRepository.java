package com.microsoft.weather_service.repositories;

import org.springframework.data.repository.CrudRepository;

import com.microsoft.weather_service.entities.WeatherHour;

public interface WeatherHourRepository extends CrudRepository<WeatherHour, Long>{

}
