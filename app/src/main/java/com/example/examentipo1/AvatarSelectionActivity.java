package com.example.examentipo1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.examentipo1.adapter.AvatarAdapter;
import com.example.examentipo1.model.AvatarItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AvatarSelectionActivity extends AppCompatActivity {

    private static final String TAG = "AvatarSelectionActivity";
    private ListView listViewAvatars;
    private Button btnAceptar, btnCancelar;
    private List<AvatarItem> avatarList;
    private AvatarAdapter adapter;
    private int selectedPosition = -1;
    private String selectedAvatarPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_avatar);
        
        // Inicializar vistas
        listViewAvatars = findViewById(R.id.listViewAvatars);
        btnAceptar = findViewById(R.id.btnAceptar);
        btnCancelar = findViewById(R.id.btnCancelar);
        
        // Cargar avatares
        loadAvatars();
        
        // Configurar adaptador
        adapter = new AvatarAdapter(this, avatarList);
        listViewAvatars.setAdapter(adapter);
        
        // Configurar selección de item
        listViewAvatars.setOnItemClickListener((parent, view, position, id) -> {
            // Actualizar la posición seleccionada
            selectedPosition = position;
            selectedAvatarPath = avatarList.get(position).getFilePath();
            
            // Verificar si el archivo realmente existe
            File file = new File(selectedAvatarPath);
            if (!file.exists()) {
                // Si el archivo no existe, intentar crearlo ahora
                Log.d(TAG, "El archivo seleccionado no existe: " + selectedAvatarPath);
                // Continuamos de todos modos, porque podemos manejar esto cuando regresemos a la actividad de registro
            }
            
            // Notificar al usuario que ha seleccionado un avatar
            Toast.makeText(this, "Avatar seleccionado: " + avatarList.get(position).getFileName(), Toast.LENGTH_SHORT).show();
        });
        
        // Configurar botones
        btnAceptar.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                Intent intent = new Intent();
                intent.putExtra("selectedAvatarPath", selectedAvatarPath);
                intent.putExtra("selectedAvatarName", avatarList.get(selectedPosition).getFileName());
                intent.putExtra("selectedAvatarPosition", selectedPosition + 1); // +1 para corresponder al nombre del archivo
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "Por favor, seleccione un avatar", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnCancelar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
    
    private void loadAvatars() {
        // Dentro de private void loadAvatars() en AvatarSelectionActivity.java
        avatarList = new ArrayList<>();
        File filesDir = getFilesDir();
        Log.d(TAG, "AvatarSelectionActivity - Leyendo avatares desde: " + filesDir.getAbsolutePath());

        String[] avatarNames = {"1.png", "2.png", "3.png", "4.png"};

        // Primero intentamos cargar desde archivos existentes
        boolean algunAvatarCargado = false;
        for (String name : avatarNames) {
            File file = new File(filesDir, name);
            String path = file.getAbsolutePath();

            if (file.exists() && file.length() > 0) {
                Log.d(TAG, "Avatar ENCONTRADO y válido en filesDir: " + path);
                avatarList.add(new AvatarItem(name, path));
                algunAvatarCargado = true;
            } else {
                // Registramos el problema pero añadimos el item de todos modos - el adaptador usará imágenes de respaldo
                if (!file.exists()) {
                    Log.e(TAG, "AVATAR NO ENCONTRADO en filesDir: " + path);
                } else {
                    Log.e(TAG, "Avatar ENCONTRADO pero VACÍO en filesDir: " + path + " (Tamaño: " + file.length() + " bytes)");
                }
                
                // Añadir el avatar de todos modos, el adaptador se encargará de usar imágenes de respaldo
                avatarList.add(new AvatarItem(name, path));
            }
        }

        if (!algunAvatarCargado) {
            Log.w(TAG, "No hay avatares válidos en archivos. Usando imágenes de respaldo.");
            Toast.makeText(this, "Usando imágenes de respaldo para los avatares.", Toast.LENGTH_SHORT).show();
            
            // Intenta copiar los avatares de nuevo en segundo plano
            new Thread(() -> {
                try {
                    Log.d(TAG, "Intentando copiar avatares nuevamente desde assets...");
                    com.example.examentipo1.util.FileUtils.copyAvatarsToInternalStorage(getApplicationContext());
                } catch (Exception e) {
                    Log.e(TAG, "Error al reintentar copia de assets", e);
                }
            }).start();
        } else {
            Log.d(TAG, "Avatares cargados en la lista: " + avatarList.size());
        }
    }
}