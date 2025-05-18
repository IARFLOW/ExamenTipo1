package com.example.examentipo1.model;

public class AvatarItem {
    private String fileName;
    private String filePath;
    
    public AvatarItem(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
}