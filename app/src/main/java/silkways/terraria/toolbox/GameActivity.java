package silkways.terraria.toolbox;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.unity3d.player.IUnityPlayerLifecycleEvents;
import com.unity3d.player.MultiWindowSupport;
import com.unity3d.player.UnityPlayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import silkways.terraria.toolbox.databinding.FullScreenLayoutBinding;
import silkways.terraria.toolbox.logic.ApplicationSettings;
import silkways.terraria.toolbox.ui.gametool.GameStatus;
import silkways.terraria.toolbox.ui.gametool.OnlineVideo;
import silkways.terraria.toolbox.ui.gametool.RunningLogs;
import silkways.terraria.toolbox.ui.gametool.Wiki;


/**
 * UnityPlayerActivity类是Unity播放器在Android上的活动容器。
 * 它负责管理Unity播放器的生命周期事件，并处理与Unity的交互。
 */
public class GameActivity extends Activity implements IUnityPlayerLifecycleEvents, View.OnTouchListener, View.OnGenericMotionListener {

    boolean[] PressedStates = new boolean[330]; // 用于跟踪按键状态
    protected UnityPlayer mUnityPlayer; //

    private int screenWidth; // 屏幕宽度
    private int screenHeight; // 屏幕高度
    private boolean isMenu = false;
    private FragmentManager fragmentManager;



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



    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
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
            this.mUnityPlayer.setPointerIcon(PointerIcon.getSystemIcon(getBaseContext(), 0));
        }
        // 设置触摸和运动事件监听器
        this.mUnityPlayer.setOnTouchListener(this);
        this.mUnityPlayer.setOnGenericMotionListener(this);


        System.loadLibrary("Major"); //加载模组主库
        getJsonContent(readFileContent(this));



        ViewGroup rootView = new FrameLayout(this);
        setContentView(rootView);

        // 初始化UnityPlayer
        mUnityPlayer = new UnityPlayer(this, this);
        FrameLayout.LayoutParams unityParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        rootView.addView(mUnityPlayer, unityParams);


        // 初始化悬浮按钮
        // 悬浮按钮视图
        View floatingButton = LayoutInflater.from(this).inflate(R.layout.draggable_view, rootView, false);

        // 直接在代码中设置悬浮按钮的尺寸（50dp x 50dp）
        int sizeInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        FrameLayout.LayoutParams floatParams = new FrameLayout.LayoutParams(sizeInPx, sizeInPx);

        // 设置悬浮按钮的初始位置（这里以右上角为例）
        floatParams.gravity = Gravity.TOP | Gravity.START;
        rootView.addView(floatingButton, floatParams);

        floatingButton.setOnTouchListener((v, event) -> {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v.getLayoutParams();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 记录按下时的原始坐标
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 计算新的位置，并确保按钮不会超出屏幕边界
                    int newX = (int) (event.getRawX());
                    int newY = (int) (event.getRawY());

                    // 检查X坐标是否小于0或大于屏幕宽度减去按钮宽度
                    newX = Math.max(0, Math.min(newX, screenWidth - layoutParams.width));
                    // 检查Y坐标是否小于0或大于屏幕高度减去按钮高度
                    newY = Math.max(0, Math.min(newY, screenHeight - layoutParams.height));

                    // 更新悬浮按钮的位置
                    layoutParams.leftMargin = newX;
                    layoutParams.topMargin = newY;
                    v.setLayoutParams(layoutParams);
                    break;
            }
            return false; // 返回false不拦截触摸事件
        });

        // 获取屏幕的宽度和高度
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        floatingButton.setOnClickListener(v -> {
            if (!isMenu){
                isMenu = true;
                addNewLayoutToRootView(rootView);
            }
        });
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


    @SuppressLint("ResourceAsColor")
    public void addNewLayoutToRootView(ViewGroup rootView) {
        LayoutInflater inflater = LayoutInflater.from(this);
        final FullScreenLayoutBinding[] binding = {FullScreenLayoutBinding.inflate(inflater, rootView, false)};

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;

        rootView.addView(binding[0].getRoot(), layoutParams);
        ApplicationSettings.setupLanguage(this);

        fragmentManager = getFragmentManager();

        loadFragment(new Wiki());
        binding[0].TrWiki.setTextColor(R.color.md_theme_primaryContainer_highContrast);

        binding[0].TrWiki.setOnClickListener(v -> {
            binding[0].TrWiki.setTextColor(R.color.md_theme_primaryContainer_highContrast);
            binding[0].OnlineVideo.setTextColor(R.color.md_theme_onSurface_highContrast);
            binding[0].GameStatus.setTextColor(R.color.md_theme_onSurface_highContrast);
            binding[0].RunningLogs.setTextColor(R.color.md_theme_onSurface_highContrast);


            switchToCustomFragment(new Wiki(), Wiki.class);
        });

        binding[0].OnlineVideo.setOnClickListener(v -> {
            binding[0].TrWiki.setTextColor(R.color.md_theme_onSurface_highContrast);
            binding[0].OnlineVideo.setTextColor(R.color.md_theme_primaryContainer_highContrast);
            binding[0].GameStatus.setTextColor(R.color.md_theme_onSurface_highContrast);
            binding[0].RunningLogs.setTextColor(R.color.md_theme_onSurface_highContrast);


            switchToCustomFragment(new OnlineVideo(), OnlineVideo.class);
        });

        binding[0].GameStatus.setOnClickListener(v -> {
            binding[0].TrWiki.setTextColor(R.color.md_theme_onSurface_highContrast);
            binding[0].OnlineVideo.setTextColor(R.color.md_theme_onSurface_highContrast);
            binding[0].GameStatus.setTextColor(R.color.md_theme_primaryContainer_highContrast);
            binding[0].RunningLogs.setTextColor(R.color.md_theme_onSurface_highContrast);


            switchToCustomFragment(new GameStatus(), GameStatus.class);
        });

        binding[0].RunningLogs.setOnClickListener(v -> {
            binding[0].TrWiki.setTextColor(R.color.md_theme_onSurface_highContrast);
            binding[0].OnlineVideo.setTextColor(R.color.md_theme_onSurface_highContrast);
            binding[0].GameStatus.setTextColor(R.color.md_theme_onSurface_highContrast);
            binding[0].RunningLogs.setTextColor(R.color.md_theme_primaryContainer_highContrast);


            switchToCustomFragment(new RunningLogs(), RunningLogs.class);
        });

        // 设置点击事件
        binding[0].closeButton.setOnClickListener(v -> {
            rootView.removeView(binding[0].getRoot());
            binding[0] = null; // 如果有垃圾回收的需求，这里可以置null

            // 获取FragmentManager
            FragmentManager fragmentManager = getFragmentManager();

            // 移除回退栈中的所有Fragment
            int backStackEntryCount = fragmentManager.getBackStackEntryCount();
            for (int i = 0; i < backStackEntryCount; i++) {
                fragmentManager.popBackStackImmediate();
            }

            // 移除当前显示的Fragment
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment);
            if (currentFragment != null) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.remove(currentFragment);
                transaction.commitAllowingStateLoss();
            }

            isMenu = false;
        });

        binding[0].getRoot();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }

    private void switchToCustomFragment(Fragment fragment, Class<?> fragmentClassToKeep) {
        // 移除所有其他Fragment
        removeAllOtherFragments(fragmentClassToKeep);
        loadFragment(fragment);
    }

    private void removeAllOtherFragments(Class<?> fragmentClassToKeep) {
        // 移除回退栈中的所有Fragment
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < backStackEntryCount; i++) {
            fragmentManager.popBackStackImmediate();
        }

        // 移除当前显示的Fragment，但保留指定的Fragment类型
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment);
        if (currentFragment != null && !currentFragment.getClass().equals(fragmentClassToKeep)) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(currentFragment);
            transaction.commitAllowingStateLoss();
        }
    }


    public static String readFileContent(Context context) {
        File file = new File(Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/ToolBoxData/ModData/mod_data.json");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            Log.e("TAG", "Error reading file: ", e);
            return "";
        }
    }

    public native void getJsonContent(String content);
}