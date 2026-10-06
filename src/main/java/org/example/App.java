package org.example;

import org.example.model.Produit;
import org.h2.tools.Server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        Server h2Server = null;
        EntityManagerFactory factory = null;

        try {
            // Démarrage de la console web H2
            h2Server = Server.createWebServer(
                    "-web",
                    "-webPort", "8082"
            ).start();

            System.out.println(
                    "Console H2 disponible sur : http://localhost:8082"
            );

            // Initialisation de JPA / Hibernate
            factory = Persistence.createEntityManagerFactory(
                    "hibernate-demo"
            );

            // Ajout des données
            ajouterCatalogue(factory);

            // Affichage de tous les produits
            afficherCatalogue(factory);

            // Recherche d'un produit par son identifiant
            rechercherProduit(factory, 2L);

            /*
             * On garde l'application active pour pouvoir consulter
             * la base H2 en mémoire depuis la console web.
             */
            System.out.println("\n----------------------------------------");
            System.out.println("Console H2 : http://localhost:8082");
            System.out.println("JDBC URL   : jdbc:h2:mem:tp1db");
            System.out.println("Utilisateur: sa");
            System.out.println("----------------------------------------");
            System.out.println("Appuyez sur Entrée pour arrêter l'application...");

            new Scanner(System.in).nextLine();

        } catch (Exception e) {

            System.err.println(
                    "Une erreur est survenue pendant l'exécution."
            );
            e.printStackTrace();

        } finally {

            if (factory != null && factory.isOpen()) {
                factory.close();
            }

            if (h2Server != null) {
                h2Server.stop();
            }
        }
    }


    private static void ajouterCatalogue(EntityManagerFactory factory) {

        EntityManager manager = factory.createEntityManager();

        try {
            manager.getTransaction().begin();

            Produit clavier = new Produit(
                    "Clavier mécanique",
                    new BigDecimal("649.90")
            );

            Produit souris = new Produit(
                    "Souris sans fil",
                    new BigDecimal("279.50")
            );

            Produit ecran = new Produit(
                    "Écran 27 pouces",
                    new BigDecimal("2199.00")
            );

            manager.persist(clavier);
            manager.persist(souris);
            manager.persist(ecran);

            manager.getTransaction().commit();

            System.out.println("Catalogue enregistré avec succès.");

        } catch (Exception e) {

            if (manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }

            System.err.println(
                    "Erreur pendant l'enregistrement des produits."
            );

            e.printStackTrace();

        } finally {
            manager.close();
        }
    }


    private static void afficherCatalogue(EntityManagerFactory factory) {

        EntityManager manager = factory.createEntityManager();

        try {

            List<Produit> catalogue = manager
                    .createQuery(
                            "SELECT p FROM Produit p",
                            Produit.class
                    )
                    .getResultList();

            System.out.println("\n--- Catalogue disponible ---");

            for (Produit produit : catalogue) {
                System.out.println(produit);
            }

        } finally {
            manager.close();
        }
    }


    private static void rechercherProduit(
            EntityManagerFactory factory,
            Long id) {

        EntityManager manager = factory.createEntityManager();

        try {

            Produit produit = manager.find(
                    Produit.class,
                    id
            );

            System.out.println(
                    "\n--- Recherche du produit " + id + " ---"
            );

            if (produit != null) {
                System.out.println(produit);
            } else {
                System.out.println(
                        "Aucun produit trouvé avec cet identifiant."
                );
            }

        } finally {
            manager.close();
        }
    }
}