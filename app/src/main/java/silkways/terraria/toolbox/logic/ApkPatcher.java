package silkways.terraria.toolbox.logic;


import com.android.tools.build.apkzlib.zip.AlignmentRules;
import com.android.tools.build.apkzlib.zip.ZFile;
import com.android.tools.build.apkzlib.zip.ZFileOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ApkPatcher {

    private static final ZFileOptions Z_FILE_OPTIONS = new ZFileOptions().setAlignmentRule(AlignmentRules.compose(
            AlignmentRules.constantForSuffix(".so", 4096),
            AlignmentRules.constantForSuffix(".assets", 4096)
    ));


    public static void addSOofAPK(String apkPath, String libso, String add){
        try(var dstZFile = ZFile.openReadWrite(new File(apkPath), Z_FILE_OPTIONS)) {
            try(var is = new FileInputStream(libso)) {
                dstZFile.add(add, is, false);
            } catch (IOException e){
                throw new RuntimeException(e);
            }
            dstZFile.realign();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
