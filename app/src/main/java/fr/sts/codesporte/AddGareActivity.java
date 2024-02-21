package fr.sts.codesporte;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class AddGareActivity extends Activity {
    private EditText gareNameEditText;
    private EditText longitudeEditText;
    private EditText latitudeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gare);

        gareNameEditText = findViewById(R.id.editTextGare);
        longitudeEditText = findViewById(R.id.editTextLongitude);
        latitudeEditText = findViewById(R.id.editTextLatitude);

        int position = getIntent().getIntExtra("position", -1);
        if (position != -1) {
            GareItem gareItem = MainActivity.getGareList().get(position);
            displayGareItem(gareItem);
        }
    }

    private void displayGareItem(GareItem gareItem) {
        gareNameEditText.setText(gareItem.getName());
        longitudeEditText.setText(String.valueOf(gareItem.getLongitude()));
        latitudeEditText.setText(String.valueOf(gareItem.getLatitude()));
    }
}