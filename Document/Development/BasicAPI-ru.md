# 📚 Базовая документация API TEFMod

## 📦 Основное пространство имен
```cpp
namespace TEFMod {
    // Все API находятся в этом пространстве имен
}
```

## 🏗️ Базовая система типов

### 🔠 BaseType.hpp
#### Шаблон ограничения типов
```cpp
template <typename T>
constexpr bool IsAllowedType() // ✅ Проверка допустимости типа
```
**Разрешенные типы**:
- `bool`, `int8_t`, `uint8_t`, `int16_t`, `uint16_t`
- `int`, `uint`, `long`, `u_long`
- `float`, `double`, `char`
- `void*`, `void`

#### 🧵 Строковый интерфейс
```cpp
class String {
    size_t length();    // 📏 Получить длину
    std::string str();  // 🔄 Преобразовать в std::string
    // ...
};
```

#### 🧩 Универсальный контейнер
```cpp
template<typename T>
class Array { // 📦 Интерфейс, аналогичный std::vector
    T& at();          // 🎯 Доступ к элементам
    std::vector<T> to_vector(); // 🔄 Преобразование в стандартный контейнер
    // ...
};
```

#### 🏛️ Система рефлексии
```cpp
class Field { // 🏷️ Рефлексия полей класса
    T Get();  // 📤 Получить значение
    void Set(); // 📥 Установить значение
};

class Method { // 📞 Рефлексия методов класса
    R Call();  // ☎️ Вызов метода
};

class Class { // 🏗️ Рефлексия классов
    TerrariaInstance CreateNewObjectParameters(); // 🏭 Создание экземпляра
};
```

## 🛠️ Инструменты отладки
### 🔧 DebugTool.hpp
```cpp
class DebugTool {
    void printMemoryHexView();  // 🧠 Просмотр памяти в HEX
    void printSystemInfo();     // 💻 Вывод системной информации
    void printProfile();        // ⏱️ Анализ производительности функций
};
```

## 📝 Система логирования
### 🪵 Logger.hpp
**Уровни логирования**:
- `Trace` 🕵️‍♂️ | `Debug` 🐛 | `Info` ℹ️ 
- `Warning` ⚠️ | `Error` ❌ | `Critical` 💥

**Методы-сокращения**:
```cpp
logger->t("Trace сообщение");    // 🕵️‍♂️
logger->d("Debug сообщение");    // 🐛
logger->i("Hello world");      // ℹ️
logger->w("Предупреждение");  // ⚠️
logger->e("Ошибка!");  // ❌
```

## 🌉 Основное API
### 🏗️ TEFMod.hpp
```cpp
class TEFModAPI {
    template<typename T> 
    T GetAPI(ModApiDescriptor); // 🎣 Получение экземпляра API
    
    void registerApiDescriptor();    // 📝 Регистрация API
    void registerFunctionDescriptor(); // 📌 Регистрация функции
};

//Пример использования

// Определение трамплина
void (*old_SetDefaults)(TEFMod::TerrariaInstance, int, bool, TEFMod::TerrariaInstance);
void SetDefaults_T(TEFMod::TerrariaInstance i, int t, bool n, TEFMod::TerrariaInstance v);

inline TEFMod::HookTemplate T_SetDefaults {
        (void*) SetDefaults_T,
        {  }
};

void SetDefaults_T(TEFMod::TerrariaInstance i, int t, bool n, TEFMod::TerrariaInstance v) {
    old_SetDefaults(i, t, n, v);     // Вызов оригинальной функции
    for (auto fun: T_SetDefaults.FunctionArray) {
        if(fun) ((void(*)(void*, int, bool, TEFMod::TerrariaInstance))fun)(i, t, n, v); // Вызов зарегистрированных функций
    }
}


// Регистрация необходимых полей
g_api->registerApiDescriptor({
            "Terraria",     // Пространство имен
            "Item",         // Класс
            "shoot",       // Имя
            "Field"         // Тип поля (также поддерживается: Method, Class, old_fun для хуков)
});

// Регистрация хука
g_api->registerFunctionDescriptor({
            "Terraria",
            "Item",
            "SetDefaults",  
            "hook>>void",   // Тип хука: hook - обычный, vhook - виртуальный, ihook - интерфейсный
            3,                         // Количество параметров
            &T_SetDefaults,             // Трамплин-функция
            { (void*)YourFunc }  // Функции для вызова
});

// Регистрация вызываемых функций
g_api->registerApiDescriptor({
            "Terraria",
            "Item",
            "buyPrice",
            "Method",
            4                   // Количество параметров (требуется и для old_fun)
});

// Необходимо зарегистрировать и обработать перед использованием

// Получение зарегистрированной функции
g_api->GetAPI<void*>({
            "Terraria",
            "Item",
            "buyPrice",
            "Method",
            4
})

// Получение зарегистрированного поля
g_api->GetAPI<void*>({
            "Terraria",  
            "Item",        
            "shoot",       
            "Field"         
})

// Получение оригинальной функции после хука
old_SetDefaults = g_api->GetAPI<void(*)(TEFMod::TerrariaInstance, int, bool, TEFMod::TerrariaInstance)>({
            "Terraria",
             "Item",
             "SetDefaults",
             "old_fun",
            3
});

```

## 🚀 Пример инициализации
```cpp
int Load(const std::string &path, MultiChannel *multiChannel) override {
    // 🎯 Получение основных компонентов
    g_debug_tool = multiChannel->receive<DebugTool*>("TEFMod::DebugTool");
    g_log = multiChannel->receive<Logger*(*)()>("TEFMod::CreateLogger")();
    g_api = multiChannel->receive<TEFModAPI*>("TEFMod::TEFModAPI");
    
    // 📡 Регистрация поддержки типов
    auto ParseIntField = multiChannel->receive<Field<int>*(*)()>("TEFMod::Field<Int>::ParseFromPointer");
    
    // ✨ Пример использования
    g_log->i("Мод инициализирован!");
    g_debug_tool->printSystemInfo(g_log);
}
```

## 🔌 API, предоставляемые загрузчиком

## 📦 Основные сервисы
| Название сервиса          | Тип                | Пример получения                     |
|--------------------------|---------------------|----------------------------------|
| `TEFMod::DebugTool`       | `DebugTool*`        | `multiChannel->receive<DebugTool*>("TEFMod::DebugTool")` |
| `TEFMod::TEFModAPI`       | `TEFModAPI*`        | `multiChannel->receive<TEFModAPI*>("TEFMod::TEFModAPI")` |

## 🛠️ Фабричные функции
### 1. Работа со строками
```cpp
// Сигнатура
TEFMod::String* CreateString(const std::string& str);

// Пример
auto strFactory = multiChannel->receive<TEFMod::String*(*)(const std::string&)>("TEFMod::CreateString");
TEFMod::String* gameStr = strFactory("Hello World");
```

### 2. Система логирования
```cpp
// Сигнатура
TEFMod::Logger* CreateLogger(
    const std::string& Tag, 
    const std::string& filePath = "", 
    const std::size_t maxCache = 0
);

// Пример
auto loggerFactory = multiChannel->receive<decltype(CreateLogger)>("TEFMod::CreateLogger");
g_log = loggerFactory("MyMod", "mod.log", 1024);
```

## 🔍 Сервисы парсинга рефлексии
### Общий шаблон
```cpp
/* Базовая сигнатура */
template<typename T>
T* ParseFromPointer(void* ptr);

/* Пример специализации */
TEFMod::Field<int>* (*ParseIntField)(void*) = 
    multiChannel->receive<decltype(ParseIntField)>("TEFMod::Field<Int>::ParseFromPointer");
```

### Таблица парсеров
| Название сервиса                               | Эквивалент C++              | Применение                      |
|-----------------------------------------------|---------------------------------------------|-----------------------------|
| `TEFMod::Method<Int>::ParseFromPointer`       | `Method<int>*(*)(void*)`                   | Методы, возвращающие int       |
| `TEFMod::Field<Float>::ParseFromPointer`      | `Field<float>*(*)(void*)`                  | Поля типа float               |
| `TEFMod::Class::ParseFromPointer`             | `Class*(*)(void*)`                         | Определения классов          |

## 📦 Операции с массивами
### 1. Создание массивов
```cpp
// Из указателя
Array<int>* (*CreateIntArrayFromPtr)(int*, size_t) = 
    multiChannel->receive<decltype(CreateIntArrayFromPtr)>("TEFMod::Array<Int>::CreateFromPointer");

// Из вектора
Array<float>* (*CreateFloatArrayFromVector)(std::vector<float>&) = 
    multiChannel->receive<decltype(CreateFloatArrayFromVector)>("TEFMod::Array<Float>::CreateFromVector");
```

### 2. Парсинг массивов
```cpp
Array<double>* (*ParseDoubleArray)(void*) = 
    multiChannel->receive<decltype(ParseDoubleArray)>("TEFMod::Array<Double>::ParseFromPointer");
```

## 🧩 Полный пример использования
```cpp
// 1. Получение парсера полей
auto fieldParser = multiChannel->receive<TEFMod::Field<int>*(*)(void*)>(
    "TEFMod::Field<Int>::ParseFromPointer");

// 2. Получение указателя через API
void* rawFieldPtr = g_api->GetAPI<void*>({
    "Terraria", "Player", "statLife", "Field" 
});

// 3. Парсинг в строго типизированное поле
TEFMod::Field<int>* healthField = fieldParser(rawFieldPtr);

// 4. Использование поля
int currentHealth = healthField->Get(playerInstance);
healthField->Set(100, playerInstance);
```

## 📜 Поддерживаемые типы
Допустимые параметры типов (замените `<T>` ниже):
- **Целые**: `Byte`(int8_t), `SByte`(uint8_t), `Short`(int16_t), `UShort`(uint16_t)
- **Длинные целые**: `Int`(int32_t), `UInt`(uint32_t), `Long`(int64_t), `ULong`(uint64_t)
- **Дробные**: `Float`(float), `Double`(double)
- **Прочие**: `Bool`(bool), `Char`(char), `Void`(void)

---

### 🚨 Важные замечания
1. **Безопасность памяти**: Объекты, созданные через `CreateFromPointer`, НЕ освобождают нативную память автоматически
2. **Строгая типизация**: `Field<Int>` нельзя использовать для полей типа `float`
3. **Ограничения потоков**: Рекомендуется выполнять все операции рефлексии в основном игровом потоке