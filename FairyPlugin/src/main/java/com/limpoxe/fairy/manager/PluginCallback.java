package com.limpoxe.fairy.manager;

/**
 *use PluginStatusChangeListener instead
 */
@Deprecated
public interface PluginCallback {

	public static final String ACTION_PLUGIN_CHANGED = "com.limpoxe.fairy.action.PLUGIN_CHANGED";

    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_VERSION = "version";
    public static final String EXTRA_RESULT_CODE = "code";
    public static final String EXTRA_SRC = "src";

    public static final String TYPE_INSTALL = "install";
    public static final String TYPE_REMOVE = "remove";
    public static final String TYPE_START = "start";
    public static final String TYPE_STOP = "stop";

    void onInstall(int result, String packageName, String version, String src);
	void onRemove(String packageName, int code);

	void onStart(String packageName);
	void onStop(String packageName);
}
