package com.example.doan.adapter; // Đảm bảo package name đúng

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.R; // Đảm bảo import R đúng
import com.example.doan.StoryDetailActivity;
import com.example.doan.model.Story;
import com.google.gson.Gson;
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private Context context;
    private List<Story> storyList;

    public StoryAdapter(Context context, List<Story> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_story_grid, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = storyList.get(position);
        holder.title.setText(story.getTitle());
        holder.author.setText(story.getAuthor());

        String imageName = story.getCover_image();
        int imageId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        if (imageId != 0) {
            holder.cover.setImageResource(imageId);
        } else {
            holder.cover.setImageResource(R.drawable.pham_nhan_tu_tien); // Ảnh mẫu
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, StoryDetailActivity.class);
            Gson gson = new Gson();
            String storyJson = gson.toJson(story);
            intent.putExtra("STORY_JSON", storyJson);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (storyList == null) return 0;
        return storyList.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title;
        TextView author;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.image_view_cover);
            title = itemView.findViewById(R.id.text_view_title);
            author = itemView.findViewById(R.id.text_view_author);
        }
    }
}