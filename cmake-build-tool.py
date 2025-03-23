import os
import sys
import subprocess
import shutil
from argparse import ArgumentParser
from concurrent.futures import ThreadPoolExecutor, as_completed
import multiprocessing
from tqdm import tqdm
import platform

# 架构和编译器的全局配置
ARCH_CONFIG = {
    'android': {
        'arm64': 'arm64-v8a',
        'arm': 'armeabi-v7a',
        'x64': 'x86_64',
        'x86': 'x86'
    },
    'linux': {
        'x86': 'x86',
        'x64': 'x64',
        'arm64': 'arm64'
    },
    'windows': {
        'x86': 'x86',
        'x64': 'x64'
    }
}

COMPILER_CONFIG = {
    'linux': {
        'x86': {'gcc': 'gcc', 'clang': 'clang'},
        'x64': {'gcc': 'gcc', 'clang': 'clang'},
        'arm64': {'gcc': 'aarch64-linux-gnu-gcc', 'clang': 'clang'}
    },
    'windows': {
        'x86': {'gcc': 'i686-w64-mingw32-gcc', 'clang': 'clang'},
        'x64': {'gcc': 'x86_64-w64-mingw32-gcc', 'clang': 'clang'}
    }
}

# Clang 交叉编译目标配置
CLANG_TARGET = {
    'x86': 'i686-linux-gnu',
    'x64': 'x86_64-linux-gnu',
    'arm64': 'aarch64-linux-gnu'
}

# 依赖列表
DEPENDENCIES = {
    'cmake': 'cmake',
    'clang': 'clang',
    'strip': 'binutils',
    'make': 'make',
    'gcc': 'gcc',
    'mingw-w64': 'mingw-w64'
}

def parse_arguments():
    parser = ArgumentParser(description="Build script for multi-platform projects.")
    parser.add_argument('-s', '--system', required=True, help='Target system: android, linux, or windows')
    parser.add_argument('-p', '--projects', help='Comma-separated list of project paths to build')
    parser.add_argument('-b', '--build_dir', help='Custom build output directory (default: project_path/build)')
    parser.add_argument('--scan_dir', help='Directory to scan for projects (looks for CMakeLists.txt)')
    parser.add_argument('--exclude', help='Comma-separated list of projects to exclude (e.g., project1,project2)')
    parser.add_argument('-ndk', '--ndk_path', help='Path to the Android NDK (required for Android builds)')
    parser.add_argument('-a', '--arch', help='Target architectures (comma-separated, e.g, x86,x64,arm64)')
    parser.add_argument('-c', '--c_std', help='C language standard (e.g., c11, c99)')
    parser.add_argument('-cpp', '--cpp_std', default='17', help='C++ language standard (e.g., c++17, c++20)')
    parser.add_argument('--compiler', default='gcc', choices=['gcc', 'clang'], help='Compiler to use: gcc or clang')
    parser.add_argument('--strip', action='store_true', help='Strip symbols from the final binary')
    parser.add_argument('--clean_intermediate', action='store_true', help='Remove intermediate files after build')
    parser.add_argument('-j', '--jobs', type=int, default=1, help='Number of parallel jobs')
    parser.add_argument('--verbose', action='store_true', help='Enable verbose logging')
    return parser.parse_args()

def find_projects(scan_dir, exclude):
    """扫描项目并过滤排除项"""
    projects = []
    exclude_set = set(exclude.split(',')) if exclude else set()
    for root, dirs, files in os.walk(scan_dir):
        if 'CMakeLists.txt' in files:
            project_name = os.path.basename(root)
            if project_name not in exclude_set and root not in exclude_set:
                projects.append(root)
    return projects

def clean_build_dir(build_dir, keep_files=[]):
    """清除构建目录"""
    if os.path.exists(build_dir):
        for root, dirs, files in os.walk(build_dir, topdown=False):
            for name in files:
                file_path = os.path.join(root, name)
                if file_path not in keep_files:
                    os.remove(file_path)
            for name in dirs:
                os.rmdir(os.path.join(root, name))
        os.rmdir(build_dir)

def run_command(command, cwd=None, log_file=None, verbose=False):
    """运行命令并实时捕获输出"""
    if verbose:
        tqdm.write(f"Running command: {command}")
    with open(log_file, 'a') as log:
        process = subprocess.Popen(
            command,
            shell=True,
            cwd=cwd,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            bufsize=1,  # 行缓冲
            universal_newlines=True
        )
        while True:
            output = process.stdout.readline()
            if output == '' and process.poll() is not None:
                break
            if output:
                log.write(output)
                if verbose:
                    tqdm.write(output.strip())
        stderr = process.stderr.read()
        if stderr:
            log.write(stderr)
            if verbose:
                tqdm.write(stderr.strip())
        if process.returncode != 0:
            tqdm.write(f"Error running command: {command}")
            sys.exit(1)

def check_dependency(dependency):
    """检查依赖是否已安装"""
    try:
        subprocess.run([dependency, '--version'], stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=True)
        return True
    except FileNotFoundError:
        return False

def install_dependency(dependency):
    """安装依赖"""
    system = platform.system().lower()
    if system == 'linux':
        if os.path.exists('/etc/debian_version'):
            # Debian/Ubuntu
            subprocess.run(['sudo', 'apt-get', 'install', '-y', DEPENDENCIES[dependency]], check=True)
        elif os.path.exists('/etc/redhat-release'):
            # RedHat/CentOS
            subprocess.run(['sudo', 'yum', 'install', '-y', DEPENDENCIES[dependency]], check=True)
        else:
            tqdm.write(f"Unsupported Linux distribution for automatic dependency installation.")
            sys.exit(1)
    elif system == 'windows':
        tqdm.write(f"Please install {dependency} manually on Windows.")
        sys.exit(1)
    elif system == 'darwin':
        # macOS
        subprocess.run(['brew', 'install', DEPENDENCIES[dependency]], check=True)
    else:
        tqdm.write(f"Unsupported operating system for automatic dependency installation.")
        sys.exit(1)

def ensure_dependencies():
    """确保所有依赖已安装"""
    for dependency in DEPENDENCIES:
        if not check_dependency(dependency):
            tqdm.write(f"{dependency} is not installed. Installing...")
            install_dependency(dependency)

def build_project(project_path, build_dir_base, system, ndk_path, arch, strip, cpp_std, clean_intermediate, compiler, c_std, verbose):
    """编译单个项目"""
    project_name = os.path.basename(project_path)

    # 为每个架构创建独立的构建目录
    arch_config = ARCH_CONFIG[system].get(arch, arch)
    build_dir = os.path.join(build_dir_base, project_name, system, arch_config)
    temp_build_dir = os.path.join(build_dir, "temp")
    log_file = os.path.join(build_dir, f"build_{arch}.log")

    # 清除临时构建目录以确保从头开始编译
    clean_build_dir(temp_build_dir)
    os.makedirs(temp_build_dir, exist_ok=True)

    # 确保 CMake 源路径正确指向项目根目录
    cmake_source_path = project_path

    if system == 'android':
        build_android(ndk_path, cmake_source_path, temp_build_dir, arch, strip, cpp_std, clean_intermediate, log_file, verbose)
    elif system == 'linux':
        build_linux(cmake_source_path, temp_build_dir, arch, compiler, c_std, cpp_std, strip, clean_intermediate, log_file, verbose)
    elif system == 'windows':
        build_windows(cmake_source_path, temp_build_dir, arch, compiler, c_std, cpp_std, clean_intermediate, log_file, verbose)
    else:
        tqdm.write(f"Error: Unsupported system '{system}'.")
        sys.exit(1)

    # 合并构建结果
    merge_build_results(temp_build_dir, build_dir, clean_intermediate)
    return build_dir

def build_android(ndk_path, project_path, build_dir, arch, strip, cpp_std, clean_intermediate, log_file, verbose):
    abi = ARCH_CONFIG['android'][arch]
    toolchain_file = f"{ndk_path}/build/cmake/android.toolchain.cmake"

    cmake_cmd = [
        "cmake",
        "-DCMAKE_SYSTEM_NAME=Android",
        f"-DANDROID_ABI={abi}",
        f"-DANDROID_NDK={ndk_path}",
        f"-DCMAKE_TOOLCHAIN_FILE={toolchain_file}",
        f"-DCMAKE_CXX_STANDARD={cpp_std}",
        project_path
    ]

    make_cmd = ["make"]

    run_command(" ".join(cmake_cmd), cwd=build_dir, log_file=log_file, verbose=verbose)
    run_command(" ".join(make_cmd), cwd=build_dir, log_file=log_file, verbose=verbose)

    if strip:
        so_files = [os.path.join(build_dir, f) for f in os.listdir(build_dir) if f.endswith('.so')]
        for so_file in so_files:
            strip_cmd = f"{ndk_path}/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-strip --strip-unneeded {so_file}"
            run_command(strip_cmd, log_file=log_file, verbose=verbose)

    tqdm.write(f"Android build for {arch} completed (C++ standard: {cpp_std}).")

def build_linux(project_path, build_dir, arch, compiler, c_std, cpp_std, strip, clean_intermediate, log_file, verbose):
    # 使用 clang 进行交叉编译
    if compiler == 'clang':
        target = CLANG_TARGET[arch]
        cmake_cmd = [
            "cmake",
            f"-DCMAKE_C_COMPILER=clang",
            f"-DCMAKE_CXX_COMPILER=clang++",
            f"-DCMAKE_C_COMPILER_TARGET={target}",
            f"-DCMAKE_CXX_COMPILER_TARGET={target}",
            f"-DCMAKE_C_STANDARD={c_std}" if c_std else "",
            f"-DCMAKE_CXX_STANDARD={cpp_std}",
            project_path
        ]
    else:
        cmake_cmd = [
            "cmake",
            f"-DCMAKE_C_COMPILER={COMPILER_CONFIG['linux'][arch][compiler]}",
            f"-DCMAKE_CXX_COMPILER={COMPILER_CONFIG['linux'][arch][compiler]}",
            f"-DCMAKE_C_STANDARD={c_std}" if c_std else "",
            f"-DCMAKE_CXX_STANDARD={cpp_std}",
            project_path
        ]

    make_cmd = ["make"]

    run_command(" ".join(cmake_cmd), cwd=build_dir, log_file=log_file, verbose=verbose)
    run_command(" ".join(make_cmd), cwd=build_dir, log_file=log_file, verbose=verbose)

    if strip:
        # 查找所有可执行文件和共享库
        binaries = []
        for root, _, files in os.walk(build_dir):
            for file in files:
                if file.endswith(('.so', '')) or os.access(os.path.join(root, file), os.X_OK):
                    binaries.append(os.path.join(root, file))
        # 剥离符号
        for binary in binaries:
            strip_cmd = f"strip --strip-unneeded {binary}"
            run_command(strip_cmd, log_file=log_file, verbose=verbose)

    tqdm.write(f"Linux build for {arch} completed (C: {c_std}, C++: {cpp_std}).")

def build_windows(project_path, build_dir, arch, compiler, c_std, cpp_std, clean_intermediate, log_file, verbose):
    static_link_flags = "-DCMAKE_CXX_FLAGS='-static -static-libgcc -static-libstdc++'"

    cmake_cmd = [
        "cmake",
        "-DCMAKE_SYSTEM_NAME=Windows",
        f"-DCMAKE_C_COMPILER={COMPILER_CONFIG['windows'][arch][compiler]}",
        f"-DCMAKE_CXX_COMPILER={COMPILER_CONFIG['windows'][arch][compiler]}",
        f"-DCMAKE_C_STANDARD={c_std}" if c_std else "",
        f"-DCMAKE_CXX_STANDARD={cpp_std}",
        static_link_flags,
        project_path
    ]

    make_cmd = ["make"]

    run_command(" ".join(cmake_cmd), cwd=build_dir, log_file=log_file, verbose=verbose)
    run_command(" ".join(make_cmd), cwd=build_dir, log_file=log_file, verbose=verbose)

    tqdm.write(f"Windows build for {arch} completed (C: {c_std}, C++: {cpp_std}).")

def delete_all_except(directory, extensions):
    """删除目录中不包含指定后缀的文件和文件夹"""
    for item in os.listdir(directory):
        item_path = os.path.join(directory, item)
        if os.path.isfile(item_path):
            if not any(item.endswith(ext) for ext in extensions):
                os.remove(item_path)
        elif os.path.isdir(item_path):
            delete_all_except(item_path, extensions)
            if not os.listdir(item_path):  # 删除空文件夹
                os.rmdir(item_path)

def merge_build_results(temp_dir, final_dir, clean=False):
    """
    移动编译产物并保留目录结构，清理目标目录
    :param temp_dir: 临时目录，包含编译产物
    :param final_dir: 最终目录，编译产物将移动到这里
    :param clean: 是否清理不必要文件
    """
    try:
        # 如果 final_dir 存在，清理它（排除 temp_dir）
        if os.path.exists(final_dir):
            for root, dirs, files in os.walk(final_dir, topdown=True):
                # 如果当前目录是 temp_dir，跳过它
                if os.path.abspath(root) == os.path.abspath(temp_dir):
                    dirs[:] = []  # 清空子目录列表，不再递归
                    continue
                # 删除文件
                for file in files:
                    file_path = os.path.join(root, file)
                    os.remove(file_path)
                # 删除空目录
                for dir_name in dirs:
                    dir_path = os.path.join(root, dir_name)
                    if os.path.abspath(dir_path) != os.path.abspath(temp_dir):
                        shutil.rmtree(dir_path)

        # 如果需要清理临时目录中的文件
        if clean:
            # 保留的文件扩展名
            keep_extensions = [".so", ".a", ".dll", ".lib", ".exe"]
            for root, _, files in os.walk(temp_dir):
                for file in files:
                    file_path = os.path.join(root, file)
                    if not any(file.endswith(ext) for ext in keep_extensions):
                        os.remove(file_path)

        # 遍历临时目录中的所有文件，移动到最终目录
        for root, _, files in os.walk(temp_dir):
            for file in files:
                src = os.path.join(root, file)
                rel_path = os.path.relpath(src, temp_dir)
                dest = os.path.join(final_dir, rel_path)
                os.makedirs(os.path.dirname(dest), exist_ok=True)
                shutil.move(src, dest)

        # 如果需要清理最终目录中的不必要文件
        if clean:
            # 保留的文件扩展名
            keep_extensions = [".so", ".a", ".dll", ".lib", ".exe"]
            for root, _, files in os.walk(final_dir):
                for file in files:
                    file_path = os.path.join(root, file)
                    if not any(file.endswith(ext) for ext in keep_extensions):
                        os.remove(file_path)

        shutil.rmtree(temp_dir)

    except Exception as e:
        print(f"Error during merge_build_results: {e}")

def main():
    args = parse_arguments()
    system = args.system.lower()

    # 确保所有依赖已安装
    ensure_dependencies()

    # 解析项目路径
    if args.scan_dir:
        projects = find_projects(args.scan_dir, args.exclude)
    elif args.projects:
        projects = [p.strip() for p in args.projects.split(',') if p.strip() not in (args.exclude or '').split(',')]
    else:
        projects = [os.getcwd()]

    # 解析目标架构
    archs = args.arch.split(',') if args.arch else ARCH_CONFIG[system].keys()

    # 使用线程池并行编译
    with ThreadPoolExecutor(max_workers=args.jobs) as executor:
        futures = {}
        for project_path in projects:
            project_path = os.path.abspath(project_path)
            for arch in archs:
                # 为每个线程分配独立的构建目录
                build_dir_base = args.build_dir if args.build_dir else os.path.join(project_path, "build")
                future = executor.submit(
                    build_project, project_path, build_dir_base, system, args.ndk_path, arch, args.strip,
                    args.cpp_std, args.clean_intermediate, args.compiler, args.c_std, args.verbose
                )
                futures[future] = (project_path, arch)

        # 显示进度条并等待所有线程完成
        with tqdm(total=len(futures), desc="Overall Progress", unit="project", position=0, mininterval=1) as pbar:
            for future in as_completed(futures):
                project_path, arch = futures[future]
                try:
                    build_dir = future.result()
                    pbar.set_description(f"Project {os.path.basename(project_path)} ({arch}): Completed")
                except Exception as e:
                    pbar.set_description(f"Project {os.path.basename(project_path)} ({arch}): Failed")
                    tqdm.write(f"Build failed for {os.path.basename(project_path)} ({arch}): {str(e)}")
                pbar.update(1)

if __name__ == "__main__":
    main()
