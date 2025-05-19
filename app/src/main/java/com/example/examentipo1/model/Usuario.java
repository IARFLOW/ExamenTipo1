package com.example.examentipo1.model;

public class Usuario {
    private int id;
    private String nombre;
    private String apellidos;
    private String direccion;
    private String telefono;
    private String login;
    private String password;
    private byte[] avatarData; // MODIFICADO

    public Usuario() {
    }

    public Usuario(String nombre, String apellidos, String direccion, String telefono, String login, String password, byte[] avatarData) { // MODIFICADO el tipo del último parámetro
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.telefono = telefono;
        this.login = login;
        this.password = password;
        this.avatarData = avatarData; // MODIFICADO
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getAvatarData() { // MODIFICADO
        return avatarData;
    }

    public void setAvatarData(byte[] avatarData) { // MODIFICADO
        this.avatarData = avatarData;
    }
}