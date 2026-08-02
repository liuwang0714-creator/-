package org.example.sotokenspringboot.utils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class DeleteFileNioDemo {
    public static void deleteFileNio(String file_url) {
        // 1. 构建 Path 对象
        Path filePath = Paths.get("E:\\pg_file\\images", file_url);

        try {
            // 2. 执行删除（文件不存在会抛出 NoSuchFileException）
            Files.delete(filePath);
            System.out.println("文件删除成功: " + filePath);
        } catch (IOException e) {
            System.err.println("文件删除失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}