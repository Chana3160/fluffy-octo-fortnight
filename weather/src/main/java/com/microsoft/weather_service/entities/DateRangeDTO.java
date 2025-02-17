package com.microsoft.weather_service.entities;

import java.time.LocalDate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateRangeDTO {
    private String startDate;
    private String endDate;

    // Convert the start and end date strings to LocalDate
    public LocalDate getStartDateAsLocalDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(startDate, formatter);
    }

    public LocalDate getEndDateAsLocalDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(endDate, formatter);
    }

    // Getters and setters
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
