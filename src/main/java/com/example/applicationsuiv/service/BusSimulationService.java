package com.example.applicationsuiv.service;

import com.example.applicationsuiv.dto.BusEvent;
import com.example.applicationsuiv.model.Parent;
import com.example.applicationsuiv.repository.ParentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusSimulationService {

    @Autowired private KafkaTemplate<String, BusEvent> kafkaTemplate;
    @Autowired private ParentRepository parentRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.kafka.topic}") private String topic;
    @Value("${app.simulation.ecole.lat:34.0000}") private double ecoleLat;
    @Value("${app.simulation.ecole.lon:-6.8000}") private double ecoleLon;

    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    public void demarrerSimulation() throws InterruptedException {
        while (true) {
            List<Parent> parents = parentRepository.findAll();
            if (parents.isEmpty()) {
                Thread.sleep(5000);
                continue;
            }

            // 1. Optimisation : Tri par Plus Proche Voisin (Greedy TSP)
            List<Parent> itineraireOptimise = optimiserOrdrePassage(new ArrayList<>(parents));

            // 2. Récupération et simulation du trajet routier
            suivreRouteRoutiere(itineraireOptimise);

            // Retour École
            envoyer("PARKED", "ÉCOLE", ecoleLat, ecoleLon, 0, false, false);
            Thread.sleep(10000);
        }
    }

    private List<Parent> optimiserOrdrePassage(List<Parent> parents) {
        List<Parent> optimise = new ArrayList<>();
        double curLat = ecoleLat;
        double curLon = ecoleLon;

        while (!parents.isEmpty()) {
            final double fLat = curLat;
            final double fLon = curLon;
            Parent proche = parents.stream()
                    .min(Comparator.comparingDouble(p -> calculateDistance(fLat, fLon, p.getLatitude(), p.getLongitude())))
                    .orElse(null);

            if (proche != null) {
                optimise.add(proche);
                parents.remove(proche);
                curLat = proche.getLatitude();
                curLon = proche.getLongitude();
            }
        }
        return optimise;
    }

    private void suivreRouteRoutiere(List<Parent> etapes) throws InterruptedException {
        double startLat = ecoleLat;
        double startLon = ecoleLon;

        for (int i = 0; i < etapes.size(); i++) {
            Parent cible = etapes.get(i);
            int restants = etapes.size() - i;

            // Appel OSRM pour avoir les points de la route réelle
            List<double[]> routePoints = fetchOSRMRoute(startLat, startLon, cible.getLatitude(), cible.getLongitude());

            for (double[] point : routePoints) {
                double lat = point[0];
                double lon = point[1];

                boolean isNear = calculateDistance(lat, lon, cible.getLatitude(), cible.getLongitude()) < 0.0015;
                envoyer("MOVING", cible.getNom(), lat, lon, restants, isNear, false);
                Thread.sleep(500); // Vitesse de simulation
            }

            // Arrêt au point de destination
            startLat = cible.getLatitude();
            startLon = cible.getLongitude();
            envoyer("STOPPED", cible.getNom(), startLat, startLon, restants, false, new Random().nextBoolean());
            Thread.sleep(4000);
        }
    }

    private List<double[]> fetchOSRMRoute(double sLat, double sLon, double eLat, double eLon) {
        List<double[]> points = new ArrayList<>();
        try {
            String url = String.format("http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=geojson",
                    sLon, sLat, eLon, eLat);

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode coordinates = root.path("routes").get(0).path("geometry").path("coordinates");

            for (JsonNode coord : coordinates) {
                points.add(new double[]{coord.get(1).asDouble(), coord.get(0).asDouble()});
            }
        } catch (Exception e) {
            // Fallback en ligne droite si l'API échoue
            points.add(new double[]{eLat, eLon});
        }
        return points;
    }

    private void envoyer(String status, String target, double lat, double lon, int rem, boolean near, boolean penalty) {
        BusEvent event = new BusEvent("BUS-01", lat, lon, status, target, rem, 0, penalty, near);
        kafkaTemplate.send(topic, "BUS-01", event);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2));
    }
}