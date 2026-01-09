package com.example.applicationsuiv.kafka.consumer;


import com.example.applicationsuiv.dto.BusEvent;
import com.example.applicationsuiv.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MonitoringConsumer {

    @Autowired
    private ParentService parentService;

    // Pour éviter de compter plusieurs fois la même pénalité lors d'un même arrêt
    private String lastProcessedTarget = "";

    // Cette méthode est appelée automatiquement à chaque message Kafka
    @KafkaListener(topics = "${app.kafka.topic}", groupId = "groupe-ecole-monitoring")
    public void listen(BusEvent event) {

        // Logique de détection des pénalités (basée sur votre code original)
        if ("STOPPED".equals(event.getStatus()) && event.isPenalty()) {

            if (!event.getTarget().equals(lastProcessedTarget) && !event.getTarget().equals("ÉCOLE")) {
                System.out.println("⚠️ Pénalité détectée pour : " + event.getTarget());

                // Appel au service JPA pour incrémenter la pénalité en BDD
                parentService.appliquerPenalite(event.getTarget());

                lastProcessedTarget = event.getTarget();
            }
        }
        else if ("MOVING".equals(event.getStatus())) {
            // On réinitialise quand le bus repart
            lastProcessedTarget = "";
        }
    }
}