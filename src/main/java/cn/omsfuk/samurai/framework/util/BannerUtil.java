package cn.omsfuk.samurai.framework.util;

import java.io.*;

/**
 * Created by omsfuk on 2017/6/8.
 */
public class BannerUtil {

    public static void printBanner(String filePath, OutputStream outputStream) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(BannerUtil.class.getResourceAsStream(filePath)));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\r\n");
            }
            sb.toString();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(sb.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
