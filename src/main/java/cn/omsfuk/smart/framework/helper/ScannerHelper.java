package cn.omsfuk.smart.framework.helper;

import cn.omsfuk.smart.framework.helper.annotation.PropertiesFile;
import cn.omsfuk.smart.framework.helper.annotation.Property;

/**
 * Created by omsfuk on 17-5-26.
 */

@PropertiesFile
public final class ScannerHelper {

    @Property("component.scan.path")
    private static String SCAN_PATH;

    static {
//        ScannerHelper.class.getClassLoader().loadClass("")
    }
}
