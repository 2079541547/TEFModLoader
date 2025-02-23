import os
import sys
import subprocess
from argparse import ArgumentParser

def parse_arguments():
    parser = ArgumentParser(description="Build script for multi-platform projects.")
    parser.add_argument('-s', '--system', required=True, help='Target system: android, linux, or windows')
    parser.add_argument('-ndk', '--ndk_path', help='Path to the Android NDK (required for Android builds)')

    return parser.parse_args()

def run_command(command, cwd=None):
    """Run a shell command and print output."""
    result = subprocess.run(command, shell=True, cwd=cwd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    if result.returncode != 0:
        print(f"Error running command: {command}")
        print(result.stderr.decode())
        sys.exit(1)
    print(result.stdout.decode())

def build_android(ndk_path, root_dir):
    abis = ['arm64-v8a', 'armeabi-v7a']
    toolchain_file = f"{ndk_path}/build/cmake/android.toolchain.cmake"

    for abi in abis:
        build_dir = os.path.join(root_dir, f"build/android/{abi}")
        os.makedirs(build_dir, exist_ok=True)
        cmake_cmd = f"cmake -DCMAKE_SYSTEM_NAME=Android -DCMAKE_ANDROID_ARCH_ABI={abi} -DCMAKE_ANDROID_NDK={ndk_path} -DCMAKE_TOOLCHAIN_FILE={toolchain_file} {root_dir}"
        make_cmd = "make"
        run_command(cmake_cmd, cwd=build_dir)
        run_command(make_cmd, cwd=build_dir)

def build_linux(root_dir):
    build_dir = os.path.join(root_dir, "build/linux")
    os.makedirs(build_dir, exist_ok=True)
    cmake_cmd = f"cmake {root_dir}"
    make_cmd = "make"
    run_command(cmake_cmd, cwd=build_dir)
    run_command(make_cmd, cwd=build_dir)

def build_windows(root_dir):
    archs = ['x86', 'x64']
    compilers = {
        'x86': {'cc': 'i686-w64-mingw32-gcc', 'cxx': 'i686-w64-mingw32-g++'},
        'x64': {'cc': 'x86_64-w64-mingw32-gcc', 'cxx': 'x86_64-w64-mingw32-g++'}
    }

    for arch in archs:
        build_dir = os.path.join(root_dir, f"build/windows/{arch}")
        os.makedirs(build_dir, exist_ok=True)

        static_link_flags = "-DCMAKE_CXX_FLAGS='-static -static-libgcc -static-libstdc++'"

        cmake_cmd = f"cmake -DCMAKE_SYSTEM_NAME=Windows -DCMAKE_C_COMPILER={compilers[arch]['cc']} -DCMAKE_CXX_COMPILER={compilers[arch]['cxx']} {static_link_flags} {root_dir}"
        make_cmd = "make"
        run_command(cmake_cmd, cwd=build_dir)
        run_command(make_cmd, cwd=build_dir)

def main():
    args = parse_arguments()
    system = args.system.lower()
    root_dir = os.getcwd()

    if system == 'android':
        if not args.ndk_path:
            print("Error: --ndk is required for Android builds.")
            sys.exit(1)
        build_android(args.ndk_path, root_dir)
    elif system == 'linux':
        build_linux(root_dir)
    elif system == 'windows':
        build_windows(root_dir)
    else:
        print(f"Error: Unsupported system '{system}'. Use 'android', 'linux', or 'windows'.")
        sys.exit(1)

if __name__ == "__main__":
    main()