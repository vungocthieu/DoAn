package com.example.doan.fragments;

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
import com.example.doan.R;
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
    private List<Story> fullStoryList = new ArrayList<>();
    private List<Story> initialNewlyUpdatedList = new ArrayList<>();
    private List<Story> initialHotList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        recyclerNewlyUpdated = view.findViewById(R.id.recycler_view_newly_updated);
        headerNewlyUpdated = view.findViewById(R.id.header_newly_updated);
        arrowNewlyUpdated = view.findViewById(R.id.arrow_newly_updated);

        recyclerHot = view.findViewById(R.id.recycler_view_hot);
        headerHot = view.findViewById(R.id.header_hot);
        arrowHot = view.findViewById(R.id.arrow_hot);

        recyclerAllStories = view.findViewById(R.id.recycler_view_all_stories);
        headerAllStories = view.findViewById(R.id.header_all_stories);
        arrowAllStories = view.findViewById(R.id.arrow_all_stories);
    }

    private void loadStoriesFromJson() {
        String jsonString;
        try {
            if (getContext() == null) return;
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

            initialNewlyUpdatedList.clear();
            if (!fullStoryList.isEmpty()) {
                int count = Math.min(5, fullStoryList.size());
                for (int i = 0; i < count; i++) {
                    initialNewlyUpdatedList.add(fullStoryList.get(i));
                }
            }

            initialHotList.clear();
            if (fullStoryList.size() > 5) {
                int start = 5;
                int count = Math.min(5, fullStoryList.size() - start);
                for (int i = 0; i < count; i++) {
                    initialHotList.add(fullStoryList.get(start + i));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            fullStoryList = new ArrayList<>();
            initialNewlyUpdatedList = new ArrayList<>();
            initialHotList = new ArrayList<>();
            Toast.makeText(getContext(), "Lỗi khi đọc file JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerViews() {
        // Constructor của StoryAdapter giờ chỉ nhận (Context, List<Story>)
        // Nó sẽ tự đặt showDeleteButton = false
        recyclerNewlyUpdated.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        newlyUpdatedAdapter = new StoryAdapter(getContext(), initialNewlyUpdatedList);
        recyclerNewlyUpdated.setAdapter(newlyUpdatedAdapter);

        recyclerHot.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        hotAdapter = new StoryAdapter(getContext(), initialHotList);
        recyclerHot.setAdapter(hotAdapter);

        recyclerAllStories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        allStoriesAdapter = new StoryAdapter(getContext(), fullStoryList);
        recyclerAllStories.setAdapter(allStoriesAdapter);
    }

    // HÀM NÀY SẼ ĐƯỢC GỌI TỪ MAINACTIVITY NẾU BẠN CÓ SEARCHVIEW TRÊN TOOLBAR
    // Hiện tại SearchFragment tự xử lý, nên hàm này trong HomeFragment có thể không cần thiết nữa
    // trừ khi bạn muốn HomeFragment cũng có thể lọc.
    public void filterStories(String query) {
        if (newlyUpdatedAdapter != null) {
            newlyUpdatedAdapter.filter(query);
        }
        if (hotAdapter != null) {
            hotAdapter.filter(query);
        }
        if (allStoriesAdapter != null) {
            allStoriesAdapter.filter(query);
        }
    }

    private void setupToggleActions() {
        arrowNewlyUpdated.setRotation(180);
        recyclerNewlyUpdated.setVisibility(View.VISIBLE);

        arrowHot.setRotation(0);
        recyclerHot.setVisibility(View.GONE);

        arrowAllStories.setRotation(0);
        recyclerAllStories.setVisibility(View.GONE);

        headerNewlyUpdated.setOnClickListener(v -> toggleSection(recyclerNewlyUpdated, arrowNewlyUpdated));
        headerHot.setOnClickListener(v -> toggleSection(recyclerHot, arrowHot));
        headerAllStories.setOnClickListener(v -> toggleSection(recyclerAllStories, arrowAllStories));
    }

    private void toggleSection(View section, ImageView arrow) {
        boolean isVisible = section.getVisibility() == View.VISIBLE;
        section.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        arrow.animate().rotation(isVisible ? 0 : 180).setDuration(300).start();
    }
}