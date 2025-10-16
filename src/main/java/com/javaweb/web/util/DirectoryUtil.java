package com.javaweb.web.util;

import java.io.File;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class DirectoryUtil {
    private static String removeVietnameseAccents(String input) {
        if (input == null) return null;
        input = input.replaceAll("[đĐ]", "d");
        // Chuẩn hóa Unicode để tách dấu ra khỏi ký tự
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Loại bỏ các dấu (ký tự kết hợp)
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String result = pattern.matcher(normalized).replaceAll("");
        // Chuyển thành chữ thường
        return result.toLowerCase();
    }

    public void createVideoDirectory(String courseTitle, String sectionTitle) {
        String noAccentCourse = removeVietnameseAccents(courseTitle);
        String noAccentSection = removeVietnameseAccents(sectionTitle);

        String safeCourseTitle = noAccentCourse.replaceAll("[^a-zA-Z0-9]", "_");
        String safeSectionTitle = noAccentSection.replaceAll("[^a-zA-Z0-9]", "_");

        String folderPath = "src/main/resources/static/videos/" + safeCourseTitle + "/" + safeSectionTitle;
        File directory = new File(folderPath);

        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Created directory: " + directory.getAbsolutePath());
            } else {
                System.err.println("Failed to create directory: " + directory.getAbsolutePath());
            }
        }
    }
    public static void createCourseVideoDirectory(String courseTitle) {
        String noAccentTitle = removeVietnameseAccents(courseTitle);
        String safeCourseTitle = noAccentTitle.replaceAll("[^a-zA-Z0-9]", "_");
        String folderPath = "src/main/resources/static/videos/" + safeCourseTitle;
        File directory = new File(folderPath);

        if (!directory.exists()) {
            System.out.println("Created course video directory: " + directory.getAbsolutePath());
        }
    }
}

