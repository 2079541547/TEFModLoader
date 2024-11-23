package eternal.future.efmodloader.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*******************************************************************************
 * 文件名称: FileUtils
 * 项目名称: TEFModLoader
 * 创建时间: 2024/11/23
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547 
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
 * 许可证: This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as published
 *         by the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program. If not, see <https://www.gnu.org/licenses/>.
 * 描述信息: 本文件为EFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

public class FileUtils {
    private static final String TAG = "FileUtils";

    public static void copyFileWithTimestamp(File sourcePath, File destPath) {
        if (destPath.exists()) {
            long sourceLastWriteTime = sourcePath.lastModified();
            long destLastWriteTime = destPath.lastModified();
            long sourceFileSize = sourcePath.length();
            long destFileSize = destPath.length();

            if (sourceLastWriteTime != destLastWriteTime || sourceFileSize != destFileSize) {
                try {
                    copyFile(sourcePath, destPath);
                    destPath.setLastModified(sourceLastWriteTime);
                    Log.d(TAG, "复制文件: " + sourcePath.getAbsolutePath() + " 到 " + destPath.getAbsolutePath());
                } catch (IOException e) {
                    Log.e(TAG, "复制文件失败: " + sourcePath.getAbsolutePath() + " 错误: " + e.getMessage());
                }
            } else {
                Log.d(TAG, "文件相同，跳过复制: " + sourcePath.getAbsolutePath() + "->" + destPath.getAbsolutePath());
            }
        } else {
            try {
                copyFile(sourcePath, destPath);
                destPath.setLastModified(sourcePath.lastModified());
                Log.d(TAG, "复制文件: " + sourcePath.getAbsolutePath() + " 到 " + destPath.getAbsolutePath());
            } catch (IOException e) {
                Log.e(TAG, "复制文件失败: " + sourcePath.getAbsolutePath() + " 错误: " + e.getMessage());
            }
        }
    }

    public static void copyFilesFromTo(File sourceDir, File destDir) {
        if (!sourceDir.exists()) {
            Log.e(TAG, "源目录不存在: " + sourceDir.getAbsolutePath());
            return;
        }

        if (!destDir.exists()) {
            destDir.mkdirs();
            Log.d(TAG, "创建目标目录: " + destDir.getAbsolutePath());
        }

        File[] entries = sourceDir.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                File destPath = new File(destDir, entry.getName());

                if (entry.isDirectory()) {
                    // 递归复制子目录
                    copyFilesFromTo(entry, destPath);
                } else {
                    // 复制文件并设置时间戳
                    FileUtils.copyFileWithTimestamp(entry, destPath);
                }
            }
        }
    }

    public static void copyFile(File source, File dest) throws IOException {
        // 使用 try-with-resources 语句自动管理资源的关闭
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(dest)) {

            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
    }
}
