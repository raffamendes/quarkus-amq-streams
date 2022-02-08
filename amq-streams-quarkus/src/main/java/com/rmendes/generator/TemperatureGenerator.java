package com.rmendes.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import com.rmendes.model.WeatherStation;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;

@ApplicationScoped
public class TemperatureGenerator {

	private static final Logger LOG = Logger.getLogger(TemperatureGenerator.class);

	private Random random = new Random();

	private List<WeatherStation> stations = List.of(
			new WeatherStation(1, "Hamburg", 13),
			new WeatherStation(2, "Snowdonia", 5),
			new WeatherStation(3, "Boston", 11),
			new WeatherStation(4, "Tokio", 16),
			new WeatherStation(5, "Cusco", 12),
			new WeatherStation(6, "Svalbard", -7),
			new WeatherStation(7, "Porthsmouth", 11),
			new WeatherStation(8, "Oslo", 7),
			new WeatherStation(9, "Marrakesh", 20));

	@Outgoing("temperatures")
	public Record<Integer, String> generate(){
		WeatherStation s = stations.get(random.nextInt(stations.size()));
		Double temperature = BigDecimal.valueOf(random.nextGaussian() * 15 +s.averageTemperature)
				.setScale(1, RoundingMode.HALF_UP)
				.doubleValue();
		LOG.infov("stations: {0}, temperature: {1}", s.name,temperature);
		return Record.of(s.id, temperature.toString());
	}

	@Outgoing("weather-stations")                                          
	public Multi<Record<Integer, String>> weatherStations() {
		return Multi.createFrom().items(stations.stream()
				.map(s -> Record.of(
						s.id,
						"{ \"id\" : " + s.id +
						", \"name\" : \"" + s.name + "\" }"))
				);
	}

}
