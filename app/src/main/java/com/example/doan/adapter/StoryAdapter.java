package com.example.doan.adapter; // Đảm bảo package name đúng

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.ReadingListHelper;
import com.example.doan.StoryDetailActivity;
import com.example.doan.fragments.LibraryFragment; // Import để gọi hàm showEmptyListMessage
import com.example.doan.model.Story;
import com.google.gson.Gson;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private Context context;
    private List<Story> storyList;
    private boolean showDeleteButton; // Cờ để kiểm soát hiển thị nút xóa

    // Constructor chính, dùng cho LibraryFragment
    public StoryAdapter(Context context, List<Story> storyList, boolean showDeleteButton) {
        this.context = context;
        this.storyList = storyList;
        this.showDeleteButton = showDeleteButton;
    }

    // Constructor phụ, dùng cho HomeFragment (không hiển thị nút xóa)
    public StoryAdapter(Context context, List<Story> storyList) {
        this(context, storyList, false); // Gọi constructor chính với showDeleteButton = false
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
            holder.cover.setImageResource(R.drawable.pham_nhan_tu_tien);
        }

        // Sự kiện click vào cả item để mở chi tiết truyện
        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Story storyToPass = storyList.get(currentPosition);
                Intent intent = new Intent(context, StoryDetailActivity.class);
                Gson gson = new Gson();
                String storyJson = gson.toJson(storyToPass);
                intent.putExtra("STORY_JSON", storyJson);
                context.startActivity(intent);
            }
        });

        // Xử lý hiển thị và sự kiện cho nút xóa
        if (showDeleteButton) {
            holder.deleteIcon.setVisibility(View.VISIBLE);
            holder.deleteIcon.setOnClickListener(v -> {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Story storyToRemove = storyList.get(currentPosition);

                    ReadingListHelper helper = new ReadingListHelper(context);
                    helper.removeStoryFromReadingList(storyToRemove.getId());

                    // Xóa item khỏi danh sách của adapter và thông báo cho RecyclerView
                    storyList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    // Thông báo cho các item còn lại thay đổi vị trí (quan trọng)
                    notifyItemRangeChanged(currentPosition, storyList.size());


                    Toast.makeText(context, "Đã xóa: " + storyToRemove.getTitle(), Toast.LENGTH_SHORT).show();

                    // Nếu danh sách rỗng và context là LibraryFragment, hiển thị thông báo
//                    if (storyList.isEmpty() && context instanceof LibraryFragment) {
//                        ((LibraryFragment) context).showEmptyListMessage(true);
//                    }
                }
            });
        } else {
            holder.deleteIcon.setVisibility(View.GONE);
        }
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
        ImageView deleteIcon; // Khai báo ImageView cho nút xóa

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.image_view_cover);
            title = itemView.findViewById(R.id.text_view_title);
            author = itemView.findViewById(R.id.text_view_author);
            deleteIcon = itemView.findViewById(R.id.image_view_delete_item); // Ánh xạ nút xóa
        }
    }
}