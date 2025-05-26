package com.example.doan.model;

import java.util.List;

public class Story {
    private int id;
    private String title;
    private String author;
    private String cover_image;
    private String description;
    private String category; // <<< THÊM THUỘC TÍNH NÀY
    private List<Chapter> chapters;

    // Getters
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public String getCover_image() {
        return cover_image;
    }
    public String getDescription() {
        return description;
    }
    public String getCategory() { // <<< THÊM GETTER NÀY
        return category;
    }
    public List<Chapter> getChapters() {
        return chapters;
    }

    // Setters (tùy chọn, không bắt buộc nếu chỉ đọc từ JSON)
    // public void setCategory(String category) { this.category = category; }
}