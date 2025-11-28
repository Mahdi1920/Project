package com.example.project.utils;

import com.example.project.models.Commande;
import com.example.project.models.OrderItem;
import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticDataProvider {

    private static List<Commande> staticCommands = null;

    public static List<Commande> getStaticCommands() {
        if (staticCommands == null) {
            staticCommands = createStaticCommands();
        }
        return new ArrayList<>(staticCommands); // Return copy
    }

    public static Commande findCommandById(String commandeId) {
        for (Commande cmd : staticCommands) {
            if (cmd.getCommandeId().equals(commandeId)) {
                return cmd;
            }
        }
        return null;
    }

    public static void updateCommandStatus(String commandeId, String newStatus) {
        for (Commande cmd : staticCommands) {
            if (cmd.getCommandeId().equals(commandeId)) {
                cmd.setStatus(newStatus);
                cmd.setUpdatedAt(Timestamp.now());
                break;
            }
        }
    }

    private static List<Commande> createStaticCommands() {
        List<Commande> commands = new ArrayList<>();

        String[] clientNames = {
                "Ahmed Ben Ali", "Fatma Trabelsi", "Mohamed Gharbi", "Salma Jendoubi",
                "Youssef Mezghani", "Leila Bouazizi", "Karim Hamdi", "Nour Sfar",
                "Amine Khalil", "Rim Ben Salem", "Mehdi Souissi", "Sarra Ayari",
                "Rami Mokrani", "Hiba Ferchichi", "Fares Maalej", "Amira Tlili",
                "Wassim Ghanmi", "Ines Karoui", "Bilel Dridi", "Marwa Chaabane"
        };

        String[] addresses = {
                "15 Avenue Habib Bourguiba, Tunis", "32 Rue de Marseille, Tunis",
                "8 Avenue de la Liberté, Tunis", "45 Rue Charles de Gaulle, Tunis",
                "22 Avenue Mohamed V, Tunis", "18 Rue de Palestine, Tunis",
                "55 Avenue de Carthage, Tunis", "12 Rue Ibn Khaldoun, Tunis",
                "28 Avenue de Paris, Tunis", "7 Rue Mongi Slim, Tunis",
                "40 Avenue Farhat Hached, Tunis", "19 Rue de Rome, Tunis",
                "33 Avenue Jugurtha, Tunis", "25 Rue Jamel Abdennasser, Tunis",
                "11 Avenue de Londres, Tunis", "48 Rue Alain Savary, Tunis",
                "36 Avenue Taieb Mhiri, Tunis", "14 Rue du Liban, Tunis",
                "50 Avenue de la République, Tunis", "9 Rue Ali Bach Hamba, Tunis"
        };

        // Tunis coordinates with small variations
        double[][] coordinates = {
                {36.8065, 10.1815}, {36.8120, 10.1790}, {36.8010, 10.1850}, {36.8095, 10.1765},
                {36.8140, 10.1820}, {36.8030, 10.1875}, {36.8085, 10.1740}, {36.8155, 10.1835},
                {36.8045, 10.1890}, {36.8105, 10.1755}, {36.8170, 10.1845}, {36.8055, 10.1905},
                {36.8115, 10.1770}, {36.8025, 10.1860}, {36.8135, 10.1785}, {36.8090, 10.1920},
                {36.8145, 10.1800}, {36.8070, 10.1935}, {36.8125, 10.1810}, {36.8080, 10.1950}
        };

        String[][] items = {
                {"Pizza Margherita", "Salade Verte"}, {"Couscous", "Brick à l'oeuf"},
                {"Tajine", "Harissa"}, {"Makloub", "Salade Mechouia"},
                {"Ojja", "Pain"}, {"Kafteji", "Thon"},
                {"Lablabi", "Harissa"}, {"Chorba", "Pain"},
                {"Mloukhia", "Riz"}, {"Kamounia", "Pain"},
                {"Poulet Rôti", "Frites"}, {"Merguez", "Salade"},
                {"Escalope Panée", "Pâtes"}, {"Poisson Grillé", "Riz"},
                {"Crevettes", "Salade"}, {"Kafteji", "Pain"},
                {"Fricassé", "Harissa"}, {"Makroudh", "Thé"},
                {"Brik Dannouni", "Salade"}, {"Mlawi", "Harissa"}
        };

        for (int i = 0; i < 20; i++) {
            Commande commande = new Commande();
            commande.setCommandeId("STATIC_CMD_" + String.format("%03d", i + 1));

            // Client info
            commande.setClientId("client_" + (i + 1));
            commande.setClientName(clientNames[i]);
            commande.setClientPhone("+216 " + (20000000 + i * 111111));
            commande.setClientAddress(addresses[i]);
            commande.setClientLatitude(coordinates[i][0]);
            commande.setClientLongitude(coordinates[i][1]);

            // Restaurant info (same for all - your restaurant)
            commande.setRestaurantId("restaurant_001");
            commande.setRestaurantName("Restaurant Le Gourmet");
            commande.setRestaurantAddress("123 Rue Principale, Tunis");
            commande.setRestaurantLatitude(36.8065);
            commande.setRestaurantLongitude(10.1815);

            // Status - all pending initially
            commande.setStatus(FirebaseHelper.STATUS_PENDING);

            // Items
            List<OrderItem> orderItems = new ArrayList<>();
            orderItems.add(new OrderItem(items[i][0], (i % 3) + 1, 12.0 + (i % 5)));
            orderItems.add(new OrderItem(items[i][1], 1, 5.0 + (i % 3)));
            commande.setItems(orderItems);

            // Calculate total
            double total = 0;
            for (OrderItem item : orderItems) {
                total += item.getSubtotal();
            }
            commande.setTotalPrice(total);

            // Timestamps
            commande.setCreatedAt(Timestamp.now());
            commande.setUpdatedAt(Timestamp.now());

            commands.add(commande);
        }

        return commands;
    }
}