package com.example.doan.adapter; // Đảm bảo package name đúng

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.R; // Đảm bảo import R đúng
import com.example.doan.ReaderActivity;
import com.example.doan.model.Chapter;
import com.example.doan.model.Story;
import com.google.gson.Gson;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private Context context;
    private List<Chapter> chapterList;
    private Story story; // Để truyền toàn bộ Story sang ReaderActivity

    public ChapterAdapter(Context context, List<Chapter> chapterList, Story story) {
        this.context = context;
        this.chapterList = chapterList;
        this.story = story;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.chapterTitle.setText(chapter.getChapter_title());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReaderActivity.class);
            Gson gson = new Gson();
            String storyJson = gson.toJson(this.story);
            intent.putExtra("STORY_JSON", storyJson);
            intent.putExtra("CHAPTER_INDEX", position);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (chapterList == null) return 0;
        return chapterList.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView chapterTitle;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            chapterTitle = itemView.findViewById(R.id.text_view_chapter_title);
        }
    }
}