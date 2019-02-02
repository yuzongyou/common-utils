package com.yygame.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class 操作工具类
 *
 * @author yzy
 */
public abstract class ClassUtil {

    private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);

    public interface ClassAccept {

        /**
         * 是否接受结果
         *
         * @param clazz 类对象
         * @return 返回是否接受
         */
        boolean accept(Class<?> clazz);
    }

    /**
     * 是否包含给定注解中的任意一个
     *
     * @param clazz       类
     * @param annotations 注解列表
     * @return 如果参数为空则返回false
     */
    public static boolean containsAnyAnnotations(Class<?> clazz, final Class<? extends Annotation>... annotations) {

        if (clazz == null || annotations == null || annotations.length < 1) {
            return false;
        }

        for (Class<? extends Annotation> annotation : annotations) {
            if (containsAnnotation(clazz, annotation)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 类是否包含某个注解
     *
     * @param clazz      类
     * @param annotation 注解
     * @return 返回是否包含
     */
    public static boolean containsAnnotation(Class<?> clazz, final Class<? extends Annotation> annotation) {
        if (clazz == null || annotation == null) {
            return false;
        }
        Object ann = clazz.getAnnotation(annotation);
        return ann != null;
    }


    /**
     * 获取包含给定注解的所有类,包或子包下面的
     *
     * @param basePackages 包路径
     * @param annotations  注解
     * @return 始终返回非 null
     */
    public static Set<Class<?>> scan(String basePackages, Class<? extends Annotation>... annotations) {
        Set<String> packages = extractPackages(basePackages);
        if (packages == null || packages.isEmpty()) {
            return new HashSet<>();
        }
        String[] packageNames = packages.toArray(new String[packages.size()]);
        return scan(packageNames, annotations);
    }

    /**
     * 获取包含给定注解的所有类,包或子包下面的
     *
     * @param basePackages 包路径
     * @param annotations  注解
     * @return 始终返回非 null
     */
    public static Set<Class<?>> scan(String[] basePackages, final Class<? extends Annotation>... annotations) {
        ClassAccept accepter = null;
        if (CommonUtil.isAnyNotNull(annotations)) {
            accepter = new ClassAccept() {
                @Override
                public boolean accept(Class<?> clazz) {
                    return null != clazz && containsAnyAnnotations(clazz, annotations);
                }
            };
        }

        return scan(true, basePackages, accepter);
    }

    /**
     * 搜索指定包下面的类，默认会扫描该包下以及子包的所有类
     *
     * @param superClass   父类
     * @param basePackages 包名
     * @return 返回继承了 superClass 的所有子类， 始终返回非 null
     */
    @SuppressWarnings({"unchecked"})
    public static Set<Class<?>> scan(Class<?> superClass, String... basePackages) {
        return scan(superClass, true, basePackages);
    }

    /**
     * 搜索给定包路径下所有继承了 给定 class的class类
     *
     * @param superClass     父类
     * @param scanSubPackage 是否遍历子包
     * @param basePackages   包列表
     * @return 始终返回非null集合
     */
    @SuppressWarnings({"unchecked"})
    public static Set<Class<?>> scan(final Class<?> superClass, boolean scanSubPackage, String... basePackages) {

        ClassAccept accepter = null;

        if (null != superClass) {
            accepter = new ClassAccept() {
                @Override
                public boolean accept(Class<?> clazz) {
                    return null != clazz && superClass.isAssignableFrom(clazz);
                }
            };
        }

        return scan(scanSubPackage, basePackages, accepter);
    }

    /**
     * 检索指定包下面的满足 accepter 的所有类
     *
     * @param scanSubPackage 是否包含子包
     * @param basePackages   包列表
     * @param accepter       类接受条件
     * @return 始终返回非null
     */
    public static Set<Class<?>> scan(boolean scanSubPackage, String[] basePackages, ClassAccept accepter) {
        // 提取包列表
        Set<String> allPackages = extractPackages(basePackages);

        if (allPackages == null || allPackages.isEmpty()) {
            return new HashSet<>();
        }
        Set<Class<?>> classSet = new HashSet<>();

        for (String packageName : basePackages) {
            Set<Class<?>> subClassSet = scanBySinglePackage(accepter, scanSubPackage, packageName);
            if (null != subClassSet && !subClassSet.isEmpty()) {
                classSet.addAll(subClassSet);
            }
        }
        return classSet;
    }

    /**
     * 解析包名，将包名解析成单独的包列表
     *
     * @param basePackages 包名数组，数组的每隔元素可能又是包含多个包名
     * @return 始终返回非null
     */
    public static Set<String> extractPackages(String[] basePackages) {
        Set<String> packageSet = new HashSet<>();
        if (basePackages == null || basePackages.length < 1) {
            return packageSet;
        }

        for (String basePackage : basePackages) {
            Set<String> subPackages = extractPackages(basePackage);
            if (null != subPackages && !subPackages.isEmpty()) {
                packageSet.addAll(subPackages);
            }
        }

        return packageSet;
    }


    /**
     * 解析包名，中间可能使用 ｛,，；;|\\s｝ 进行分割
     *
     * @param basePackages 使用分隔符进行分割的一个或多个包名
     * @return 始终返回非 null
     */
    public static Set<String> extractPackages(Collection<String> basePackages) {
        if (null == basePackages || basePackages.isEmpty()) {
            return new HashSet<>();
        }
        return extractPackages(basePackages.toArray(new String[basePackages.size()]));
    }

    /**
     * 解析包名，中间可能使用 ｛,，；;|\\s｝ 进行分割
     *
     * @param basePackage 使用分隔符进行分割的一个或多个包名
     * @return 始终返回非 null
     */
    public static Set<String> extractPackages(String basePackage) {

        Set<String> packageSet = new HashSet<>();

        if (StringUtils.isBlank(basePackage)) {
            return packageSet;
        }

        String[] packageNames = basePackage.split("[,;|\\s，；]+");
        for (String packageName : packageNames) {
            if (isValidPackageName(packageName)) {
                packageSet.add(packageName.trim());
            }
        }

        return packageSet;
    }

    /**
     * 检查一个包名是否合法:
     * <p/>
     * 1. 不能以数字开头
     * 2. 不允许出现 -
     *
     * @param packageName 包名
     * @return 返回是否合法
     */
    public static boolean isValidPackageName(String packageName) {
        if (StringUtils.isBlank(packageName)) {
            return false;
        }
        String[] array = packageName.split("\\.");
        for (String name : array) {
            if (name.matches("^[0-9]+") || name.contains("-")) {
                return false;
            }
        }

        return true;
    }

    /**
     * 从单个包名中搜索类
     *
     * @param accepter       类接收器
     * @param packageName    包名
     * @param scanSubPackage 是否遍历子包
     * @return 始终返回非 null 集合
     */
    private static Set<Class<?>> scanBySinglePackage(ClassAccept accepter, boolean scanSubPackage, String packageName) {

        Set<Class<?>> classSet = new HashSet<>();

        if (StringUtils.isBlank(packageName)) {
            return classSet;
        }

        try {
            String packagePath = packageName.replace(".", "/");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> urls = classLoader.getResources(packagePath);

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (null == url) {
                    continue;
                }

                String protocol = url.getProtocol();

                if (protocol.equalsIgnoreCase("file")) {

                    Set<Class<?>> subClassSet = scanFromUrlPath(classLoader, url.getPath(), packageName, accepter, scanSubPackage);
                    if (null != subClassSet && !subClassSet.isEmpty()) {
                        classSet.addAll(subClassSet);
                    }
                    continue;
                }

                if (protocol.equalsIgnoreCase("jar")) {
                    Set<Class<?>> subClassSet = scanFromJarPath(classLoader, url, packageName, accepter, scanSubPackage);
                    if (null != subClassSet && !subClassSet.isEmpty()) {
                        classSet.addAll(subClassSet);
                    }
                }
            }


        } catch (Exception e) {
            throw new RuntimeException("解析[" + packageName + "]下的类错误！", e);
        }
        return classSet;
    }

    /**
     * 从 Jar 包进行搜索
     *
     * @param classLoader    类加载器
     * @param url            url
     * @param basePackage    包名
     * @param accepter       类接受器
     * @param scanSubPackage 是否扫描子包
     * @return 始终返回非 null
     */
    private static Set<Class<?>> scanFromJarPath(ClassLoader classLoader, URL url, String basePackage, ClassAccept accepter, boolean scanSubPackage) {
        Set<Class<?>> classSet = new HashSet<>();
        try {

            JarFile jarFile = getJarFileFromUrl(url);
            Enumeration<JarEntry> entries = jarFile.entries();

            classLoader = classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;

            final String regex = "^(.+?)\\.([^\\.]+\\.class)$";

            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();

                if (!entryName.endsWith(".class")) {
                    continue;
                }

                String classPath = entryName.replace('/', '.');

                if (!classPath.startsWith(basePackage)) {
                    continue;
                }

                String packageName = classPath.replaceAll(regex, "$1");
                boolean isSubPackage = !packageName.equals(basePackage);

                if (scanSubPackage || !isSubPackage) {
                    try {
                        classPath = classPath.replaceFirst("(?i)\\.class$", "");
                        Class<?> clazz = classLoader.loadClass(classPath);
                        boolean isAccept = null == accepter || accepter.accept(clazz);
                        if (isAccept) {
                            classSet.add(clazz);
                        }
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("解析[" + url + "]下的类错误！", e);
        }

        return classSet;
    }

    private static JarFile getJarFileFromUrl(URL url) {

        try {
            String urlJarPath = urlToAbsolutePath(url.getPath());
            return new JarFile(new File(urlJarPath));
        } catch (Exception e) {

            try {
                JarURLConnection connection = (JarURLConnection) url.openConnection();
                return connection.getJarFile();
            } catch (Exception e1) {
                throw new RuntimeException("无法将url转换成JarFile: " + e1.getMessage());
            }
        }
    }

    /**
     * 将 URL 对象的 path 转换成绝对路径
     *
     * @param urlPath URL对象getPath
     * @return 返回绝对路径
     */
    private static String urlToAbsolutePath(final String urlPath) {

        String path = urlPath;

        path = path.replaceFirst("(?i)file:[/\\\\]+", "");
        path = path.replaceFirst("(?i)/([^:]+:)", "$1");
        path = path.replaceFirst("(?i)jar:[/\\\\]*", "");
        path = path.replaceFirst("(?i)![^\\\\!]+$", "");

        if (!path.contains(":") && !path.startsWith("/")) {
            path = "/" + path;
        }

        return path;

    }

    /**
     * 从 文件路径下进行类搜索
     *
     * @param classLoader    类加载器
     * @param urlPath        文件路径
     * @param basePackage    基础包名
     * @param accepter       是否接受
     * @param scanSubPackage 是否搜索子包
     * @return 返回非 null 集合
     */
    private static Set<Class<?>> scanFromUrlPath(ClassLoader classLoader, String urlPath, String basePackage, ClassAccept accepter, boolean scanSubPackage) {

        String filePath = urlToAbsolutePath(urlPath);

        File file = new File(filePath);

        return scanFromFile(classLoader, file, basePackage, accepter, scanSubPackage);

    }


    /**
     * 从文件中搜索class
     *
     * @param classLoader    类加载器
     * @param file           文件
     * @param basePackage    基础包名
     * @param accepter       是否接受指定的类
     * @param scanSubPackage 是否搜索子包
     * @return 始终返回非 null
     */
    private static Set<Class<?>> scanFromFile(ClassLoader classLoader, File file, String basePackage, ClassAccept accepter, boolean scanSubPackage) {

        Set<Class<?>> classSet = new HashSet<>();

        if (null == file) {
            return classSet;
        }

        Class<?> clazz = convertFileToClass(classLoader, file, basePackage);
        boolean isAccept = clazz != null && (null == accepter || accepter.accept(clazz));
        if (isAccept) {
            classSet.add(clazz);
            return classSet;
        }

        // 如果是目录才会继续处理
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (null == childFiles || childFiles.length < 1) {
                return classSet;
            }

            for (File childFile : childFiles) {
                Set<Class<?>> subClassSet = scanFromFile(classLoader, childFile, basePackage, accepter, scanSubPackage);
                if (subClassSet != null && !subClassSet.isEmpty()) {
                    classSet.addAll(subClassSet);
                }
            }
        }
        return classSet;
    }

    private static boolean isClassFile(File file) {
        return null != file && file.isFile() && file.getPath().endsWith(".class");
    }

    /**
     * 根据文件解析Class
     *
     * @param classLoader 类加载器
     * @param file        文件对象
     * @param basePackage 基础包名
     * @return 如果不存在则返回null，不会抛出异常
     */
    private static Class<?> convertFileToClass(ClassLoader classLoader, File file, String basePackage) {

        if (!isClassFile(file)) {
            return null;
        }

        String filePath = file.getPath();

        filePath = PathUtil.normalizePath(filePath);
        String basePackagePath = StringUtils.isBlank(basePackage) ? "" : basePackage.replace('.', '/') + "/";

        int index = filePath.lastIndexOf("/classes/" + basePackagePath);
        if (index < 1) {
            return null;
        }

        // 截取classes后面的
        String classPath = filePath.substring(index);
        classPath = classPath.replaceFirst("/classes/", "");
        classPath = classPath.replace('/', '.');
        classPath = classPath.replaceAll("\\.class$", "");

        classLoader = classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;

        try {
            return classLoader.loadClass(classPath);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
