package fr.sts.codesporte;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation du RecyclerView
        RecyclerView recyclerView = findViewById(R.id.list_gare);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Création de la liste des données
        List<GareItem> gareList = new ArrayList<>();

        // Exemple d'ajout de données dans la liste
        gareList.add(new GareItem("Gare de Lyon", 5));
        gareList.add(new GareItem("Gare du Nord", 3));

        // Configuration de l'adaptateur avec la liste des données
        recyclerView.setAdapter(new GareAdapter(gareList));
    }
}