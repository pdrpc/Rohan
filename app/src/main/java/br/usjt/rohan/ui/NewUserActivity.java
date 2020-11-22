package br.usjt.rohan.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import br.usjt.rohan.R;
import maes.tech.intentanim.CustomIntent;

public class NewUserActivity extends AppCompatActivity {

    private EditText editTextNewUserEmail;
    private EditText editTextNewUserPass;
    private FirebaseAuth auth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getString("darkMode", null).equals("ativado")){
            setTheme(R.style.AppThemeDark);
            SharedPreferences.Editor editorIf = sharedPreferences.edit();
            editorIf.putString("darkMode", "ativado");
            editorIf.apply();
        }else {
            setTheme(R.style.AppTheme);
            SharedPreferences.Editor editorIf = sharedPreferences.edit();
            editorIf.putString("darkMode", "desativado");
            editorIf.apply();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        editTextNewUserEmail = findViewById(R.id.editTextNewUserEmail);
        editTextNewUserPass = findViewById(R.id.editTextNewUserPass);
        auth = FirebaseAuth.getInstance();
        CustomIntent.customType(NewUserActivity.this, "fadein-to-fadeout");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem switch_dark_mode = menu.findItem(R.id.act_bar_switch);

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch widget_switch = switch_dark_mode.getActionView().findViewById(R.id.switch_widget);

        widget_switch.setChecked(sharedPreferences.getString("darkMode", null).equals("ativado"));

        widget_switch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked){
                SharedPreferences.Editor editorIf = sharedPreferences.edit();
                editorIf.putString("darkMode", "ativado");
                editorIf.apply();
            }else{
                SharedPreferences.Editor editorIf = sharedPreferences.edit();
                editorIf.putString("darkMode", "desativado");
                editorIf.apply();
            }
            startActivity(new Intent(this, NewUserActivity.class));
            finish();
        });
        return true;
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