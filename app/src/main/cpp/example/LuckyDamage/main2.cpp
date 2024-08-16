#include <iostream>
#include <random>
#include <climits>
#include <cmath>


int generateAndProcess1(int value)
{
    // 创建随机数生成器
    std::random_device rd;  // 用来获取随机数种子
    std::mt19937 gen(rd()); // 使用Mersenne Twister算法生成随机数

    // 生成随机上限和下限
    std::uniform_int_distribution<> limitsDis(INT32_MIN, INT32_MAX);
    int lowerLimit = limitsDis(gen);
    int upperLimit = limitsDis(gen);

    // 确保下限小于上限
    if (lowerLimit > upperLimit)
    {
        std::swap(lowerLimit, upperLimit);
    }
    int randomValue = 0;

    do
    {
        randomValue = value;

        // 事件选择器
        std::uniform_int_distribution<> eventSelector(0, 30); // 29个事件加上0本身

        // 特殊事件选择器
        std::uniform_int_distribution<> specialEventSelector(0, 999); // 1000个可能的值

        // 选择一个随机事件
        int selectedEvent = eventSelector(gen);

        // 检查是否触发特殊事件
        if (specialEventSelector(gen) == 0)
        {
            // 选择 INT32_MIN 或 INT32_MAX
            std::bernoulli_distribution specialDist(0.5); // 50%概率
            if (specialDist(gen))
            {
                return specialEventMin(randomValue); // INT32_MIN
            }
            else
            {
                return specialEventMax(randomValue); // INT32_MAX
            }
        }

        // 根据所选事件应用操作
        switch (selectedEvent)
        {
        case 0:
            // 随机添加一个正态分布的值
            randomValue = addRandom(randomValue);
            break;
        case 1:
            // 随机减去一个正态分布的值
            randomValue = subtractRandom(randomValue);
            break;
        case 2:
            // 随机乘以一个正态分布的值
            randomValue = multiplyRandom(randomValue);
            break;
        case 3:
            // 随机除以一个正态分布的值
            randomValue = divideRandom(randomValue);
            break;
        case 4:
            // 随机翻转符号
            randomValue = invertSignRandom(randomValue);
            break;
        case 5:
            // 返回0作为一个单独的事件
            randomValue = 0;
            break;
        case 6: // 乘以随机次数方
            // 随机乘以一个指数
            randomValue = multiplyPowerRandom(randomValue);
            break;
        case 7: // 除以随机次数方
            // 随机除以一个指数
            randomValue = dividePowerRandom(randomValue);
            break;
        case 8: // 随机旋转数字
            // 随机旋转数字位
            randomValue = rotateDigitsRandom(randomValue);
            break;
        case 9: // 随机交换数字
            // 随机交换两个数字位
            randomValue = swapDigitsRandom(randomValue);
            break;
        case 10: // 随机加位数
            // 随机在末尾添加一个数字
            randomValue = addDigitRandom(randomValue);
            break;
        case 11: // 随机减位数
            // 随机删除一个数字
            randomValue = removeDigitRandom(randomValue);
            break;
        case 12: // 随机反转
            // 随机反转数字位序
            randomValue = reverseDigitsRandom(randomValue);
            break;
        case 13: // 随机清零
            // 将值置零
            randomValue = zeroOutRandom(randomValue);
            break;
        case 14: // 随机位翻转
            // 随机翻转一个位
            randomValue = bitFlipRandom(randomValue);
            break;
        case 15: // 随机位填充
            // 随机填充一个位
            randomValue = bitFillRandom(randomValue);
            break;
        case 16: // 随机位清除
            // 随机清除一个位
            randomValue = bitClearRandom(randomValue);
            break;
        case 17: // 随机位设置
            // 随机设置一个位
            randomValue = bitSetRandom(randomValue);
            break;
        case 18: // 随机位移动
            // 随机移动一个位
            randomValue = bitMoveRandom(randomValue);
            break;
        case 19: // 随机置换数字
            // 随机置换两个数字位
            randomValue = permuteDigitsRandom(randomValue);
            break;
        case 20: // 随机嵌入数字
            // 随机在一个位置插入一个数字
            randomValue = embedDigitRandom(randomValue);
            break;
        case 21: // 随机减位数2
            // 随机删除一个数字（不同实现）
            randomValue = removeDigitRandom2(randomValue);
            break;
        case 22: // 随机复制数字
            // 随机复制一个数字位
            randomValue = duplicateDigitRandom(randomValue);
            break;
        case 23: // 随机旋转数字2
            // 随机旋转数字位（不同实现）
            randomValue = rotateDigitsRandom2(randomValue);
            break;
        case 24: // 随机替换数字
            // 随机替换一个数字位
            randomValue = replaceDigitRandom(randomValue);
            break;
        case 25: // 随机位交换
            // 随机交换两个位
            randomValue = bitSwapRandom(randomValue);
            break;
        case 26: // 随机位旋转
            // 随机旋转位
            randomValue = bitRotateRandom(randomValue);
            break;
        case 27: // 随机位滑动
            // 随机滑动位
            randomValue = bitSlideRandom(randomValue);
            break;
        case 28: // 随机追加序列
            // 随机在末尾追加一个数字序列
            randomValue = appendSequenceRandom(randomValue);
            break;

        case 29:
            randomValue = clearRandomBits(randomValue);
            break;

        case 30:
            randomValue = changeSign(randomValue);
            break;

        default:
            // 如果没有其他情况，保持原始随机数
            break;
        }

        // 检查是否超过范围
        if (randomValue < lowerLimit || randomValue > upperLimit)
        {
            if (rand() % 100 == 0)
            { // rand() % 100 会生成 0 到 99 之间的随机数
                return (lowerLimit < 0) ? INT32_MIN : INT32_MAX;
            }
        }
        else
        {
            return randomValue; // 随机值在范围内，直接返回
        }


    } while (randomValue < lowerLimit || randomValue > upperLimit);

    return randomValue;
}
