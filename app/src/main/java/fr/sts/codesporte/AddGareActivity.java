package fr.sts.codesporte;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fr.sts.codesporte.repository.GareRepository;

public class  AddGareActivity extends Activity {
    private EditText gareNameEditText;
    private EditText longitudeEditText;
    private EditText latitudeEditText;

    private GareRepository gareRepository = new GareRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gare);

        Button addButton = findViewById(R.id.modifsavegare_button);
        TextView titreAjoutGare = findViewById(R.id.textViewTitreAddModifGare);

        gareNameEditText = findViewById(R.id.editTextGare);
        longitudeEditText = findViewById(R.id.editTextLongitude);
        latitudeEditText = findViewById(R.id.editTextLatitude);

        int position = getIntent().getIntExtra("position", -1);
        if (position != -1) {
            // Mode modification
            GareItem gareItem = MainActivity.getGareList().get(position);
            displayGareItem(gareItem);

            addButton.setText("Modifier la gare");
            titreAjoutGare.setText("Modifier la gare");
        }
    }

    public void ajouterModifier(View view) {
        String nomGare = gareNameEditText.getText().toString();
        String strLongitude = longitudeEditText.getText().toString();
        String strLatitude = latitudeEditText.getText().toString();


        if (nomGare.isEmpty() || strLongitude.isEmpty() || strLatitude.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            double longitude = Double.parseDouble(strLongitude);
            double latitude = Double.parseDouble(strLatitude);
            if (longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90) {
                Toast.makeText(this, "Valeurs de longitude/latitude invalides", Toast.LENGTH_SHORT).show();
                return;
            }
            GareItem gareItem = new GareItem(nomGare, null, longitude, latitude);
            gareRepository.addGare(gareItem);
            setResult(RESULT_OK);
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Erreur de format dans les coordonnées", Toast.LENGTH_SHORT).show();
        }
    }


    private void displayGareItem(GareItem gareItem) {
        gareNameEditText.setText(gareItem.getNom());
        longitudeEditText.setText(String.valueOf(gareItem.getLongitude()));
        latitudeEditText.setText(String.valueOf(gareItem.getLatitude()));
    }

    public void annulerRetour(View view) {
        finish();
    }
}