package fr.sts.codesporte;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import fr.sts.codesporte.repository.GareRepository;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    // Déclaration de la liste et de l'adaptateur comme variables de la classe
    private static final List<GareItem> gareList = new ArrayList<>();
    private final List<GareItem> filteredGareList = new ArrayList<>();
    private GareAdapter gareAdapter;
    private SearchView searchView;
    public static final int ADD_GARE_REQUEST = 1;

    private GoogleMap mMap;

    private final GareRepository gareRepository = new GareRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton addButton = findViewById(R.id.add_gare);
        RecyclerView recyclerView = findViewById(R.id.list_gare);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        gareRepository.getAllGares().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<GareItem> gares = task.getResult();

                filteredGareList.addAll(gares); // Initialiser avec toutes les gares
                gareAdapter = new GareAdapter(filteredGareList);
                recyclerView.setAdapter(gareAdapter);

                setupItemTouchHelper(recyclerView);

                gareAdapter.setOnItemClickListener(position -> {
                    Intent intent = new Intent(MainActivity.this, PorteActivity.class);
                    intent.putExtra("position", filteredGareList.get(position).getId());
                    intent.putExtra("nomGare", filteredGareList.get(position).getNom());
                    startActivity(intent);
                });

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addGare(view);
                    }
                });

                searchView = findViewById(R.id.search_gare);
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
        gareRepository.getAllGares().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<GareItem> gares = task.getResult();
                for (GareItem gare : gares) {
                    LatLng latLng = new LatLng(gare.getLatitude(), gare.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(gare.getNom()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
        });
    }

    public static List<GareItem> getGareList() {
        return gareList;
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
                filteredGareList.clear();
                if (newText.isEmpty()) {
                    filteredGareList.addAll(gareList);
                } else {
                    List<GareItem> filtered = gareList.stream()
                            .filter(gare -> gare.getNom().toLowerCase().contains(newText.toLowerCase()))
                            .collect(Collectors.toList());
                    filteredGareList.addAll(filtered);
                }
                gareAdapter.notifyDataSetChanged();

                // Afficher ou cacher le TextView basé sur la taille de filteredGareList
                TextView tvNoResult = findViewById(R.id.tvNoResult);
                if (filteredGareList.isEmpty()) {
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

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Utilisez viewHolder.getBindingAdapterPosition() pour obtenir la position actuelle
                int position = viewHolder.getBindingAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    GareItem swipedGare = filteredGareList.get(position);
                    Log.d("MainActivity", "swipedGare: " + swipedGare.getId() + " " + swipedGare.getNom());
                    int actualPosition = gareList.indexOf(swipedGare);
                    Log.d("MainActivity1", "onSwiped: " + actualPosition);
                    if (direction == ItemTouchHelper.LEFT) {
                        showDeleteConfirmationDialog(actualPosition);
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        modifyGare(actualPosition);
                    }
                    // Réinitialisation manuelle de la vue
                    viewHolder.itemView.setTranslationX(0);

                    // Si nécessaire, notifiez l'adaptateur du changement
                    gareAdapter.notifyItemChanged(position);
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
                        icon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_edit);
                        paint.setColor(ContextCompat.getColor(MainActivity.this, R.color.orange));
                    } else {
                        // Swipe to the left (Delete action)
                        icon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_delete);
                        paint.setColor(ContextCompat.getColor(MainActivity.this, R.color.red));
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

    private void showDeleteConfirmationDialog(final int actualPosition) {
        new AlertDialog.Builder(this)
                .setTitle("Suppression")
                .setMessage("Voulez-vous vraiment supprimer la gare " + filteredGareList.get(actualPosition).getNom() + " ?")
                .setPositiveButton(R.string.action_yes, (dialog, which) -> deleteGare(actualPosition))
                .setNegativeButton(R.string.action_no, (dialog, which) -> gareAdapter.notifyItemChanged(actualPosition)) // Réinitialise l'état visuel de l'item
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteGare(int actualPosition) {
        if (actualPosition >= 0 && actualPosition < gareList.size()) {
            // Supprime de la liste principale
            gareList.remove(actualPosition);
            gareRepository.deleteGare(filteredGareList.get(actualPosition).getId());

            // Mise à jour et filtrage de la liste affichée si nécessaire
            updateFilteredGareList();

            // Inutile d'appeler notifyItemRemoved et notifyItemRangeChanged en même temps
            gareAdapter.notifyItemRemoved(actualPosition);
            gareAdapter.notifyItemRangeChanged(actualPosition, gareList.size());
        }
    }

    private void updateFilteredGareList() {
        // Met à jour filteredGareList basé sur votre logique de filtrage actuelle
        // Par exemple, si aucun filtre n'est appliqué, copiez simplement gareList
        filteredGareList.clear();
        filteredGareList.addAll(gareList);
        // Si un filtre est appliqué, refiltrez gareList ici selon vos critères de filtrage
    }

    private void modifyGare(int position) {
        Intent intent = new Intent(MainActivity.this, AddGareActivity.class);
        intent.putExtra("position", position);
        Log.d("MainActivity", "modifyGare: " + position);
        startActivity(intent);
    }

    public void addGare(View view) {
        Intent intent = new Intent(this, AddGareActivity.class);
        startActivityForResult(intent, ADD_GARE_REQUEST);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateGareList() {
        GareRepository gareRepository = new GareRepository();
        gareRepository.getAllGares().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<GareItem> gares = task.getResult();
                gareList.clear();
                gareList.addAll(gares);
                updateFilteredGareList();
                gareAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGareList();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_GARE_REQUEST && resultCode == RESULT_OK) {
            GareRepository gareRepository = new GareRepository();
            gareRepository.getAllGares().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<GareItem> gares = task.getResult();
                    gareList.clear();
                    gareList.addAll(gares);
                    updateFilteredGareList();
                    gareAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}