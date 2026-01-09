package com.example.applicationsuiv.service;


import com.example.applicationsuiv.dto.BusEvent;
import com.example.applicationsuiv.model.Parent;
import com.example.applicationsuiv.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BusSimulationService {

    @Autowired private KafkaTemplate<String, BusEvent> kafkaTemplate;
    @Autowired private ParentRepository parentRepository;

    @Value("${app.kafka.topic}") private String topic;
    @Value("${app.simulation.ecole.lat}") private double ecoleLat;
    @Value("${app.simulation.ecole.lon}") private double ecoleLon;

    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    public void demarrerSimulation() throws InterruptedException {
        double currentLat = ecoleLat;
        double currentLon = ecoleLon;

        while (true) {
            List<Parent> parents = new ArrayList<>(parentRepository.findAll());
            if (parents.isEmpty()) { Thread.sleep(5000); continue; }

            // Tri par proximité (Plus Proche Voisin)
            while (!parents.isEmpty()) {
                final double la = currentLat; final double lo = currentLon;
                Parent cible = parents.stream().min(Comparator.comparingDouble(p ->
                        calculateDistance(la, lo, p.getLatitude(), p.getLongitude()))).get();
                parents.remove(cible);

                // Déplacement
                while (calculateDistance(currentLat, currentLon, cible.getLatitude(), cible.getLongitude()) > 0.0002) {
                    currentLat += (currentLat < cible.getLatitude()) ? 0.0001 : -0.0001;
                    currentLon += (currentLon < cible.getLongitude()) ? 0.0001 : -0.0001;

                    boolean isNear = calculateDistance(currentLat, currentLon, cible.getLatitude(), cible.getLongitude()) < 0.0015;
                    envoyer("MOVING", cible.getNom(), currentLat, currentLon, parents.size()+1, isNear, false);
                    Thread.sleep(800);
                }
                // Arrêt
                envoyer("STOPPED", cible.getNom(), cible.getLatitude(), cible.getLongitude(), parents.size()+1, false, new Random().nextBoolean());
                Thread.sleep(3000);
            }
            // Retour École
            currentLat = ecoleLat; currentLon = ecoleLon;
            envoyer("PARKED", "ÉCOLE", ecoleLat, ecoleLon, 0, false, false);
            Thread.sleep(5000);
        }
    }

    private void envoyer(String status, String target, double lat, double lon, int rem, boolean near, boolean penalty) {
        BusEvent event = new BusEvent("BUS-01", lat, lon, status, target, rem, 0, penalty, near);
        kafkaTemplate.send(topic, "BUS-01", event);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2));
    }
}