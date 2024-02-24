package fr.sts.codesporte;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddGareActivity extends Activity {
    private EditText gareNameEditText;
    private EditText longitudeEditText;
    private EditText latitudeEditText;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gare);

        gareNameEditText = findViewById(R.id.editTextGare);
        longitudeEditText = findViewById(R.id.editTextLongitude);
        latitudeEditText = findViewById(R.id.editTextLatitude);

        Button addButton = findViewById(R.id.modifsavegare_button);
        TextView titreAjoutGare = findViewById(R.id.textViewTitreAddModifGare);

        int position = getIntent().getIntExtra("position", -1);
        if (position != -1) {
            addButton.setText("Modifier la gare");
            titreAjoutGare.setText("Modifier la gare");
            GareItem gareItem = MainActivity.getGareList().get(position);
            displayGareItem(gareItem);
        } else {
            addButton.setText("Ajouter la gare");
            titreAjoutGare.setText("Ajouter la gare");
        }
    }

    private void displayGareItem(GareItem gareItem) {
        gareNameEditText.setText(gareItem.getName());
        longitudeEditText.setText(String.valueOf(gareItem.getLongitude()));
        latitudeEditText.setText(String.valueOf(gareItem.getLatitude()));
    }

    public void ajoutermodifier(View view) {
        String name = gareNameEditText.getText().toString();
        double longitude = Double.parseDouble(longitudeEditText.getText().toString());
        double latitude = Double.parseDouble(latitudeEditText.getText().toString());
    }

    public void annuler(View view) {
        finish();
    }
}