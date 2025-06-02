#### **EFMod System File Packaging Specifications**

##### 1. File Format Standards
- **Mod Files**: Use `.efmod` extension (e.g., `my_mod.efmod`).
- **Loader Files**: Use `.efml` extension (e.g., `loader_v1.efml`).

##### 2. Packaging Tool Requirements
The **SilkCasket** tool is recommended for packaging, supporting:
- Custom block size (suggested: 4096 bytes).
- Optional password protection (default: `"EFMod"`).
- Cross-platform compatibility.

##### 3. Packaging Command Examples
```bash
# Mod packaging (with password):  
SilkCasket -c ./mod_files/ my_mod.efmod 4096 EFMod  

# Loader packaging (no password):  
SilkCasket -c ./loader_files/ loader_v1.efml 4096 ""  
```

##### 4. Directory Structure Requirements
Both file types must follow this structure:
```
Root Directory/  
├── lib/          # Platform-specific libraries  
│   ├── windows/  # Subdirectories per platform  
│   └── android/  
├── Config File   # efmod.toml or efml.toml  
└── Icon File     # efmod.icon or efml.icon  
```

##### 5. Key Notes
1. Password protection is optional but **strongly recommended** for production environments.
2. Block size impacts performance; test to optimize (4096–8192 bytes).
3. Verify directory integrity before packaging.

For detailed parameters, refer to the **SilkCasket official documentation** .