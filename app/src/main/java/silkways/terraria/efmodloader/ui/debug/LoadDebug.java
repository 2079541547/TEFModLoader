package silkways.terraria.efmodloader.ui.debug;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import silkways.terraria.efmodloader.R;
import silkways.terraria.efmodloader.logic.JsonConfigModifier;


public class LoadDebug {

    private int screenWidth; // 屏幕宽度
    private int screenHeight; // 屏幕高度
    private boolean isMenu = false;
    private FragmentManager fragmentManager;

    @SuppressLint("ClickableViewAccessibility")
    public void LoadMain(ViewGroup rootView, Context context){

        if ((boolean) JsonConfigModifier.readJsonValue(context, "ToolBoxData/game_settings.json", "debug")){

            System.loadLibrary("Tool");

            // 初始化悬浮按钮
            View floatingButton = LayoutInflater.from(context).inflate(R.layout.draggable_view, rootView, false);
            ImageView imageView = floatingButton.findViewById(R.id.floating_button);
            imageView.setImageResource(R.drawable.twotone_architecture_24);

            // 直接在代码中设置悬浮按钮的尺寸（50dp x 50dp）
            int sizeInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, context.getResources().getDisplayMetrics());
            FrameLayout.LayoutParams floatParams = new FrameLayout.LayoutParams(sizeInPx, sizeInPx);

            // 设置悬浮按钮的初始位置（这里以右上角为例）
            floatParams.gravity = Gravity.TOP - 100 | Gravity.START - 100;
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
        final EditText input = new EditText(context);

        // 创建AlertDialog实例
        new AlertDialog.Builder(context)
                .setTitle("请输入内容")
                .setMessage("这里可以输入一些信息")
                .setView(input)
                .setPositiveButton("执行", (dialog, which) -> {
                    String userInput = input.getText().toString();
                    dumpzhiz(userInput, 1024*1024);
                    isMenu = false;
                    Toast.makeText(context, "输入的内容是：" + input, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    isMenu = false;
                    dialog.dismiss();
                })
                .show();
    }


    public native void dumpzhiz(String pointerAddress, Integer size);

}


// 0x7150b9f660/home/eternalfuture/项目/vscode/EtherealEcho/library1
