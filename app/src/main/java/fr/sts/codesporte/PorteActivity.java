package fr.sts.codesporte;

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
import android.view.View;
import android.widget.SearchView;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PorteActivity extends AppCompatActivity {
    private List<PorteItem> listePorte = new ArrayList<>();
    private List<PorteItem> filteredListePorte = new ArrayList<>();
    private PorteAdapter porteAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portes); // Assurez-vous d'avoir un layout correspondant

        RecyclerView recyclerView = findViewById(R.id.list_code); // Assurez-vous que l'ID correspond à celui dans votre layout XML
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Supposons que vous récupériez la liste des portes de l'intent
        int positionGare = getIntent().getIntExtra("position", 0);
        listePorte.addAll(MainActivity.getGareList().get(positionGare).getPorteList());
        filteredListePorte.addAll(listePorte);

        porteAdapter = new PorteAdapter(filteredListePorte);
        recyclerView.setAdapter(porteAdapter);

        setupItemTouchHelper(recyclerView);

        porteAdapter.setOnItemClickListener(position -> {
            // Ici, vous pouvez gérer le clic sur un élément, par exemple afficher les détails de la porte
        });

        searchView = findViewById(R.id.search_code); // Assurez-vous que l'ID correspond
        setupSearchView();
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
                    PorteItem swipedPorte = filteredListePorte.get(position);
                    int actualPosition = listePorte.indexOf(swipedPorte);
                    if (direction == ItemTouchHelper.LEFT) {
                        showDeleteConfirmationDialog(actualPosition);
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        modifyPorte(actualPosition);
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
        new AlertDialog.Builder(this)
                .setTitle("Suppression")
                .setMessage("Voulez-vous vraiment supprimer cet porte ?")
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
        // Supprimez l'élément de la liste
        listePorte.remove(position);
        // Notifiez l'adaptateur du changement
        porteAdapter.notifyItemRemoved(position);
        porteAdapter.notifyItemRangeChanged(position, listePorte.size());
    }

    private void modifyPorte(int position) {
        /*Intent intent = new Intent(MainActivity.this, AddGareActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);*/
    }

    public void addPorte(View view) {
        Intent intent = new Intent(this, AddGareActivity.class);
        startActivity(intent);
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
}
