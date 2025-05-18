package com.example.examentipo1.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.examentipo1.R;
import com.example.examentipo1.model.AvatarItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class AvatarAdapter extends ArrayAdapter<AvatarItem> {
    
    private static final String TAG = "AvatarAdapter";
    private Context context;
    private List<AvatarItem> avatarList;
    
    public AvatarAdapter(Context context, List<AvatarItem> avatarList) {
        super(context, 0, avatarList);
        this.context = context;
        this.avatarList = avatarList;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener el item de la posición actual
        AvatarItem currentItem = getItem(position);
        
        // Reutilizar la vista si existe, si no, inflar una nueva
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_avatar, parent, false);
        }
        
        // Obtener las vistas del layout
        ImageView imageView = convertView.findViewById(R.id.ivAvatarItem);
        TextView textView = convertView.findViewById(R.id.tvAvatarName);
        
        // Establecer el nombre del archivo
        textView.setText(currentItem.getFileName());
        
        // Cargar la imagen desde el archivo interno si existe
        boolean loadedFromFile = false;
        
        if (currentItem.getFilePath() != null) {
            try {
                File file = new File(currentItem.getFilePath());
                if (file.exists()) {
                    Log.d(TAG, "Cargando avatar desde archivo: " + file.getAbsolutePath());
                    FileInputStream fis = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        loadedFromFile = true;
                    }
                    fis.close();
                } else {
                    Log.d(TAG, "El archivo no existe: " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                Log.e(TAG, "Error al cargar archivo: " + currentItem.getFilePath(), e);
            }
        }
        
        // Si no pudimos cargar desde el archivo, usar el recurso drawable como respaldo
        if (!loadedFromFile) {
            Log.d(TAG, "Usando drawable de respaldo para: " + currentItem.getFileName());
            
            // Determinar qué drawable cargar basado en el nombre del archivo
            if (currentItem.getFileName().startsWith("1")) {
                imageView.setImageResource(R.drawable.avatar1);
            } else if (currentItem.getFileName().startsWith("2")) {
                imageView.setImageResource(R.drawable.avatar2);
            } else if (currentItem.getFileName().startsWith("3")) {
                imageView.setImageResource(R.drawable.avatar3);
            } else if (currentItem.getFileName().startsWith("4")) {
                imageView.setImageResource(R.drawable.avatar4);
            } else {
                // Avatar por defecto
                imageView.setImageResource(R.drawable.avatar1);
            }
        }
        
        return convertView;
    }
}