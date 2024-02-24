package fr.sts.codesporte;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListeCodeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CodeAdapter codeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_code);

        recyclerView = findViewById(R.id.list_code);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Récupérez la position de la gare sélectionnée
        int position = getIntent().getIntExtra("position", -1);
        if (position != -1) {
            // Obtenez la gare sélectionnée à partir de la liste des gares dans MainActivity
            GareItem selectedGare = MainActivity.getGareList().get(position);
            // Obtenez les codes (portes) associés à la gare sélectionnée
            List<CodeItem> codes = selectedGare.getCodes();
            // Initialisez l'adaptateur avec la liste des codes et associez-le au RecyclerView
            codeAdapter = new CodeAdapter(codes);
            recyclerView.setAdapter(codeAdapter);
        }
    }

}