package fr.sts.codesporte;

import android.os.Bundle;
import android.util.Log;
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
    private PorteRepository porteRepository;
    private String porteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_porte);

        editTextCode = findViewById(R.id.editTextCode);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextLatitude = findViewById(R.id.editTextLatitude);
        editTextLongitude = findViewById(R.id.editTextLongitude);
        Button addButton = findViewById(R.id.buttonSubmit);
        TextView titreAjoutPorte = findViewById(R.id.textViewTitreAddModifPorte);

        boolean modify = getIntent().getBooleanExtra("modify", false);
        porteId = getIntent().getStringExtra("porteId");

        if (modify) {
            // Mode modification
            editTextCode.setText(getIntent().getStringExtra("code"));
            editTextDescription.setText(getIntent().getStringExtra("description"));
            editTextLatitude.setText(String.valueOf(getIntent().getDoubleExtra("latitude", 0)));
            editTextLongitude.setText(String.valueOf(getIntent().getDoubleExtra("longitude", 0)));
            addButton.setText("Modifier la porte");
            titreAjoutPorte.setText("Modifier la porte");
        }
    }

    public void ajouterModifier(View view) {
        String description = editTextDescription.getText().toString();
        String code = editTextCode.getText().toString();
        String strLatitude = editTextLatitude.getText().toString();
        String strLongitude = editTextLongitude.getText().toString();

        if (description.isEmpty() || code.isEmpty() || strLongitude.isEmpty() || strLatitude.isEmpty()) {
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

            PorteItem porteItem = new PorteItem(description, code, latitude, longitude);
            porteId = getIntent().getStringExtra("porteId");
            boolean modify = getIntent().getBooleanExtra("modify", false);

            if (modify) {
                // Mode modification
                porteRepository.editPorte(porteId, porteItem);
            } else {
                // Mode ajout
                porteRepository.addPorte(porteItem);
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
