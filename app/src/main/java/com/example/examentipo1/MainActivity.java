package com.example.examentipo1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
        setContentView(R.layout.activity_main);
        
        // Copiar avatares al almacenamiento interno en el primer inicio
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean(KEY_FIRST_RUN, true);

        // Comprobar si los archivos de avatar existen en filesDir
        File filesDir = getFilesDir();
        boolean algunoEncontrado = false;
        for (String avatarName : new String[]{"1.png", "2.png", "3.png", "4.png"}) {
            File file = new File(filesDir, avatarName);
            if (file.exists() && file.length() > 0) {
                algunoEncontrado = true;
                Log.d("MainActivity", "Archivo de avatar " + avatarName + " encontrado: " + file.getAbsolutePath());
                break; // Con encontrar uno es suficiente
            }
        }

        // Si es la primera ejecución o no se encontraron archivos de avatar, copiarlos
        if (isFirstRun || !algunoEncontrado) {
            try {
                Log.d("MainActivity", "COPIANDO AVATARES: " + (isFirstRun ? "Primera ejecución" : "No se encontraron avatares"));
                FileUtils.copyAvatarsToInternalStorage(this);
                
                // Marcar como que no es la primera ejecución
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(KEY_FIRST_RUN, false);
                editor.apply();
                
                Log.d("MainActivity", "Avatares procesados y KEY_FIRST_RUN puesto a false.");
            } catch (Exception e) {
                Log.e("MainActivity", "Error al copiar avatares", e);
                Toast.makeText(this, "Error al configurar avatares. La aplicación puede mostrar imágenes por defecto.", Toast.LENGTH_LONG).show();
                // Seguimos adelante aunque haya errores, FileUtils intentará crear placehoders
            }
        } else {
            Log.d("MainActivity", "Avatares encontrados en almacenamiento interno, no es necesario copiarlos.");
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
            // Cargar avatar si existe (desde byte[])
            byte[] avatarBytes = currentUser.getAvatarData(); // MODIFICADO: Obtener byte[]
            if (avatarBytes != null && avatarBytes.length > 0) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length); // MODIFICADO: Decodificar byte[]
                    ivAvatar.setImageBitmap(bitmap);
                } catch (Exception e) { // Captura Exception general por si acaso
                    e.printStackTrace();
                    Toast.makeText(this, "Error al decodificar avatar desde BD", Toast.LENGTH_SHORT).show();
                    ivAvatar.setImageResource(R.mipmap.ic_launcher); // Opcional: imagen por defecto en error
                }
            } else {
                // Opcional: Poner una imagen por defecto si no hay avatar o está vacío
                ivAvatar.setImageResource(R.mipmap.ic_launcher); // Usa tu placeholder si tienes uno, o ic_launcher
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
        editor.remove("userName"); // MODIFICADO (usa la misma cadena que KEY_USER_NAME en LoginActivity)
        editor.apply();
        
        // Redirigir a LoginActivity
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}