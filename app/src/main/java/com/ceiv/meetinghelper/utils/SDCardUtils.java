package com.ceiv.meetinghelper.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * SD卡的操作工作类
 * 提供以下操作方法:
 * 1、判断SD卡是否挂载正常
 * 2、获取SD卡根目录的绝对路径
 * 3、获取SD卡的总空间大小
 * 4、获取SD卡的剩余空间大小
 * 5、读取SD卡上的某个文件(byte[])
 * 6、将文件（byte[]）写入到SD卡的指定位置
 * 7、复制SD卡上的文件
 */
public class SDCardUtils {

    /**
     * 判断SD卡是否挂载正常
     */
    public static boolean isMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取SD卡根目录的绝对路径
     */
    public static String getRoot() {
        if (isMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * 获取外置SD卡路径
     */
    public static String[] getExtSDCardPath(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context
                .STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            return (String[]) invoke;
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取SD卡的总空间大小
     */
    public static long getTotalSize() {
        if (isMounted()) {
            StatFs stat = new StatFs(getRoot());
            long blockCount = stat.getBlockCount();
            long blockSize = stat.getBlockSize();
            return blockCount * blockSize;
        } else {
            return 0;
        }
    }

    /**
     * 获取SD卡的剩余空间大小
     */
    public static long getAvailableSize() {
        if (isMounted()) {
            StatFs stat = new StatFs(getRoot());
            long avilableblockCount = stat.getAvailableBlocks();
            long blockSize = stat.getBlockSize();
            return avilableblockCount * blockSize;
        } else {
            return 0;
        }
    }

    /**
     * 读取SD卡上的某个文件(byte[])
     */
    public static byte[] readFile(File path) {
        if (isMounted()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(path);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                return out.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                        fis = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 将文件（byte[]）写入到SD卡的指定位置
     *
     * @param data 要写入SD卡的数据
     * @param des  保存文件的路径
     * @return true，写入成功；false，写入失败
     */
    public static boolean writeFile(byte[] data, File des) {
        if (isMounted()) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(des);
                fos.write(data);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                        fos = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 复制SD卡上的文件
     *
     * @param src 被复制的文件路径
     * @param des 复制文件的目标路径
     * @return true，复制成功；false，复制失败
     */
    public static boolean copyFile(String src, String des) {
        if (isMounted()) {
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(src));
                bos = new BufferedOutputStream(new FileOutputStream(des));
                int b = 0;
                while ((b = bis.read()) != -1) {
                    bos.write(b);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                        bos = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bis != null) {
                    try {
                        bis.close();
                        bis = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 在手机固定目录中写入配置文件
     */
    public static void writeTxt(String message ,String fileName) {
        //新建文件夹
        String folderName = "meetingSystem";
        File sdCardDir2 = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), folderName);
        File sdCardDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), folderName);
        if (!sdCardDir.exists()) {
            if (!sdCardDir.mkdirs()) {
                try {
                    sdCardDir.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            //新建文件
            File saveFile = new File(sdCardDir, fileName+".txt");
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
            // FileOutputStream outStream =null;
            //outStream = new FileOutputStream(saveFile);
            final FileOutputStream outStream = new FileOutputStream(saveFile);
            try {
                outStream.write(message.getBytes());
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //outStream.write("测试写入文件".getBytes());
            //outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取在手机固定目录中的文件的内容
     */
    public static String readTxt(String fileName) {
        BufferedReader bre = null;
        String str = null;
        try {
//            String file2 = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DOWNLOADS) + "/User/user.txt";
            String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/meetingSystem/"+fileName+".txt";
            bre = new BufferedReader(new FileReader(file));//此时获取到的bre就是整个文件的缓存流

            String a;
            while ((str = bre.readLine()) != null) { // 判断最后一行不存在，为空结束循环

                String[] arr = str.split("\\s+");

                for (String ss : arr) {
                    a = arr[0];
                }
                return str;
            }
        } catch (Exception e) {

        }
        return null;
    }

}
