package com.example.doan.fragments; // Đảm bảo package name đúng

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.doan.R; // Đảm bảo import R đúng
import com.example.doan.adapter.StoryAdapter;
import com.example.doan.model.Story;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // Khai báo các View đã có
    private RecyclerView recyclerNewlyUpdated, recyclerHot, recyclerAllStories;
    private RelativeLayout headerNewlyUpdated, headerHot, headerAllStories;
    private ImageView arrowNewlyUpdated, arrowHot, arrowAllStories;
    // Bỏ khai báo recyclerReading, headerReading, arrowReading nếu bạn chưa làm mục "Đang đọc"

    private StoryAdapter newlyUpdatedAdapter, hotAdapter, allStoriesAdapter;
    // Bỏ khai báo readingAdapter nếu chưa làm mục "Đang đọc"

    private List<Story> fullStoryList = new ArrayList<>(); // Danh sách chứa TẤT CẢ truyện từ JSON
    private List<Story> newlyUpdatedStoryList = new ArrayList<>(); // Danh sách CHỈ chứa truyện mới cập nhật
    private List<Story> hotStoryList = new ArrayList<>(); // Danh sách CHỈ chứa truyện hot (tạm thời có thể lấy một phần từ fullStoryList)


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadStoriesFromJson(); // Hàm này sẽ điền dữ liệu vào fullStoryList, newlyUpdatedStoryList, hotStoryList
        setupRecyclerViews();
        setupToggleActions();
    }

    private void initViews(View view) {
        // Mục Truyện Mới Cập Nhật
        recyclerNewlyUpdated = view.findViewById(R.id.recycler_view_newly_updated);
        headerNewlyUpdated = view.findViewById(R.id.header_newly_updated);
        arrowNewlyUpdated = view.findViewById(R.id.arrow_newly_updated);

        // Mục Đang Hot
        recyclerHot = view.findViewById(R.id.recycler_view_hot);
        headerHot = view.findViewById(R.id.header_hot);
        arrowHot = view.findViewById(R.id.arrow_hot);

        // Mục Tất Cả Truyện
        recyclerAllStories = view.findViewById(R.id.recycler_view_all_stories);
        headerAllStories = view.findViewById(R.id.header_all_stories);
        arrowAllStories = view.findViewById(R.id.arrow_all_stories);

        // Bỏ ánh xạ cho mục "Đang đọc" nếu chưa làm
    }

    private void loadStoriesFromJson() {
        String jsonString;
        try {
            InputStream is = requireContext().getAssets().open("stories_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Type storyListType = new TypeToken<ArrayList<Story>>(){}.getType();
            fullStoryList = gson.fromJson(jsonString, storyListType);

            if (fullStoryList == null) {
                fullStoryList = new ArrayList<>();
            }

            // --- LỌC TRUYỆN MỚI CẬP NHẬT ---
            newlyUpdatedStoryList.clear(); // Xóa dữ liệu cũ (nếu có)
            if (!fullStoryList.isEmpty()) {
                int countNewlyUpdated = Math.min(5, fullStoryList.size()); // Lấy tối đa 5 truyện
                for (int i = 0; i < countNewlyUpdated; i++) {
                    newlyUpdatedStoryList.add(fullStoryList.get(i));
                }
            }

            // --- GIẢ LẬP TRUYỆN HOT (VÍ DỤ LẤY TỪ TRUYỆN THỨ 6 ĐẾN 10) ---
            hotStoryList.clear();
            if (fullStoryList.size() > 5) { // Chỉ lấy nếu có đủ truyện
                int startHotIndex = 5;
                int countHot = Math.min(5, fullStoryList.size() - startHotIndex);
                for (int i = 0; i < countHot; i++) {
                    hotStoryList.add(fullStoryList.get(startHotIndex + i));
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
            fullStoryList = new ArrayList<>();
            newlyUpdatedStoryList = new ArrayList<>();
            hotStoryList = new ArrayList<>();
            Toast.makeText(getContext(), "Lỗi khi đọc file JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerViews() {
        // Truyện Mới Cập Nhật (cuộn ngang)
        recyclerNewlyUpdated.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        newlyUpdatedAdapter = new StoryAdapter(getContext(), newlyUpdatedStoryList); // Dùng danh sách đã lọc
        recyclerNewlyUpdated.setAdapter(newlyUpdatedAdapter);

        // Đang Hot (cuộn ngang)
        recyclerHot.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        hotAdapter = new StoryAdapter(getContext(), hotStoryList); // Dùng danh sách đã lọc
        recyclerHot.setAdapter(hotAdapter);

        // Tất Cả Truyện (lưới 2 cột)
        recyclerAllStories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        allStoriesAdapter = new StoryAdapter(getContext(), fullStoryList); // Dùng danh sách đầy đủ
        recyclerAllStories.setAdapter(allStoriesAdapter);

        // Bỏ phần setup cho recyclerReading nếu chưa làm
    }

    private void setupToggleActions() {
        // Mục Mới Cập Nhật: Mặc định hiện, mũi tên xoay lên
        arrowNewlyUpdated.setRotation(180); // Vì trong XML visibility="visible"

        // Mục Đang Hot: Mặc định ẩn, mũi tên xoay xuống
        arrowHot.setRotation(0); // Vì trong XML visibility="gone"

        // Mục Tất Cả Truyện: Mặc định ẩn, mũi tên xoay xuống
        arrowAllStories.setRotation(0); // Vì trong XML visibility="gone"

        headerNewlyUpdated.setOnClickListener(v -> toggleSection(recyclerNewlyUpdated, arrowNewlyUpdated));
        headerHot.setOnClickListener(v -> toggleSection(recyclerHot, arrowHot));
        headerAllStories.setOnClickListener(v -> toggleSection(recyclerAllStories, arrowAllStories));
        // Bỏ phần setup click cho headerReading nếu chưa làm
    }

    private void toggleSection(View section, ImageView arrow) {
        boolean isVisible = section.getVisibility() == View.VISIBLE;
        section.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        arrow.animate().rotation(isVisible ? 0 : 180).setDuration(300).start();
    }

    // Bỏ onResume() nếu bạn chưa làm tính năng "Đang đọc"
}