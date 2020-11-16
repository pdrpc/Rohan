package br.usjt.rohan.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import br.usjt.rohan.R;

public class NewUserActivity extends AppCompatActivity {

    private EditText editTextNewUserEmail;
    private EditText editTextNewUserPass;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        editTextNewUserEmail = findViewById(R.id.editTextNewUserEmail);
        editTextNewUserPass = findViewById(R.id.editTextNewUserPass);

        auth = FirebaseAuth.getInstance();
    }

    public void newUserSignUp (View view){
        String email = editTextNewUserEmail.getEditableText().toString();
        String senha = editTextNewUserPass.getEditableText().toString();

        auth.createUserWithEmailAndPassword(email, senha).addOnSuccessListener((result)->{
            Toast.makeText(this, result.getUser().getEmail(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }).addOnFailureListener((error-> {
            Toast.makeText(this, "Email ou senha inválidos!", Toast.LENGTH_LONG).show();
            error.printStackTrace();
        }));

    }
}