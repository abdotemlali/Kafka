package com.example.applicationsuiv.config;


import com.example.applicationsuiv.dto.BusEvent;
import com.example.applicationsuiv.service.ParentService;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
@EnableKafkaStreams // Active Kafka Streams selon le cours [cite: 1374]
public class StreamConfig {

    @Autowired
    private ParentService parentService;

    @Bean
    public KStream<String, BusEvent> kStream(StreamsBuilder builder) {
        // Définition du flux d'entrée immuable [cite: 1350, 1389]
        KStream<String, BusEvent> busStream = builder.stream("positions-bus",
                Consumed.with(Serdes.String(), new JsonSerde<>(BusEvent.class)));

        // Transformation sans état : filtrage des pénalités [cite: 1413, 1418]
        busStream.filter((key, event) -> "STOPPED".equals(event.getStatus()) && event.isPenalty())
                .foreach((key, event) -> {
                    System.out.println("⚠️ [STREAMS] Détection automatique : " + event.getTarget());
                    parentService.appliquerPenalite(event.getTarget());
                });

        return busStream;
    }
}