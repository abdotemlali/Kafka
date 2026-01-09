package com.example.applicationsuiv.service;

import com.example.applicationsuiv.model.Parent;
import com.example.applicationsuiv.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    // Remplace DatabaseManager.ajouterParent
    public void ajouterParent(String nom, double lat, double lon) {
        Parent parent = new Parent(nom, lat, lon);
        parentRepository.save(parent);
    }

    // Remplace DatabaseManager.getParentsLocations
    public List<Parent> getTousLesParents() {
        return parentRepository.findAll();
    }

    // Remplace DatabaseManager.appliquerPenaliteParNom
    public void appliquerPenalite(String nom) {
        parentRepository.incrementerPenalite(nom);
    }
}