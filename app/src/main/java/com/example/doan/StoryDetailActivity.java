package com.example.doan; // Đảm bảo package name đúng

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.example.doan.adapter.ChapterAdapter;
import com.example.doan.model.Story;

public class StoryDetailActivity extends AppCompatActivity {

    private ImageView coverImage;
    private TextView authorText, descriptionText;
    private CollapsingToolbarLayout collapsingToolbar;
    private RecyclerView chapterRecyclerView;
    private ChapterAdapter chapterAdapter;
    private RelativeLayout headerChapters;
    private ImageView arrowChapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
        setupToggleChapterListAction();

        String storyJson = getIntent().getStringExtra("STORY_JSON");
        if (storyJson != null) {
            Gson gson = new Gson();
            Story story = gson.fromJson(storyJson, Story.class);
            if (story != null) {
                displayStoryDetails(story);
            } else {
                collapsingToolbar.setTitle("Lỗi tải truyện");
                descriptionText.setText("Không thể tải thông tin chi tiết của truyện.");
                Toast.makeText(this, "Lỗi khi parse dữ liệu truyện", Toast.LENGTH_SHORT).show();
            }
        } else {
            collapsingToolbar.setTitle("Không có dữ liệu");
            descriptionText.setText("Không nhận được thông tin truyện.");
            Toast.makeText(this, "Không nhận được STORY_JSON từ Intent", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        coverImage = findViewById(R.id.image_view_detail_cover);
        authorText = findViewById(R.id.text_view_detail_author);
        descriptionText = findViewById(R.id.text_view_detail_description);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        chapterRecyclerView = findViewById(R.id.recycler_view_chapters);
        headerChapters = findViewById(R.id.header_chapters);
        arrowChapters = findViewById(R.id.arrow_chapters);
    }

    private void displayStoryDetails(Story story) {
        collapsingToolbar.setTitle(story.getTitle());
        authorText.setText("Tác giả: " + story.getAuthor());
        descriptionText.setText(story.getDescription());

        int imageId = getResources().getIdentifier(story.getCover_image(), "drawable", getPackageName());
        if (imageId != 0) {
            coverImage.setImageResource(imageId);
        } else {
            coverImage.setImageResource(R.drawable.pham_nhan_tu_tien);
        }

        chapterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chapterAdapter = new ChapterAdapter(this, story.getChapters(), story);
        chapterRecyclerView.setAdapter(chapterAdapter);
    }

    private void setupToggleChapterListAction() {
        chapterRecyclerView.setVisibility(View.GONE);
        arrowChapters.setRotation(0);

        headerChapters.setOnClickListener(v -> {
            boolean isVisible = chapterRecyclerView.getVisibility() == View.VISIBLE;
            chapterRecyclerView.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            arrowChapters.animate().rotation(isVisible ? 0 : 180).setDuration(300).start();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Ghi đè onSupportNavigateUp để nút back trên toolbar hoạt động
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}