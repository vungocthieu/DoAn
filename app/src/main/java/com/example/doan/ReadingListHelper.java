package com.example.doan; // Đảm bảo package name đúng với project của bạn

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors; // Cần Java 8 trở lên

public class ReadingListHelper {

    private static final String PREFS_NAME = "ReadingStoryPrefs"; // Đổi tên file prefs nếu muốn
    private static final String KEY_READING_STORY_IDS = "reading_story_ids_set"; // Key để lưu Set
    private SharedPreferences sharedPreferences;

    public ReadingListHelper(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null for ReadingListHelper");
        }
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Lưu danh sách ID truyện đang đọc (dưới dạng Set<String>)
    public void saveReadingStoryIds(Set<Integer> storyIds) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> stringSet = new HashSet<>();
        if (storyIds != null) {
            for (Integer id : storyIds) {
                stringSet.add(String.valueOf(id));
            }
        }
        editor.putStringSet(KEY_READING_STORY_IDS, stringSet);
        editor.apply(); // Dùng apply() để lưu bất đồng bộ
    }

    // Lấy danh sách ID truyện đang đọc (trả về Set<Integer>)
    public Set<Integer> getReadingStoryIds() {
        Set<String> stringSet = sharedPreferences.getStringSet(KEY_READING_STORY_IDS, new HashSet<>());
        Set<Integer> integerSet = new HashSet<>();
        if (stringSet != null) {
            for (String s : stringSet) {
                try {
                    integerSet.add(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    // Ghi log lỗi hoặc bỏ qua ID không hợp lệ
                    e.printStackTrace();
                }
            }
        }
        return integerSet;
    }

    // Thêm một ID truyện vào danh sách đang đọc
    public void addStoryToReadingList(int storyId) {
        Set<Integer> currentIds = getReadingStoryIds();
        currentIds.add(storyId);
        saveReadingStoryIds(currentIds);
    }

    // (Tùy chọn) Xóa một ID truyện khỏi danh sách đang đọc
    public void removeStoryFromReadingList(int storyId) {
        Set<Integer> currentIds = getReadingStoryIds();
        currentIds.remove(storyId);
        saveReadingStoryIds(currentIds);
    }

    // (Tùy chọn) Kiểm tra xem một truyện có đang trong danh sách đọc không
    public boolean isStoryInReadingList(int storyId) {
        return getReadingStoryIds().contains(storyId);
    }
}