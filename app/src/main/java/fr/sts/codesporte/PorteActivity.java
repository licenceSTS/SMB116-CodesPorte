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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.sts.codesporte.repository.PorteRepository;

public class PorteActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final List<PorteItem> porteList = new ArrayList<>();
    private final List<PorteItem> filteredPorteList = new ArrayList<>();
    private PorteAdapter porteAdapter;
    private SearchView searchView;
    private PorteRepository porteRepository = new PorteRepository(null);
    private GoogleMap mMap;
    private static final int ADD_PORTE_REQUEST = 1;

    public static List<PorteItem> getPorteList() {
        return porteList;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portes);

        Button backButton = findViewById(R.id.back_button);
        FloatingActionButton addButton = findViewById(R.id.add_codeporte);
        TextView titreListCode = findViewById(R.id.titreListCode);
        RecyclerView recyclerView = findViewById(R.id.list_code);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String idGare = getIntent().getStringExtra("position");
        String nomGare = getIntent().getStringExtra("nomGare");

        titreListCode.setText("Liste des codes de la gare : " + nomGare);
        backButton.setOnClickListener(view -> finish());

        porteRepository = new PorteRepository(idGare);
        Log.e("idGare", idGare);

        porteRepository.getAllPortes().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PorteItem> portes = task.getResult();

                filteredPorteList.addAll(portes); // Initialiser avec toutes les portes
                porteAdapter = new PorteAdapter(filteredPorteList);
                recyclerView.setAdapter(porteAdapter);

                setupItemTouchHelper(recyclerView);

                porteAdapter.setOnItemClickListener(position -> {
                });

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addPorte(view);
                    }
                });

                searchView = findViewById(R.id.search_code);
                setupSearchView();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney, Australia, and move the camera.
        mMap.clear();

        porteRepository.getAllPortes().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PorteItem> portes = task.getResult();
                for (PorteItem porte : portes) {
                    LatLng latLng = new LatLng(porte.getLatitude(), porte.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(porte.getDescription()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
        });
    }

    private void setupSearchView() {
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
                // Afficher ou cacher le TextView basé sur la taille de filteredGareList
                TextView tvNoResult = findViewById(R.id.tvNoResult);
                if (filteredPorteList.isEmpty()) {
                    tvNoResult.setVisibility(View.VISIBLE);
                } else {
                    tvNoResult.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    private void setupItemTouchHelper(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // Pas de déplacement supporté
            }

            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    PorteItem swipedPorte = filteredPorteList.get(position);
                    Log.d("PorteActivity", "swipedPorte: " + swipedPorte.getId() + " " + swipedPorte.getDescription());
                    int actualPosition = porteList.indexOf(swipedPorte);
                    Log.d("PorteActivity1", "onSwiped: " + actualPosition);
                    if (direction == ItemTouchHelper.LEFT) {
                        showDeleteConfirmationDialog(actualPosition);
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        modifyPorte(actualPosition);
                    }
                    viewHolder.itemView.setTranslationX(0);
                    porteAdapter.notifyItemChanged(actualPosition);
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

    private void showDeleteConfirmationDialog(final int position) {
        if (position >= 0 && position < filteredPorteList.size()) {
            PorteItem itemToDelete = filteredPorteList.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Suppression")
                    .setMessage("Voulez-vous vraiment supprimer la porte " + itemToDelete.getDescription() + " ?")
                    .setPositiveButton(R.string.action_yes, (dialog, which) -> deletePorte(position))
                    .setNegativeButton(R.string.action_no, (dialog, which) -> porteAdapter.notifyItemChanged(position))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Log.e("PorteActivity", "Position invalide : " + position);
        }
    }

    private void deletePorte(int actualPosition) {
        if (actualPosition >= 0 && actualPosition < porteList.size()) {
            // Supprime de la liste principale
            porteList.remove(actualPosition);
            porteRepository.deletePorte(filteredPorteList.get(actualPosition).getId());

            updateFilteredPorteList();

            porteAdapter.notifyItemRemoved(actualPosition);
            porteAdapter.notifyItemRangeChanged(actualPosition, filteredPorteList.size());
        }
    }

    private void updateFilteredPorteList() {
        // Met à jour filteredGareList basé sur votre logique de filtrage actuelle
        // Par exemple, si aucun filtre n'est appliqué, copiez simplement gareList
        filteredPorteList.clear();
        filteredPorteList.addAll(porteList);
        // Si un filtre est appliqué, refiltrez gareList ici selon vos critères de filtrage
    }

    private void modifyPorte(int position) {
        Intent intent = new Intent(PorteActivity.this, AddPorteActivity.class);
        intent.putExtra("idGare", getIntent().getStringExtra("idGare"));
        intent.putExtra("nomGare", getIntent().getStringExtra("nomGare"));
        intent.putExtra("positionPorte", position);
        Log.d("PorteActivity", "modifyPorte: " + position);
        startActivity(intent);
    }

    public void addPorte(View view) {
        Intent intent = new Intent(this, AddPorteActivity.class);
        intent.putExtra("nomGare", getIntent().getStringExtra("nomGare"));
        startActivityForResult(intent, ADD_PORTE_REQUEST);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updatePorteList() {
        String idGare = getIntent().getStringExtra("idGare");
        if (idGare != null) {
            PorteRepository porteRepository = new PorteRepository(idGare);
            porteRepository.getAllPortes().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<PorteItem> portes = task.getResult();
                    porteList.clear();
                    porteList.addAll(portes);
                    updateFilteredPorteList();

                    // Pas besoin de vérifier si porteAdapter est null ici car il est initialisé dans onCreate
                    porteAdapter.notifyDataSetChanged();
                }
            });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_PORTE_REQUEST && resultCode == RESULT_OK) {
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
    }
}
