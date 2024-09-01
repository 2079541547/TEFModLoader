package silkways.terraria.toolbox.ui.gametool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import silkways.terraria.toolbox.R;
import silkways.terraria.toolbox.databinding.FullScreenLayoutBinding;
import silkways.terraria.toolbox.logic.ApplicationSettings;
import silkways.terraria.toolbox.logic.JsonConfigModifier;


public class LoadTool {

    private int screenWidth; // 屏幕宽度
    private int screenHeight; // 屏幕高度
    private boolean isMenu = false;
    private FragmentManager fragmentManager;

    @SuppressLint("ClickableViewAccessibility")
    public void LoadMain(ViewGroup rootView, Context context){

        if ((boolean) JsonConfigModifier.readJsonValue(context, "ToolBoxData/game_settings.json", "suspended_window")){
            // 初始化悬浮按钮
            View floatingButton = LayoutInflater.from(context).inflate(R.layout.draggable_view, rootView, false);

            // 直接在代码中设置悬浮按钮的尺寸（50dp x 50dp）
            int sizeInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
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
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;

            floatingButton.setOnClickListener(v -> {
                if (!isMenu){
                    isMenu = true;
                    addNewLayoutToRootView(rootView, context);
                }
            });
        }
    }

    @SuppressLint("ResourceAsColor")
    public void addNewLayoutToRootView(ViewGroup rootView, Context context) {
        Activity activity = (Activity) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        final FullScreenLayoutBinding[] binding = {FullScreenLayoutBinding.inflate(inflater, rootView, false)};

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;

        rootView.addView(binding[0].getRoot(), layoutParams);

        if (ApplicationSettings.isDarkThemeEnabled(context)){

            binding[0].TrWiki.setTextColor(0xFFDEE3E6);
            binding[0].GameStatus.setTextColor(0xFFDEE3E6);
            binding[0].OnlineVideo.setTextColor(0xFFDEE3E6);
            binding[0].RunningLogs.setTextColor(0xFFDEE3E6);

            binding[0].cardView.setCardBackgroundColor(0xFF0F1416);
            binding[0].cardView2.setCardBackgroundColor(0xFF0F1416);

            binding[0].closeButton.setTextColor(0xFF0F1416);
            binding[0].closeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#88D0ED")));

        }

        fragmentManager = activity.getFragmentManager();

        loadFragment(new Wiki());

        binding[0].TrWiki.setOnClickListener(v -> {
            switchToCustomFragment(new Wiki(), Wiki.class);
        });

        binding[0].OnlineVideo.setOnClickListener(v -> {
            switchToCustomFragment(new OnlineVideo(), OnlineVideo.class);
        });

        binding[0].GameStatus.setOnClickListener(v -> {
            switchToCustomFragment(new GameStatus(), GameStatus.class);
        });

        binding[0].RunningLogs.setOnClickListener(v -> {
            switchToCustomFragment(new RunningLogs(), RunningLogs.class);
        });

        // 设置点击事件
        binding[0].closeButton.setOnClickListener(v -> {
            rootView.removeView(binding[0].getRoot());
            binding[0] = null; // 如果有垃圾回收的需求，这里可以置null

            removeAllFragments(context);

            isMenu = false;
        });

        binding[0].getRoot();


        binding[0].closeButton.setText(Language.getClose(context));
        binding[0].RunningLogs.setText(Language.getLog(context));
        binding[0].GameStatus.setText(Language.getStatus(context));
        binding[0].OnlineVideo.setText(Language.getOnlineVideo(context));
    }

    private void removeAllFragments(Context context){
        Activity activity = (Activity) context;
        // 获取FragmentManager
        FragmentManager fragmentManager = activity.getFragmentManager();

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
}
