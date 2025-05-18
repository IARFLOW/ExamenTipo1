package com.example.examentipo1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.examentipo1.db.DatabaseHelper;
import com.example.examentipo1.model.Usuario;
import com.example.examentipo1.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_LOGIN = "userLogin";
    private static final String KEY_FIRST_RUN = "firstRun";
    
    private EditText etNombre, etApellidos, etDireccion, etTelefono;
    private TextView tvLoginPrincipal;
    private ImageView ivAvatar;
    private Button btnModificar, btnCerrarSesion;
    private DatabaseHelper dbHelper;
    private int userId;
    private Usuario currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Configurar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Copiar avatares al almacenamiento interno en el primer inicio
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean(KEY_FIRST_RUN, true);
        
        if (isFirstRun) {
            FileUtils.copyAvatarsToInternalStorage(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_FIRST_RUN, false);
            editor.apply();
        }
        
        // Verificar si el usuario está autenticado
        userId = prefs.getInt(KEY_USER_ID, -1);
        
        if (userId == -1) {
            // Usuario no autenticado, redirigir a LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etDireccion = findViewById(R.id.etDireccion);
        etTelefono = findViewById(R.id.etTelefono);
        tvLoginPrincipal = findViewById(R.id.tvLoginPrincipal);
        ivAvatar = findViewById(R.id.ivAvatar);
        btnModificar = findViewById(R.id.btnModificar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        
        // Inicializar base de datos
        dbHelper = new DatabaseHelper(this);
        
        // Cargar datos del usuario
        loadUserData();
        
        // Configurar botones
        btnModificar.setOnClickListener(v -> updateUserData());
        btnCerrarSesion.setOnClickListener(v -> logoutUser());
    }
    
    private void loadUserData() {
        // Obtener datos del usuario de la base de datos
        currentUser = dbHelper.getUsuarioById(userId);
        
        if (currentUser != null) {
            // Mostrar datos en la interfaz
            etNombre.setText(currentUser.getNombre());
            etApellidos.setText(currentUser.getApellidos());
            etDireccion.setText(currentUser.getDireccion());
            etTelefono.setText(currentUser.getTelefono());
            tvLoginPrincipal.setText(currentUser.getLogin());
            
            // Cargar avatar si existe
            if (currentUser.getAvatarPath() != null && !currentUser.getAvatarPath().isEmpty()) {
                try {
                    File avatarFile = new File(currentUser.getAvatarPath());
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
        } else {
            Toast.makeText(this, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show();
            // En caso de error, cerrar sesión
            logoutUser();
        }
    }
    
    private void updateUserData() {
        // Actualizar objeto usuario con los datos de la interfaz
        currentUser.setNombre(etNombre.getText().toString().trim());
        currentUser.setApellidos(etApellidos.getText().toString().trim());
        currentUser.setDireccion(etDireccion.getText().toString().trim());
        currentUser.setTelefono(etTelefono.getText().toString().trim());
        
        // Guardar cambios en la base de datos
        int rowsUpdated = dbHelper.updateUsuario(currentUser);
        
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al actualizar datos", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void logoutUser() {
        // Limpiar las preferencias de usuario
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_LOGIN);
        editor.apply();
        
        // Redirigir a LoginActivity
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}