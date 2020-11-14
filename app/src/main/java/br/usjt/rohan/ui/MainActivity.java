package br.usjt.rohan.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import br.usjt.rohan.R;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPass;
    private FirebaseAuth auth;

    public static void info(String s){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPass = findViewById(R.id.editTextPass);
        auth = FirebaseAuth.getInstance();
    }

    public void newUser (View view){
        startActivity(new Intent(this, NewUserActivity.class));
    }

    public void login (View v){
        String email = editTextEmail.getEditableText().toString();
        String senha = editTextPass.getEditableText().toString();
        auth.signInWithEmailAndPassword(email, senha).addOnSuccessListener((result)->{
            Toast.makeText(this, "Sucesso!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener((error)->{
            Toast.makeText(this, "Falha!", Toast.LENGTH_SHORT).show();
        });
        startActivity(new Intent(this, DashboardActivity.class));
    }

    public void onLocationChanged(Location location){

    }

    public void  onStatusChanged(String provider, int Status, Bundle extras){

    }
}