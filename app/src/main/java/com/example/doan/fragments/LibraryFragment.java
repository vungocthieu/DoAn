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

    private static final String TAG = "LibraryFragment"; // Thêm TAG để dễ debug

    private RecyclerView recyclerReadingList;
    private TextView textViewEmptyList;
    private StoryAdapter readingListAdapter;
    private List<Story> fullStoryListFromAsset = new ArrayList<>(); // Danh sách tất cả truyện từ JSON
    private List<Story> currentlyReadingStoriesForUi = new ArrayList<>(); // Danh sách để adapter hiển thị

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        recyclerReadingList = view.findViewById(R.id.recycler_view_reading_list_library);
        textViewEmptyList = view.findViewById(R.id.text_view_empty_reading_list);

        recyclerReadingList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // Khởi tạo adapter với danh sách rỗng ban đầu, và true để hiển thị nút xóa
        readingListAdapter = new StoryAdapter(getContext(), new ArrayList<>(), true); // Truyền danh sách rỗng ban đầu
        recyclerReadingList.setAdapter(readingListAdapter);

        // Tải danh sách đầy đủ tất cả truyện trước, sau đó mới lọc truyện đang đọc
        loadFullStoryListAndThenCurrentlyReading();
    }

    private void loadFullStoryListAndThenCurrentlyReading() {
        Log.d(TAG, "loadFullStoryListAndThenCurrentlyReading called");
        String jsonString;
        try {
            if (getContext() == null) {
                Log.e(TAG, "Context is null in loadFullStoryListFromJsonForLibrary");
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
            fullStoryListFromAsset = gson.fromJson(jsonString, storyListType);

            if (fullStoryListFromAsset == null) {
                fullStoryListFromAsset = new ArrayList<>();
                Log.w(TAG, "Parsed fullStoryListFromAsset is null, initialized as empty list.");
            }
            Log.d(TAG, "Full story list loaded from JSON: " + fullStoryListFromAsset.size() + " items");

            // Sau khi load xong fullStoryList, gọi loadCurrentlyReadingStories
            loadCurrentlyReadingStories();

        } catch (IOException e) {
            e.printStackTrace();
            fullStoryListFromAsset = new ArrayList<>();
            if (getContext() != null) {
                Toast.makeText(getContext(), "Lỗi khi đọc file JSON", Toast.LENGTH_SHORT).show();
            }
            Log.e(TAG, "IOException while loading JSON: " + e.getMessage());
            showEmptyListMessage(true); // Hiển thị lỗi nếu không đọc được JSON
        }
    }

    private void loadCurrentlyReadingStories() {
        Log.d(TAG, "loadCurrentlyReadingStories called");
        if (getContext() == null) {
            Log.e(TAG, "Context is null in loadCurrentlyReadingStories");
            return;
        }
        if (fullStoryListFromAsset.isEmpty()) {
            Log.w(TAG, "fullStoryListFromAsset is empty, cannot load reading stories.");
            // Có thể gọi loadFullStoryListAndThenCurrentlyReading() nếu muốn thử tải lại,
            // nhưng cẩn thận vòng lặp vô hạn nếu file JSON thực sự rỗng hoặc có vấn đề.
            // Hiện tại, nếu list gốc rỗng, danh sách đọc cũng sẽ rỗng.
            showEmptyListMessage(true);
            return;
        }

        ReadingListHelper readingListHelper = new ReadingListHelper(requireContext());
        Set<Integer> readingIds = readingListHelper.getReadingStoryIds();
        Log.d(TAG, "Reading IDs from SharedPreferences: " + readingIds.toString());

        currentlyReadingStoriesForUi.clear(); // Xóa danh sách UI cũ

        for (Story story : fullStoryListFromAsset) { // Duyệt qua danh sách ĐẦY ĐỦ từ JSON
            if (readingIds.contains(story.getId())) {
                currentlyReadingStoriesForUi.add(story);
            }
        }
        Log.d(TAG, "Currently reading stories count: " + currentlyReadingStoriesForUi.size());

        // Cập nhật adapter bằng phương thức updateData (hoặc tạo mới nếu muốn)
        if (readingListAdapter != null) {
            // Quan trọng: StoryAdapter của bạn cần một phương thức để cập nhật dữ liệu
            // Dựa trên code StoryAdapter bạn gửi, nó có hàm filter và constructor.
            // Chúng ta sẽ sử dụng lại constructor để "làm mới" adapter với dữ liệu đúng.
            // Hoặc tốt hơn là có một hàm setData(List<Story> newList) trong StoryAdapter.
            // Hiện tại, tôi sẽ cập nhật trực tiếp list của adapter và notify.

            // Cập nhật danh sách mà adapter đang giữ tham chiếu
            readingListAdapter.updateData(currentlyReadingStoriesForUi);
        } else {
            // Trường hợp này ít khi xảy ra nếu adapter được khởi tạo trong onViewCreated
            Log.e(TAG, "readingListAdapter is null, creating new one.");
            readingListAdapter = new StoryAdapter(getContext(), currentlyReadingStoriesForUi, true);
            recyclerReadingList.setAdapter(readingListAdapter);
        }
        showEmptyListMessage(currentlyReadingStoriesForUi.isEmpty());
    }

    // Hàm này được gọi từ StoryAdapter khi một item bị xóa, hoặc khi cần kiểm tra
    public void checkAndShowEmptyListMessage() {
        if(readingListAdapter != null && textViewEmptyList != null && recyclerReadingList != null){
            showEmptyListMessage(readingListAdapter.getItemCount() == 0);
        } else if (textViewEmptyList != null && recyclerReadingList != null) {
            Log.w(TAG, "checkAndShowEmptyListMessage called but adapter is null or views are null.");
            textViewEmptyList.setVisibility(View.VISIBLE);
            recyclerReadingList.setVisibility(View.GONE);
        }
    }

    // Hàm riêng để hiển thị/ẩn thông báo danh sách trống
    public void showEmptyListMessage(boolean show) {
        if (textViewEmptyList != null && recyclerReadingList != null) {
            Log.d(TAG, "showEmptyListMessage: " + show);
            textViewEmptyList.setVisibility(show ? View.VISIBLE : View.GONE);
            recyclerReadingList.setVisibility(show ? View.GONE : View.VISIBLE);
        } else {
            Log.e(TAG, "textViewEmptyList or recyclerReadingList is null in showEmptyListMessage");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        if (isAdded() && getContext() != null) {
            // Luôn tải lại danh sách đầy đủ và sau đó lọc truyện đang đọc
            // để đảm bảo dữ liệu là mới nhất nếu file JSON có thể thay đổi (dù trong TH này là không)
            // và để đảm bảo fullStoryListFromAsset không rỗng.
            loadFullStoryListAndThenCurrentlyReading();
        }
    }
}