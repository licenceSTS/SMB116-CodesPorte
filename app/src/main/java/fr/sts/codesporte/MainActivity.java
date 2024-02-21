package fr.sts.codesporte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Déclaration de la liste et de l'adaptateur comme variables de la classe
    private static final List<GareItem> gareList = new ArrayList<>();
    private GareAdapter gareAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation du RecyclerView
        RecyclerView recyclerView = findViewById(R.id.list_gare);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialisation de la liste des données et de l'adaptateur
        initGareList(); // Initialise la liste des gares
        gareAdapter = new GareAdapter(gareList);
        recyclerView.setAdapter(gareAdapter);

        // Configuration de ItemTouchHelper
        setupItemTouchHelper(recyclerView);
    }

    public static List<GareItem> getGareList() {
        return gareList;
    }

    private void initGareList() {
        // Exemple d'ajout de données dans la liste
        List<CodeItem> codes = new ArrayList<>();
        // Add CodeItem objects to the list as needed
        // For example: codes.add(new CodeItem("description", "code", 0.0, 0.0));

        double dummyLongitude = 0.0;
        double dummyLatitude = 0.0;

        gareList.add(new GareItem("Gare de Lyon", codes, dummyLongitude, dummyLatitude));
        gareList.add(new GareItem("Gare du Nord", codes, dummyLongitude, dummyLatitude));
    }

    private void setupItemTouchHelper(RecyclerView recyclerView) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    showDeleteConfirmationDialog(position);
                    /*gareList.remove(position);
                    gareAdapter.notifyItemRemoved(position);*/
                }
                if (direction == ItemTouchHelper.RIGHT) {
                    modifyGare(position);
                }
                // Optional: handle right swipe if needed
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
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showDeleteConfirmationDialog(final int position) {
        new AlertDialog.Builder(this)
            .setTitle("Suppression")
            .setMessage("Voulez-vous vraiment supprimer cet gare ?")
            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                // Suppression confirmée
                deleteGare(position);
            })
            .setNegativeButton(android.R.string.no, (dialog, which) -> {
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