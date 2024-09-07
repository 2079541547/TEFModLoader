package silkways.terraria.toolbox.logic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;

import silkways.terraria.toolbox.core.LoadMod;
import silkways.terraria.toolbox.ui.gametool.LoadTool;

public class LoadMain {


    @SuppressLint("NotConstructor")
    public void LoadMain(ViewGroup rootView, Context context) {

        LoadMod loadMod = new LoadMod();
        LoadTool loadTool = new LoadTool();

        //loadMod.LoadMian(context);
        loadTool.LoadMain(rootView, context);


    }
}
 //