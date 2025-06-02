#### **Спецификации упаковки файлов системы EFMod**

##### 1. Стандарты форматов файлов
- **Mod-файлы**: Используйте расширение `.efmod` (напр., `my_mod.efmod`).
- **Файлы загрузчика**: Используйте расширение `.efml` (напр., `loader_v1.efml`).

##### 2. Требования к инструменту упаковки
Рекомендуется **SilkCasket**, поддерживающий:
- Настраиваемый размер блока (рекомендуется: 4096 байт).
- Опциональную защиту паролем (по умолчанию: `"EFMod"`).
- Кросс-платформенную совместимость.

##### 3. Примеры команд упаковки
```bash
# Упаковка Mod (с паролем):  
SilkCasket -c ./mod_files/ my_mod.efmod 4096 EFMod  

# Упаковка загрузчика (без пароля):  
SilkCasket -c ./loader_files/ loader_v1.efml 4096 ""  
```

##### 4. Требования к структуре каталогов
Оба типа файлов должны соответствовать структуре:
```
Корневая директория/  
├── lib/          # Библиотеки для платформ  
│   ├── windows/  # Подкаталоги по платформам  
│   └── android/  
├── Конфигурационный файл   # efmod.toml или efml.toml  
└── Файл иконки   # efmod.icon или efml.icon  
```

##### 5. Важные замечания
1. Защита паролем опциональна, но **рекомендуется** для рабочих сред.
2. Размер блока влияет на производительность; тестируйте (4096–8192 байт).
3. Проверяйте целостность структуры перед упаковкой.

Подробности см. в **официальной документации SilkCasket** .

---  

### **Key Considerations for Translation**
1. **Technical Accuracy**:
    - Terms like "block size" and "cross-platform" are standardized in both languages .
    - Russian chemical terminology (e.g., "соль" for "salt") aligns with UN guidelines for technical docs .

2. **Localization Best Practices**:
    - Avoid idioms; use direct phrasing (e.g., "verify directory integrity" → "проверьте целостность структуры") .
    - Maintain consistent glossary terms (e.g., "SilkCasket" remains untranslated as a tool name) .

3. **File Structure**:
    - For multilingual projects, add a `locales/` directory with subfolders (`en/`, `ru/`) for translated docs .

For further optimization, consider using translation memory tools (e.g., **Smartling**) to ensure consistency across updates .