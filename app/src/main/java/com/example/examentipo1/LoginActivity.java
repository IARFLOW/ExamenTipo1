package com.example.examentipo1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
// Importar el Log
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.examentipo1.db.DatabaseHelper;
import com.example.examentipo1.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName"; // MODIFICADO (era KEY_USER_LOGIN)

    private EditText etLoginUsuario, etLoginPassword;
    private Button btnLogin, btnRegistrarse;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Inicializar vistas
        etLoginUsuario = findViewById(R.id.etLoginUsuario);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        
        // Inicializar base de datos
        dbHelper = new DatabaseHelper(this);
        
        // Configurar listeners de botones
        btnLogin.setOnClickListener(v -> loginUser());
        btnRegistrarse.setOnClickListener(v -> goToRegister());
    }
    
    private void loginUser() {
        String login = etLoginUsuario.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();
        
        // Validar que los campos no estén vacíos
        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            // Verificar credenciales en la base de datos
            Usuario usuario = dbHelper.checkUsuario(login, password);
            
            if (usuario != null) {
                // Guardar ID y login en SharedPreferences
                SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(KEY_USER_ID, usuario.getId());
                editor.putString(KEY_USER_NAME, usuario.getNombre()); // MODIFICADO
                editor.apply();


                // Verificar que el ID se guardó correctamente
                int savedId = prefs.getInt(KEY_USER_ID, -1);
                Log.d("LoginActivity", "ID guardado en SharedPreferences: " + savedId);
                
                // Ir a la pantalla principal
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("LoginActivity", "Error al verificar credenciales", e);
            Toast.makeText(this, "Error al intentar iniciar sesión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void goToRegister() {
        startActivity(new Intent(this, RegistroActivity.class));
    }
}