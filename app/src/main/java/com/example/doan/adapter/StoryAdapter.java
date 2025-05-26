package com.example.doan.adapter; // Thay bằng package name của bạn

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.doan.fragments.LibraryFragment;
import com.example.doan.model.Story;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private Context context;
    private List<Story> storyListDisplay; // Danh sách truyện đang thực sự được hiển thị
    private List<Story> storyListOriginal; // Danh sách gốc, dùng cho việc reset filter
    private boolean showDeleteButton;

    // Constructor chính
    public StoryAdapter(Context context, List<Story> originalStoryList, boolean showDeleteButton) {
        this.context = context;
        // Tạo bản sao để tránh tham chiếu trực tiếp và các vấn đề khi nhiều adapter dùng chung list
        this.storyListOriginal = new ArrayList<>(originalStoryList);
        this.storyListDisplay = new ArrayList<>(originalStoryList);
        this.showDeleteButton = showDeleteButton;
    }

    // Constructor phụ
    public StoryAdapter(Context context, List<Story> originalStoryList) {
        this(context, originalStoryList, false);
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_story_grid, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        if (storyListDisplay == null || position < 0 || position >= storyListDisplay.size()) {
            Log.e("StoryAdapter", "Invalid position or null list: " + position + ", size: " + (storyListDisplay == null ? "null" : storyListDisplay.size()));
            return;
        }
        Story story = storyListDisplay.get(position);
        holder.title.setText(story.getTitle());
        holder.author.setText(story.getAuthor());

        String imageName = story.getCover_image();
        int imageId = 0;
        if (imageName != null && !imageName.isEmpty()) {
            imageId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        }

        if (imageId != 0) {
            holder.cover.setImageResource(imageId);
        } else {
            holder.cover.setImageResource(R.drawable.pham_nhan_tu_tien);
        }

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION && currentPosition < storyListDisplay.size()) {
                Story storyToPass = storyListDisplay.get(currentPosition);
                Intent intent = new Intent(context, StoryDetailActivity.class);
                Gson gson = new Gson();
                String storyJson = gson.toJson(storyToPass);
                intent.putExtra("STORY_JSON", storyJson);
                context.startActivity(intent);
            }
        });

        if (holder.deleteIcon != null) {
            if (showDeleteButton) {
                holder.deleteIcon.setVisibility(View.VISIBLE);
                holder.deleteIcon.setOnClickListener(v -> {
                    int currentPosition = holder.getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION && currentPosition < storyListDisplay.size()) {
                        Story storyToRemove = storyListDisplay.get(currentPosition);

                        ReadingListHelper helper = new ReadingListHelper(context);
                        helper.removeStoryFromReadingList(storyToRemove.getId());

                        // Xóa khỏi cả hai danh sách để đồng bộ
                        // Quan trọng: Phải xóa khỏi storyListOriginal trước nếu nó là nguồn cho storyListDisplay khi filter
                        boolean removedFromOriginal = storyListOriginal.remove(storyToRemove);
                        boolean removedFromDisplay = storyListDisplay.remove(storyToRemove); // Hoặc remove theo position


                        if(removedFromDisplay) { // Chỉ notify nếu thực sự xóa khỏi list display
                            notifyItemRemoved(currentPosition);
                            notifyItemRangeChanged(currentPosition, storyListDisplay.size());
                        } else if (removedFromOriginal) {
                            // Nếu chỉ xóa khỏi original mà không có trong display (ít khi xảy ra nếu logic đúng)
                            // thì có thể cần cập nhật lại display list từ original list đã thay đổi
                            filter(""); // Gọi filter với query rỗng để làm mới display list từ original list
                        }


                        Toast.makeText(context, "Đã xóa: " + storyToRemove.getTitle(), Toast.LENGTH_SHORT).show();

//                        if (context instanceof LibraryFragment) {
//                            ((LibraryFragment) context).checkAndShowEmptyListMessage();
//                        }
                    }
                });
            } else {
                holder.deleteIcon.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (storyListDisplay == null) return 0;
        return storyListDisplay.size();
    }

    // Phương thức để lọc danh sách
    public void filter(String query) {
        List<Story> filteredList = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            filteredList.addAll(storyListOriginal);
        } else {
            String filterPattern = query.toLowerCase(Locale.getDefault()).trim();
            for (Story story : storyListOriginal) {
                if (story.getTitle().toLowerCase(Locale.getDefault()).contains(filterPattern) ||
                        (story.getAuthor() != null && story.getAuthor().toLowerCase(Locale.getDefault()).contains(filterPattern))) {
                    filteredList.add(story);
                }
            }
        }
        storyListDisplay.clear();
        storyListDisplay.addAll(filteredList);
        notifyDataSetChanged();
    }

    // PHƯƠNG THỨC NÀY PHẢI TỒN TẠI
    public void updateData(List<Story> newStoryList) {
        this.storyListOriginal.clear();
        this.storyListOriginal.addAll(newStoryList);
        this.storyListDisplay.clear();
        this.storyListDisplay.addAll(newStoryList);
        notifyDataSetChanged();
        Log.d("StoryAdapter", "Data updated for Library/Category. Displaying: " + (storyListDisplay != null ? storyListDisplay.size() : "null"));
    }

    // PHƯƠNG THỨC NÀY PHẢI TỒN TẠI ĐÚNG NHƯ VẦY
    public void setData(List<Story> newStoryList) {
        if (this.storyListOriginal == null) {
            this.storyListOriginal = new ArrayList<>();
        }
        if (this.storyListDisplay == null) {
            this.storyListDisplay = new ArrayList<>();
        }
        this.storyListOriginal.clear();
        if (newStoryList != null) {
            this.storyListOriginal.addAll(newStoryList);
        }
        this.storyListDisplay.clear();
        if (newStoryList != null) {
            this.storyListDisplay.addAll(newStoryList);
        }
        notifyDataSetChanged();
        Log.d("StoryAdapter", "setData called. Displaying: " + this.storyListDisplay.size() + " items. Original: " + this.storyListOriginal.size());
    }


    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title;
        TextView author;
        ImageView deleteIcon;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.image_view_cover);
            title = itemView.findViewById(R.id.text_view_title);
            author = itemView.findViewById(R.id.text_view_author);
            deleteIcon = itemView.findViewById(R.id.image_view_delete_item);
        }
    }
}