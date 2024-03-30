package fr.sts.codesporte;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fr.sts.codesporte.repository.PorteRepository;

public class AddPorteActivity extends Activity {
    private EditText editTextCode;
    private EditText editTextDescription;
    private EditText editTextLatitude;
    private EditText editTextLongitude;

    private PorteRepository porteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_porte);

        String idGare = getIntent().getStringExtra("idGare");
        String porteId = getIntent().getStringExtra("porteId");

        Button buttonSubmit = findViewById(R.id.buttonSubmit);
        TextView textViewTitreAddModifPorte = findViewById(R.id.textViewTitreAddModifPorte);

        editTextCode = findViewById(R.id.editTextCode);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextLatitude = findViewById(R.id.editTextLatitude);
        editTextLongitude = findViewById(R.id.editTextLongitude);

        porteRepository = new PorteRepository(idGare);

        int idPorte = getIntent().getIntExtra("positionPorte", -1);
        Log.e("positionPorte", String.valueOf(idPorte));
        if (idPorte != -1) {
            // Mode modification
            PorteItem porteItem = PorteActivity.getPorteList().get(idPorte);
            displayPorteItem(porteItem);

            buttonSubmit.setText("Modifier la porte");
            textViewTitreAddModifPorte.setText("Modifier la porte " + porteItem.getDescription());
        }
    }

    public void ajouterModifier(View view) {
        String codePorte = editTextCode.getText().toString();
        String nomPorte = editTextDescription.getText().toString();
        String strLongitude = editTextLongitude.getText().toString();
        String strLatitude = editTextLatitude.getText().toString();

        if (codePorte.isEmpty() || nomPorte.isEmpty() || strLongitude.isEmpty() || strLatitude.isEmpty()) {
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
            PorteItem porteItem = new PorteItem(nomPorte, codePorte, longitude, latitude);
            int position = getIntent().getIntExtra("position", -1);
            if (position != -1) {
                // Mode modification
                porteRepository.editPorte(PorteActivity.getPorteList().get(position).getId(), porteItem);
                setResult(RESULT_OK);
                finish();
            } else {
                porteRepository.addPorte(porteItem);
                setResult(RESULT_OK);
                finish();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Erreur de format dans les coordonnées", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayPorteItem(PorteItem porteItem) {
        editTextCode.setText(porteItem.getCode());
        editTextDescription.setText(porteItem.getDescription());
        editTextLatitude.setText(String.valueOf(porteItem.getLatitude()));
        editTextLongitude.setText(String.valueOf(porteItem.getLongitude()));
    }

    public void annulerRetour(View view) {
        finish();
    }
}


