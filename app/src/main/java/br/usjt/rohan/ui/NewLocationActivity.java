package br.usjt.rohan.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.usjt.rohan.R;

public class NewLocationActivity extends AppCompatActivity {
    private static final String TAG = "NewLocationActivity";

    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
//    private static final String KEY_LONGITUDE = "longitude";
//    private static final String KEY_LATITUDE = "latitude";
//    private static final String KEY_DT_CREATED = "dt_created";

    private EditText editTextLocationName;
    private EditText editTextLocationDesc;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        editTextLocationName = findViewById(R.id.editTextLocationName);
        editTextLocationDesc = findViewById(R.id.editTextLocationDesc);
    }

    public void saveLocation(View view) {
        String name = editTextLocationName.getEditableText().toString();
        String description = editTextLocationDesc.getEditableText().toString();

        Map<String, Object> location = new HashMap<>();
        location.put(KEY_NAME, name);
        location.put(KEY_DESCRIPTION, description);

        db.collection("Locations").document("My first Location").set(location)
                .addOnSuccessListener((result)->{
                    Toast.makeText(this, "Sucesso!", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener((error)->{
            Toast.makeText(this, "Falha!", Toast.LENGTH_SHORT).show();
        });

        startActivity(new Intent(this, DashboardActivity.class));
    }
}