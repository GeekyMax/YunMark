package com.geekymax.util;

import org.omg.PortableInterceptor.SUCCESSFUL;

import java.net.URL;
import java.util.Objects;

/**
 * @author Max Huang
 */
public class FileUtil {
    public static String getAbsolutePath(String path) {
        return FileUtil.class.getResource(path).getPath();
    }

}
