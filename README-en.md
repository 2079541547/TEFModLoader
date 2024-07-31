# **EternalCraft.efmod**

* This is a unique MOD that can be launched without a launcher, and can also be launched without the main so. We advocate freedom, and no APIs are needed during the development process.

  ## Android Studio

  * We recommend using Android Studio for packaging, because if you use the Android compiler, the support is not very good. Using Android Studio for packaging can use the most complete system, including two instances. One is not much different from the Android compiler, and the other is integrated with shadowhook, BNM (for short). This instance is completely free and can completely detach from the main so. It is mainly used to extend the originally limited functions, with C++20 as the minimum standard.

  ## AIDE

  * You must install NDK, with C++17 as the minimum standard. This template does not have strong extensibility, and some parts still need to rely on the main so, such as calling the original game functions.

  # How to make a Mod?

  * Prepare the compilation tool, using C++ as the standard.

    ## 1. Create your code

    ```C++
    /*
      This is the simplest example code.
    */

    extern "C"{
      // Must use extern "C"! Otherwise, it cannot be called by Major because the functions of the binary package will not have modifiers.
      // Suppose this is the code that needs to be called, func1 is an int type that returns 114514, func2 is a bool type, and returns true.
      int func1(){
        return 114514;
      }
      bool func2(){
        return true;
      }
    }
    ```

    ## 2. Create the mod configuration JSON

    * mod_info

      ```Json
      [
          {
              "author": "Name", // Author's name
              "modName": "MyMod", // Mod name
              "build": 1, // Version
              "modIntroduce": "This is my mod", // Mod introduction
              "Opencode": false, // Whether it is open source
              "OpencodeUrl": null, // If open source, fill in the open source link
              "enable": false, // Whether it is enabled, must be filled with false
              "enableLibName": "libMyMod.so" // The full name of the so library called
          }
      ]
      ```

    * mod_data

      ```Json
      [
        [
          {
            "author": "Name", // Author's name
            "modName": "MyMod", // Mod name
            "libname": "libMyMod.so" // Full name of the mod's so library
          },
          [
              {
                "position": "Assembly-CSharp.Cursor.Id", // The function to be hooked, the first is the name of the dll, the second is the namespace (if there is none, then directly fill in the third), the third is the function/field
                "function": ["func1"], // Your function, can be multiple such as ["func1", "func2"]
                "type": "int", // Type
                "arrays": 0 // The value in the array returned, 0 is the first value, 1 is the second value and so on
              },
              {
                // If you need to hook multiple functions
                // Similar to the above
                "position": "Assembly-CSharp.Terraria.NPC.GetNPCLocation", 
                "function": ["func2"],
                "type": "bool",
                "arrays": 0
              }
          ]
        ]
      ]
      ```

      ## 3. Create the mod directory

      ```txt
      MyMod-Name  The root directory of the mod, must be the mod name + author's name
      MyMod-Name/resources  The resources used by the mod
      MyMod-Name/icon.png  The mod icon
      MyMod-Name/libMyMod.so  The mod so library
      MyMod-Name/mod_data.json  The mod configuration
      MyMod-Name/mod_info.json  The mod information
      ```

    ## 4. Package the mod

    * Essentially it is a zip file, you can run a Python script or third-party tools to compress it, and the suffix is efmod.
    * To run the Python script, make sure you have installed the zipfile library
      > pip install zipfile
    * Pass in the mod directory as follows:
      > /home/eternalfuture/Documents/GitHub/Terraria-ToolBox/MyMod-Name
