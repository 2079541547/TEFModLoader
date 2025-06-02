# 📚 TEFMod API 基础文档

## 📦 核心命名空间
```cpp
namespace TEFMod {
    // 所有API均在此命名空间下
}
```

## 🏗️ 基础类型系统

### 🔠 BaseType.hpp
#### 类型约束模板
```cpp
template <typename T>
constexpr bool IsAllowedType() // ✅ 检查是否为合法类型
```
**允许的类型**：
- `bool`, `int8_t`, `uint8_t`, `int16_t`, `uint16_t`
- `int`, `uint`, `long`, `u_long`
- `float`, `double`, `char`
- `void*`, `void`

#### 🧵 字符串接口
```cpp
class String {
    size_t length();    // 📏 获取长度
    std::string str();  // 🔄 转std::string
    // ...
};
```

#### 🧩 泛型容器
```cpp
template<typename T>
class Array { // 📦 类似std::vector的接口
    T& at();          // 🎯 元素访问
    std::vector<T> to_vector(); // 🔄 转换标准容器
    // ...
};
```

#### 🏛️ 反射系统
```cpp
class Field { // 🏷️ 成员变量反射
    T Get();  // 📤 获取值
    void Set(); // 📥 设置值
};

class Method { // 📞 成员函数反射
    R Call();  // ☎️ 调用方法
};

class Class { // 🏗️ 类反射
    TerrariaInstance CreateNewObjectParameters(); // 🏭 创建实例
};
```

## 🛠️ 调试工具
### 🔧 DebugTool.hpp
```cpp
class DebugTool {
    void printMemoryHexView();  // 🧠 内存十六进制查看
    void printSystemInfo();     // 💻 系统信息打印
    void printProfile();        // ⏱️ 函数性能分析
};
```

## 📝 日志系统
### 🪵 Logger.hpp
**日志等级**：
- `Trace` 🕵️‍♂️ | `Debug` 🐛 | `Info` ℹ️ 
- `Warning` ⚠️ | `Error` ❌ | `Critical` 💥

**快捷方法**：
```cpp
logger->t("Trace message");    // 🕵️‍♂️
logger->d("Debug message");    // 🐛
logger->i("Hello world");      // ℹ️
logger->w("Warning message");  // ⚠️
logger->e("Error occurred!");  // ❌
```

## 🌉 核心API
### 🏗️ TEFMod.hpp
```cpp
class TEFModAPI {
    template<typename T> 
    T GetAPI(ModApiDescriptor); // 🎣 获取API实例
    
    void registerApiDescriptor();    // 📝 注册API
    void registerFunctionDescriptor(); // 📌 注册函数
};

//使用实例

// 跳板定义
void (*old_SetDefaults)(TEFMod::TerrariaInstance, int, bool, TEFMod::TerrariaInstance);
void SetDefaults_T(TEFMod::TerrariaInstance i, int t, bool n, TEFMod::TerrariaInstance v);

inline TEFMod::HookTemplate T_SetDefaults {
        (void*) SetDefaults_T,
        {  }
};

void SetDefaults_T(TEFMod::TerrariaInstance i, int t, bool n, TEFMod::TerrariaInstance v) {
    old_SetDefaults(i, t, n, v);     // 调用原始函数
    for (auto fun: T_SetDefaults.FunctionArray) {
        if(fun) ((void(*)(void*, int, bool, TEFMod::TerrariaInstance))fun)(i, t, n, v); // 调用注册的函数
    }
}


// 注册需要使用的字段
g_api->registerApiDescriptor({
            "Terraria",     // 命名空间
            "Item",         // 类
            "shoot",       // 名称
            "Field"         // 字段类型（还支持函数: Method, 类: Class, hook原始函数: old_fun）
});

// 注册Hook
g_api->registerFunctionDescriptor({
            "Terraria",
            "Item",
            "SetDefaults",  
            "hook>>void",   // Hook类型 hook: 普通hook, vhook: 虚函数hook, ihook: 虚拟hook
            3,                         // 参数数量
            &T_SetDefaults,             // 跳板函数
            { (void*)YourFunc }  // 调用的函数
});

// 注册需要调用的函数
g_api->registerApiDescriptor({
            "Terraria",
            "Item",
            "buyPrice",
            "Method",
            4                   // 参数数量，old_fun类型也需要
});

// 必须注册且处理后才能使用

// 接收获取的函数
g_api->GetAPI<void*>({
            "Terraria",
            "Item",
            "buyPrice",
            "Method",
            4
})

// 接收获取到的字段
g_api->GetAPI<void*>({
            "Terraria",  
            "Item",        
            "shoot",       
            "Field"         
})

// 接收hook后的原始函数
old_SetDefaults = g_api->GetAPI<void(*)(TEFMod::TerrariaInstance, int, bool, TEFMod::TerrariaInstance)>({
            "Terraria",
             "Item",
             "SetDefaults",
             "old_fun",
            3
});

```


## 🚀 初始化示例
```cpp
int Load(const std::string &path, MultiChannel *multiChannel) override {
    // 🎯 获取核心组件
    g_debug_tool = multiChannel->receive<DebugTool*>("TEFMod::DebugTool");
    g_log = multiChannel->receive<Logger*(*)()>("TEFMod::CreateLogger")();
    g_api = multiChannel->receive<TEFModAPI*>("TEFMod::TEFModAPI");
    
    // 📡 注册类型支持
    auto ParseIntField = multiChannel->receive<Field<int>*(*)()>("TEFMod::Field<Int>::ParseFromPointer");
    
    // ✨ 使用示例
    g_log->i("Mod初始化完成！");
    g_debug_tool->printSystemInfo(g_log);
}
```

## 🔌 加载器提供的API

## 📦 核心服务实例
| 服务名称                  | 类型                | 获取方式示例                     |
|--------------------------|---------------------|----------------------------------|
| `TEFMod::DebugTool`       | `DebugTool*`        | `multiChannel->receive<DebugTool*>("TEFMod::DebugTool")` |
| `TEFMod::TEFModAPI`       | `TEFModAPI*`        | `multiChannel->receive<TEFModAPI*>("TEFMod::TEFModAPI")` |

## 🛠️ 工厂函数服务
### 1. 字符串处理
```cpp
// 签名
TEFMod::String* CreateString(const std::string& str);

// 使用示例
auto strFactory = multiChannel->receive<TEFMod::String*(*)(const std::string&)>("TEFMod::CreateString");
TEFMod::String* gameStr = strFactory("Hello World");
```

### 2. 日志系统
```cpp
// 签名
TEFMod::Logger* CreateLogger(
    const std::string& Tag, 
    const std::string& filePath = "", 
    const std::size_t maxCache = 0
);

// 使用示例
auto loggerFactory = multiChannel->receive<decltype(CreateLogger)>("TEFMod::CreateLogger");
g_log = loggerFactory("MyMod", "mod.log", 1024);
```

## 🔍 反射解析服务
### 通用解析模板
```cpp
/* 基础签名 */
template<typename T>
T* ParseFromPointer(void* ptr);

/* 特化示例（实际使用时需替换<Type>为具体类型） */
TEFMod::Field<int>* (*ParseIntField)(void*) = 
    multiChannel->receive<decltype(ParseIntField)>("TEFMod::Field<Int>::ParseFromPointer");
```

### 类型解析器对照表
| 服务名称                                      | 等效C++签名                                  | 适用场景                      |
|-----------------------------------------------|---------------------------------------------|-----------------------------|
| `TEFMod::Method<Int>::ParseFromPointer`       | `Method<int>*(*)(void*)`                   | 解析返回int的成员方法        |
| `TEFMod::Field<Float>::ParseFromPointer`      | `Field<float>*(*)(void*)`                  | 解析float类型字段           |
| `TEFMod::Class::ParseFromPointer`             | `Class*(*)(void*)`                         | 解析类定义                  |

## 📦 数组操作服务
### 1. 数组创建
```cpp
// 从原生指针创建
Array<int>* (*CreateIntArrayFromPtr)(int*, size_t) = 
    multiChannel->receive<decltype(CreateIntArrayFromPtr)>("TEFMod::Array<Int>::CreateFromPointer");

// 从vector创建
Array<float>* (*CreateFloatArrayFromVector)(std::vector<float>&) = 
    multiChannel->receive<decltype(CreateFloatArrayFromVector)>("TEFMod::Array<Float>::CreateFromVector");
```

### 2. 数组解析
```cpp
Array<double>* (*ParseDoubleArray)(void*) = 
    multiChannel->receive<decltype(ParseDoubleArray)>("TEFMod::Array<Double>::ParseFromPointer");
```

## 🧩 完整使用流程示例
```cpp
// 1. 获取字段解析器
auto fieldParser = multiChannel->receive<TEFMod::Field<int>*(*)(void*)>(
    "TEFMod::Field<Int>::ParseFromPointer");

// 2. 通过API获取原始指针
void* rawFieldPtr = g_api->GetAPI<void*>({
    "Terraria", "Player", "statLife", "Field" 
});

// 3. 解析为强类型字段
TEFMod::Field<int>* healthField = fieldParser(rawFieldPtr);

// 4. 使用字段
int currentHealth = healthField->Get(playerInstance);
healthField->Set(100, playerInstance);
```

## 📜 类型支持清单
合法类型参数（需替换下方`<T>`）：
- **整型**：`Byte`(int8_t), `SByte`(uint8_t), `Short`(int16_t), `UShort`(uint16_t)
- **长整型**：`Int`(int32_t), `UInt`(uint32_t), `Long`(int64_t), `ULong`(uint64_t)
- **浮点型**：`Float`(float), `Double`(double)
- **其他**：`Bool`(bool), `Char`(char), `Void`(void)

---

### 🚨 重要注意事项
1. **内存安全**：所有通过`CreateFromPointer`创建的对象**不**自动释放原生内存
2. **类型严格匹配**：`Field<Int>`不能用于解析`float`类型字段
3. **线程约束**：建议在主游戏线程执行所有反射操作