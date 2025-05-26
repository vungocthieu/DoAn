package com.example.doan.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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

public class SearchFragment extends Fragment {

    private SearchView searchViewFragment;
    private RecyclerView recyclerSearchResults;
    private TextView textViewNoResults;
    private StoryAdapter searchResultsAdapter;
    private List<Story> fullStoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchViewFragment = view.findViewById(R.id.search_view_fragment);
        recyclerSearchResults = view.findViewById(R.id.recycler_view_search_results);
        textViewNoResults = view.findViewById(R.id.text_view_no_search_results);

        loadFullStoryListFromJson();

        recyclerSearchResults.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // Khởi tạo adapter với danh sách đầy đủ, nó sẽ tự lọc khi query thay đổi
        searchResultsAdapter = new StoryAdapter(getContext(), fullStoryList);
        recyclerSearchResults.setAdapter(searchResultsAdapter);

        setupSearchView();
        // Ban đầu không lọc gì cả, adapter sẽ hiển thị storyListFull (có thể là rỗng nếu chưa load xong)
        // Hoặc bạn có thể gọi filter("") để đảm bảo hiển thị toàn bộ nếu fullStoryList đã có dữ liệu
        if (searchResultsAdapter != null) {
            searchResultsAdapter.filter(""); // Hiển thị tất cả ban đầu
        }
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
            Type storyListType = new TypeToken<ArrayList<Story>>() {}.getType();
            fullStoryList = gson.fromJson(jsonString, storyListType);
            if (fullStoryList == null) {
                fullStoryList = new ArrayList<>();
            }
            Log.d("SearchFragment", "Full story list loaded: " + fullStoryList.size() + " items");
        } catch (IOException e) {
            e.printStackTrace();
            fullStoryList = new ArrayList<>();
            Toast.makeText(getContext(), "Lỗi khi đọc file JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSearchView() {
        if (searchViewFragment == null) return;
        searchViewFragment.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (searchResultsAdapter != null) {
                    searchResultsAdapter.filter(query);
                }
                searchViewFragment.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchResultsAdapter != null) {
                    searchResultsAdapter.filter(newText);
                }
                // Cập nhật thông báo không có kết quả
                if (newText != null && !newText.isEmpty() && searchResultsAdapter != null && searchResultsAdapter.getItemCount() == 0) {
                    textViewNoResults.setVisibility(View.VISIBLE);
                    recyclerSearchResults.setVisibility(View.GONE);
                } else {
                    textViewNoResults.setVisibility(View.GONE);
                    if(recyclerSearchResults != null) recyclerSearchResults.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        searchViewFragment.onActionViewExpanded();
    }
}