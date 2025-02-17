package com.microsoft.weather_service.entities;

public class TemperatureDTO {

    private String city;
    private Double minTemperature;
    private Double maxTemperature;
    private Double avgMinTemp;
    private Double avgMaxTemp;

    public TemperatureDTO(String city, Double temperature, boolean isMinTemperature) {
        this.city = city;
        if (isMinTemperature) {
            this.minTemperature = temperature;
        } else {
            this.maxTemperature = temperature;
        }
    }

    public TemperatureDTO(String city, Double avgMinTemp, Double avgMaxTemp) {
        this.city = city;
        this.avgMinTemp = avgMinTemp;
        this.avgMaxTemp = avgMaxTemp;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(Double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public Double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Double getAvgMinTemp() {
        return avgMinTemp;
    }

    public void setAvgMinTemp(Double avgMinTemp) {
        this.avgMinTemp = avgMinTemp;
    }

    public Double getAvgMaxTemp() {
        return avgMaxTemp;
    }

    public void setAvgMaxTemp(Double avgMaxTemp) {
        this.avgMaxTemp = avgMaxTemp;
    }
}
