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

    private RecyclerView recyclerNewlyUpdated, recyclerHot, recyclerAllStories;
    private RelativeLayout headerNewlyUpdated, headerHot, headerAllStories;
    private ImageView arrowNewlyUpdated, arrowHot, arrowAllStories;

    private StoryAdapter newlyUpdatedAdapter, hotAdapter, allStoriesAdapter;
    private List<Story> storyList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Liên kết layout fragment_home.xml với Fragment này
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadStoriesFromJson();
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
    }

    private void loadStoriesFromJson() {
        String jsonString;
        try {
            // Sử dụng requireContext() trong Fragment để đảm bảo Context không null
            InputStream is = requireContext().getAssets().open("stories_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Type storyListType = new TypeToken<ArrayList<Story>>(){}.getType();
            storyList = gson.fromJson(jsonString, storyListType);

            if (storyList == null) { // Đề phòng trường hợp file JSON rỗng hoặc sai cú pháp
                storyList = new ArrayList<>();
            }

        } catch (IOException e) {
            e.printStackTrace();
            storyList = new ArrayList<>(); // Khởi tạo list rỗng nếu có lỗi
            Toast.makeText(getContext(), "Lỗi khi đọc file JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerViews() {
        // Truyện Mới Cập Nhật (cuộn ngang)
        recyclerNewlyUpdated.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        newlyUpdatedAdapter = new StoryAdapter(getContext(), storyList); // Tạm thời dùng chung danh sách
        recyclerNewlyUpdated.setAdapter(newlyUpdatedAdapter);

        // Đang Hot (cuộn ngang)
        recyclerHot.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        hotAdapter = new StoryAdapter(getContext(), storyList); // Tạm thời dùng chung danh sách
        recyclerHot.setAdapter(hotAdapter);

        // Tất Cả Truyện (lưới 2 cột)
        recyclerAllStories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        allStoriesAdapter = new StoryAdapter(getContext(), storyList); // Tạm thời dùng chung danh sách
        recyclerAllStories.setAdapter(allStoriesAdapter);
    }

    private void setupToggleActions() {
        // Mục Mới Cập Nhật: Mặc định hiện (visibility="visible" trong XML), mũi tên xoay lên
        arrowNewlyUpdated.setRotation(180);

        // Mục Đang Hot: Mặc định ẩn (visibility="gone" trong XML), mũi tên xoay xuống
        arrowHot.setRotation(0);
        // Không cần gọi toggleSection(recyclerHot, arrowHot, false) vì visibility đã là 'gone' trong XML

        // Mục Tất Cả Truyện: Mặc định ẩn (visibility="gone" trong XML), mũi tên xoay xuống
        arrowAllStories.setRotation(0);
        // Không cần gọi toggleSection(recyclerAllStories, arrowAllStories, false)

        headerNewlyUpdated.setOnClickListener(v -> toggleSection(recyclerNewlyUpdated, arrowNewlyUpdated));
        headerHot.setOnClickListener(v -> toggleSection(recyclerHot, arrowHot));
        headerAllStories.setOnClickListener(v -> toggleSection(recyclerAllStories, arrowAllStories));
    }

    // Đã sửa lại hàm toggleSection để đơn giản hơn dựa trên visibility đã có
    private void toggleSection(View section, ImageView arrow) {
        boolean isVisible = section.getVisibility() == View.VISIBLE;
        section.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        arrow.animate().rotation(isVisible ? 0 : 180).setDuration(300).start();
    }
}