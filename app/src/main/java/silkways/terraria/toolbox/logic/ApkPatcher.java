package silkways.terraria.toolbox.logic;

import com.android.tools.build.apkzlib.zip.AlignmentRules;
import com.android.tools.build.apkzlib.zip.ZFile;
import com.android.tools.build.apkzlib.zip.ZFileOptions;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ApkPatcher {

    private static final ZFileOptions Z_FILE_OPTIONS = new ZFileOptions().setAlignmentRule(AlignmentRules.compose(
            AlignmentRules.constantForSuffix(".so", 4096),
            AlignmentRules.constantForSuffix(".assets", 4096)
    ));


    /**
     * 将指定目录及其子目录下的所有 .so 文件添加到给定的 APK 文件中。
     * .so 文件将被放置在 APK 文件内的 "lib/arm64-v8a/" 路径下，
     * 只保留文件名（包括后缀），不保留原有的子目录结构。
     *
     * @param apkPath APK 文件的路径。
     * @param libDir  包含要添加的 .so 文件的目录。
     */
    public static void addSOsToAPK(String apkPath, String libDir) {
        File dir = new File(libDir);

        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Provided path is not a directory: " + libDir);
        }

        // 递归遍历目录及其子目录中的所有 .so 文件
        boolean hasSoFiles = addSOsRecursively(dir, apkPath);

        if (!hasSoFiles) {
            System.out.println("No .so files found in the provided directory.");
        }
    }

    private static boolean addSOsRecursively(File currentDir, String apkPath) {
        File[] files = currentDir.listFiles();

        if (files == null || files.length == 0) {
            return false;
        }

        boolean hasSoFiles = false;

        for (File file : files) {
            if (file.isDirectory()) {
                // 如果是目录，则递归处理该目录
                hasSoFiles |= addSOsRecursively(file, apkPath);
            } else if (file.getName().endsWith(".so")) {
                // 如果是 .so 文件，则添加到 APK
                try (var dstZFile = ZFile.openReadWrite(new File(apkPath), Z_FILE_OPTIONS)) {
                    try (FileInputStream is = new FileInputStream(file)) {
                        // 构建在 APK 内的目标路径
                        String relativePath = "lib/arm64-v8a/" + file.getName();
                        dstZFile.add(relativePath, is, false);
                    } catch (IOException e) {
                        throw new RuntimeException("无法添加 .so 文件: " + file, e);
                    }

                    dstZFile.realign();
                    hasSoFiles = true;
                } catch (IOException e) {
                    throw new RuntimeException("无法修改 APK: " + apkPath, e);
                }
            }
        }

        return hasSoFiles;
    }



}
