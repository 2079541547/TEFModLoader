# 目錄
- [簡介](#introduction)
    - [Mod問題](#mod)
        - [Mod沒有效果？](#modinvalid)
        - [Mod加載時閃退？](#modflashretreat)
        - [Mod不顯示圖標信息？](#modnoiconorinformation)
        - [Mod解壓時閃退？](#modflashbackduringdecompression)
        - [Mod運行中閃退？](#modruntimecrash)
        - [Mod解壓時出現空指針？](#emptypointerappearsduringmoddecompression)

## 簡介 <a id="introduction"></a>
* 請確保您具有正常的理解能力和視力，並且不會在一棵樹上吊死，這是閱讀本文檔的最低要求。請不要像個小唐人一樣到處問這個問那個，實在不行去掛個腦科

## Mod問題 <a id="mod"></a>

### Mod沒有效果？<a id="modinvalid"></a>
* 請確保內核已注入遊戲，且遊戲擁有讀取/寫入權限。如果是Android 13以下可以嘗試使用共享模式，您需要在遊戲聲明文件中添加 `android:sharedUserId="silkways.terraria.efmodloader"`，注意：此工作模式的遊戲可以不需要權限即可。

### Mod加載時閃退？<a id="modflashretreat"></a>
* 查看日誌，通常是因為Mod使用了過時API導致的，如果是獨立Mod就可能是依賴或其他問題。如果都不是可以在GitHub上發問題以及詳細的日誌文件

### Mod不顯示圖標信息？<a id="modnoiconorinformation"></a>
* 一般是因為不支持文件或者根本不是EFMod。也可能是因为Mod使用的文件系統版本較為落後或者高於當前管理器文件系統的版本，導致管理器的文件系統無法正常解析

### Mod解壓時閃退？<a id="modflashbackduringdecompression"></a>
* 導致的因素很多，如：Mod使用了非標準或已廢棄甚至領先的壓縮模式，您應該聯繫Mod作者

### Mod運行中閃退？<a id="modruntimecrash"></a>
* 這很難說，您應該提供日誌文件，如果您能看懂的話可以嘗試查找是內核的bug還是mod的bug，然後找對應的開發者解決

### Mod解壓時出現空指針？<a id="emptypointerappearsduringmoddecompression"></a>
* 大概率是因為您沒有給TEFModLoader管理文件權限，如果不是請聯繫開發者並提供日誌文件