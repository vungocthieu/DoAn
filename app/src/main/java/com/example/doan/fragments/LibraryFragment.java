package com.example.doan.fragments; // Đảm bảo package name đúng

import android.os.Bundle;
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
import com.example.doan.ReadingListHelper;
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
import java.util.Set;

public class LibraryFragment extends Fragment {

    private RecyclerView recyclerReadingList;
    private TextView textViewEmptyList;
    private StoryAdapter readingListAdapter;
    private List<Story> fullStoryList = new ArrayList<>();
    private List<Story> currentlyReadingStories = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerReadingList = view.findViewById(R.id.recycler_view_reading_list_library);
        textViewEmptyList = view.findViewById(R.id.text_view_empty_reading_list);

        recyclerReadingList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // Truyền true cho showDeleteButton khi tạo adapter cho LibraryFragment
        readingListAdapter = new StoryAdapter(getContext(), currentlyReadingStories, true);
        recyclerReadingList.setAdapter(readingListAdapter);

        loadFullStoryListFromJson();
        // loadCurrentlyReadingStories(); // Sẽ gọi trong onResume
    }

    private void loadFullStoryListFromJson() {
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
        } catch (IOException e) {
            e.printStackTrace();
            fullStoryList = new ArrayList<>();
            Toast.makeText(getContext(), "Lỗi khi đọc file JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCurrentlyReadingStories() {
        if (getContext() == null) return;
        if (fullStoryList.isEmpty() && getView() != null) {
            loadFullStoryListFromJson();
            if(fullStoryList.isEmpty()){
                showEmptyListMessage(true);
                return;
            }
        }

        ReadingListHelper readingListHelper = new ReadingListHelper(requireContext());
        Set<Integer> readingIds = readingListHelper.getReadingStoryIds();
        currentlyReadingStories.clear();

        for (Story story : fullStoryList) {
            if (readingIds.contains(story.getId())) {
                currentlyReadingStories.add(story);
            }
        }

        showEmptyListMessage(currentlyReadingStories.isEmpty());

        if (readingListAdapter != null) {
            readingListAdapter.notifyDataSetChanged();
        }
    }

    // Hàm để hiển thị/ẩn thông báo danh sách trống
    public void showEmptyListMessage(boolean show) {
        if (textViewEmptyList != null && recyclerReadingList != null) {
            textViewEmptyList.setVisibility(show ? View.VISIBLE : View.GONE);
            recyclerReadingList.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded() && getContext() != null) {
            if (fullStoryList.isEmpty()) {
                loadFullStoryListFromJson();
            }
            loadCurrentlyReadingStories();
        }
    }
}