package fr.sts.codesporte.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    String description = documentSnapshot.getString("description");
                    String code = documentSnapshot.getString("code");
                    double latitudePorte = documentSnapshot.getDouble("latitude");
                    double longitudePorte = documentSnapshot.getDouble("longitude");
                    PorteItem porte = new PorteItem(documentSnapshot.getId(), description, code, latitudePorte, longitudePorte);
                    portes.add(porte);
                }
                return Tasks.forResult(portes);
            } else {
                Log.e(TAG, "Erreur lors de la sélection des portes", task.getException());
                return Tasks.forException(task.getException());
            }
        });
    }

    public Task<DocumentReference> addPorte(PorteItem porte) {
        // Assurez-vous que porte n'est pas null pour éviter NullPointerException.
        if (porte == null) {
            Log.e(TAG, "L'objet PorteItem est null");
            return Tasks.forException(new IllegalArgumentException("L'objet PorteItem ne peut pas être null"));
        }
        return portesCollection.add(porte)
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
