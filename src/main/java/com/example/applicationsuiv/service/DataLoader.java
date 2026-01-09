package com.example.applicationsuiv.service;

import com.example.applicationsuiv.model.Parent;
import com.example.applicationsuiv.repository.ParentRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ParentRepository parentRepository;

    @Override
    public void run(String... args) throws Exception {
        if (parentRepository.count() == 0) {
            System.out.println("📂 Importation automatique des parents depuis JSON...");

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<Parent>> typeReference = new TypeReference<List<Parent>>(){};

            // Lit le fichier dans src/main/resources/parents.json
            InputStream inputStream = TypeReference.class.getResourceAsStream("/parents.json");

            try {
                List<Parent> parents = mapper.readValue(inputStream, typeReference);
                parentRepository.saveAll(parents);
                System.out.println("✅ " + parents.size() + " familles ajoutées à la ligne de bus !");
            } catch (Exception e) {
                System.out.println("❌ Erreur lors de l'import : " + e.getMessage());
            }
        } else {
            System.out.println("ℹ️ La base de données contient déjà des parents, import sauté.");
        }
    }
}