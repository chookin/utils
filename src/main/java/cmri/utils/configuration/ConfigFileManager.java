package cmri.utils.configuration;

import cmri.utils.io.FileHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyin on 3/20/15.
 */
public class ConfigFileManager {
    public static final String BASE_DIR;
    static {
        String confDir = System.getProperty("CONF_DIR");
        if(confDir == null){
            BASE_DIR = "conf/";
        }else {
            BASE_DIR = confDir;
        }
    }
    public static String dump(String filename) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        String myName = BASE_DIR + filename;
        FileHelper.save(in, myName);
        return myName;
    }

    public static String dumpIfNotExists(String filename) throws IOException {
        if (exists(filename)) {
            return filename;
        }
        return dump(filename);
    }

    public static boolean exists(String filename){
        return new File(BASE_DIR + filename).exists();
    }

    public static String getPath(String filename){
        return BASE_DIR + filename;
    }

    /**
     * @param name The resource name
     * @return  An input stream for reading the resource, or <tt>null</tt> if the resource could not be found
     * @throws IOException
     */
    public static InputStream getResourceFile(String name) throws IOException {
        if (exists(name)) {
            return new FileInputStream(getPath(name));
        } else {
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        }
    }

    public static List<String> readLines(String filename) throws IOException {
        InputStream in = getResourceFile(filename);
        if(in == null){
            throw new IOException("cannot load resource: "+filename);
        }
        List<String> lines = new ArrayList<>();
        InputStreamReader reader = new InputStreamReader(in, "utf-8");
        try (BufferedReader br = new BufferedReader(reader)) {
            String line = br.readLine();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }
        }
        return lines;
    }
}
