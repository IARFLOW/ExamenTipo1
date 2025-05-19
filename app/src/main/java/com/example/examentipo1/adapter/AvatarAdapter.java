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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class AvatarAdapter extends ArrayAdapter<AvatarItem> {

    private static final String TAG = "AvatarAdapter";
    // private Context context; // 'context' no se usa, puedes eliminarlo si no lo necesitas para otra cosa.
    // private List<AvatarItem> avatarList; // 'avatarList' no se usa, superclase maneja la lista.

    public AvatarAdapter(Context context, List<AvatarItem> avatarList) {
        super(context, 0, avatarList);
        // this.context = context;
        // this.avatarList = avatarList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AvatarItem currentItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_avatar, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.ivAvatarItem);
        TextView textView = convertView.findViewById(R.id.tvAvatarName);

        if (currentItem == null) {
            Log.e(TAG, "currentItem es nulo en la posición: " + position);
            // Manejar el caso de item nulo, quizás poner una imagen/texto por defecto o esconder la vista.
            textView.setText("Error");
            imageView.setImageResource(R.mipmap.ic_launcher); // Placeholder
            return convertView;
        }

        textView.setText(currentItem.getFileName());
        Log.d(TAG, "Intentando mostrar avatar: " + currentItem.getFileName() + " con ruta: " + currentItem.getFilePath());

        boolean loadedFromFile = false;

        if (currentItem.getFilePath() != null && !currentItem.getFilePath().isEmpty()) {
            File file = new File(currentItem.getFilePath());
            if (file.exists() && file.isFile() && file.length() > 0) { // Comprobaciones más robustas
                Log.d(TAG, "Archivo EXISTE y NO ESTÁ VACÍO: " + file.getAbsolutePath() + " (Tamaño: " + file.length() + " bytes)");
                try (FileInputStream fis = new FileInputStream(file)) { // Usar try-with-resources
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        loadedFromFile = true;
                        Log.d(TAG, "IMAGEN CARGADA DESDE ARCHIVO: " + currentItem.getFilePath());
                    } else {
                        Log.e(TAG, "BitmapFactory.decodeStream devolvió null para: " + currentItem.getFilePath());
                    }
                } catch (IOException e) {
                    Log.e(TAG, "IOException al cargar archivo: " + currentItem.getFilePath(), e);
                } catch (OutOfMemoryError e) {
                    Log.e(TAG, "OutOfMemoryError al cargar bitmap: " + currentItem.getFilePath(), e);
                    // Considera cargar una versión más pequeña de la imagen si esto ocurre.
                }
            } else {
                if (!file.exists()) {
                    Log.e(TAG, "El archivo NO EXISTE: " + file.getAbsolutePath());
                } else if (!file.isFile()) {
                    Log.e(TAG, "La ruta NO ES UN ARCHIVO: " + file.getAbsolutePath());
                } else if (file.length() == 0) {
                    Log.e(TAG, "El archivo EXISTE PERO ESTÁ VACÍO: " + file.getAbsolutePath());
                }
            }
        } else {
            Log.w(TAG, "La ruta del archivo es nula o vacía para: " + currentItem.getFileName());
        }

        if (!loadedFromFile) {
            Log.w(TAG, "No se pudo cargar la imagen del avatar desde el archivo: " + currentItem.getFileName());
            // Usar una imagen por defecto genérica para todos los casos de error
            imageView.setImageResource(R.mipmap.ic_launcher);
        }
        return convertView;
    }
}