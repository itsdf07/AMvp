package com.itsdf07.alog;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @Description ：文件相关操作工具类
 * @Author itsdf07
 * @Time 2018/6/3
 */

public class FileUtils {
    private static final String TAG = "FFileUtils";

    /**
     * 删除垃圾文件完成后（不论成功与否，只要跳出删除）后的回调
     */
    public interface IDelJunkFileCallback {
        /**
         * 删除成功
         *
         * @param files 成功删除的文件；列表
         */
        void delSuccess(ArrayList<File> files);

        /**
         * 删除失败
         *
         * @param files 删除失败的文件列表
         */
        void delFailed(ArrayList<File> files);

    }

    /**
     * 判断是否为【目录】
     *
     * @param path
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDir(String path) {
        File file = getFileByPath(path);
        return isDir(file);
    }

    /**
     * 判断是否为【目录】
     *
     * @param file
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDir(File file) {
        return isFileExists(file) && file.isDirectory();
    }

    /**
     * 判断是否为【文件】
     *
     * @param path
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(String path) {
        File file = getFileByPath(path);
        return isFile(file);
    }

    /**
     * 判断是否为【文件】
     *
     * @param file
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(File file) {
        return isFileExists(file) && file.isFile();
    }

    /**
     * 根据【文件路径】获取对应的【文件】
     *
     * @param path
     * @return 文件
     */
    public static File getFileByPath(String path) {
        return TextUtils.isEmpty(path.trim()) ? null : new File(path);
    }

    /**
     * 判断【文件/文件夹】是否存在
     *
     * @param path
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFileExists(String path) {
        File file = getFileByPath(path);
        return isFileExists(file);
    }

    /**
     * 判断【文件/文件夹】是否存在
     *
     * @param file
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFileExists(File file) {
        return null != file && file.exists();
    }

    /**----------------------------------------------------*/

    /**
     * 创建目标【目录】：如果父级目录不存在，则一同创建<br/>
     * 1、如果目标file已存在，则返回是否为路径<br/>
     * 2、如果目标file不存在，则返回创建目标目录是否成功<br/>
     *
     * @param path
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(String path) {
        File file = getFileByPath(path);
        return createOrExistsDir(file);
    }

    /**
     * 创建目标【目录】：如果父级目录不存在，则一同创建<br/>
     * 1、如果目标file已存在，则返回是否为路径<br/>
     * 2、如果目标file不存在，则返回创建目标目录是否成功<br/>
     *
     * @param file
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(File file) {
//        if (null == file) {
//            return false;
//        }
//        if (file.exists()) {
//            return file.isDirectory();
//        }
//        return file.mkdirs();
        return null != file && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 创建目标【文件】
     * 1、如果目标file已存在，则返回是否为文件<br/>
     * 2、如果file不存在，则优先创建其父目录<br/>
     * 2.1、创建成功，则继续创建文件<br/>
     * 2.2、创建失败，则返回创建文件失败<br/>
     * 2、如果目标file不存在，则返回创建目标目录是否成功<br/>
     *
     * @param path 文件路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(String path) {
        File file = getFileByPath(path);
        return createOrExistsFile(file);
    }

    /**
     * 创建目标【文件】
     * 1、如果目标file已存在，则返回是否为文件<br/>
     * 2、如果file不存在，则优先创建其父目录<br/>
     * 2.1、创建成功，则继续创建文件<br/>
     * 2.2、创建失败，则返回创建文件失败<br/>
     * 2、如果目标file不存在，则返回创建目标目录是否成功<br/>
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(File file) {
        if (null == file) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }

        if (!createOrExistsDir(file.getParentFile())) {//创建文件目录
            return false;
        }
        try {
            return file.createNewFile();//创建文件
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建【文件】：如果存在则在创建之前删除原有文件
     *
     * @param filePath 文件路径
     * @return {@code true}: 创建成功<br>{@code false}: 创建失败
     */
    public static boolean createFileByDeleteOldFile(String filePath) {
        File file = getFileByPath(filePath);
        return createFileByDeleteOldFile(file);
    }

    /**
     * 创建【文件】：如果存在则在创建之前删除原有文件
     *
     * @param file 文件
     * @return {@code true}: 创建成功<br>{@code false}: 创建失败
     */
    public static boolean createFileByDeleteOldFile(File file) {
        if (null == file) {
            return false;
        }

        // 文件存在并且删除失败返回false
        if (isFile(file)) {
            String filePath = file.getPath() + System.currentTimeMillis();
            File oldFile = new File(filePath);
            if (rename(file, oldFile.getName())) {
                delJunkFile(oldFile, false, true, null);
            } else {
                return false;
            }

        }
        // 创建目录失败返回false
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**----------------------------------------------------*/
    /**
     * 重命名【文件】
     *
     * @param path    文件路径
     * @param newName 新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static boolean rename(String path, String newName) {
        File file = getFileByPath(path);
        return rename(file, newName);
    }

    /**
     * 重命名文件
     *
     * @param file    文件
     * @param newName 新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static boolean rename(File file, String newName) {
        // 文件为空返回false
        if (null == file) {
            return false;
        }
        // 文件不存在返回false
        if (!file.exists()) {
            return false;
        }
        // 新的文件名为空返回false
        if (TextUtils.isEmpty(newName)) {
            return false;
        }
        // 如果文件名没有改变返回true
        if (newName.equals(file.getName())) {
            return true;
        }
        File newFile = new File(file.getParent() + File.separator + newName);
        // 如果重命名的文件已存在返回false
        return !newFile.exists() && file.renameTo(newFile);
    }
    /**----------------------------------------------------*/

    /**
     * 在线程中删除【文件】
     *
     * @param path       需要删除的垃圾文件(单个File)
     * @param needRename 是否需要重命名的方式来删除
     * @param callback   文件删除完成之后的回调，不需要的话可以传入null
     */
    public static void delJunkFile(final String path, final boolean needRename, final boolean inThread, final IDelJunkFileCallback callback) {
        File file = getFileByPath(path);
        delJunkFile(file, needRename, inThread, callback);
    }

    /**
     * 在线程中删除【文件】
     *
     * @param file       需要删除的垃圾文件(单个File)
     * @param needRename 是否需要重命名的方式来删除
     * @param callback   文件删除完成之后的回调，不需要的话可以传入null
     */
    public static void delJunkFile(final File file, final boolean needRename, final boolean inThread, final IDelJunkFileCallback callback) {
        if (!isFileExists(file)) {
            return;
        }
        ArrayList<File> files = new ArrayList<>();
        files.add(file);
        if (inThread) {
            delJunkFileInThread(files, needRename, callback);
        } else {
            delJunkFile(files, needRename, callback);
        }
    }

    /**
     * 在线程中删除【文件】
     * */
    /**
     * @param files      需要删除的垃圾文件(多个)
     * @param needRename 是否需要重命名的方式来删除
     * @param callback   文件删除完成之后的回调，不需要的话可以传入null
     */
    public static void delJunkFileInThread(final ArrayList<File> files, final boolean needRename, final IDelJunkFileCallback callback) {
        if (null == files || files.size() <= 0) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                delJunkFile(files, needRename, callback);
            }
        }).start();
    }

    /**
     * 在线程中删除【文件】
     * */
    /**
     * @param files      需要删除的垃圾文件(多个)
     * @param needRename 是否需要重命名的方式来删除
     * @param callback   文件删除完成之后的回调，不需要的话可以传入null
     */
    private static void delJunkFile(final ArrayList<File> files, final boolean needRename, final IDelJunkFileCallback callback) {
        if (null == files || files.size() <= 0) {
            return;
        }
        ArrayList<File> delFiles = new ArrayList<File>();
        ArrayList<File> unDelFiles = new ArrayList<File>();

        for (File file : files) {
            if (isFileExists(file)) {
                if (needRename) {
                    File fileRename = new File(file.getPath() + System.currentTimeMillis());
                    if (file.renameTo(fileRename)) {
                        file = fileRename;
                    }
                }
                boolean success = file.delete();
                if (success) {
                    delFiles.add(file);
                } else {
                    unDelFiles.add(file);
                }
            }
        }
        if (null != callback) {
            callback.delSuccess(delFiles);
            callback.delFailed(unDelFiles);
        }
    }


    /**
     * 获取路径下的所有文件集，使用时注意判 null
     *
     * @param path
     * @return
     */
    public static File[] getFileList(String path) {
        File file = new File(path);
        File[] fileLists = file.listFiles();
        return fileLists;
    }

    /**
     * ----------------------------------------------------
     */

    public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值
    public static final int FILESCALE_1024 = 1024;//文件大小单位换算比例

    /**
     * 统计文件夹耗时：获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize);
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (IOException e) {

            } finally {
                fis.close();
            }
        } else {
            file.createNewFile();
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < FILESCALE_1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < (FILESCALE_1024 * FILESCALE_1024)) {
            fileSizeString = df.format((double) fileS / FILESCALE_1024) + "KB";
        } else if (fileS < (FILESCALE_1024 * FILESCALE_1024 * FILESCALE_1024)) {
            fileSizeString = df.format((double) fileS / (FILESCALE_1024 * FILESCALE_1024)) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / (FILESCALE_1024 * FILESCALE_1024 * FILESCALE_1024)) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    public static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / FILESCALE_1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / (FILESCALE_1024 * FILESCALE_1024)));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df
                        .format((double) fileS / (FILESCALE_1024 * FILESCALE_1024 * FILESCALE_1024)));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 外部存储是否可用 (存在且具有读写权限)
     *
     * @return
     */

    public static boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// MEDIA_MOUNTED_READ_ONLY (SDcard存在，只可以进行读操作)
    }

    /**
     * 获取手机内部可用空间大小
     *
     * @return
     */

    public static long getAvailableMemorySize(String path) {
        StatFs stat = new StatFs(path);
        long blockSize;
        long availableBlocks;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        } else {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        }
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部空间总大小
     *
     * @return
     */

    public static long getTotalMemorySize(String path) {
        StatFs stat = new StatFs(path);

        long blockSize;      //每个block 占字节数
        long totalBlocks;   //block总数
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
        } else {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
        }
        return totalBlocks * blockSize;
    }

    /**----------------------------------------------------*/

    /**
     * 获取内置sd卡路径，<br/>
     * 目前个人认为可以移除就是外卡，不能移除就是内卡，只经过大量试验，没有任何依据！此处需慎重！！！
     *
     * @param context
     * @return 内卡路径
     */
    public static String getInnerSDPath(Context context) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 12) {
            try {
                StorageManager manager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
                /************** StorageManager的方法 ***********************/
                Method getVolumeList = StorageManager.class.getMethod("getVolumeList");
                Method getVolumeState = StorageManager.class.getMethod("getVolumeState", String.class);

                Object[] Volumes = (Object[]) getVolumeList.invoke(manager);
                String state = null;
                String path = null;

                for (Object volume : Volumes) {
                    /************** StorageVolume的方法 ***********************/
                    Method getPath = volume.getClass().getMethod("getPath");
                    path = (String) getPath.invoke(volume);
                    state = (String) getVolumeState.invoke(manager, getPath.invoke(volume));

                    /**
                     * 是否可以移除(内置sdcard) TODO:
                     * 目前个人认为可以移除就是外卡，不能移除就是内卡，只经过大量试验，没有任何依据！此处需慎重！！！
                     */
                    Method isRemovable = volume.getClass().getMethod("isRemovable");
                    boolean removable = (Boolean) isRemovable.invoke(volume);

                    if (null != path && null != state && state.equals(Environment.MEDIA_MOUNTED)) {
                        if (false == removable) {
                            return path;
                        }
                    }
                }
            } catch (Exception e) {
                return "";
            }
        }
        // 得到存储卡路径
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡
        // 或可存储空间是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取sd卡或可存储空间的跟目录
            return sdDir.toString();
        }

        return "";
    }

    /**
     * 信息写入文件
     *
     * @param file    写入的文件
     * @param content 写入的数据
     * @param append  是否覆盖写入
     * @return {@code true}: 信息写入成功<br>{@code false}: 信息写入失败
     */
    public static boolean write2File(File file, String content, boolean append) {
        if (TextUtils.isEmpty(content)) {
            content = "";
        }
        byte[] aData = content.getBytes();
        return write2File(file, aData, append);
    }

    /**
     * 信息写入文件
     *
     * @param file   写入的文件
     * @param aData  写入的数据
     * @param append 是否覆盖写入
     * @return {@code true}: 信息写入成功<br>{@code false}: 信息写入失败
     */
    private static boolean write2File(File file, byte[] aData, boolean append) {
        if (!createOrExistsFile(file)) {
            //写入文件不存在且创建失败时return false
            return false;
        }
        OutputStream out = null;
        boolean ok = false;
        try {
            out = new FileOutputStream(file, append);
            out.write(aData);
            ok = true;
        } catch (Exception e) {
            ALog.eTag(TAG, e, "log write failure!");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    ALog.eTag(TAG, e, "OutputStream close failure!");
                }
            }
        }
        return ok;
    }
}
