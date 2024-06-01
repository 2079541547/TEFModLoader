package com.limpoxe.fairy.core.android;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import com.limpoxe.fairy.util.RefInvoker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cailiming on 16/10/30.
 */

public class HackSupportV4LocalboarcastManager {

    /**
     * 实际上项目迁移到AndroidX之后，最新的AGP脚本会自动在编译时通过修改字节码的方式将这个字符串修改为AndroidX的
     * 通过debug可以看到这个字段已经被改变了
     */
    private static final String ClassName = "android.support.v4.content.LocalBroadcastManager";

    private static final String Field_mInstance = "mInstance";
    private static final String Field_mReceivers = "mReceivers";

    private static final String Method_unregisterReceiver = "unregisterReceiver";

    private Object instance ;

    public HackSupportV4LocalboarcastManager(Object instance) {
        this.instance = instance;
    }

    public static Object getInstance() {
        return RefInvoker.getField(null, ClassName, Field_mInstance);
    }

    public HashMap<BroadcastReceiver, ArrayList<IntentFilter>> getReceivers() {
        return (HashMap<BroadcastReceiver, ArrayList<IntentFilter>>)RefInvoker.getField(instance, ClassName, Field_mReceivers);
    }

    public void unregisterReceiver(BroadcastReceiver item) {
        RefInvoker.invokeMethod(instance, ClassName, Method_unregisterReceiver, new Class[]{BroadcastReceiver.class}, new Object[]{item});

    }

}
