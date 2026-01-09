package com.example.applicationsuiv.service;



import com.example.applicationsuiv.dto.BusEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BusStateService {

    // Initialisation avec la position de l'école pour éviter que l'API renvoie null
    private BusEvent dernierEvenementBus = new BusEvent("BUS-01", 34.0000, -6.8000, "STARTING", "École", 0, 0, false, false);

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "groupe-parents-web")
    public void updateBusState(BusEvent event) {
        if (event != null && event.getLat() != 0) {
            this.dernierEvenementBus = event;
        }
    }

    public BusEvent getDernierePosition() {
        return dernierEvenementBus;
    }
}