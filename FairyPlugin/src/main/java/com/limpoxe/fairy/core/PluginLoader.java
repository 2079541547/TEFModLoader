package com.limpoxe.fairy.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.limpoxe.fairy.content.LoadedPlugin;
import com.limpoxe.fairy.content.PluginDescriptor;
import com.limpoxe.fairy.core.android.HackLayoutInflater;
import com.limpoxe.fairy.core.compat.CompatForFragmentClassCache;
import com.limpoxe.fairy.core.compat.CompatForSupportv7ViewInflater;
import com.limpoxe.fairy.core.proxy.systemservice.AndroidAppIActivityManager;
import com.limpoxe.fairy.core.proxy.systemservice.AndroidAppINotificationManager;
import com.limpoxe.fairy.core.proxy.systemservice.AndroidAppIPackageManager;
import com.limpoxe.fairy.core.proxy.systemservice.AndroidOsServiceManager;
import com.limpoxe.fairy.core.proxy.systemservice.AndroidWebkitWebViewFactoryProvider;
import com.limpoxe.fairy.core.proxy.systemservice.AndroidWidgetToast;
import com.limpoxe.fairy.manager.PluginManagerHelper;
import com.limpoxe.fairy.manager.PluginManagerProviderClient;
import com.limpoxe.fairy.manager.mapping.StubActivityMappingProcessor;
import com.limpoxe.fairy.manager.mapping.StubReceiverMappingProcessor;
import com.limpoxe.fairy.manager.mapping.StubServiceMappingProcessor;
import com.limpoxe.fairy.util.FreeReflection;
import com.limpoxe.fairy.util.LogUtil;
import com.limpoxe.fairy.util.ProcessUtil;

public class PluginLoader {

	private PluginLoader() {
	}

    /**
	 * 初始化loader, 只可调用一次
	 * 
	 * @param app
	 */
	public static synchronized void initLoader(Application app) {
		if (FairyGlobal.isInited()) {
			return;
		}

        LogUtil.v("插件框架初始化中...");
        long t1 = System.currentTimeMillis();

        if (Build.VERSION.SDK_INT >= 28) {
            boolean ret = FreeReflection.exemptAll(app);
            LogUtil.v("hidden api exempt " + ret);
        }

        FairyGlobal.setApplication(app);
        FairyGlobal.registStubMappingProcessor(new StubActivityMappingProcessor());
        FairyGlobal.registStubMappingProcessor(new StubServiceMappingProcessor());
        FairyGlobal.registStubMappingProcessor(new StubReceiverMappingProcessor());

        //这里的isPluginProcess方法需要在安装AndroidAppIActivityManager之前执行一次。
        //原因见AndroidAppIActivityManager的getRunningAppProcesses()方法
        boolean isPluginProcess = ProcessUtil.isPluginProcess();
        if(ProcessUtil.isPluginProcess()) {
            AndroidOsServiceManager.installProxy();
            AndroidWidgetToast.installProxy();
        }

        AndroidAppIActivityManager.installProxy();
        AndroidAppINotificationManager.installProxy();
        AndroidAppIPackageManager.installProxy(FairyGlobal.getHostApplication().getPackageManager());

        if (isPluginProcess) {
            HackLayoutInflater.installPluginCustomViewConstructorCache();
            CompatForSupportv7ViewInflater.installPluginCustomViewConstructorCache();
            CompatForFragmentClassCache.installFragmentClassCache();
            CompatForFragmentClassCache.installSupportV4FragmentClassCache();
            CompatForFragmentClassCache.installAndroidXFragmentClassCache();
            //不可在主进程中同步安装，因为此时ActivityThread还没有准备好, 会导致空指针。
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    AndroidWebkitWebViewFactoryProvider.installProxy();
                }
            });
        }

        PluginInjector.injectHandlerCallback();//本来宿主进程是不需要注入handlecallback的，这里加上是为了对抗360安全卫士等软件，提高Instrumentation的成功率
        PluginInjector.injectInstrumentation();
        PluginInjector.injectBaseContext(FairyGlobal.getHostApplication());
        PluginInjector.injectAppComponentFactory();

        if (isPluginProcess) {
            if (Build.VERSION.SDK_INT >= 14) {
                FairyGlobal.getHostApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    }

                    @Override
                    public void onActivityStarted(Activity activity) {
                    }

                    @Override
                    public void onActivityResumed(Activity activity) {
                    }

                    @Override
                    public void onActivityPaused(Activity activity) {
                    }

                    @Override
                    public void onActivityStopped(Activity activity) {
                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {
                        Intent intent = activity.getIntent();
                        if (intent != null && intent.getComponent() != null) {
                            LogUtil.v("回收绑定关系");
                            PluginManagerProviderClient.unBindLaunchModeStubActivity(intent.getComponent().getClassName(), activity.getClass().getName());
                        }
                    }
                });
            }
        }

        FairyGlobal.setIsInited(true);

        long t2 = System.currentTimeMillis();
        LogUtil.w("插件框架初始化完成", "耗时：" + (t2-t1));
	}

	public static Context fixBaseContextForReceiver(Context superApplicationContext) {
		if (superApplicationContext instanceof ContextWrapper) {
			return ((ContextWrapper)superApplicationContext).getBaseContext();
		} else {
			return superApplicationContext;
		}
	}


	/**
	 * 根据插件中的classId加载一个插件中的class
	 * 
	 * @param clazzId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class loadPluginFragmentClassById(String clazzId) {
		PluginDescriptor pluginDescriptor = PluginManagerHelper.getPluginDescriptorByFragmentId(clazzId);
		if (pluginDescriptor != null) {
            String clazzName = pluginDescriptor.getPluginClassNameById(clazzId);
            return loadPluginClassByName(pluginDescriptor, clazzName);
		} else {
            LogUtil.e("PluginDescriptor Not Found for classId ", clazzId);
        }
		return null;

	}

	@SuppressWarnings("rawtypes")
	public static Class loadPluginClassByName(String clazzName) {
		PluginDescriptor pluginDescriptor = PluginManagerHelper.getPluginDescriptorByClassName(clazzName);
		return loadPluginClassByName(pluginDescriptor, clazzName);
	}

	public static Class loadPluginClassByName(PluginDescriptor pluginDescriptor, String clazzName) {

		if (pluginDescriptor != null && clazzName != null) {
			//插件可能尚未初始化，确保使用前已经初始化
			LoadedPlugin plugin = PluginLauncher.instance().startPlugin(pluginDescriptor);
			if (plugin != null) {
                return plugin.loadClassByName(clazzName);
            } else {
                LogUtil.e("Plugin is not running", clazzName);
            }
		} else {
			LogUtil.e("loadPluginClass Fail for clazzName ", clazzName, pluginDescriptor==null?"pluginDescriptor = null":"pluginDescriptor not null");
		}

		return null;
	}

	/**
	 * 获取当前class所在插件的Context
	 * 每个插件只有1个DefaultContext,
	 * 是当前插件中所有class公用的Context
	 * 
	 * @param clazz
	 * @return
	 */
	public static Context getDefaultPluginContext(@SuppressWarnings("rawtypes") Class clazz) {

		Context pluginContext = null;
		PluginDescriptor pluginDescriptor = PluginManagerHelper.getPluginDescriptorByClassName(clazz.getName());

		if (pluginDescriptor != null) {
            LoadedPlugin plugin = PluginLauncher.instance().getRunningPlugin(pluginDescriptor.getPackageName());
            if (plugin != null) {
                pluginContext = plugin.pluginContext;;
            } else {
                LogUtil.e("Plugin is not running", clazz.getName());
            }
		} else {
			LogUtil.e("PluginDescriptor Not Found for ", clazz.getName());
		}

		if (pluginContext == null) {
			LogUtil.e("Context Not Found for ", clazz.getName());
		}

		return pluginContext;
	}
}
