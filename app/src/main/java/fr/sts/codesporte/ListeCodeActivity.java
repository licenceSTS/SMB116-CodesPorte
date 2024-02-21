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

        int position = getIntent().getIntExtra("position", -1);
        if (position != -1) {
            List<CodeItem> codes = MainActivity.getGareList().get(position).getCodes();
            codeAdapter = new CodeAdapter(codes);
            recyclerView.setAdapter(codeAdapter);
        }
    }
}