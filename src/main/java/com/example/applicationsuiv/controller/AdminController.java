package com.example.applicationsuiv.controller;

import com.example.applicationsuiv.model.Parent;
import com.example.applicationsuiv.repository.ParentRepository;
import com.example.applicationsuiv.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin") // Toutes les URLs commenceront par /api/admin
public class AdminController {

    @Autowired
    private ParentService parentService;

    // Remplace l'option 2 de votre AdminApp : Voir la liste
    @GetMapping("/parents")
    public List<Parent> listerLesParents() {
        return parentService.getTousLesParents();
    }

    // Remplace l'option 1 de votre AdminApp : Ajouter un parent
    // Vous pouvez envoyer un JSON comme : {"nom": "Famille Smith", "latitude": 34.0050, "longitude": -6.7950}
    @PostMapping("/parents")
    public String ajouterParent(@RequestBody Parent parent) {
        parentService.ajouterParent(parent.getNom(), parent.getLatitude(), parent.getLongitude());
        return "✅ Parent " + parent.getNom() + " ajouté avec succès !";
    }
}