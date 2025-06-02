# 📚 TEFMod API Basic Documentation

## 📦 Core Namespace
```cpp
namespace TEFMod {
    // All APIs are under this namespace
}
```

## 🏗️ Basic Type System

### 🔠 BaseType.hpp
#### Type Constraint Template
```cpp
template <typename T>
constexpr bool IsAllowedType() // ✅ Check if it's a valid type
```
**Allowed Types**:
- `bool`, `int8_t`, `uint8_t`, `int16_t`, `uint16_t`
- `int`, `uint`, `long`, `u_long`
- `float`, `double`, `char`
- `void*`, `void`

#### 🧵 String Interface
```cpp
class String {
    size_t length();    // 📏 Get length
    std::string str();  // 🔄 Convert to std::string
    // ...
};
```

#### 🧩 Generic Container
```cpp
template<typename T>
class Array { // 📦 Similar to std::vector interface
    T& at();          // 🎯 Element access
    std::vector<T> to_vector(); // 🔄 Convert to standard container
    // ...
};
```

#### 🏛️ Reflection System
```cpp
class Field { // 🏷️ Member variable reflection
    T Get();  // 📤 Get value
    void Set(); // 📥 Set value
};

class Method { // 📞 Member function reflection
    R Call();  // ☎️ Call method
};

class Class { // 🏗️ Class reflection
    TerrariaInstance CreateNewObjectParameters(); // 🏭 Create instance
};
```

## 🛠️ Debugging Tools
### 🔧 DebugTool.hpp
```cpp
class DebugTool {
    void printMemoryHexView();  // 🧠 Memory hex view
    void printSystemInfo();     // 💻 Print system info
    void printProfile();        // ⏱️ Function performance analysis
};
```

## 📝 Logging System
### 🪵 Logger.hpp
**Log Levels**:
- `Trace` 🕵️‍♂️ | `Debug` 🐛 | `Info` ℹ️ 
- `Warning` ⚠️ | `Error` ❌ | `Critical` 💥

**Shortcut Methods**:
```cpp
logger->t("Trace message");    // 🕵️‍♂️
logger->d("Debug message");    // 🐛
logger->i("Hello world");      // ℹ️
logger->w("Warning message");  // ⚠️
logger->e("Error occurred!");  // ❌
```

## 🌉 Core API
### 🏗️ TEFMod.hpp
```cpp
class TEFModAPI {
    template<typename T> 
    T GetAPI(ModApiDescriptor); // 🎣 Get API instance
    
    void registerApiDescriptor();    // 📝 Register API
    void registerFunctionDescriptor(); // 📌 Register function
};

// Usage Example

// Trampoline definition
void (*old_SetDefaults)(TEFMod::TerrariaInstance, int, bool, TEFMod::TerrariaInstance);
void SetDefaults_T(TEFMod::TerrariaInstance i, int t, bool n, TEFMod::TerrariaInstance v);

inline TEFMod::HookTemplate T_SetDefaults {
        (void*) SetDefaults_T,
        {  }
};

void SetDefaults_T(TEFMod::TerrariaInstance i, int t, bool n, TEFMod::TerrariaInstance v) {
    old_SetDefaults(i, t, n, v);     // Call original function
    for (auto fun: T_SetDefaults.FunctionArray) {
        if(fun) ((void(*)(void*, int, bool, TEFMod::TerrariaInstance))fun)(i, t, n, v); // Call registered functions
    }
}


// Register required fields
g_api->registerApiDescriptor({
            "Terraria",     // Namespace
            "Item",         // Class
            "shoot",       // Name
            "Field"         // Field type (also supports: Method, Class, old_fun for hooking)
});

// Register Hook
g_api->registerFunctionDescriptor({
            "Terraria",
            "Item",
            "SetDefaults",  
            "hook>>void",   // Hook type: hook - normal, vhook - virtual, ihook - interface
            3,                         // Parameter count
            &T_SetDefaults,             // Trampoline function
            { (void*)YourFunc }  // Functions to call
});

// Register functions to call
g_api->registerApiDescriptor({
            "Terraria",
            "Item",
            "buyPrice",
            "Method",
            4                   // Parameter count (required for old_fun too)
});

// Must register and process before use

// Get registered function
g_api->GetAPI<void*>({
            "Terraria",
            "Item",
            "buyPrice",
            "Method",
            4
})

// Get registered field
g_api->GetAPI<void*>({
            "Terraria",  
            "Item",        
            "shoot",       
            "Field"         
})

// Get original hooked function
old_SetDefaults = g_api->GetAPI<void(*)(TEFMod::TerrariaInstance, int, bool, TEFMod::TerrariaInstance)>({
            "Terraria",
             "Item",
             "SetDefaults",
             "old_fun",
            3
});

```

## 🚀 Initialization Example
```cpp
int Load(const std::string &path, MultiChannel *multiChannel) override {
    // 🎯 Get core components
    g_debug_tool = multiChannel->receive<DebugTool*>("TEFMod::DebugTool");
    g_log = multiChannel->receive<Logger*(*)()>("TEFMod::CreateLogger")();
    g_api = multiChannel->receive<TEFModAPI*>("TEFMod::TEFModAPI");
    
    // 📡 Register type support
    auto ParseIntField = multiChannel->receive<Field<int>*(*)()>("TEFMod::Field<Int>::ParseFromPointer");
    
    // ✨ Usage example
    g_log->i("Mod initialization complete!");
    g_debug_tool->printSystemInfo(g_log);
}
```

## 🔌 Loader-Provided APIs

## 📦 Core Service Instances
| Service Name               | Type                | Example Access                     |
|--------------------------|---------------------|----------------------------------|
| `TEFMod::DebugTool`       | `DebugTool*`        | `multiChannel->receive<DebugTool*>("TEFMod::DebugTool")` |
| `TEFMod::TEFModAPI`       | `TEFModAPI*`        | `multiChannel->receive<TEFModAPI*>("TEFMod::TEFModAPI")` |

## 🛠️ Factory Function Services
### 1. String Handling
```cpp
// Signature
TEFMod::String* CreateString(const std::string& str);

// Example
auto strFactory = multiChannel->receive<TEFMod::String*(*)(const std::string&)>("TEFMod::CreateString");
TEFMod::String* gameStr = strFactory("Hello World");
```

### 2. Logging System
```cpp
// Signature
TEFMod::Logger* CreateLogger(
    const std::string& Tag, 
    const std::string& filePath = "", 
    const std::size_t maxCache = 0
);

// Example
auto loggerFactory = multiChannel->receive<decltype(CreateLogger)>("TEFMod::CreateLogger");
g_log = loggerFactory("MyMod", "mod.log", 1024);
```

## 🔍 Reflection Parsing Services
### Generic Parser Template
```cpp
/* Base signature */
template<typename T>
T* ParseFromPointer(void* ptr);

/* Specialization example */
TEFMod::Field<int>* (*ParseIntField)(void*) = 
    multiChannel->receive<decltype(ParseIntField)>("TEFMod::Field<Int>::ParseFromPointer");
```

### Type Parser Reference
| Service Name                                      | Equivalent C++ Signature              | Usage                      |
|-----------------------------------------------|---------------------------------------------|-----------------------------|
| `TEFMod::Method<Int>::ParseFromPointer`       | `Method<int>*(*)(void*)`                   | int-returning methods       |
| `TEFMod::Field<Float>::ParseFromPointer`      | `Field<float>*(*)(void*)`                  | float fields               |
| `TEFMod::Class::ParseFromPointer`             | `Class*(*)(void*)`                         | Class definitions          |

## 📦 Array Operations
### 1. Array Creation
```cpp
// From raw pointer
Array<int>* (*CreateIntArrayFromPtr)(int*, size_t) = 
    multiChannel->receive<decltype(CreateIntArrayFromPtr)>("TEFMod::Array<Int>::CreateFromPointer");

// From vector
Array<float>* (*CreateFloatArrayFromVector)(std::vector<float>&) = 
    multiChannel->receive<decltype(CreateFloatArrayFromVector)>("TEFMod::Array<Float>::CreateFromVector");
```

### 2. Array Parsing
```cpp
Array<double>* (*ParseDoubleArray)(void*) = 
    multiChannel->receive<decltype(ParseDoubleArray)>("TEFMod::Array<Double>::ParseFromPointer");
```

## 🧩 Complete Usage Example
```cpp
// 1. Get field parser
auto fieldParser = multiChannel->receive<TEFMod::Field<int>*(*)(void*)>(
    "TEFMod::Field<Int>::ParseFromPointer");

// 2. Get raw pointer via API
void* rawFieldPtr = g_api->GetAPI<void*>({
    "Terraria", "Player", "statLife", "Field" 
});

// 3. Parse as strong-typed field
TEFMod::Field<int>* healthField = fieldParser(rawFieldPtr);

// 4. Use field
int currentHealth = healthField->Get(playerInstance);
healthField->Set(100, playerInstance);
```

## 📜 Supported Types List
Valid type parameters (replace `<T>` below):
- **Integers**: `Byte`(int8_t), `SByte`(uint8_t), `Short`(int16_t), `UShort`(uint16_t)
- **Long Integers**: `Int`(int32_t), `UInt`(uint32_t), `Long`(int64_t), `ULong`(uint64_t)
- **Floats**: `Float`(float), `Double`(double)
- **Others**: `Bool`(bool), `Char`(char), `Void`(void)

---

### 🚨 Important Notes
1. **Memory Safety**: Objects created via `CreateFromPointer` do NOT automatically free native memory
2. **Strict Type Matching**: `Field<Int>` cannot parse `float` fields
3. **Thread Constraints**: Recommend performing all reflection operations in main game thread