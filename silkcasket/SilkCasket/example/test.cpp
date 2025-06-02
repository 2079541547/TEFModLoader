/*******************************************************************************
 * 文件名称: test
 * 项目名称: SilkCasket
 * 创建时间: 2025/01/22
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
 * 许可证: Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 *
 * 描述信息: 本文件为Silk Casket项目中的一部分，允许在遵守Apache License 2.0的条件下自由用于商业用途。
 * 注意事项: 请严格遵守Apache License 2.0协议使用本代码。Apache License 2.0允许商业用途，无需额外授权。
 *******************************************************************************/

#include <SilkCasket/build.hpp>
#include <SilkCasket/analysis_men.hpp>
#include <SilkCasket/utils.hpp>

int main() {

    SilkCasket::builder B;
    B.build("/home/yuwu/下载/AES-main",
            "a.skc",
            {true, true, true, true, true},
            8096 * 1024,
            true,
            "Hello");

    SilkCasket::analysis_men A(SilkCasket::Utils::readFile("a.skc"), "Hello");
    A.printALL();
    A.releaseEntry("/home/yuwu/下载/新建文件夹");



    return 0;
};