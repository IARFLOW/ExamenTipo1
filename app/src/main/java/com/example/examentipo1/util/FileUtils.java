package com.example.examentipo1.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.Log;
import androidx.core.content.ContextCompat;

import com.example.examentipo1.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    private static final String TAG = "FileUtils";

    /**
     * Copia las imágenes de avatar predefinidas desde los drawables a la carpeta de archivos interna.
     * Se llama desde la MainActivity la primera vez que se inicia la aplicación.
     */
    public static void copyAvatarsToInternalStorage(Context context) {
        try {
            // Avatar 1
            copyDrawableToFile(context, R.drawable.avatar1, "1.png");
            
            // Avatar 2
            copyDrawableToFile(context, R.drawable.avatar2, "2.png");
            
            // Avatar 3
            copyDrawableToFile(context, R.drawable.avatar3, "3.png");
            
            // Avatar 4
            copyDrawableToFile(context, R.drawable.avatar4, "4.png");
            
            Log.d(TAG, "Todos los avatares se han copiado correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error general al copiar avatares", e);
        }
    }
    
    /**
     * Copia un drawable a un archivo en el almacenamiento interno
     * Funciona tanto con drawables vectoriales como con bitmaps
     */
    private static void copyDrawableToFile(Context context, int drawableId, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        
        // Si el archivo ya existe, no lo copiamos de nuevo
        if (file.exists()) {
            Log.d(TAG, "El avatar ya existe: " + fileName);
            return;
        }
        
        try {
            // Convertir el drawable (bitmap o vector) a bitmap
            Bitmap bitmap = getBitmapFromDrawable(context, drawableId);
            
            if (bitmap == null) {
                Log.e(TAG, "No se pudo convertir el drawable a bitmap: " + fileName);
                return;
            }
            
            // Guardar el bitmap al archivo
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            
            Log.d(TAG, "Avatar copiado correctamente: " + fileName);
        } catch (IOException e) {
            Log.e(TAG, "Error al copiar avatar: " + fileName, e);
        } catch (Exception e) {
            Log.e(TAG, "Error inesperado al copiar avatar: " + fileName, e);
        }
    }
    
    /**
     * Convierte un drawable (bitmap o vector) a bitmap
     */
    private static Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        try {
            Drawable drawable = ContextCompat.getDrawable(context, drawableId);
            
            if (drawable == null) {
                return null;
            }
            
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
            
            // Para drawables vectoriales o de otro tipo
            Bitmap bitmap;
            
            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                // Tamaño por defecto para drawables sin tamaño intrínseco
                bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), 
                                           drawable.getIntrinsicHeight(), 
                                           Bitmap.Config.ARGB_8888);
            }
            
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            
            return bitmap;
        } catch (Exception e) {
            Log.e(TAG, "Error al convertir drawable a bitmap", e);
            return null;
        }
    }
}