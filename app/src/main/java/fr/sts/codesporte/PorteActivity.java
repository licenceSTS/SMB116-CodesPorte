package fr.sts.codesporte;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.sts.codesporte.repository.GareRepository;
import fr.sts.codesporte.repository.PorteRepository;

public class PorteActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final List<PorteItem> listePorte = new ArrayList<>();
    private final List<PorteItem> filteredListePorte = new ArrayList<>();
    private PorteAdapter porteAdapter;
    private SearchView searchView;
    private GareRepository gareRepository = new GareRepository();
    private PorteRepository porteRepository;

    private GoogleMap mMap;

    private static final int ADD_PORTE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portes);

        setupViews();
        setupListeners();

        String idGare = getIntent().getStringExtra("idGare");
        if (idGare == null || idGare.isEmpty()) {
            Log.e("PorteActivity", "L'ID de la gare est requis pour PorteActivity");
            finish();
            return;
        }

        porteRepository = new PorteRepository(idGare);

        retrieveAndDisplayPortes();
    }

    private void retrieveAndDisplayPortes() {
        String idGare = getIntent().getStringExtra("idGare");
        if (idGare != null) {
            porteRepository.getAllPortesFromGare().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<PorteItem> portes = task.getResult();

                    listePorte.clear();
                    listePorte.addAll(portes);
                    filteredListePorte.addAll(portes);
                    porteAdapter.notifyDataSetChanged();

                    if (mMap != null) {
                        mMap.clear();
                        for (PorteItem porte : portes) {
                            LatLng portePosition = new LatLng(porte.getLatitude(), porte.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(portePosition).title(porte.getDescription()));
                        }

                        if (!portes.isEmpty()) {
                            LatLng firstPortePosition = new LatLng(portes.get(0).getLatitude(), portes.get(0).getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPortePosition, 15));
                        }
                    }
                } else {
                    Log.e("PorteActivity", "Erreur lors de la récupération des portes", task.getException());
                }
            });
        }
    }

    private void setupViews() {
        RecyclerView recyclerView = findViewById(R.id.list_code);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        porteAdapter = new PorteAdapter(new ArrayList<>());
        recyclerView.setAdapter(porteAdapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        searchView = findViewById(R.id.search_code);
    }

    private void setupListeners() {
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        FloatingActionButton addButton = findViewById(R.id.add_codeporte);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(PorteActivity.this, AddPorteActivity.class);
            intent.putExtra("idGare", getIntent().getStringExtra("idGare"));
            startActivityForResult(intent, ADD_PORTE_REQUEST);
        });

        setupSearchView();
        setupItemTouchHelper((RecyclerView) findViewById(R.id.list_code));
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredListePorte.clear();
                if (newText.isEmpty()) {
                    filteredListePorte.addAll(listePorte);
                } else {
                    List<PorteItem> filtered = listePorte.stream()
                            .filter(porte -> porte.getDescription().toLowerCase().contains(newText.toLowerCase()))
                            .collect(Collectors.toList());
                    filteredListePorte.addAll(filtered);
                }
                porteAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    private void setupItemTouchHelper(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (direction == ItemTouchHelper.LEFT) {
                        deletePorte(position);
                    } else {
                        modifyPorte(position);
                    }
                    porteAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                // Code pour personnaliser le swipe visuellement (optionnel)
            }
        };

        new ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(recyclerView);
    }

    private void updateMapWithPortes(List<PorteItem> portes) {
        if (mMap != null) {
            mMap.clear();
            for (PorteItem porte : portes) {
                LatLng portePosition = new LatLng(porte.getLatitude(), porte.getLongitude());
                mMap.addMarker(new MarkerOptions().position(portePosition).title(porte.getDescription()));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney, Australia, and move the camera.
        mMap.clear();

        porteRepository.getAllPortesFromGare().addOnSuccessListener(portes -> {
            if (!portes.isEmpty()) {
                for (PorteItem porte : portes) {
                    LatLng porteLatLng = new LatLng(porte.getLatitude(), porte.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(porteLatLng).title(porte.getDescription()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(porteLatLng));
                }
                Log.d("PorteActivity", "Nombre de portes récupérées: " + portes.size());
            }
        }).addOnFailureListener(e ->
            Log.e("PorteActivity", "Erreur lors de la sélection des portes", e)
        );
    }

    private void updatePorteList() {
        PorteRepository porteRepository = new PorteRepository(getIntent().getStringExtra("idGare"));
        porteRepository.getAllPortesFromGare().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PorteItem> portes = task.getResult();
                listePorte.clear();
                listePorte.addAll(portes);
                updateFilteredListePorte();
                porteAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePorteList();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void showDeleteConfirmationDialog(final int actualPosition) {
        new AlertDialog.Builder(this)
                .setTitle("Suppression")
                .setMessage("Voulez-vous vraiment supprimer cette porte ?")
                .setPositiveButton(R.string.action_yes, (dialog, which) -> deletePorte(actualPosition))
                .setNegativeButton(R.string.action_no, (dialog, which) -> porteAdapter.notifyItemChanged(actualPosition)) // Réinitialise l'état visuel de l'item
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deletePorte(int actualPosition) {
        if (actualPosition >= 0 && actualPosition < listePorte.size()) {
            // Supprime de la liste principale
            listePorte.remove(actualPosition);
            porteRepository.deletePorte(filteredListePorte.get(actualPosition).getId());

            // Mise à jour et filtrage de la liste affichée si nécessaire
            updateFilteredListePorte();

            // Inutile d'appeler notifyItemRemoved et notifyItemRangeChanged en même temps
            porteAdapter.notifyDataSetChanged(); // Ceci est suffisant pour rafraîchir la vue
        }
    }

    private void updateFilteredListePorte() {
        // Met à jour filteredGareList basé sur votre logique de filtrage actuelle
        // Par exemple, si aucun filtre n'est appliqué, copiez simplement gareList
        filteredListePorte.clear();
        filteredListePorte.addAll(listePorte);
        // Si un filtre est appliqué, refiltrez gareList ici selon vos critères de filtrage
    }

    private void modifyPorte(int positionPorte) {
        PorteItem porteItem = listePorte.get(positionPorte);
        Intent intent = new Intent(this, AddPorteActivity.class);
        intent.putExtra("modify", true);
        intent.putExtra("code", porteItem.getCode());
        intent.putExtra("description", porteItem.getDescription());
        intent.putExtra("latitude", porteItem.getLatitude());
        intent.putExtra("longitude", porteItem.getLongitude());
        startActivityForResult(intent, ADD_PORTE_REQUEST);
    }

    public void addPorte(View view) {
        Intent intent = new Intent(this, AddPorteActivity.class);
        int positionGare = getIntent().getIntExtra("position", 0);
        gareRepository.getAllGares().addOnSuccessListener(new OnSuccessListener<List<GareItem>>() {
            @Override
            public void onSuccess(List<GareItem> gareItems) {
                intent.putExtra("idGare", gareItems.get(positionGare).getId());
                startActivityForResult(intent, ADD_PORTE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_PORTE_REQUEST && resultCode == RESULT_OK) {
            int positionGare = getIntent().getIntExtra("position", 0);
            gareRepository.getAllGares().addOnSuccessListener(new OnSuccessListener<List<GareItem>>() {
                @Override
                public void onSuccess(List<GareItem> gareItems) {
                    PorteRepository porteRepository = new PorteRepository(gareItems.get(positionGare).getId());
                    porteRepository.getAllPortesFromGare().addOnSuccessListener(
                            porteItems -> {
                                System.out.println("Portes: " + porteItems.size());
                                listePorte.clear();
                                listePorte.addAll(porteItems);
                                filteredListePorte.clear();
                                filteredListePorte.addAll(listePorte);
                                porteAdapter.notifyDataSetChanged();
                            }
                    );
                }
            });

        }
    }
}
