package com.example.doan.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.adapter.CategoryAdapter;
import com.example.doan.model.Story;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    private static final String TAG = "CategoryFragment";
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;
    private List<Story> fullStoryList = new ArrayList<>();
    private List<String> uniqueCategoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        recyclerViewCategories = view.findViewById(R.id.recycler_view_categories);
        // Đảm bảo layout fragment_category.xml có RecyclerView với ID này
        if (recyclerViewCategories == null) {
            Log.e(TAG, "RecyclerView for categories not found!");
            return;
        }
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo adapter với danh sách rỗng ban đầu
        categoryAdapter = new CategoryAdapter(getContext(), uniqueCategoryList, this);
        recyclerViewCategories.setAdapter(categoryAdapter);

        loadFullStoryListFromJson();
    }

    private void loadFullStoryListFromJson() {
        Log.d(TAG, "loadFullStoryListFromJson called");
        String jsonString;
        try {
            if (getContext() == null) {
                Log.e(TAG, "Context is null in loadFullStoryListFromJson");
                return;
            }
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
                Log.w(TAG, "Parsed fullStoryList is null, initialized as empty list.");
            }
            Log.d(TAG, "Full story list loaded: " + fullStoryList.size() + " items");

            extractAndDisplayUniqueCategories();

        } catch (IOException e) {
            e.printStackTrace();
            fullStoryList = new ArrayList<>();
            if (getContext() != null) {
                Toast.makeText(getContext(), "Lỗi khi đọc file JSON", Toast.LENGTH_SHORT).show();
            }
            Log.e(TAG, "IOException while loading JSON: " + e.getMessage());
        }
    }

    private void extractAndDisplayUniqueCategories() {
        if (fullStoryList.isEmpty()) {
            Log.w(TAG, "Full story list is empty, cannot extract categories.");
            uniqueCategoryList.clear();
            if (categoryAdapter != null) {
                categoryAdapter.notifyDataSetChanged();
            }
            return;
        }

        Set<String> categorySet = new HashSet<>();
        for (Story story : fullStoryList) {
            if (story.getCategory() != null && !story.getCategory().trim().isEmpty()) {
                categorySet.add(story.getCategory().trim());
            }
        }
        uniqueCategoryList.clear();
        uniqueCategoryList.addAll(categorySet);
        Collections.sort(uniqueCategoryList);

        Log.d(TAG, "Unique categories extracted: " + uniqueCategoryList.size());
        if (categoryAdapter != null) {
            categoryAdapter.notifyDataSetChanged();
        }
    }

    // Trong CategoryFragment.java
    @Override
    public void onCategoryClick(String categoryName) {
        Log.d("CategoryFragment", "onCategoryClick called with category: " + categoryName); // KIỂM TRA
        Toast.makeText(getContext(), "Đang mở thể loại: " + categoryName, Toast.LENGTH_SHORT).show();

        StoriesByCategoryFragment storiesFragment = StoriesByCategoryFragment.newInstance(categoryName);

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, storiesFragment)
                    .addToBackStack(null)
                    .commit();
            Log.d("CategoryFragment", "Fragment transaction committed for: " + categoryName); // KIỂM TRA
        } else {
            Log.e("CategoryFragment", "getActivity() is null, cannot perform transaction."); // KIỂM TRA
        }
    }
}