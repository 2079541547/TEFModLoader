package silkways.tefmodloader.core;

public class NativeLib {

    // Used to load the 'core' library on application startup.
    static {
        System.loadLibrary("core");
    }

    /**
     * A native method that is implemented by the 'core' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}