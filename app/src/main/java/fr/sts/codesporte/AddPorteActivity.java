package fr.sts.codesporte;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fr.sts.codesporte.repository.PorteRepository;

public class AddPorteActivity extends AppCompatActivity {
    private EditText editTextCode;
    private EditText editTextDescription;
    private EditText editTextLatitude;
    private EditText editTextLongitude;

    // Ajouter ces variables pour déterminer si on est en mode ajout ou modification
    private boolean isEditMode = false;
    private PorteItem existingPorteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_porte);

        int idGare = getIntent().getIntExtra("idGare", -1);
        int positionPorte = getIntent().getIntExtra("position", -1); // Supposons que la position est passée

        TextView textviewTitreAddModifGare = findViewById(R.id.textViewTitreAddModifGare);
        editTextCode = findViewById(R.id.editTextCode);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextLatitude = findViewById(R.id.editTextLatitude);
        editTextLongitude = findViewById(R.id.editTextLongitude);
        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        if (positionPorte != -1) {
            // Mode modification
            isEditMode = true;
            // Récupérez l'objet PorteItem à partir de la position ou de l'ID
            PorteItem porteItem = PorteActivity.getPorteList().get(positionPorte);
            displayPorteDetails(porteItem);

            buttonSubmit.setText("Modifier la porte");
            textviewTitreAddModifGare.setText("Modifier la porte " + porteItem.getDescription());
        }

        buttonSubmit.setOnClickListener(v -> {
            submitPorte(String.valueOf(idGare), positionPorte);
        });
    }

    private void displayPorteDetails(PorteItem porteItem) {
        editTextCode.setText(porteItem.getCode());
        editTextDescription.setText(porteItem.getDescription());
        editTextLatitude.setText(String.valueOf(porteItem.getLatitude()));
        editTextLongitude.setText(String.valueOf(porteItem.getLongitude()));
    }

    private void submitPorte(String idGare, int positionPorte) {
        String code = editTextCode.getText().toString();
        String description = editTextDescription.getText().toString();
        String latitude = editTextLatitude.getText().toString();
        String strLongitude = editTextLongitude.getText().toString();

        if (code.isEmpty() || description.isEmpty() || latitude.isEmpty() || strLongitude.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double longitude = Double.parseDouble(strLongitude);
            double lat = Double.parseDouble(latitude);

            PorteRepository porteRepository = new PorteRepository(idGare);
            if (isEditMode) {
                // Mode modification
                existingPorteItem.setCode(code);
                existingPorteItem.setDescription(description);
                existingPorteItem.setLatitude(lat);
                existingPorteItem.setLongitude(longitude);
                porteRepository.editPorte(existingPorteItem.getId(), existingPorteItem);
            } else {
                // Mode ajout
                PorteItem newPorteItem = new PorteItem(description, code, longitude, lat);
                porteRepository.addPorte(newPorteItem);
            }

            setResult(RESULT_OK);
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Erreur de format dans les coordonnées", Toast.LENGTH_SHORT).show();
        }
    }

    public void annulerRetour(View view) {
        finish();
    }
}

