package fr.sts.codesporte;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.sts.codesporte.repository.PorteRepository;

public class PorteActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final List<PorteItem> porteList = new ArrayList<>();
    private static final int ADD_PORTE_REQUEST = 1;
    private final List<PorteItem> filteredPorteList = new ArrayList<>();
    private static final int MODIFY_PORTE_REQUEST = 2;
    private PorteAdapter porteAdapter;
    private SearchView searchView;
    private GoogleMap mMap;
    private PorteRepository porteRepository = new PorteRepository(null);
    private TextView tvNoResult;

    public static List<PorteItem> getPorteList() {
        return porteList;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portes);

        initUI();
        loadPortes();
    }

    private void initUI() {
        RecyclerView recyclerView = findViewById(R.id.list_code);
        FloatingActionButton addButton = findViewById(R.id.add_codeporte);
        Button backButton = findViewById(R.id.back_button);
        searchView = findViewById(R.id.search_code);
        tvNoResult = findViewById(R.id.tvNoResult);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setTitleText();

        addButton.setOnClickListener(view -> addPorte(view));
        backButton.setOnClickListener(view -> finishActivity());
        tvNoResult.setOnClickListener(view -> addPorte(view));

        setupMapFragment();
    }

    private void setTitleText() {
        TextView titreListCode = findViewById(R.id.titreListCode);
        String nomGare = getIntent().getStringExtra("nomGare");
        titreListCode.setText("Liste des codes de la gare : " + nomGare);
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void loadPortes() {
        porteRepository = new PorteRepository(getIntent().getStringExtra("idGare"));
        porteRepository.getAllPortes().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PorteItem> portes = task.getResult();
                filteredPorteList.addAll(portes);
                RecyclerView recyclerView = findViewById(R.id.list_code);
                porteAdapter = new PorteAdapter(filteredPorteList);
                recyclerView.setAdapter(porteAdapter);
                setupItemTouchHelper(recyclerView);
                porteAdapter.setOnItemClickListener(this::onPorteItemClick);
            }
            updateUiBasedOnPortes();
        });
    }

    private void onPorteItemClick(int position) {
        if (position != RecyclerView.NO_POSITION) {
            PorteItem selectedPorte = filteredPorteList.get(position);
            zoomToPorte(selectedPorte);
        }
    }

    private void zoomToPorte(PorteItem porte) {
        double latitude = porte.getLatitude();
        double longitude = porte.getLongitude();
        LatLng porteLocation = new LatLng(latitude, longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(porteLocation, 50)); // Ajustez le niveau de zoom si nécessaire
    }

    private void finishActivity() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void updateUiBasedOnPortes() {
        if (filteredPorteList.isEmpty()) {
            tvNoResult.setVisibility(View.VISIBLE);
            hideMap();
        } else {
            tvNoResult.setVisibility(View.GONE);
            showMap();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePorteList();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.clear();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        porteRepository.getAllPortes().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PorteItem> portes = task.getResult();
                for (PorteItem porte : portes) {
                    LatLng latLng = new LatLng(porte.getLatitude(), porte.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(porte.getDescription()));
                    builder.include(latLng);  // Ajouter le point à la construction des limites
                }

                if (!portes.isEmpty()) {
                    LatLngBounds bounds = builder.build();
                    // Ajustement pour les marges, ici spécifié comme 100, mais vous pouvez ajuster selon le besoin
                    int padding = 100;
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cu);
                }
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideMap();
                } else {
                    showMap();
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String newText) {
                filteredPorteList.clear();
                if (newText.isEmpty()) {
                    filteredPorteList.addAll(porteList);
                } else {
                    List<PorteItem> filtered = porteList.stream()
                            .filter(porte -> porte.getDescription().toLowerCase().contains(newText.toLowerCase()))
                            .collect(Collectors.toList());
                    filteredPorteList.addAll(filtered);
                }
                porteAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    private void showMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            getSupportFragmentManager().beginTransaction().show(mapFragment).commit();
        }
    }

    private void hideMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(mapFragment).commit();
        }
    }

    private void setupItemTouchHelper(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // Pas de déplacement supporté
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Utilisez viewHolder.getBindingAdapterPosition() pour obtenir la position actuelle
                int position = viewHolder.getBindingAdapterPosition();
                Log.d("PorteActivity - onSwiped", "Position : " + position);

                if (position != RecyclerView.NO_POSITION) {
                    PorteItem swipedPorte = filteredPorteList.get(position);
                    int actualPosition = findIndexInMainList(swipedPorte);

                    Log.d("PorteActivity - onSwiped", "swipedPorte : " + swipedPorte);
                    Log.d("PorteActivity - onSwiped", "actualPosition : " + actualPosition);

                    if (actualPosition != -1) {
                    if (direction == ItemTouchHelper.LEFT) {
                        showDeleteConfirmationDialog(actualPosition);
                        Log.d("Gauche", "Suppression de la porte : " + swipedPorte.getDescription());
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        modifyPorte(actualPosition);
                        Log.d("Droite", "Modification de la porte : " + swipedPorte.getDescription());
                    }
                    } else {
                        Log.e("PorteActivity", "Élément non trouvé dans la liste principale.");
                    }
                    // Réinitialisation manuelle de la vue
                    viewHolder.itemView.setTranslationX(0);

                    // Si nécessaire, notifiez l'adaptateur du changement
                    porteAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (dX != 0 || isCurrentlyActive) {
                    View itemView = viewHolder.itemView;
                    Paint paint = new Paint();
                    Drawable icon;

                    if (dX > 0) {
                        // Swipe to the right (Edit action)
                        icon = ContextCompat.getDrawable(PorteActivity.this, R.drawable.ic_edit);
                        paint.setColor(ContextCompat.getColor(PorteActivity.this, R.color.orange));
                    } else {
                        // Swipe to the left (Delete action)
                        icon = ContextCompat.getDrawable(PorteActivity.this, R.drawable.ic_delete);
                        paint.setColor(ContextCompat.getColor(PorteActivity.this, R.color.red));
                    }

                    // Draw the red or orange background
                    if (dX > 0) { // Right swipe
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), paint);
                    } else { // Left swipe
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                    }

                    assert icon != null;
                    int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconLeft = itemView.getLeft() + iconMargin;
                    int iconRight = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();

                    if (dX > 0) { // Right swipe
                        icon.setBounds(iconLeft, iconTop, iconLeft + icon.getIntrinsicWidth(), iconTop + icon.getIntrinsicHeight());
                    } else { // Left swipe
                        icon.setBounds(iconRight, iconTop, iconRight + icon.getIntrinsicWidth(), iconTop + icon.getIntrinsicHeight());
                    }
                    icon.draw(c);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private int findIndexInMainList(PorteItem item) {
        Log.d("PorteActivity", "Recherche de l'index pour l'item avec ID: " + item.getId());
        for (int i = 0; i < porteList.size(); i++) {
            PorteItem currentItem = porteList.get(i);
            Log.d("PorteActivity", "Comparaison avec l'item à l'index " + i + " ayant l'ID: " + currentItem.getId());
            if (item.getId().equals(currentItem.getId())) {
                Log.d("PorteActivity", "Correspondance trouvée à l'index: " + i);
                return i;
            }
        }
        Log.e("PorteActivity", "Aucune correspondance trouvée pour l'item avec ID: " + item.getId());
        return -1;
    }


    private void showDeleteConfirmationDialog(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Suppression")
                .setMessage("Voulez-vous vraiment supprimer la porte " + porteList.get(position).getDescription() + " de la gare " + getIntent().getStringExtra("nomGare") + " ?")
                .setPositiveButton(R.string.action_yes, (dialog, which) -> {
                    // Suppression confirmée
                    deletePorte(position);
                })
                .setNegativeButton(R.string.action_no, (dialog, which) -> {
                    // Annulation de la suppression, restaurez la vue
                    porteAdapter.notifyItemChanged(position);
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deletePorte(int position) {
        if (position >= 0 && position < porteList.size()) {
            // Supprime de la liste principale
            porteList.remove(position);
            porteRepository.deletePorte(filteredPorteList.get(position).getId());

            // Mise à jour et filtrage de la liste affichée si nécessaire
            updateFilteredPorteList();

            porteAdapter.notifyItemRemoved(position);
            porteAdapter.notifyItemRangeChanged(position, porteList.size());
        }
    }

    private void modifyPorte(int position) {
        if (position >= 0 && position < porteList.size()) {
            PorteItem porteToModify = porteList.get(position);
            // Lancez une activité pour modifier la porte, passez les données nécessaires
            Intent intent = new Intent(PorteActivity.this, AddPorteActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("idGare", getIntent().getStringExtra("idGare"));
            intent.putExtra("nomGare", getIntent().getStringExtra("nomGare"));

            // Vous pouvez passer d'autres données au besoin
            startActivityForResult(intent, MODIFY_PORTE_REQUEST);
        }
    }

    public void addPorte(View view) {
        Intent intent = new Intent(this, AddPorteActivity.class);
        intent.putExtra("idGare", getIntent().getStringExtra("idGare"));
        intent.putExtra("nomGare", getIntent().getStringExtra("nomGare"));
        Log.d("PorteActivity - AddPorte", "idGare : " + getIntent().getStringExtra("idGare"));
        startActivityForResult(intent, ADD_PORTE_REQUEST);
    }

    private void updatePorteList() {
        PorteRepository porteRepository = new PorteRepository(getIntent().getStringExtra("idGare"));
        porteRepository.getAllPortes().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PorteItem> portes = task.getResult();
                porteList.clear();
                porteList.addAll(portes);
                updateFilteredPorteList();
                porteAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ADD_PORTE_REQUEST || requestCode == MODIFY_PORTE_REQUEST) && resultCode == RESULT_OK) {
            String idGare = getIntent().getStringExtra("idGare");

            PorteRepository porteRepository = new PorteRepository(idGare);
            porteRepository.getAllPortes().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<PorteItem> portes = task.getResult();
                    porteList.clear();
                    porteList.addAll(portes);
                    updateFilteredPorteList();
                    porteAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void updateFilteredPorteList() {
        // Met à jour filteredGareList basé sur votre logique de filtrage actuelle
        // Par exemple, si aucun filtre n'est appliqué, copiez simplement gareList
        filteredPorteList.clear();
        filteredPorteList.addAll(porteList);
        // Si un filtre est appliqué, refiltrez gareList ici selon vos critères de filtrage
    }

    public void annulerRetour(View view) {
        finish();
    }
}
