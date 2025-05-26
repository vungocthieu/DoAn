package com.example.doan.fragments; // Hoặc package của bạn

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.adapter.StoryAdapter;
import com.example.doan.model.Story;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StoriesByCategoryFragment extends Fragment {

    private static final String TAG = "StoriesByCategoryFrag";
    private static final String ARG_CATEGORY_NAME = "SELECTED_CATEGORY";

    private RecyclerView recyclerViewStories;
    private TextView textViewCategoryHeader;
    private TextView textViewNoStories;
    private StoryAdapter storyAdapter;
    private List<Story> fullStoryList = new ArrayList<>();
    private String selectedCategoryName;

    public static StoriesByCategoryFragment newInstance(String categoryName) {
        StoriesByCategoryFragment fragment = new StoriesByCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_NAME, categoryName);
        fragment.setArguments(args);
        Log.d(TAG, "newInstance created for category: " + categoryName); // KIỂM TRA
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedCategoryName = getArguments().getString(ARG_CATEGORY_NAME);
            Log.d(TAG, "onCreate - Selected category received: " + selectedCategoryName); // KIỂM TRA
        } else {
            Log.e(TAG, "onCreate - No arguments (category name) received!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stories_by_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewStories = view.findViewById(R.id.recycler_view_stories_by_category);
        textViewCategoryHeader = view.findViewById(R.id.text_view_category_header);
        textViewNoStories = view.findViewById(R.id.text_view_no_stories_in_category);

        if (selectedCategoryName != null) {
            textViewCategoryHeader.setText("Thể loại: " + selectedCategoryName);
        } else {
            textViewCategoryHeader.setText("Không rõ thể loại");
        }

        recyclerViewStories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // Khởi tạo adapter với danh sách rỗng ban đầu
        storyAdapter = new StoryAdapter(getContext(), new ArrayList<Story>()); // Đảm bảo kiểu Story
        recyclerViewStories.setAdapter(storyAdapter);

        loadFullStoryListFromJsonAndFilter();
    }

    private void loadFullStoryListFromJsonAndFilter() {
        String jsonString;
        try {
            if (getContext() == null) {
                Log.e(TAG, "Context is null in loadFullStoryList");
                showNoStoriesMessage(true);
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

            filterAndDisplayStoriesForCategory();

        } catch (IOException e) {
            e.printStackTrace();
            fullStoryList = new ArrayList<>();
            if (getContext() != null) {
                Toast.makeText(getContext(), "Lỗi khi đọc file JSON", Toast.LENGTH_SHORT).show();
            }
            Log.e(TAG, "IOException while loading JSON: " + e.getMessage());
            showNoStoriesMessage(true);
        }
    }

    private void filterAndDisplayStoriesForCategory() {
        // Dòng gây lỗi trước đó là ở đây, khi selectedCategoryName hoặc fullStoryList rỗng
        // và chúng ta gọi storyAdapter.setData(new ArrayList<>());
        // Giờ chúng ta sẽ đảm bảo truyền new ArrayList<Story>()

        if (selectedCategoryName == null || fullStoryList == null ) { // Kiểm tra null cho fullStoryList
            Log.d(TAG, "Cannot filter: category name is null or fullStoryList is null.");
            showNoStoriesMessage(true);
            if(storyAdapter != null) {
                storyAdapter.setData(new ArrayList<Story>()); // SỬA Ở ĐÂY: Chỉ định rõ kiểu Story
            }
            return;
        }
        if (fullStoryList.isEmpty()){ // Kiểm tra nếu fullStoryList rỗng sau khi load
            Log.d(TAG, "Cannot filter: fullStoryList is empty.");
            showNoStoriesMessage(true);
            if(storyAdapter != null) {
                storyAdapter.setData(new ArrayList<Story>()); // SỬA Ở ĐÂY: Chỉ định rõ kiểu Story
            }
            return;
        }


        List<Story> filteredStories = new ArrayList<>();
        String categoryToFilter = selectedCategoryName.toLowerCase(Locale.getDefault()).trim();

        for (Story story : fullStoryList) {
            if (story.getCategory() != null &&
                    story.getCategory().toLowerCase(Locale.getDefault()).trim().equals(categoryToFilter)) {
                filteredStories.add(story);
            }
        }
        Log.d(TAG, "Stories found in category '" + selectedCategoryName + "': " + filteredStories.size());

        if (storyAdapter != null) {
            storyAdapter.setData(filteredStories);
        }
        showNoStoriesMessage(filteredStories.isEmpty());
    }

    // Trong StoriesByCategoryFragment.java
    private void showNoStoriesMessage(boolean show) {
        if (textViewNoStories != null && recyclerViewStories != null) {
            Log.d(TAG, "showNoStoriesMessage: " + show + ", RecyclerView visibility: " + (show ? "GONE" : "VISIBLE")); // KIỂM TRA
            textViewNoStories.setVisibility(show ? View.VISIBLE : View.GONE);
            recyclerViewStories.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}