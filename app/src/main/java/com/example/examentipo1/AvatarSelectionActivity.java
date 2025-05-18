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
        avatarList = new ArrayList<>();
        
        // Obtener directorio files de la aplicación
        File filesDir = getFilesDir();
        Log.d(TAG, "Directorio de archivos: " + filesDir.getAbsolutePath());
        
        // Crear objetos de avatar para cada uno disponible
        // y verificar si los archivos existen
        String[] avatarNames = {"1.png", "2.png", "3.png", "4.png"};
        
        for (String name : avatarNames) {
            File file = new File(filesDir, name);
            String path = file.getAbsolutePath();
            
            if (file.exists()) {
                Log.d(TAG, "Archivo encontrado: " + path);
            } else {
                Log.d(TAG, "Archivo NO encontrado: " + path);
            }
            
            // Agregar a la lista de todas formas, el adaptador manejará los archivos inexistentes
            avatarList.add(new AvatarItem(name, path));
        }
        
        if (avatarList.isEmpty()) {
            Log.e(TAG, "¡No se encontraron avatares!");
            Toast.makeText(this, "No se encontraron avatares", Toast.LENGTH_SHORT).show();
        }
    }
}