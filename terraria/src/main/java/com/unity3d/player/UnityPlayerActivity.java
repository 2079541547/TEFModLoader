package com.unity3d.player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;

import androidx.annotation.NonNull;


/**
 * UnityPlayerActivity类是Unity播放器在Android上的活动容器。
 * 它负责管理Unity播放器的生命周期事件，并处理与Unity的交互。
 */
public class UnityPlayerActivity extends Activity implements IUnityPlayerLifecycleEvents, View.OnTouchListener, View.OnGenericMotionListener {
    boolean[] PressedStates = new boolean[330]; // 用于跟踪按键状态
    protected UnityPlayer mUnityPlayer; // Unity播放器实例

    private static final String MAPPING = "abcdefghijklmnopqrstuvwxyz1234567890"; // 键盘映射字符串
    private static final int[][] SPECIAL_CASES = {{111, 27}, {4, 27}, {68, 96}, {7, 48}, {62, 32}, {69, 45},
            {70, 61}, {67, 8}, {112, 127}, {59, 304}, {60, 303}, {66, 13}, {22, 275}, {21, 276}, {19, 273},
            {20, 274}, {55, 44}, {56, 46}, {76, 47}, {57, 308}, {58, 307}, {114, 305}, {113, 306}, {115, 301},
            {61, 9}, {72, 93}, {71, 91}, {75, 39}, {74, 59}}; // 特殊按键映射

    /**
     * 根据索引获取Unity中对应的按键码。
     * @param i 索引值
     * @return 对应的按键码，如果没有映射则返回0。
     */
    public int GetUnityKeyCode(int i) {
        if (SPECIAL_CASES.length > i) {
            return SPECIAL_CASES[i][1];
        }
        return MAPPING.indexOf(String.valueOf(i)) + 1;
    }

    @Override
    public void onUnityPlayerQuitted() {  }

    /**
     * 更新Unity命令行参数。
     * @param str 原始命令行参数
     * @return 更新后的命令行参数
     */
    protected String updateUnityCommandLineArguments(String str) {
        return str;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle bundle) {
        requestWindowFeature(1);
        super.onCreate(bundle);
        // 更新Unity的命令行参数并初始化Unity播放器
        getIntent().putExtra("unity", updateUnityCommandLineArguments(getIntent().getStringExtra("unity")));
        UnityPlayer unityPlayer = new UnityPlayer(this, this);
        this.mUnityPlayer = unityPlayer;
        setContentView(unityPlayer);
        this.mUnityPlayer.requestFocus();
        // 如果设备支持触摸屏，设置指针图标
        if (getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.touchscreen")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                this.mUnityPlayer.setPointerIcon(PointerIcon.getSystemIcon(getBaseContext(), 0));
            }
        }
        // 设置触摸和运动事件监听器
        this.mUnityPlayer.setOnTouchListener(this);
        this.mUnityPlayer.setOnGenericMotionListener(this);
    }

    @Override
    public void onUnityPlayerUnloaded() {
        moveTaskToBack(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        this.mUnityPlayer.newIntent(intent);
    }

    @Override
    protected void onDestroy() {
        this.mUnityPlayer.destroy();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 在多窗口模式下暂停Unity播放器
        if (MultiWindowSupport.getAllowResizableWindow(this)) {
            this.mUnityPlayer.pause();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 在多窗口模式下恢复Unity播放器
        if (MultiWindowSupport.getAllowResizableWindow(this)) {
            this.mUnityPlayer.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 保存多窗口模式状态，并在非多窗口模式下暂停Unity播放器
        MultiWindowSupport.saveMultiWindowMode(this);
        if (MultiWindowSupport.getAllowResizableWindow(this)) {
            return;
        }
        this.mUnityPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在非多窗口模式或多窗口模式改变为真时恢复Unity播放器
        if (!MultiWindowSupport.getAllowResizableWindow(this) || MultiWindowSupport.isMultiWindowModeChangedToTrue(this)) {
            this.mUnityPlayer.resume();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        this.mUnityPlayer.lowMemory();
    }

    @Override
    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        // 当系统进行内存优化时通知Unity播放器
        if (i == 15) {
            this.mUnityPlayer.lowMemory();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mUnityPlayer.configurationChanged(configuration);
    }

    @Override
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        this.mUnityPlayer.windowFocusChanged(z);
    }

    /**
     * 分发键盘事件给Unity播放器。
     * @param keyEvent 键盘事件
     * @return 如果事件被消费则返回true，否则返回false。
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 2) {
            return this.mUnityPlayer.injectEvent(keyEvent);
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    /**
     * 处理按键释放事件。
     * @param i 按键码
     * @param keyEvent 键盘事件
     * @return 如果事件被消费则返回true，否则返回false。
     */
    @Override
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        int GetUnityKeyCode = GetUnityKeyCode(i);
        if (GetUnityKeyCode != 0) {
            this.PressedStates[GetUnityKeyCode] = false;
        }
        return this.mUnityPlayer.injectEvent(keyEvent);
    }

    /**
     * 处理按键按下事件。
     * @param i 按键码
     * @param keyEvent 键盘事件
     * @return 如果事件被消费则返回true，否则返回false。
     */
    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        int GetUnityKeyCode = GetUnityKeyCode(i);
        if (GetUnityKeyCode != 0) {
            this.PressedStates[GetUnityKeyCode] = true;
        }
        return this.mUnityPlayer.injectEvent(keyEvent);
    }

    /**
     * 处理触摸事件并分发给Unity播放器。
     * @param motionEvent 触摸事件
     * @return 如果事件被消费则返回true，否则返回false。
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return onTouch(this.mUnityPlayer, motionEvent);
    }

    /**
     * 触摸事件的处理函数。
     * @param view 触摸的视图
     * @param motionEvent 触摸事件
     * @return 如果事件被消费则返回true，否则返回false。
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        InputDevice device;
        // 仅当设备支持触控且为手指触控时处理事件
        if ((device = motionEvent.getDevice()) == null || !device.supportsSource(1041) || motionEvent.getToolType(0) != MotionEvent.TOOL_TYPE_FINGER) {
            return this.mUnityPlayer.injectEvent(motionEvent);
        }
        return true;
    }

    /**
     * 分发通用运动事件给Unity播放器。
     * @param motionEvent 通用运动事件
     * @return 如果事件被消费则返回true，否则返回false。
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return onGenericMotion(this.mUnityPlayer, motionEvent);
    }

    /**
     * 通用运动事件的处理函数。
     * @param view 触发运动事件的视图
     * @param motionEvent 通用运动事件
     * @return 如果事件被消费则返回true，否则返回false。
     */
    @Override
    public boolean onGenericMotion(View view, MotionEvent motionEvent) {
        InputDevice device;
        // 仅当设备支持触控且为手指触控时处理事件
        if ((device = motionEvent.getDevice()) == null || !device.supportsSource(1041) || motionEvent.getToolType(0) != MotionEvent.TOOL_TYPE_FINGER) {
            return this.mUnityPlayer.injectEvent(motionEvent);
        }
        return true;
    }
}