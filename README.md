# **EternalCraft.efmod**

* [Русский 👈](https://github.com/2079541547/Terraria-ToolBox/blob/mod/README-ru.md)
* [English 👈](https://github.com/2079541547/Terraria-ToolBox/blob/mod/README-en.md)

* 这是独一无二的MOD，可以脱离启动器，也可以脱离主要so，我们提倡自由，开发过程中无需使用任何API
  
  ## Android Studio
  
  * 我们推荐使用Android Studio打包，因为如果用安卓上的编译器支持不是很好，使用Android Studio打包可以使用最完整的体系，包含两个实例，一个与安卓上的编译器无大差别，另一个则集成了shadowhook ，BNM(简称)，此实例完全自由可以完全脱离主要so，主要是扩展原本受限的功能，c++20为最低标准
  
  ## AIDE
  
  * 必须安装NDK，c++17为最低标准，此模板并没有很强的扩展性，有部分依旧需要依赖主要so，如调用游戏原函数等
  
  # 如何制作Mod？
  
  * 准备编译工具，使用c++为标准
    
    ## 1.创建你的代码
    
    ```C++
    /*
      此为最简单的实例代码
    */
    
    extern "C"{
      //必须使用extern "C"！否则无法被Major调用，因为使用此声明后打包的二进制的函数不会有修饰词
      //假设此是需要调用的代码，func1为int类型返回了114514，func2为bool类型，返回true
      int func1(){
        return 114514;
      }
      bool func2(){
        return true;
      }
    }
    ```
    
    ## 2.创建模组配置的json
    
    * mod_info
      
      ```Json
      [
          {
              "author": "Name", //作者名称
              "modName": "MyMod", //模组名称
              "build": 1, //版本
              "modIntroduce": "This is my mod", //模组介绍
              "Opencode": false, //是否开源
              "OpencodeUrl": null, //如果开源则填写开源链接
              "enable": false, //是否已启用，必须填写false
              "enableLibName": "libMyMod.so" //调用的so库完整名称
          }
      ]
      ```
    * mod_data
      
      ```Json
      [
        [
          {
            "author": "Name", //作者名称
            "modName": "MyMod", //模组名称
            "libname": "libMyMod.so" //模组的完整so库名称
          },
          [
              {
                "position": "Assembly-CSharp.Cursor.Id", //被hook的函数，第一个为dll名称，第二个为命名空间（没有则直接填写第三个），第三个为函数/字段
                "function": ["func1"], //你的函数，可以多个如["func1", "func2"]
                "type": "int", //类型
                "arrays": 0 //返回的数组中的值，0则是第一个值，1则是第二个值以此类推
              },
              {
                //如果需要hook多个函数
                //与上方相似
                "position": "Assembly-CSharp.Terraria.NPC.GetNPCLocation", 
                "function": ["func2"],
                "type": "bool",
                "arrays": 0
              }
          ]
        ]
      ]
      ```
      
      ## 3.创建模组目录
      
      ```txt
      MyMod-Name 模组根目录，必须为模组名称+作者名称
      MyMod-Name/resources 模组使用的资源
      MyMod-Name/icon.png 模组图标
      MyMod-Name/libMyMod.so 模组so库
      MyMod-Name/mod_data.json 模组配置
      MyMod-Name/mod_info.json 模组信息
      ```
    
    ## 4.打包模组
    
    * 本质上是一个zip文件，你可以运行Python脚本或者第三方工具进行压缩，后缀为efmod
    * 运行Python脚本请确保安装了zipfile库
      > pip install zipfile
    * 传入模组目录即可如：
      > /home/eternalfuture/Documents/GitHub/Terraria-ToolBox/MyMod-Name

