package fr.sts.codesporte.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.sts.codesporte.GareItem;
import fr.sts.codesporte.PorteItem;

public class GareRepository {
    private static final String TAG = "GareRepository";
    private final FirebaseFirestore db;
    private final CollectionReference garesCollection;

    private final List<PorteItem> porteList = new ArrayList<>();




    public GareRepository() {
        db = FirebaseFirestore.getInstance();
        garesCollection = db.collection("gares");
    }

    public Task<List<GareItem>> getAllGares() {
        return garesCollection.get().continueWithTask(task -> {
            if (task.isSuccessful()) {
                List<Task<List<PorteItem>>> allPorteTasks = new ArrayList<>(); // Liste des tâches de récupération de portes

                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    Task<QuerySnapshot> porteTask = documentSnapshot.getReference().collection("portes").get();
                    Task<List<PorteItem>> mappedPorteTask = porteTask.continueWith(mappedTask -> {
                        List<PorteItem> porteList = new ArrayList<>();
                        if (mappedTask.isSuccessful()) {
                            for (DocumentSnapshot porteSnapshot : mappedTask.getResult().getDocuments()) {
                                String description = porteSnapshot.getString("description");
                                String code = porteSnapshot.getString("code");
                                double latitudePorte = porteSnapshot.getDouble("latitude");
                                double longitudePorte = porteSnapshot.getDouble("longitude");
                                PorteItem porte = new PorteItem(description, code, latitudePorte, longitudePorte);
                                porteList.add(porte);
                            }
                        } else {
                            Log.e(TAG, "Erreur lors de la récupération des portes", mappedTask.getException());
                        }
                        return porteList;
                    });
                    allPorteTasks.add(mappedPorteTask); // Ajoute la tâche à la liste
                }

                return Tasks.whenAllSuccess(allPorteTasks).continueWith(mappedTask -> {
                    List<GareItem> gares = new ArrayList<>();
                    List<Object> allPorteResults = mappedTask.getResult();
                    for (int i = 0; i < task.getResult().size(); i++) {
                        QueryDocumentSnapshot documentSnapshot = (QueryDocumentSnapshot) task.getResult().getDocuments().get(i);
                        String nom = documentSnapshot.getString("nom");
                        double latitude = documentSnapshot.getDouble("latitude");
                        double longitude = documentSnapshot.getDouble("longitude");
                        List<PorteItem> porteList = (List<PorteItem>) allPorteResults.get(i);
                        GareItem gare = new GareItem(documentSnapshot.getId(), nom, porteList, latitude, longitude);
                        gares.add(gare);
                    }
                    return gares;
                });
            } else {
                Log.e(TAG, "Erreur lors de la récupération des gares", task.getException());
                return Tasks.forResult(null);
            }
        });
    }

    public interface GareFetchCallback {
        void onGareFetched(List<GareItem> gares);
    }

    public interface PorteFetchCallback {
        void onPorteFetched(List<PorteItem> portes);
    }

    public void addGare(GareItem gare) {
        // Ajouter une gare à la collection "gares"
        garesCollection.add(gare)
                .addOnSuccessListener(documentReference -> System.out.println("Gare ajoutée avec l'ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur lors de l'ajout de la gare", e));
    }

    public void deleteGare(String gareId) {
        // Supprimer une gare de la collection "gares"
        PorteRepository porteRepository = new PorteRepository(gareId);
        porteRepository.getAllPortes().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PorteItem> portes = task.getResult();
                for (PorteItem porte : portes) {
                    porteRepository.deletePorte(porte.getId());
                }
            } else {
                Log.e(TAG, "Erreur lors de la récupération des portes", task.getException());
            }
        });

        garesCollection.document(gareId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Gare supprimée avec succès"))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur lors de la suppression de la gare", e));
    }

    public void editGare(String gareId, GareItem gare) {
        // Mettre à jour les données d'une gare dans la collection "gares"
        garesCollection.document(gareId).set(gare)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Gare mise à jour avec succès"))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur lors de la mise à jour de la gare", e));
    }
}
