package silkways.terraria.efmodloader.logic.efmod;

import java.util.Map;

/*******************************************************************************
 * 文件名称: SilkCasketCompressor
 * 项目名称: TEFModLoader
 * 创建时间: 2024/12/8
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547 
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
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
 *
 * 描述信息: 本文件为TEFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

public class SilkCasket {
    public static native void compressDirectory(boolean suffix, String targetPath, String outPath, boolean[] mode, long blockSize, boolean entryEncryption, String key);
    public static native void compressAFile(boolean suffix, String targetPath, String outPath, boolean[] mode, long blockSize, boolean entryEncryption, String key);
    public static native void compressFiles(boolean suffix, Map<String, String> targetPaths, String outPath, boolean[] mode, long blockSize, boolean entryEncryption, String key);
    public static native void compress(boolean suffix, Map<String, String> targetPaths, String outPath, boolean[] mode, long blockSize, boolean entryEncryption, String key);
    public static native void releaseAllEntry(String filePath, String outPath, String key);
    public static native void releaseEntry(String filePath, String entry, String outPath, String key);
    public static native void releaseFolder(String filePath, String entry, String outPath, String key);
    public static native byte[] getEntryData(String filePath, String entry, String key);
}