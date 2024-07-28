package silkways.terraria.toolbox.logic.game;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class getContext {
    public Context getContext(String name, Context context) throws PackageManager.NameNotFoundException {
        return context.createPackageContext(name, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
    }
}
