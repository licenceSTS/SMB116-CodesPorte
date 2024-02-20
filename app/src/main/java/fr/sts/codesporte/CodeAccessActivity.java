package fr.sts.codesporte;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;

public class CodeAccessActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_access);

        // Récupération des données de l'intent
        String namePorte = getIntent().getStringExtra("EXTRA_GARE_NAME");

        // Récupération des références aux vues EditText
        EditText editTextPorteName = findViewById(R.id.editTextPorte);
        EditText editTextCode = findViewById(R.id.editTextCode);
        EditText editTextLongitude = findViewById(R.id.editTextLongitude);
        EditText editTextLatitude = findViewById(R.id.editTextLatitude);

        // Remplissage des champs avec les données
        editTextPorteName.setText(namePorte);
        editTextCode.setText(codePorte);
        editTextLongitude.setText(longitudePorte);
        editTextLatitude.setText(latitudePorte);
    }

    MaterialButton modifsaveButton = findViewById(R.id.modifsave_button);
    // Pour modifier
    //modifsaveButton.setText("Modifier");

    // Pour enregistrer
    //modifsaveButton.setText("Enregistrer");

}
