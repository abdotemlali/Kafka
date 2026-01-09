package com.example.applicationsuiv.repository;



import com.example.applicationsuiv.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    // Pour retrouver un parent par son nom (utile pour les pénalités)
    Optional<Parent> findByNom(String nom);

    // Remplace la méthode appliquerPenaliteParNom de DatabaseManager
    @Modifying
    @Transactional
    @Query("UPDATE Parent p SET p.penalites = p.penalites + 1 WHERE p.nom = ?1")
    void incrementerPenalite(String nom);
}