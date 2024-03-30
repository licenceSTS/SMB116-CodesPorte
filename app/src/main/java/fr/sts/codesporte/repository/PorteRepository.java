package fr.sts.codesporte.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import fr.sts.codesporte.PorteItem;

public class PorteRepository {
    private static final String TAG = "PorteRepository";
    private final FirebaseFirestore db;
    private final CollectionReference portesCollection;

    public PorteRepository(String gareId) {
        db = FirebaseFirestore.getInstance();
        portesCollection = db.collection("/gares/" + gareId + "/portes");
    }

    public Task<List<PorteItem>> getAllPortes() {
        return portesCollection.get().continueWithTask(task -> {
            if (task.isSuccessful()) {
                List<PorteItem> portes = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    String id = documentSnapshot.getId();
                    String code = documentSnapshot.getString("code");
                    String description = documentSnapshot.getString("description");
                    double latitude = documentSnapshot.getDouble("latitude");
                    double longitude = documentSnapshot.getDouble("longitude");
                    PorteItem porte = new PorteItem(id,code, description, latitude, longitude);
                    portes.add(porte);
                }
                return Tasks.forResult(portes);
            } else {
                Log.e(TAG, "Erreur lors de la récupération des portes", task.getException());
                return Tasks.forResult(null);
            }
        });
    }

    public Task<PorteItem> getPorteById(String id) {
        return portesCollection.document(id).get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String code = document.getString("code");
                    String description = document.getString("description");
                    Double latitude = document.getDouble("latitude");
                    Double longitude = document.getDouble("longitude");
                    return new PorteItem(id, code, description, latitude, longitude);
                } else {
                    Log.e(TAG, "No Porte found with id: " + id);
                    return null;
                }
            } else {
                throw task.getException();
            }
        });
    }

    public Task<Void> editPorte(String porteId, PorteItem porte) {
        return portesCollection.document(porteId).set(porte)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Porte mise à jour avec succès"))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur lors de la mise à jour de la porte", e));
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

    public interface PorteFetchCallback {
        void onPorteFetched(List<PorteItem> portes);
    }
}
