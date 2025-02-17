package com.microsoft.weather_service.entities;

import java.time.LocalDate;

public class ColdWaveDTO {

    private String city;
    private Long groupId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long coldwaveLength;

    public ColdWaveDTO(String city, Long groupId, LocalDate startDate, LocalDate endDate, Long coldwaveLength) {
        this.city = city;
        this.groupId = groupId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coldwaveLength = coldwaveLength;
    }

    // Getters and Setters
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getColdwaveLength() {
        return coldwaveLength;
    }

    public void setColdwaveLength(Long coldwaveLength) {
        this.coldwaveLength = coldwaveLength;
    }
}
