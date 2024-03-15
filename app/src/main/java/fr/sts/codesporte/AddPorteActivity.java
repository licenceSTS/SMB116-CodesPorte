package fr.sts.codesporte;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fr.sts.codesporte.repository.PorteRepository;

public class AddPorteActivity extends AppCompatActivity {
    private EditText editTextCode;
    private EditText editTextDescription;

    private EditText editTextLatitude;

    private EditText editTextLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_porte);

        String idGare = getIntent().getStringExtra("idGare");

        editTextCode = findViewById(R.id.editTextCode);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextLatitude = findViewById(R.id.editTextLatitude);
        editTextLongitude = findViewById(R.id.editTextLongitude);
        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCode.getText().toString();
                String description = editTextDescription.getText().toString();
                String latitude = editTextLatitude.getText().toString();
                String strLongitude = editTextLongitude.getText().toString();

                if (code.isEmpty() || description.isEmpty()) {
                    Toast.makeText(AddPorteActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }
                double longitude = Double.parseDouble(strLongitude);
                double lat = Double.parseDouble(latitude);
                PorteItem porteItem = new PorteItem(description,code, longitude, lat);
                PorteRepository porteRepository = new PorteRepository(getIntent().getStringExtra("idGare"));
                porteRepository.addPorte(porteItem);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
