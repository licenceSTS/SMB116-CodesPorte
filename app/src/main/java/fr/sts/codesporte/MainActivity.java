package fr.sts.codesporte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    // Déclaration de la liste et de l'adaptateur comme variables de la classe
    private static final List<GareItem> gareList = new ArrayList<>();
    private final List<GareItem> filteredGareList = new ArrayList<>();
    private GareAdapter gareAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.list_gare);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initGareList(); // Initialise la liste des gares

        filteredGareList.addAll(gareList); // Initialiser avec toutes les gares
        gareAdapter = new GareAdapter(filteredGareList);
        recyclerView.setAdapter(gareAdapter);

        setupItemTouchHelper(recyclerView);

        gareAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(MainActivity.this, PorteActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
        });

        searchView = findViewById(R.id.search_gare);
        setupSearchView();
    }

    private void initGareList() {
        // Créez des listes de codes pour chaque gare
        List<PorteItem> codesGareDeLyon = new ArrayList<>();
        codesGareDeLyon.add(new PorteItem("Porte A", "CodeA", 2.374, 48.844)); // Exemple de données
        codesGareDeLyon.add(new PorteItem("Porte B", "CodeB", 2.375, 48.845));

        List<PorteItem> codesGareDuNord = new ArrayList<>();
        codesGareDuNord.add(new PorteItem("Porte C", "CodeC", 2.356, 48.876));
        codesGareDuNord.add(new PorteItem("Porte D", "CodeD", 2.357, 48.877));

        // Ajoutez vos gares initiales ici, avec leurs codes respectifs
        gareList.add(new GareItem("Gare de Lyon", codesGareDeLyon, 2.323, 48.844));
        gareList.add(new GareItem("Gare du Nord", codesGareDuNord, 2.356, 48.876));
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
                    int actualPosition = gareList.indexOf(swipedGare);
                    if (direction == ItemTouchHelper.LEFT) {
                        showDeleteConfirmationDialog(actualPosition);
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        modifyGare(actualPosition);
                    }
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

    private void showDeleteConfirmationDialog(final int position) {
        new AlertDialog.Builder(this)
            .setTitle("Suppression")
            .setMessage("Voulez-vous vraiment supprimer cet gare ?")
            .setPositiveButton(R.string.action_yes, (dialog, which) -> {
                // Suppression confirmée
                deleteGare(position);
            })
            .setNegativeButton(R.string.action_no, (dialog, which) -> {
                // Annulation de la suppression, restaurez la vue
                gareAdapter.notifyItemChanged(position);
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    private void deleteGare(int position) {
        // Supprimez l'élément de la liste
        gareList.remove(position);
        // Notifiez l'adaptateur du changement
        gareAdapter.notifyItemRemoved(position);
        gareAdapter.notifyItemRangeChanged(position, gareList.size());
    }

    private void modifyGare(int position) {
        Intent intent = new Intent(MainActivity.this, AddGareActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    public void addGare(View view) {
        Intent intent = new Intent(this, AddGareActivity.class);
        startActivity(intent);
    }
}