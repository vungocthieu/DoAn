package com.example.doan; // Thay bằng package name chính của bạn

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.example.doan.model.Chapter;
import com.example.doan.model.Story;

public class ReaderActivity extends AppCompatActivity {

    private TextView textContent;
    private CoordinatorLayout container;
    private Toolbar toolbarReader;
    private float currentFontSize = 18f;
    private int currentTheme = 0;
    // Không cần currentStory và currentChapterIndex ở cấp lớp cho phiên bản đơn giản này

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        toolbarReader = findViewById(R.id.toolbar_reader);
        textContent = findViewById(R.id.text_view_content);
        container = findViewById(R.id.reader_container);
        FloatingActionButton fabFont = findViewById(R.id.fab_change_font);
        FloatingActionButton fabTheme = findViewById(R.id.fab_change_theme);

        setSupportActionBar(toolbarReader);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String storyJson = getIntent().getStringExtra("STORY_JSON");
        int chapterIndexToShow = getIntent().getIntExtra("CHAPTER_INDEX", 0);

        String chapterTitleToShow = "Đọc truyện";
        String chapterContentToShow = "Lỗi: Không tìm thấy nội dung chương.";

        if (storyJson != null && !storyJson.isEmpty()) {
            Gson gson = new Gson();
            Story story = gson.fromJson(storyJson, Story.class);

            if (story != null && story.getChapters() != null &&
                    chapterIndexToShow >= 0 && chapterIndexToShow < story.getChapters().size()) {

                Chapter currentChapter = story.getChapters().get(chapterIndexToShow);
                chapterTitleToShow = story.getTitle() + " - " + currentChapter.getChapter_title();
                chapterContentToShow = currentChapter.getContent();

                // --- LƯU TRUYỆN VÀO DANH SÁCH ĐANG ĐỌC ---
                ReadingListHelper readingListHelper = new ReadingListHelper(this);
                readingListHelper.addStoryToReadingList(story.getId());
                // --- KẾT THÚC PHẦN LƯU ---

            } else {
                Toast.makeText(this, "Lỗi xử lý dữ liệu truyện hoặc chương", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không nhận được dữ liệu truyện từ Intent", Toast.LENGTH_SHORT).show();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(chapterTitleToShow);
        }
        textContent.setText(chapterContentToShow);

        fabFont.setOnClickListener(v -> changeFontSize());
        fabTheme.setOnClickListener(v -> changeTheme());
    }

    private void changeFontSize() {
        currentFontSize += 2f;
        if (currentFontSize > 30f) {
            currentFontSize = 16f;
        }
        textContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentFontSize);
    }

    private void changeTheme() {
        currentTheme = (currentTheme + 1) % 3;
        switch (currentTheme) {
            case 0: // Sáng
                container.setBackgroundColor(Color.WHITE);
                textContent.setTextColor(Color.parseColor("#333333"));
                break;
            case 1: // Tối
                container.setBackgroundColor(Color.parseColor("#121212"));
                textContent.setTextColor(Color.parseColor("#E2E8F0"));
                break;
            case 2: // Vàng giấy
                container.setBackgroundColor(Color.parseColor("#F3E9D6"));
                textContent.setTextColor(Color.parseColor("#5B4636"));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}