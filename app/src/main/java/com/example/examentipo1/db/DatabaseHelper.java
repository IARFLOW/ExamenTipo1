package com.example.examentipo1.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.examentipo1.model.Usuario;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "usuarios.db";
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla
    public static final String TABLE_USUARIOS = "usuarios";

    // Columnas de la tabla
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_APELLIDOS = "apellidos";
    public static final String COLUMN_DIRECCION = "direccion";
    public static final String COLUMN_TELEFONO = "telefono";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_AVATAR = "avatar";

    // SQL para crear la tabla
    private static final String CREATE_TABLE_USUARIOS = "CREATE TABLE " + TABLE_USUARIOS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NOMBRE + " TEXT, " +
            COLUMN_APELLIDOS + " TEXT, " +
            COLUMN_DIRECCION + " TEXT, " +
            COLUMN_TELEFONO + " TEXT, " +
            COLUMN_LOGIN + " TEXT UNIQUE, " +
            COLUMN_PASSWORD + " TEXT, " +
            COLUMN_AVATAR + " BLOB" +
            ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USUARIOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    // Insertar un nuevo usuario
    public long insertUsuario(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_NOMBRE, usuario.getNombre());
        values.put(COLUMN_APELLIDOS, usuario.getApellidos());
        values.put(COLUMN_DIRECCION, usuario.getDireccion());
        values.put(COLUMN_TELEFONO, usuario.getTelefono());
        values.put(COLUMN_LOGIN, usuario.getLogin());
        values.put(COLUMN_PASSWORD, usuario.getPassword());
        values.put(COLUMN_AVATAR, usuario.getAvatarData()); // MODIFICADO

        long id = db.insert(TABLE_USUARIOS, null, values);
        db.close();
        return id;
    }

    // Verificar credenciales de usuario
    public Usuario checkUsuario(String login, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = {
                COLUMN_ID, 
                COLUMN_NOMBRE, 
                COLUMN_APELLIDOS, 
                COLUMN_DIRECCION, 
                COLUMN_TELEFONO,
                COLUMN_LOGIN,
                COLUMN_AVATAR
        };
        
        String selection = COLUMN_LOGIN + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {login, password};
        
        Cursor cursor = db.query(TABLE_USUARIOS, columns, selection, selectionArgs, null, null, null);
        
        Usuario usuario = null;
        
        if (cursor != null && cursor.moveToFirst()) {
            try {
                usuario = new Usuario();
                usuario.setId(cursor.getInt(0)); // COLUMN_ID
                usuario.setNombre(cursor.getString(1)); // COLUMN_NOMBRE
                usuario.setApellidos(cursor.getString(2)); // COLUMN_APELLIDOS
                usuario.setDireccion(cursor.getString(3)); // COLUMN_DIRECCION
                usuario.setTelefono(cursor.getString(4)); // COLUMN_TELEFONO
                usuario.setLogin(cursor.getString(5)); // COLUMN_LOGIN
                usuario.setAvatarData(cursor.getBlob(6)); // COLUMN_AVATAR - MODIFICADO                usuario.setPassword(password); // No lo recuperamos de la BD, pero lo necesitamos
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Error al leer datos del cursor", e);
            } finally {
                cursor.close();
            }
        }
        
        db.close();
        return usuario;
    }

    // Obtener usuario por ID
    public Usuario getUsuarioById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = {
                COLUMN_ID, 
                COLUMN_NOMBRE, 
                COLUMN_APELLIDOS, 
                COLUMN_DIRECCION, 
                COLUMN_TELEFONO,
                COLUMN_LOGIN,
                COLUMN_PASSWORD,
                COLUMN_AVATAR
        };
        
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        
        Cursor cursor = db.query(TABLE_USUARIOS, columns, selection, selectionArgs, null, null, null);
        
        Usuario usuario = null;
        
        if (cursor != null && cursor.moveToFirst()) {
            try {
                usuario = new Usuario();
                usuario.setId(cursor.getInt(0)); // COLUMN_ID
                usuario.setNombre(cursor.getString(1)); // COLUMN_NOMBRE
                usuario.setApellidos(cursor.getString(2)); // COLUMN_APELLIDOS
                usuario.setDireccion(cursor.getString(3)); // COLUMN_DIRECCION
                usuario.setTelefono(cursor.getString(4)); // COLUMN_TELEFONO
                usuario.setLogin(cursor.getString(5)); // COLUMN_LOGIN
                usuario.setPassword(cursor.getString(6)); // COLUMN_PASSWORD
                usuario.setAvatarData(cursor.getBlob(7)); // COLUMN_AVATAR - MODIFICADO
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Error al leer datos del cursor", e);
            } finally {
                cursor.close();
            }
        }
        
        db.close();
        return usuario;
    }

    // Actualizar los datos de un usuario
    public int updateUsuario(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_NOMBRE, usuario.getNombre());
        values.put(COLUMN_APELLIDOS, usuario.getApellidos());
        values.put(COLUMN_DIRECCION, usuario.getDireccion());
        values.put(COLUMN_TELEFONO, usuario.getTelefono());
        
        // Actualizamos por ID
        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(usuario.getId())};
        
        int rowsAffected = db.update(TABLE_USUARIOS, values, whereClause, whereArgs);
        db.close();
        return rowsAffected;
    }
}