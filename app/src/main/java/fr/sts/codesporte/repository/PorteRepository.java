package fr.sts.codesporte.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.sts.codesporte.PorteItem;

public class PorteRepository {
    private static final String TAG = "PorteRepository";
    private FirebaseFirestore db;
    private CollectionReference portesCollection;

    public PorteRepository(String gareId) {
        db = FirebaseFirestore.getInstance();
        portesCollection = db.collection("/gares/" + gareId + "/portes");
    }

    public Task<List<PorteItem>> getAllPortesFromGare() {
        return portesCollection.get().continueWith(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Erreur lors de la récupération des portes", task.getException());
                throw task.getException();
            }

            List<PorteItem> portes = new ArrayList<>();
            for (QueryDocumentSnapshot document : task.getResult()) {
                PorteItem porte = document.toObject(PorteItem.class);
                portes.add(porte);
            }
            return portes;
        });
    }

    public void addPorte(PorteItem porte) {
        portesCollection.add(porte)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Porte ajoutée avec l'ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur lors de l'ajout de la porte", e));
    }

    public void deletePorte(String porteId) {
        portesCollection.document(porteId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Porte supprimée avec succès"))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur lors de la suppression de la porte", e));
    }

    public void editPorte(String porteId, PorteItem porte) {
        portesCollection.document(porteId).set(porte)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Porte mise à jour avec succès"))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur lors de la mise à jour de la porte", e));
    }
}
