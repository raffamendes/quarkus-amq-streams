package com.rmendes.streams;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import com.rmendes.streams.model.Aggregation;
import com.rmendes.streams.model.TemperatureMeasurement;
import com.rmendes.streams.model.WeatherStation;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;


@ApplicationScoped
public class Streamer {

	static final String WEATHER_STATIONS_STORE = "weather-stations-store";
	private static final String WEATHER_STATIONS_TOPIC = "weather-stations";
	private static final String TEMPERATURE_VALUES_TOPIC = "temperatures";
	private static final String TEMPERATURES_AGGREGATED_TOPIC = "temperatures-aggregated";

	@Produces
	public Topology buildTopology() {
		StreamsBuilder builder = new StreamsBuilder();
		ObjectMapperSerde<WeatherStation> stationSerde = new ObjectMapperSerde<WeatherStation>(WeatherStation.class);
		ObjectMapperSerde<Aggregation> aggregationSerde = new ObjectMapperSerde<Aggregation>(Aggregation.class);
		KeyValueBytesStoreSupplier supplier = Stores.persistentKeyValueStore(WEATHER_STATIONS_STORE);
		
		GlobalKTable<Integer, WeatherStation> stations = builder.globalTable(WEATHER_STATIONS_TOPIC, Consumed.with(Serdes.Integer(), stationSerde));
		
		builder.stream(TEMPERATURE_VALUES_TOPIC,
				Consumed.with(Serdes.Integer(), Serdes.String())
				)
		.join(
				stations, 
				(stationId, timestampAndValue) -> stationId,
				(value, station) ->{
					return new TemperatureMeasurement(station.id, station.name, Double.valueOf(value));
				}
				
			)
		.groupByKey()
		.aggregate(
				Aggregation::new,
				(stationsId, value, aggregation) -> aggregation.updateFrom(value),
				Materialized.<Integer, Aggregation> as(supplier)
				.withKeySerde(Serdes.Integer())
				.withValueSerde(aggregationSerde)
				)
		.toStream()
		.to(TEMPERATURES_AGGREGATED_TOPIC, Produced.with(Serdes.Integer(), aggregationSerde));
		
		return builder.build();
		
		
	}

}
