//package tn.esprit.project;
//
//import android.content.Context;
//import android.util.Log;
//
//import java.util.List;
//
//import tn.esprit.project.models.Restaurant;
//import tn.esprit.project.utils.MyDatabase;
//
//public class TestRestaurantActivity {
//
//    public static void runCRUD(Context context) {
//
//        // 1️⃣ Obtenir la base
//        MyDatabase db = MyDatabase.getInstance(context);
//
//        // 2️⃣ Créer un restaurant
//        Restaurant r1 = new Restaurant("Pizza House", "123 Rue Main");
//        long id1 = db.restaurantDAO().insert(r1);
//        r1.setId((int) id1);
//        Log.d("CRUD_TEST", "Restaurant ajouté : " + r1.getName());
//
//
//
//        // 4️⃣ Mettre à jour
//        r1.setName("Pizza Heaven");
//        db.restaurantDAO().update(r1);
//        Log.d("CRUD_TEST", "Restaurant mis à jour : " + r1.getName());
//
//
//
//        // 6️⃣ Supprimer
//        db.restaurantDAO().delete(r1);
//        Log.d("CRUD_TEST", "Restaurant supprimé : " + r1.getName());
//
//        // 7️⃣ Vérification finale
//
//  }
//}
