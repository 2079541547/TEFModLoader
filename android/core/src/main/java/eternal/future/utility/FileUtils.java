package eternal.future.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileUtils {
    public static void copyFileWithTimestamp(File sourcePath, File destPath) {
        if (destPath.exists()) {
            long sourceLastWriteTime = sourcePath.lastModified();
            long destLastWriteTime = destPath.lastModified();
            long sourceFileSize = sourcePath.length();
            long destFileSize = destPath.length();

            if (sourceLastWriteTime != destLastWriteTime || sourceFileSize != destFileSize) {
                copyFile(sourcePath, destPath);
                destPath.setLastModified(sourceLastWriteTime);
            }
        } else {
            copyFile(sourcePath, destPath);
            destPath.setLastModified(sourcePath.lastModified());
        }
    }

    public static void copyFilesFromTo(File sourceDir, File destDir) {
        if (!sourceDir.exists()) {
            return;
        }

        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        File[] entries = sourceDir.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                File destPath = new File(destDir, entry.getName());

                if (entry.isDirectory()) {
                    copyFilesFromTo(entry, destPath);
                } else {
                    copyFileWithTimestamp(entry, destPath);
                }
            }
        }
    }


    public static void moveContent(File sourceDir, File targetDir) {

        if (!sourceDir.isDirectory()) {
            throw new IllegalArgumentException("Source must be a directory.");
        }

        if (targetDir.exists()) {
            deleteDirectory(targetDir);
        }

        if (!targetDir.mkdirs()) {
            throw new RuntimeException("Failed to create target directory.");
        }

        File[] contents = sourceDir.listFiles();
        if (contents != null) {
            for (File source : contents) {
                File target = new File(targetDir, source.getName());
                moveFileOrDir(source, target);
            }
        }
    }

    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] entries = dir.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    deleteDirectory(entry);
                }
            }
        }
        if (!dir.delete()) {
            throw new RuntimeException("Failed to delete file or directory: " + dir.getPath());
        }
    }

    private static void moveFileOrDir(File source, File target) {
        if (source.isDirectory()) {
            if (!target.exists() && !target.mkdirs()) {
                System.out.println("Failed to create directory: " + target.getAbsolutePath());
                return;
            }
            File[] contents = source.listFiles();
            if (contents != null) {
                for (File file : contents) {
                    moveFileOrDir(file, new File(target, file.getName()));
                }
            }
            if (!source.delete()) {
                System.out.println("Failed to delete directory: " + source.getAbsolutePath());
            }
        } else {
            boolean success = copyFile(source, target);
            if (success && !source.delete()) {
                System.out.println("Failed to delete file: " + source.getAbsolutePath());
            }
        }
    }

    private static boolean copyFile(File source, File target) {
        try {
            FileInputStream inputStream = new FileInputStream(source);
            FileOutputStream outputStream = new FileOutputStream(target);
            FileChannel inChannel = inputStream.getChannel(), outChannel = outputStream.getChannel();

            try {
                inChannel.transferTo(0, inChannel.size(), outChannel);
            } finally {
                if (inChannel != null) inChannel.close();
                if (outChannel != null) outChannel.close();
                inputStream.close();
                outputStream.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}