package com.example.doan.model;

import java.util.List;

public class Story {
    private int id;
    private String title;
    private String author;
    private String cover_image; // Tên file ảnh trong drawable, không có đuôi .png
    private String description;
    private List<Chapter> chapters;

    // Getters (bắt buộc)
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

    public List<Chapter> getChapters() {
        return chapters;
    }

    // Setters (tùy chọn)
    // public void setId(int id) { this.id = id; }
    // public void setTitle(String title) { this.title = title; }
    // public void setAuthor(String author) { this.author = author; }
    // public void setCover_image(String cover_image) { this.cover_image = cover_image; }
    // public void setDescription(String description) { this.description = description; }
    // public void setChapters(List<Chapter> chapters) { this.chapters = chapters; }
}