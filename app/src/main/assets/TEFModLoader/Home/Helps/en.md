# Table of Contents
- [Introduction](#introduction)
    - [Mod Issues](#mod)
        - [Mod has no effect?](#modinvalid)
        - [Mod crashes on loading?](#modflashretreat)
        - [Mod does not display icon information?](#modnoiconorinformation)
        - [Mod crashes during decompression?](#modflashbackduringdecompression)
        - [Mod crashes during runtime?](#modruntimecrash)
        - [Null pointer appears during mod decompression?](#emptypointerappearsduringmoddecompression)

## Introduction <a id="introduction"></a>
* Please ensure you have normal comprehension and vision, and do not stubbornly stick to one method without trying alternatives. This is the minimum requirement for reading this document. Do not act like someone who asks questions everywhere without thinking. If you really cannot solve it, consider seeking professional help.

## Mod Issues <a id="mod"></a>

### Mod has no effect? <a id="modinvalid"></a>
* Ensure that the kernel has been injected into the game, and that the game has read/write permissions. For Android versions below 13, you can try using shared mode by adding `android:sharedUserId="silkways.terraria.efmodloader"` in the game's manifest file. Note: Games running in this mode may not require specific permissions.

### Mod crashes on loading? <a id="modflashretreat"></a>
* Check the logs; it is usually due to the Mod using outdated APIs. If it is an independent Mod, there might be dependency issues or other problems. If none of these apply, you can report the issue on GitHub along with detailed log files.

### Mod does not display icon information? <a id="modnoiconorinformation"></a>
* This is generally due to unsupported files or the file not being an EFMod. It could also be because the Mod uses an older or newer file system version than the current manager's file system, causing the manager's file system to fail to parse it correctly.

### Mod crashes during decompression? <a id="modflashbackduringdecompression"></a>
* There can be many causes, such as the Mod using non-standard, deprecated, or advanced compression modes. You should contact the Mod author for assistance.

### Mod crashes during runtime? <a id="modruntimecrash"></a>
* This can be difficult to diagnose. You should provide log files. If you can understand them, try to determine whether the issue is with the kernel or the Mod, then contact the appropriate developer for a solution.

### Null pointer appears during mod decompression? <a id="emptypointerappearsduringmoddecompression"></a>
* Most likely, you have not granted TEFModLoader permission to manage files. If this is not the case, contact the developer and provide log files for further analysis.