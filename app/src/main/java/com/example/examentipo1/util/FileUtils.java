package com.example.examentipo1.util;

import android.content.Context;
import android.util.Log;

import com.example.examentipo1.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    private static final String TAG = "FileUtils"; // TAG para filtrar

    public static void copyAvatarsToInternalStorage(Context context) {
        String[] avatarNames = {"1.png", "2.png", "3.png", "4.png"};
        File internalDir = context.getFilesDir();

        Log.d(TAG, ">>> FileUtils: INICIANDO COPIA desde ASSETS a " + internalDir.getAbsolutePath()); // Log A

        // Aseguramos que el directorio existe
        if (!internalDir.exists()) {
            boolean dirCreated = internalDir.mkdirs();
            Log.d(TAG, ">>> FileUtils: Directorio interno creado: " + dirCreated);
        }

        boolean assetsCopiados = false;

        for (String avatarName : avatarNames) {
            File destFile = new File(internalDir, avatarName);
            
            // Si el archivo ya existe y no está vacío, no lo copiamos de nuevo
            if (destFile.exists() && destFile.length() > 0) {
                Log.d(TAG, ">>> FileUtils: El archivo '" + avatarName + "' ya existe y no está vacío. Tamaño: " + destFile.length() + " bytes");
                assetsCopiados = true;
                continue;
            }
            
            Log.d(TAG, ">>> FileUtils: Intentando copiar asset '" + avatarName + "' a '" + destFile.getAbsolutePath() + "'"); // Log B

            try (InputStream in = context.getAssets().open(avatarName); // <--- ESTA LÍNEA ES CLAVE
                 OutputStream out = new FileOutputStream(destFile)) {

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
                Log.d(TAG, ">>> FileUtils: COPIADO asset '" + avatarName + "' (Tamaño final: " + destFile.length() + " bytes)"); // Log C

                if (destFile.length() == 0) {
                    Log.e(TAG, "!!! FileUtils: ALERTA - Archivo copiado " + avatarName + " TIENE 0 BYTES!"); // Log D
                } else {
                    assetsCopiados = true;
                }

            } catch (IOException e) {
                Log.e(TAG, "!!! FileUtils: IOException al copiar '" + avatarName + "'. Asset existe? Nombres coinciden? Error: " + e.getMessage(), e); // Log E

            } catch (Exception e) {
                Log.e(TAG, "!!! FileUtils: Error GENERAL al copiar '" + avatarName + "'. Error: " + e.getMessage(), e); // Log F
            }
        }
        
        if (!assetsCopiados) {
            Log.e(TAG, ">>> FileUtils: ALERTA - No se pudo copiar ningún asset.");
        }
    }
}