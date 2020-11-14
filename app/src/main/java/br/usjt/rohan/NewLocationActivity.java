package br.usjt.rohan;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewLocationActivity extends AppCompatActivity {

    private EditText editTextLocationName;
    private EditText editTextLocationDesc;
    private Button saveLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);
    }

    public void saveLocation(View view) {
        String name = editTextLocationName.getEditableText().toString();
        String desc = editTextLocationDesc.getEditableText().toString();

    }
}