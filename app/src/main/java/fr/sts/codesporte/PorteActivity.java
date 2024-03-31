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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.sts.codesporte.repository.PorteRepository;

public class PorteActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final List<PorteItem> porteList = new ArrayList<>();
    private static final int MODIFY_PORTE_REQUEST = 2;
    private PorteAdapter porteAdapter;
    private SearchView searchView;
    private final List<PorteItem> filteredPorteList = new ArrayList<>();

    private GoogleMap mMap;

    private static final int ADD_PORTE_REQUEST = 1;
    private PorteRepository porteRepository = new PorteRepository(null);

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
        RecyclerView recyclerView = findViewById(R.id.list_code); // Assurez-vous que l'ID correspond à celui dans votre layout XML
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TextView titreListCode = findViewById(R.id.titreListCode);

        String nomGare = getIntent().getStringExtra("nomGare");
        titreListCode.setText("Liste des codes de la gare : " + nomGare);

        porteRepository = new PorteRepository(getIntent().getStringExtra("idGare"));

        int positionGare = getIntent().getIntExtra("position", 0);
        porteRepository.getAllPortes().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PorteItem> portes = task.getResult();
                recyclerView.setLayoutManager(new LinearLayoutManager(this));

                filteredPorteList.addAll(portes); // Initialiser avec toutes les gares
                porteAdapter = new PorteAdapter(filteredPorteList);
                recyclerView.setAdapter(porteAdapter);

                setupItemTouchHelper(recyclerView);

                porteAdapter.setOnItemClickListener(position -> {
                    // Ici, vous pouvez gérer le clic sur un élément, par exemple afficher les détails de la porte
                });
                searchView = findViewById(R.id.search_code);
                setupSearchView();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPorte(view);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Utilisez viewHolder.getBindingAdapterPosition() pour obtenir la position actuelle
                int position = viewHolder.getBindingAdapterPosition();
                Log.d("onSwiped", "position : " + position);

                if (position != RecyclerView.NO_POSITION) {
                    PorteItem swipedPorte = filteredPorteList.get(position);
                    int actualPosition = porteList.indexOf(swipedPorte);

                    Log.d("onSwiped", "swipedPorte : " + swipedPorte);
                    Log.d("onSwiped", "actualPosition : " + actualPosition);

                    if (direction == ItemTouchHelper.LEFT) {
                        showDeleteConfirmationDialog(actualPosition);
                        Log.d("Gauche", "Suppression de la porte : " + swipedPorte.getDescription());
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        modifyPorte(actualPosition);
                        Log.d("Droite", "Modification de la porte : " + swipedPorte.getDescription());
                    } else {
                        // Gérer le cas où la position n'est pas valide
                        Log.e("PorteActivity", "Position invalide lors du swipe");
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
        for (int i = 0; i < porteList.size(); i++) {
            if (porteList.get(i).getId().equals(item.getId())) {
                return i; // L'élément correspondant a été trouvé, retournez son index.
            }
        }
        return -1; // Si aucun élément correspondant n'a été trouvé, retournez -1.
    }


    private void showDeleteConfirmationDialog(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Suppression")
                .setMessage("Voulez-vous vraiment supprimer cet porte " + porteList.get(position).getDescription() + " ?")
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
            PorteItem porteToDelete = porteList.get(position);
            // Supprimez la porte de la base de données
            // Ceci est un exemple, remplacez-le par votre propre méthode de suppression dans PorteRepository
            PorteRepository porteRepository = new PorteRepository(getIntent().getStringExtra("idGare"));
            porteRepository.deletePorte(porteToDelete.getId());

            // Supprimez la porte de la liste affichée
            porteList.remove(position);
            filteredPorteList.remove(porteToDelete);  // Assurez-vous que la liste filtrée est également mise à jour
            porteAdapter.notifyItemRemoved(position);
            porteAdapter.notifyItemRangeChanged(position, porteList.size());
        }
    }

    private void modifyPorte(int position) {
        Log.d("Droite", "Position : " + position);
        if (position >= 0 && position < porteList.size()) {
            PorteItem porteToModify = porteList.get(position);
            // Lancez une activité pour modifier la porte, passez les données nécessaires
            Intent intent = new Intent(PorteActivity.this, AddPorteActivity.class);
            intent.putExtra("porteId", porteToModify.getId());
            // Vous pouvez passer d'autres données au besoin
            startActivityForResult(intent, MODIFY_PORTE_REQUEST);
        }
    }

    public void addPorte(View view) {
        Intent intent = new Intent(this, AddPorteActivity.class);
        intent.putExtra("idGare", getIntent().getStringExtra("idGare"));
        Log.d("addPorte", "idGare : " + getIntent().getStringExtra("idGare"));
        startActivityForResult(intent, ADD_PORTE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ADD_PORTE_REQUEST || requestCode == MODIFY_PORTE_REQUEST) && resultCode == RESULT_OK) {
            String idGare = getIntent().getStringExtra("idGare");

            PorteRepository porteRepository = new PorteRepository(idGare);
            porteRepository.getAllPortes().addOnSuccessListener(new OnSuccessListener<List<PorteItem>>() {
                @Override
                public void onSuccess(List<PorteItem> porteItems) {
                    // Mettez à jour les listes de portes avec les nouvelles données
                    porteList.clear();
                    porteList.addAll(porteItems);
                    filteredPorteList.clear();
                    filteredPorteList.addAll(porteItems);
                    porteAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void annulerRetour(View view) {
        finish();
    }
}
