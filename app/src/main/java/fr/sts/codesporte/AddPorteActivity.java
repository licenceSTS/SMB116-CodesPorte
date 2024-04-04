package fr.sts.codesporte;


import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import fr.sts.codesporte.repository.PorteRepository;

public class AddPorteActivity extends AppCompatActivity {
    private EditText editTextCode;
    private EditText editTextDescription;
    private EditText editTextLatitude;
    private EditText editTextLongitude;
    private PorteRepository porteRepository = new PorteRepository(null);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_porte);

        String gareId = getIntent().getStringExtra("idGare");
        Log.d("AddPorteActivity - OnCreate", "Recup gareId : " + gareId);

        editTextCode = findViewById(R.id.editTextCode);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextLatitude = findViewById(R.id.editTextLatitude);
        editTextLongitude = findViewById(R.id.editTextLongitude);
        Button buttonSubmit = findViewById(R.id.buttonSubmit);
        TextView textViewTitreAddModifGare = findViewById(R.id.textViewTitreAddModifGare);

        porteRepository = new PorteRepository(gareId);

        int position = getIntent().getIntExtra("position", -1);
        if (position != -1) {
            // Mode modification
            PorteItem porteItem = PorteActivity.getPorteList().get(position);
            displayPorteItem(porteItem);

            buttonSubmit.setText("Modifier la porte");
            textViewTitreAddModifGare.setText("Modifier la porte : " + porteItem.getDescription());
        }
    }

    public void ajouterModifier(View view) {
        String code = editTextCode.getText().toString();
        String description = editTextDescription.getText().toString();
        String strLongitude = editTextLongitude.getText().toString();
        String strLatitude = editTextLatitude.getText().toString();

        if (code.isEmpty() || description.isEmpty() || strLongitude.isEmpty() || strLatitude.isEmpty()) {
            makeText(AddPorteActivity.this, "Veuillez remplir tous les champs", LENGTH_SHORT).show();
            return;
        }

        try {
            double longitude = Double.parseDouble(strLongitude);
            double latitude = Double.parseDouble(strLatitude);
            if (longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90) {
                makeText(AddPorteActivity.this, "Valeurs de longitude/latitude invalides", LENGTH_SHORT).show();
                return;
            }

            PorteItem porteItem = new PorteItem(description, code, longitude, latitude);
            int position = getIntent().getIntExtra("position", -1);
            if (position != -1) {
                // Mode modification
                Log.println(Log.INFO, "AddPorteActivity - Modif", "Ajout Modif" + porteItem + " gareId : " + porteRepository);
                porteRepository.editPorte(PorteActivity.getPorteList().get(position).getId(), porteItem);
                setResult(RESULT_OK);
                finish();
            } else {
                // Mode ajout
                Log.println(Log.INFO, "AddPorteActivity - Ajout", "Ajout porte" + porteItem + " gareId : " + porteRepository);
                porteRepository.addPorte(porteItem);
                setResult(RESULT_OK);
                finish();
            }
        } catch (NumberFormatException e) {
            makeText(AddPorteActivity.this, "Erreur de format dans les coordonnées", LENGTH_SHORT).show();
        }
    }

    private void displayPorteItem(PorteItem porteItem) {
        editTextDescription.setText(porteItem.getDescription());
        editTextCode.setText(porteItem.getCode());
        editTextLongitude.setText(String.valueOf(porteItem.getLongitude()));
        editTextLatitude.setText(String.valueOf(porteItem.getLatitude()));
    }

    public void annulerRetour(View view) {
        finish();
    }
}
