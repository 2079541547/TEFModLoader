#include <iostream>
#include <type_traits>
#include <utility>
#include <cstdint>

// 通用函数调用器
template<typename R, typename... Args>
R callFunction(void *funcPtr, Args&&... args) {
    // 从 uintptr_t 转换为函数指针
    using FuncPtr = R (*)(Args...);
    auto f = reinterpret_cast<FuncPtr>(funcPtr);
    
    // 调用函数
    return f(std::forward<Args>(args)...);
}

// 定义一个示例函数
void exampleFunction(int a, double b, const char *c) {
    std::cout << "Called with: " << a << ", " << b << ", " << c << std::endl;
}

// 定义一个示例函数，返回值类型为 int
int exampleFunctionWithReturn(int a, double b, const char *c) {
    std::cout << "Called with: " << a << ", " << b << ", " << c << std::endl;
    return a + static_cast<int>(b);
}

int main() {
    // 将函数指针转换为 uintptr_t
    uintptr_t funcPtr = reinterpret_cast<uintptr_t>(&exampleFunction);
    uintptr_t funcPtrWithReturn = reinterpret_cast<uintptr_t>(&exampleFunctionWithReturn);

    // 调用无返回值的函数
    callFunction<void>(reinterpret_cast<void*>(funcPtr), 10, 20.5, "Hello World");

    // 调用有返回值的函数
    int result = callFunction<int>(reinterpret_cast<void*>(funcPtrWithReturn), 10, 20.5, "Hello World");
    std::cout << "Result: " << result << std::endl;

    return 0;
}
