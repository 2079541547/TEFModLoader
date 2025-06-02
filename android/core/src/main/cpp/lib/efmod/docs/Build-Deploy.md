#### **EFMod System File Packaging Specifications**

##### 1. 文件格式规范
- **Mod文件**：使用`.efmod`后缀（如`my_mod.efmod`）
- **加载器文件**：使用`.efml`后缀（如`loader_v1.efml`）

##### 2. 打包工具要求
推荐使用SilkCasket工具进行打包，该工具支持：
- 自定义块大小（建议4096字节）
- 可选密码保护（默认密码"EFMod"）
- 跨平台兼容性

##### 3. 打包命令示例
```bash
# Mod打包（带密码）
SilkCasket -c ./mod_files/ my_mod.efmod 4096 EFMod

# 加载器打包（无密码） 
SilkCasket -c ./loader_files/ loader_v1.efml 4096 ""
```

##### 4. 目录结构要求
两种文件类型需保持相同结构：
```
项目根目录/
├── lib/          # 平台相关库
│   ├── windows/  # 各平台子目录
│   └── android/
├── 配置文件      # efmod.toml或efml.toml
└── 图标文件      # efmod.icon或efml.icon
```

##### 5. 注意事项
1. 密码保护为可选功能，但生产环境建议启用
2. 块大小影响性能，建议测试后确定最优值（4096-8192）
3. 打包前需验证目录结构完整性

如需更详细的参数说明，可参考SilkCasket工具的官方文档。