package com.example.applicationsuiv.service;

import com.example.applicationsuiv.model.Parent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteOptimizerService {

    /**
     * Optimise l'ordre de passage en utilisant l'algorithme du plus proche voisin.
     */
    public List<Parent> optimiserTrajet(double ecoleLat, double ecoleLon, List<Parent> parents) {
        if (parents == null || parents.isEmpty()) return new ArrayList<>();

        List<Parent> aVisiter = new ArrayList<>(parents);
        List<Parent> itineraire = new ArrayList<>();

        double currentLat = ecoleLat;
        double currentLon = ecoleLon;

        while (!aVisiter.isEmpty()) {
            final double fLat = currentLat;
            final double fLon = currentLon;

            // Recherche du parent le plus proche de la position actuelle
            Parent plusProche = aVisiter.stream()
                    .min(Comparator.comparingDouble(p ->
                            calculateDistance(fLat, fLon, p.getLatitude(), p.getLongitude())))
                    .orElse(null);

            if (plusProche != null) {
                itineraire.add(plusProche);
                aVisiter.remove(plusProche);
                // La position actuelle devient celle du parent que l'on vient de visiter
                currentLat = plusProche.getLatitude();
                currentLon = plusProche.getLongitude();
            }
        }

        return itineraire;
    }

    /**
     * Calcule la distance euclidienne entre deux points GPS.
     * C'est cette méthode qui manquait dans votre classe.
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2));
    }
}