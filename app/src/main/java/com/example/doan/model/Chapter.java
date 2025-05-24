package com.example.doan.model;

public class Chapter {
    private String chapter_title;
    private String content;

    // Getters (bắt buộc để Gson có thể truy cập)
    public String getChapter_title() {
        return chapter_title;
    }

    public String getContent() {
        return content;
    }

    // Setters (tùy chọn, không bắt buộc nếu bạn chỉ đọc từ JSON)
    // public void setChapter_title(String chapter_title) {
    //     this.chapter_title = chapter_title;
    // }
    //
    // public void setContent(String content) {
    //     this.content = content;
    // }
}