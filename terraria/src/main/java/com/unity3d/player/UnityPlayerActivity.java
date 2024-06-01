package com.unity3d.player;

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


public class UnityPlayerActivity extends Activity implements IUnityPlayerLifecycleEvents, View.OnTouchListener, View.OnGenericMotionListener {
    boolean[] PressedStates = new boolean[330];
    protected UnityPlayer mUnityPlayer;

    int GetUnityKeyCode(int i) {
        if (i >= 29 && i <= 54) {
            return (i - 29) + 97;
        }
        if (i >= 8 && i <= 16) {
            return (i - 8) + 49;
        }
        if (i >= 131 && i <= 142) {
            return (i - 131) + 282;
        }
        if (i == 111 || i == 4) {
            return 27;
        }
        if (i == 68) {
            return 96;
        }
        if (i == 7) {
            return 48;
        }
        if (i == 62) {
            return 32;
        }
        if (i == 69) {
            return 45;
        }
        if (i == 70) {
            return 61;
        }
        if (i == 67) {
            return 8;
        }
        if (i == 112) {
            return 127;
        }
        if (i == 59) {
            return 304;
        }
        if (i == 60) {
            return 303;
        }
        if (i == 66) {
            return 13;
        }
        if (i == 22) {
            return 275;
        }
        if (i == 21) {
            return 276;
        }
        if (i == 19) {
            return 273;
        }
        if (i == 20) {
            return 274;
        }
        if (i == 55) {
            return 44;
        }
        if (i == 56) {
            return 46;
        }
        if (i == 76) {
            return 47;
        }
        if (i == 57) {
            return 308;
        }
        if (i == 58) {
            return 307;
        }
        if (i == 114) {
            return 305;
        }
        if (i == 113) {
            return 306;
        }
        if (i == 115) {
            return 301;
        }
        if (i == 61) {
            return 9;
        }
        if (i == 72) {
            return 93;
        }
        if (i == 71) {
            return 91;
        }
        if (i == 75) {
            return 39;
        }
        if (i == 74) {
            return 59;
        }
        return i == 73 ? 92 : 0;
    }

    @Override // com.unity3d.player.IUnityPlayerLifecycleEvents
    public void onUnityPlayerQuitted() {
    }

    protected String updateUnityCommandLineArguments(String str) {
        return str;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        requestWindowFeature(1);
        super.onCreate(bundle);
        getIntent().putExtra("unity", updateUnityCommandLineArguments(getIntent().getStringExtra("unity")));
        UnityPlayer unityPlayer = new UnityPlayer(this, this);
        this.mUnityPlayer = unityPlayer;
        setContentView(unityPlayer);
        this.mUnityPlayer.requestFocus();
        if (Build.VERSION.SDK_INT >= 24 && getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.touchscreen")) {
            this.mUnityPlayer.setPointerIcon(PointerIcon.getSystemIcon(getBaseContext(), 0));
        }
        this.mUnityPlayer.setOnTouchListener(this);
        this.mUnityPlayer.setOnGenericMotionListener(this);
    }

    @Override // com.unity3d.player.IUnityPlayerLifecycleEvents
    public void onUnityPlayerUnloaded() {
        moveTaskToBack(true);
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        this.mUnityPlayer.newIntent(intent);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.mUnityPlayer.destroy();
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        if (MultiWindowSupport.getAllowResizableWindow(this)) {
            this.mUnityPlayer.pause();
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        if (MultiWindowSupport.getAllowResizableWindow(this)) {
            this.mUnityPlayer.resume();
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        MultiWindowSupport.saveMultiWindowMode(this);
        if (MultiWindowSupport.getAllowResizableWindow(this)) {
            return;
        }
        this.mUnityPlayer.pause();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (!MultiWindowSupport.getAllowResizableWindow(this) || MultiWindowSupport.isMultiWindowModeChangedToTrue(this)) {
            this.mUnityPlayer.resume();
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        this.mUnityPlayer.lowMemory();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks2
    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        if (i == 15) {
            this.mUnityPlayer.lowMemory();
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mUnityPlayer.configurationChanged(configuration);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        this.mUnityPlayer.windowFocusChanged(z);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 2) {
            return this.mUnityPlayer.injectEvent(keyEvent);
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public void SetMouseCursorMode(int i) {
        if (Build.VERSION.SDK_INT >= 24) {
            if (i == 0) {
                this.mUnityPlayer.setPointerIcon(PointerIcon.getSystemIcon(getBaseContext(), 0));
                return;
            }
            if (i != 1) {
                if (i == 2) {
                    this.mUnityPlayer.setPointerIcon(PointerIcon.getSystemIcon(getBaseContext(), 1000));
                }
            } else if (!getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.touchscreen")) {
                this.mUnityPlayer.setPointerIcon(PointerIcon.getSystemIcon(getBaseContext(), 1000));
            } else {
                this.mUnityPlayer.setPointerIcon(PointerIcon.getSystemIcon(getBaseContext(), 0));
            }
        }
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        int GetUnityKeyCode = GetUnityKeyCode(i);
        if (GetUnityKeyCode != 0) {
            this.PressedStates[GetUnityKeyCode] = false;
        }
        return this.mUnityPlayer.injectEvent(keyEvent);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        int GetUnityKeyCode = GetUnityKeyCode(i);
        if (GetUnityKeyCode != 0) {
            this.PressedStates[GetUnityKeyCode] = true;
        }
        return this.mUnityPlayer.injectEvent(keyEvent);
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return onTouch(this.mUnityPlayer, motionEvent);
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        InputDevice device;
        if (Build.VERSION.SDK_INT < 21 || (device = motionEvent.getDevice()) == null || !device.supportsSource(1041) || motionEvent.getToolType(0) != MotionEvent.TOOL_TYPE_FINGER) {
            return this.mUnityPlayer.injectEvent(motionEvent);
        }
        return true;
    }

    @Override // android.app.Activity
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return onGenericMotion(this.mUnityPlayer, motionEvent);
    }

    public boolean IsKeyPressed(int i) {
        return this.PressedStates[i];
    }

    @Override // android.view.View.OnGenericMotionListener
    public boolean onGenericMotion(View view, MotionEvent motionEvent) {
        InputDevice device;
        if (Build.VERSION.SDK_INT < 21 || (device = motionEvent.getDevice()) == null || !device.supportsSource(1041) || motionEvent.getToolType(0) != MotionEvent.TOOL_TYPE_FINGER) {
            return this.mUnityPlayer.injectEvent(motionEvent);
        }
        return true;
    }
}
