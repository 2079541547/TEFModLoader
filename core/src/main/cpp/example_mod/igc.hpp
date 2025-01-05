//
// Created by eternalfuture on 2024/9/22.
//
#include <iostream>
#include <random>
#include <climits>
#include <cmath>

std::mt19937 gen;

int addRandom(int value)
{
    std::normal_distribution<> dist(0, 100); // 均值为0，标准差为100
    return value + static_cast<int>(dist(gen));
}

int subtractRandom(int value)
{
    std::normal_distribution<> dist(0, 100); // 均值为0，标准差为100
    return value - static_cast<int>(dist(gen));
}

int multiplyRandom(int value)
{
    std::normal_distribution<> dist(1, 0.5); // 均值为1，标准差为0.5
    double multiplier = dist(gen);
    if (multiplier == 0)
        multiplier = 1; // 避免乘以0
    return static_cast<int>(value * multiplier);
}

int divideRandom(int value)
{
    std::normal_distribution<> dist(1, 0.5); // 均值为1，标准差为0.5
    double divisor = dist(gen);
    if (divisor == 0)
        divisor = 1; // 避免除以0
    return static_cast<int>(value / divisor);
}

int invertSignRandom(int value)
{
    std::bernoulli_distribution dist(0.5); // 50%概率翻转符号
    if (dist(gen))
    {
        return -value;
    }
    return value;
}

int multiplyPowerRandom(int value)
{
    std::uniform_real_distribution<> powerDist(-32, 32); // -32次方到32次方
    double power = powerDist(gen);
    return static_cast<int>(std::pow(value, power));
}

int dividePowerRandom(int value)
{
    std::uniform_real_distribution<> powerDist(-32, 32); // -32次方到32次方
    double power = powerDist(gen);
    return static_cast<int>(std::pow(value, 1.0 / power));
}

int rotateDigitsRandom(int value)
{
    std::uniform_int_distribution<> rotateDist(0, 31); // 旋转0到31位
    int shift = rotateDist(gen);
    return ((value << shift) | (value >> (32 - shift))) & 0xFFFFFFFF;
}

int swapDigitsRandom(int value)
{
    std::uniform_int_distribution<> swapDist(0, 31); // 选择0到31位
    int pos1 = swapDist(gen), pos2 = swapDist(gen);
    int mask1 = 1 << pos1, mask2 = 1 << pos2;
    value ^= mask1 ^ mask2 ^ (value & (mask1 | mask2));
    return value;
}

int addDigitRandom(int value)
{
    std::uniform_int_distribution<> addDist(0, 9); // 添加0到9的数字
    int digit = addDist(gen);
    return value * 10 + digit;
}

int removeDigitRandom(int value)
{
    if (value == 0)
        return 0;
    std::uniform_int_distribution<> removeDist(1, 10); // 10%的概率不做移除
    if (removeDist(gen) == 1)
    {
        return value / 10;
    }
    return value;
}

int reverseDigitsRandom(int value)
{
    int reversed = 0;
    while (value > 0)
    {
        reversed = (reversed * 10) + (value % 10);
        value /= 10;
    }
    return reversed;
}

int zeroOutRandom(int value)
{
    return 0;
}

int bitFlipRandom(int value)
{
    std::uniform_int_distribution<> bitPosDist(0, 31); // 选择0到31位
    int bitPos = bitPosDist(gen);
    return value ^ (1 << bitPos);
}

int bitFillRandom(int value)
{
    std::uniform_int_distribution<> bitPosDist(0, 31); // 选择0到31位
    std::uniform_int_distribution<> fillDist(0, 1);    // 选择0或1
    int bitPos = bitPosDist(gen), fill = fillDist(gen) ? 0xF : 0x0;
    return value | (fill << bitPos);
}

int bitClearRandom(int value)
{
    std::uniform_int_distribution<> bitPosDist(0, 31); // 选择0到31位
    int bitPos = bitPosDist(gen);
    return value & ~(1 << bitPos);
}

int bitSetRandom(int value)
{
    std::uniform_int_distribution<> bitPosDist(0, 31); // 选择0到31位
    int bitPos = bitPosDist(gen);
    return value | (1 << bitPos);
}

int bitMoveRandom(int value)
{
    std::uniform_int_distribution<> fromPosDist(0, 31); // 选择0到31位
    std::uniform_int_distribution<> toPosDist(0, 31);   // 选择0到31位
    int fromPos = fromPosDist(gen), toPos = toPosDist(gen);
    int bitToMove = (value >> fromPos) & 1;
    value &= ~(1 << fromPos);
    value |= (bitToMove << toPos);
    return value;
}

int permuteDigitsRandom(int value)
{
    std::uniform_int_distribution<> digitPosDist(0, 9); // 选择0到9位
    int pos1 = digitPosDist(gen), pos2 = digitPosDist(gen);
    std::string digits = std::to_string(value);
    if (pos1 != pos2)
    {
        std::swap(digits[pos1], digits[pos2]);
    }
    return std::stoi(digits);
}

int embedDigitRandom(int value)
{
    std::uniform_int_distribution<> digitPosDist(0, 10); // 选择0到10位
    std::uniform_int_distribution<> digitDist(0, 9);     // 选择0到9的数字
    int pos = digitPosDist(gen), digit = digitDist(gen);
    std::string digits = std::to_string(value);
    digits.insert(pos, std::to_string(digit));
    return std::stoi(digits);
}

int removeDigitRandom2(int value)
{
    if (value == 0)
        return 0;
    std::uniform_int_distribution<> digitPosDist(0, 9); // 选择0到9位
    int pos = digitPosDist(gen);
    std::string digits = std::to_string(value);
    if (!digits.empty())
    {
        digits.erase(pos, 1);
    }
    return std::stoi(digits);
}

int duplicateDigitRandom(int value)
{
    std::uniform_int_distribution<> digitPosDist(0, 9);      // 选择0到9位
    std::uniform_int_distribution<> duplicatePosDist(0, 10); // 选择0到10位
    int pos = digitPosDist(gen), duplicatePos = duplicatePosDist(gen);
    std::string digits = std::to_string(value);
    if (pos < digits.length())
    {
        digits.insert(duplicatePos, std::to_string(digits[pos]));
    }
    return std::stoi(digits);
}

int rotateDigitsRandom2(int value)
{
    std::uniform_int_distribution<> rotateDist(0, 1); // 0为左旋，1为右旋
    std::uniform_int_distribution<> shiftDist(0, 9);  // 选择0到9位
    std::string digits = std::to_string(value);
    int shift = shiftDist(gen);
    if (digits.length() > 1)
    {
        if (rotateDist(gen) == 0)
        { // 左旋
            digits = digits.substr(shift) + digits.substr(0, shift);
        }
        else
        { // 右旋
            digits = digits.substr(digits.length() - shift) + digits.substr(0, digits.length() - shift);
        }
    }
    return std::stoi(digits);
}

int replaceDigitRandom(int value)
{
    std::uniform_int_distribution<> digitPosDist(0, 9); // 选择0到9位
    std::uniform_int_distribution<> digitDist(0, 9);    // 选择0到9的数字
    int pos = digitPosDist(gen), newDigit = digitDist(gen);
    std::string digits = std::to_string(value);
    if (pos < digits.length())
    {
        digits[pos] = '0' + newDigit;
    }
    return std::stoi(digits);
}

int bitSwapRandom(int value)
{
    std::uniform_int_distribution<> bitPosDist(0, 31); // 选择0到31位
    int pos1 = bitPosDist(gen), pos2 = bitPosDist(gen);
    int mask1 = 1 << pos1, mask2 = 1 << pos2;
    value ^= mask1 ^ mask2 ^ ((value & (mask1 | mask2)) >> (pos2 - pos1));
    return value;
}

int bitRotateRandom(int value)
{
    std::uniform_int_distribution<> rotateDist(0, 1); // 0为左旋，1为右旋
    std::uniform_int_distribution<> shiftDist(0, 31); // 选择0到31位
    int shift = shiftDist(gen);
    if (shift > 0)
    {
        if (rotateDist(gen) == 0)
        { // 左旋
            value = ((value << shift) & 0xFFFFFFFF) | (value >> (32 - shift));
        }
        else
        { // 右旋
            value = ((value >> shift) & 0xFFFFFFFF) | (value << (32 - shift));
        }
    }
    return value;
}

int bitSlideRandom(int value)
{
    std::uniform_int_distribution<> slideDist(0, 1);  // 0为向左滑动，1为向右滑动
    std::uniform_int_distribution<> startDist(0, 31); // 选择0到31位
    std::uniform_int_distribution<> endDist(0, 31);   // 选择0到31位
    int start = startDist(gen), end = endDist(gen);
    if (start != end && start <= end)
    {
        if (slideDist(gen) == 0)
        { // 向左滑动
            value = (value & ~((1 << (end - start + 1)) - 1)) | ((value & ((1 << (end - start + 1)) - 1)) << (end - start));
        }
        else
        { // 向右滑动
            value = (value & ~((1 << (end - start + 1)) - 1)) | ((value & ((1 << (end - start + 1)) - 1)) >> (end - start));
        }
    }
    return value;
}

int appendSequenceRandom(int value)
{
    std::uniform_int_distribution<> lengthDist(1, 5); // 选择1到5的长度
    std::uniform_int_distribution<> digitDist(0, 9);  // 选择0到9的数字
    int length = lengthDist(gen);
    std::string sequence;
    for (int i = 0; i < length; ++i)
    {
        sequence += std::to_string(digitDist(gen));
    }
    std::string digits = std::to_string(value) + sequence;
    return std::stoi(digits);
}

int clearRandomBits(int number) {
    // 使用当前时间作为随机数生成器的种子。
    srand(static_cast<unsigned int>(time(nullptr)));

    int maxBitsToClear = 32; // 最多可以清除的位数
    int bitsCleared = 0;     // 已经清除的位数

    // 只要还没有达到最大清除位数且数字不为0，就继续循环
    while (bitsCleared < maxBitsToClear && number != 0) {
        // 生成一个介于 0 到 31 之间的随机位位置。
        int bitPosition = rand() % 32;

        // 创建一个掩码，除了 bitPosition 位外，其他位都设为 1。
        int mask = ~(1 << bitPosition);

        // 使用掩码与数字进行按位与运算，以清除选定的位。
        number &= mask;

        bitsCleared++; // 增加已清除的位数计数
    }

    return number; // 返回修改后的数字
}


int changeSign(int number) {
    return number * -1;
}

int specialEventMax(int value)
{
    return INT32_MAX;
}

int specialEventMin(int value)
{
    return INT32_MIN;
}

// 主要的随机数生成和事件处理函数
int generateAndProcess()
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

    // 尝试生成一个符合范围的随机数
    int attemptCount = 0;
    int randomValue = 0;

    do
    {
        // 生成一个随机整数
        std::uniform_int_distribution<> dis(lowerLimit, upperLimit);
        randomValue = dis(gen);

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


int limitValue(int value) {
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<> dis(0, 100); // 生成0到100之间的随机数
    std::uniform_int_distribution<> disLimit; // 用于生成不同范围内的随机数

    if (value > 200) {
        int randomNum = dis(gen);
        if (randomNum > 1) {
            disLimit = std::uniform_int_distribution<>(0, 160);
            value = disLimit(gen);
        }
    } else if (value > 10) {
        int randomNum = dis(gen);
        if (randomNum > 1) {
            disLimit = std::uniform_int_distribution<>(0, 10);
            value = disLimit(gen);
        }
    }

    return value;
}



int Limit_value(int value){

    if (value >= INT32_MAX)
    {
        return INT32_MAX ;
    }else if (value <= INT32_MIN)
    {
        return INT32_MIN;
    }else{
        return limitValue(value);
    }
    return value;
}






//带限制版本
int Limit_Damage(){
    int Damage;
    try {
        Damage = Limit_value(generateAndProcess());
    } catch (const std::exception& e) {
        Damage = 0;
    }
    return Damage;
}
