import os
import zipfile
from pathlib import Path

def create_mod_zip(mod_dir):
    # 确保传入的路径是一个目录
    if not os.path.isdir(mod_dir):
        print("提供的路径不是一个目录，请重新输入模组目录的路径。")
        return

    # 获取模组目录的名称
    mod_name = os.path.basename(mod_dir)

    # 定义输出的zip文件名，后缀为.efmod
    zip_filename = f"{mod_name}.efmod"

    # 获取模组目录的父目录路径
    parent_dir = os.path.dirname(mod_dir)

    # 定义最终的zip文件路径
    final_zip_path = os.path.join(parent_dir, zip_filename)

    # 创建zip文件
    with zipfile.ZipFile(final_zip_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
        # 遍历目录中的所有文件和子目录
        for root, dirs, files in os.walk(mod_dir):
            for file in files:
                # 创建文件的完整路径
                file_path = os.path.join(root, file)
                # 添加文件到zip文件中，arcname为相对于mod_dir的路径
                zipf.write(file_path, arcname=os.path.relpath(file_path, mod_dir))

    print(f"模组打包完成，文件已保存到：{final_zip_path}")

if __name__ == "__main__":
    # 获取用户输入的模组目录路径
    mod_dir = input("请输入模组目录的完整路径：")
    create_mod_zip(mod_dir)