package main;

import entities.Evenement;
import services.EvenementService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TestJDBC {

    public static void main(String[] args) {
        EvenementService es = new EvenementService();

        Evenement e1 = new Evenement(
                1, "Lancement Produit", "Description de l'événement", 
                "Corporate", LocalDateTime.now(), LocalDateTime.now().plusHours(2), 
                "Tunis", 100, "Planifié", "Public"
        );

        try {
            // Test Create
            es.create(e1);
            
            // Test Read
            List<Evenement> list = es.readAll();
            for (Evenement ev : list) {
                System.out.println(ev);
            }
            
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
