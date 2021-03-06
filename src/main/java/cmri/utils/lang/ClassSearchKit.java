package cmri.utils.lang;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;


public class ClassSearchKit {
    protected static final Logger LOG = Logger.getLogger(ClassSearchKit.class);

    private List<String> includepaths = Lists.newArrayList();

    @SuppressWarnings("unchecked")
    private static <T> List<Class<? extends T>> extraction(Class<T> clazz, List<String> classFileList) {
        List<Class<? extends T>> classList = Lists.newArrayList();
        for (String classFile : classFileList) {
            Class<?> classInFile = Reflect.on(classFile).get();
            if (clazz.isAssignableFrom(classInFile) && clazz != classInFile) {
                classList.add((Class<? extends T>) classInFile);
            }
        }

        return classList;
    }

    public static ClassSearchKit of(Class target) {
        return new ClassSearchKit(target);
    }

    /**
     * 递归查找文件
     *
     * @param baseDirName    查找的文件夹路径
     * @param targetFileName 需要查找的文件名
     */
    private static List<String> findFiles(String baseDirName, String targetFileName) {
        /**
         * 算法简述： 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件， 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。 队列不空，重复上述操作，队列为空，程序结束，返回结果。
         */
        List<String> classFiles = Lists.newArrayList();
        //判断class路径
        Enumeration<URL> baseURLs = null;
        try {
            baseURLs = ClassSearchKit.class.getClassLoader().getResources(File.separator + baseDirName.replaceAll("\\.", "/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        URL baseURL = null;
        while (baseURLs.hasMoreElements()) {
            baseURL = baseURLs.nextElement();
            if (baseURL != null) {
                // 得到协议的名称
                String protocol = baseURL.getProtocol();
                String basePath = baseURL.getFile();
                if ("jar".equals(protocol)) {
                    String[] paths = basePath.split("!/");
                    // 获取jar
                    try {
                        classFiles = findJarFile(URLDecoder.decode(paths[0].replace("file:", ""), "UTF-8"), paths[1]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    classFiles = findPathFiles(basePath, targetFileName);
                }
            }
        }
        return classFiles;
    }

    /**
     * 查找根目录下的文件
     *
     * @param baseDirName    路径
     * @param targetFileName 文件匹配
     * @return
     */
    private static List<String> findPathFiles(String baseDirName, String targetFileName) {
        List<String> classFiles = Lists.newArrayList();
        String tempName;
        // 判断目录是否存在
        File baseDir = new File(baseDirName);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            LOG.error("search error：" + baseDirName + "is not a dir！");
        } else {
            String[] filelist = baseDir.list();
            String classname;
            String tem;
            for (String filename : filelist) {
                File readfile = new File(baseDirName + File.separator + filename);
                if (readfile.isDirectory()) {
                    classFiles.addAll(findPathFiles(baseDirName + File.separator + filename, targetFileName));
                } else {
                    tempName = readfile.getName();
                    if (ClassSearchKit.wildcardMatch(targetFileName, tempName)) {
                        tem = readfile.getAbsoluteFile().toString().replaceAll("\\\\", "/");
                        classname = tem.substring(tem.indexOf("classes/") + "classes/".length(),
                                tem.indexOf(".class"));
                        classFiles.add(classname.replaceAll("/", "."));
                    }
                }
            }
        }
        return classFiles;
    }

    /**
     * 通配符匹配
     *
     * @param pattern 通配符模式
     * @param str     待匹配的字符串 <a href="http://my.oschina.net/u/556800" target="_blank" rel="nofollow">@return</a>
     *                匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                // 通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1), str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                // 通配符问号?表示匹配任意一个字符
                strIndex++;
                if (strIndex > strLength) {
                    // 表示str中已经没有字符匹配?了。
                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return strIndex == strLength;
    }

    private Class target;

    public ClassSearchKit(Class target) {
        this.target = target;
    }

    public ClassSearchKit includepaths(String... classpaths) {
        if (classpaths != null) {
            Collections.addAll(this.includepaths, classpaths);
        }
        return this;
    }

    public ClassSearchKit includepaths(List<String> classpaths) {
        if (classpaths != null) {
            this.includepaths.addAll(classpaths.stream().collect(Collectors.toList()));
        }
        return this;
    }


    public <T> List<Class<? extends T>> search() {
        if (includepaths.size() <= 0) {
            List<String> classFileList = Lists.newArrayList();
            Enumeration<URL> resources = null;
            try {
                resources = ClassSearchKit.class.getClassLoader().getResources("");
            } catch (IOException e) {
                e.printStackTrace();
            }
            URL resource;
            while (resources.hasMoreElements()) {
                resource = resources.nextElement();
                classFileList.addAll(findPathFiles(resource.getPath(), "*.class"));
            }

            return extraction(target, classFileList);
        } else {
            List<String> classFileList = Lists.newArrayList();
            for (String classpath : includepaths) {
                classFileList.addAll(findFiles(classpath, "*.class"));
            }
            return extraction(target, classFileList);
        }
    }

    /**
     * 查找jar包中的class
     *
     * @param baseDirName jar路径
     * @param includeJars
     * @param includeJars jar文件地址 <a href="http://my.oschina.net/u/556800" target="_blank" rel="nofollow">@return</a>
     */
    private List<String> findjarFiles(String baseDirName, final List<String> includeJars, String packageName) {
        List<String> classFiles = Lists.newArrayList();
        try {
            // 判断目录是否存在
            File baseDir = new File(baseDirName);
            if (!baseDir.exists() || !baseDir.isDirectory()) {
                LOG.error("file serach error：" + baseDirName + " is not a dir！");
            } else {
                String[] filelist = baseDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return includeJars.contains(name);
                    }
                });
                for (int i = 0; i < filelist.length; i++) {
                    classFiles.addAll(findJarFile(baseDirName + File.separator + filelist[i], packageName));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return classFiles;

    }

    /**
     * find jar file
     *
     * @param filePath    文件路径
     * @param packageName 包名
     * @return list
     * @throws java.io.IOException 文件读取异常
     */
    private static List<String> findJarFile(String filePath, String packageName) throws IOException {
        List<String> classFiles = Lists.newArrayList();
        JarFile localJarFile = new JarFile(new File(filePath));
        classFiles = findInJar(localJarFile, packageName);
        localJarFile.close();
        return classFiles;
    }

    private static List<String> findInJar(JarFile localJarFile, String packageName) {
        List<String> classFiles = Lists.newArrayList();
        Enumeration<JarEntry> entries = localJarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName();
            if (!jarEntry.isDirectory() && (packageName == null || entryName.startsWith(packageName)) && entryName.endsWith(".class")) {
                String className = entryName.replaceAll("/", ".").substring(0, entryName.length() - 6);
                classFiles.add(className);
            }
        }
        return classFiles;
    }
}

