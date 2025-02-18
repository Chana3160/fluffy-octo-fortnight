package com.microsoft.weather_service.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.microsoft.weather_service.WeatherConfiguration;
import com.microsoft.weather_service.entities.ColdWaveDTO;
import com.microsoft.weather_service.entities.DateRangeDTO;
import com.microsoft.weather_service.entities.TemperatureDTO;
import com.microsoft.weather_service.entities.WeatherDay;
import com.microsoft.weather_service.entities.WeatherHour;
import com.microsoft.weather_service.repositories.WeatherDayRepository;
import com.microsoft.weather_service.repositories.WeatherHourRepository;

import jakarta.annotation.PostConstruct;



@Service
public class WeatherServiceImpl implements WeatherService {

	private final WeatherHourRepository weatherHourRepository;
	private final WeatherDayRepository weatherDayRepository;
	private final WeatherConfiguration weatherConfiguration;

	public WeatherServiceImpl(WeatherConfiguration weatherConfiguration, WeatherDayRepository weatherRepository,
			WeatherHourRepository weatherHourRepository) {
		this.weatherConfiguration = weatherConfiguration;
		this.weatherDayRepository = weatherRepository;
		this.weatherHourRepository = weatherHourRepository;
	}
	
	 @PostConstruct
	    public void init() {
	        System.out.println("Fetching weather data for Jerusalem and Tel Aviv...");
	        fetchWeatherData("Jerusalem");
	        fetchWeatherData("TelAviv");
	        System.out.println("Weather data fetching completed.");
	    }

	@Override
	public List<WeatherDay> fetchWeatherData(String city) {
		String API_KEY = this.weatherConfiguration.getSecretKey();
		List<WeatherDay> dailyWeatherList = new ArrayList<>();
		List<WeatherHour> hourlyWeatherList = new ArrayList<>();

		try {
			String apiUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"+city+"?unitGroup=us&key=" 
                   +API_KEY+"&contentType=json";

			URL url = new URL(apiUrl.replace(" ", ""));
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			// Read the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			// Parse the JSON response
			JSONObject responseJson = new JSONObject(response.toString());
			JSONArray days = responseJson.getJSONArray("days");

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			for (int i = 0; i < days.length(); i++) {
				JSONObject day = days.getJSONObject(i);

				// Extract daily weather data
				String timestampStr = day.getString("datetime"); // e.g., "2025-01-16"
				Date timestamp = dateFormat.parse(timestampStr);
				double maxTemp = day.getDouble("tempmax");
				double minTemp = day.getDouble("tempmin");
				double avgTemp = day.getDouble("temp");
				double humidity = day.getDouble("humidity");
				double windSpeed = day.getDouble("windspeed");
				double precipitation = day.getDouble("precip");

				// Create WeatherDay entity
				WeatherDay dailyWeather = new WeatherDay();
				dailyWeather.setCity(city);
				dailyWeather.setMaxTemp(maxTemp);
				dailyWeather.setMinTemp(minTemp);
				dailyWeather.setAvgTemp(avgTemp);
				dailyWeather.setHumidity(humidity);
				dailyWeather.setWindSpeed(windSpeed);
				dailyWeather.setPrecipitation(precipitation);
				dailyWeather.setTimestamp(timestamp);

				dailyWeatherList.add(dailyWeather);
			}

			// Save all WeatherDay entries and get saved records with IDs
			List<WeatherDay> savedDays = (List<WeatherDay>) weatherDayRepository.saveAll(dailyWeatherList);

			for (int i = 0; i < savedDays.size(); i++) {
				WeatherDay savedDay = savedDays.get(i);
				JSONObject day = days.getJSONObject(i);
				JSONArray hours = day.getJSONArray("hours");

				for (int j = 0; j < hours.length(); j++) {
					JSONObject hour = hours.getJSONObject(j);

					// Extract hourly data
					String timeStr = hour.getString("datetime"); // e.g., "00:00:00"
					LocalTime time = LocalTime.parse(timeStr);
					double temp = hour.getDouble("temp");
					double feelsLike = hour.getDouble("feelslike");
					double humidity = hour.getDouble("humidity");
					double windSpeed = hour.getDouble("windspeed");
					double precipitation = hour.getDouble("precip");

					// Create WeatherHour entity
					WeatherHour hourlyWeather = new WeatherHour();
					hourlyWeather.setWeatherDayId(savedDay.getId()); // Set foreign key
					hourlyWeather.setTime(time);
					hourlyWeather.setTemp(temp);
					hourlyWeather.setFeelsLike(feelsLike);
					hourlyWeather.setHumidity(humidity);
					hourlyWeather.setWindSpeed(windSpeed);
					hourlyWeather.setPrecipitation(precipitation);

					hourlyWeatherList.add(hourlyWeather);
				}
			}

			// Save all hourly weather data
			weatherHourRepository.saveAll(hourlyWeatherList);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dailyWeatherList;
	}

	@Override
	public List<ColdWaveDTO> findColdWaves() {
	    try {
	        // Ensure that the query is returning the correct fields that match your DTO
	        List<Object[]> rawResults = this.weatherDayRepository.findColdWavesRaw(); 
	        
	        // Map raw results to ColdWaveDTO
	        List<ColdWaveDTO> coldWaveDTOs = new ArrayList<>();
	        for (Object[] result : rawResults) {
	            String city = (String) result[0];
	            Long groupId = (Long) result[1];
	            LocalDate startDate = ((java.sql.Date) result[2]).toLocalDate();
	            LocalDate endDate = ((java.sql.Date) result[3]).toLocalDate();
	            Long coldwaveLength = (Long) result[4];

	            coldWaveDTOs.add(new ColdWaveDTO(city, groupId, startDate, endDate, coldwaveLength));
	        }
	        
	        return coldWaveDTOs;
	    } catch (Exception e) {
	        // Log the exception or handle accordingly
	        System.out.println(e.toString());
	    }
	    return null;
	}

	
	@Override
	public List<TemperatureDTO> findMinTemperaturePerCity() {
	    try {
	        List<Object[]> rawResults = this.weatherDayRepository.findMinTemperaturePerCity();

	        List<TemperatureDTO> minTempDTOs = new ArrayList<>();
	        for (Object[] result : rawResults) {
	            String city = (String) result[0];
	            Double minTemperature = (Double) result[1];

	            // Use the new constructor to specify it's the min temperature
	            minTempDTOs.add(new TemperatureDTO(city, minTemperature, true));
	        }

	        return minTempDTOs;
	    } catch (Exception e) {
	        System.out.println(e.toString());
	    }
	    return null;
	}

	@Override
	public List<TemperatureDTO> findMaxTemperaturePerCity() {
	    try {
	        List<Object[]> rawResults = this.weatherDayRepository.findMaxTemperaturePerCity();

	        List<TemperatureDTO> maxTempDTOs = new ArrayList<>();
	        for (Object[] result : rawResults) {
	            String city = (String) result[0];
	            Double maxTemperature = (Double) result[1];

	            // Use the new constructor to specify it's the max temperature
	            maxTempDTOs.add(new TemperatureDTO(city, maxTemperature, false));
	        }

	        return maxTempDTOs;
	    } catch (Exception e) {
	        System.out.println(e.toString());
	    }
	    return null;
	}

	@Override
	public List<TemperatureDTO> findAvgTemperaturePerCityInDateRange(DateRangeDTO dateRange) {
	    try {
	    	
	    	LocalDate startDate = dateRange.getStartDateAsLocalDate();
	        LocalDate endDate = dateRange.getEndDateAsLocalDate();
	        // Call the repository method with dynamic dates
	        List<Object[]> rawResults = this.weatherDayRepository.findAvgTemperaturePerCityInDateRange(startDate, endDate);

	        List<TemperatureDTO> avgTempDTOs = new ArrayList<>();
	        for (Object[] result : rawResults) {
	            String city = (String) result[0];
	            Double avgMinTemp = (Double) result[1];
	            Double avgMaxTemp = (Double) result[2];

	            avgTempDTOs.add(new TemperatureDTO(city, avgMinTemp, avgMaxTemp));
	        }

	        return avgTempDTOs;
	    } catch (Exception e) {
	        System.out.println(e.toString());
	    }
	    return null;
	}

}
