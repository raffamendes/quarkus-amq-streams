package com.rmendes.streams.model;

public class TemperatureMeasurement {

	public Integer stationId;
	public String stationName;
	public Double value;

	public TemperatureMeasurement(Integer stationId, String stationName, Double value) {
		this.stationId = stationId;
		this.stationName = stationName;
		this.value = value;
	}

}
