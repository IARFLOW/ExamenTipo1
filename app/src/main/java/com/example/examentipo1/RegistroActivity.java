package com.example.examentipo1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.examentipo1.db.DatabaseHelper;
import com.example.examentipo1.model.Usuario;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class RegistroActivity extends AppCompatActivity {

    private EditText etRegNombre, etRegApellidos, etRegDireccion, etRegTelefono, etRegUsuario, etRegPassword;
    private ImageView ivAvatar;
    private Button btnSelImagen, btnAlta;
    private DatabaseHelper dbHelper;
    private String selectedAvatarPath;
    
    private ActivityResultLauncher<Intent> selectAvatarLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        
        // Inicializar vistas
        etRegNombre = findViewById(R.id.etRegNombre);
        etRegApellidos = findViewById(R.id.etRegApellidos);
        etRegDireccion = findViewById(R.id.etRegDireccion);
        etRegTelefono = findViewById(R.id.etRegTelefono);
        etRegUsuario = findViewById(R.id.etRegUsuario);
        etRegPassword = findViewById(R.id.etRegPassword);
        ivAvatar = findViewById(R.id.ivAvatar);
        btnSelImagen = findViewById(R.id.btnSelImagen);
        btnAlta = findViewById(R.id.btnAlta);
        
        // Inicializar base de datos
        dbHelper = new DatabaseHelper(this);
        
        // Configurar ActivityResultLauncher para seleccionar avatar
        selectAvatarLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedAvatarPath = result.getData().getStringExtra("selectedAvatarPath");
                    if (selectedAvatarPath != null) {
                        try {
                            File avatarFile = new File(selectedAvatarPath);
                            if (avatarFile.exists()) {
                                FileInputStream fis = new FileInputStream(avatarFile);
                                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                                ivAvatar.setImageBitmap(bitmap);
                                fis.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error al cargar avatar", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        );
        
        // Configurar listeners
        btnSelImagen.setOnClickListener(v -> openAvatarSelection());
        btnAlta.setOnClickListener(v -> registerUser());
    }
    
    private void openAvatarSelection() {
        Intent intent = new Intent(this, AvatarSelectionActivity.class);
        selectAvatarLauncher.launch(intent);
    }
    
    private void registerUser() {
        // Obtener valores de los campos
        String nombre = etRegNombre.getText().toString().trim();
        String apellidos = etRegApellidos.getText().toString().trim();
        String direccion = etRegDireccion.getText().toString().trim();
        String telefono = etRegTelefono.getText().toString().trim();
        String login = etRegUsuario.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();
        
        // Validar campos
        if (nombre.isEmpty() || apellidos.isEmpty() || direccion.isEmpty() || 
            telefono.isEmpty() || login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Si no se seleccionó avatar, mostrar un mensaje pero permitir continuar
        if (selectedAvatarPath == null) {
            Toast.makeText(this, "No se ha seleccionado un avatar. Se usará uno por defecto.", Toast.LENGTH_SHORT).show();
        }
        
        try {
            // Crear objeto Usuario
            Usuario nuevoUsuario = new Usuario(nombre, apellidos, direccion, telefono, login, password, selectedAvatarPath);
            
            // Guardar en la base de datos
            long id = dbHelper.insertUsuario(nuevoUsuario);
            
            if (id != -1) {
                Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                // Volver a la pantalla de login
                finish();
            } else {
                Toast.makeText(this, "Error al registrar usuario. El login ya puede existir.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("RegistroActivity", "Error al registrar usuario", e);
            Toast.makeText(this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}