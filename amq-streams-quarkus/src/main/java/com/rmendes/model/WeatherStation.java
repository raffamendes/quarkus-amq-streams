package com.rmendes.model;

public class WeatherStation {
	
	public Integer id;
	
	public String name;
	
	public Integer averageTemperature;

	public WeatherStation(Integer id, String name, Integer averageTemperature) {
		super();
		this.id = id;
		this.name = name;
		this.averageTemperature = averageTemperature;
	}
	
	

}
