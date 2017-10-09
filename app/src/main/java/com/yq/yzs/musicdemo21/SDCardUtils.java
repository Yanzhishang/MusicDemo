package com.yq.yzs.musicdemo21;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @作者 Yzs
 * 2017/9/5.
 * 描述:  SDCard 操作工具类
 */

public class SDCardUtils {
    //判断SDCard是否挂载
    public static boolean isSDCardMounted() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    //获得SDCard的根目录  如/storage/storaged/
    public static String getSDCardBaseDir() {
        if (isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    //获得SD卡的全部控件的大小（单位是M）
    public static long getSDCardSize() {
        if (isSDCardMounted()) {
            //获得SD卡的根目录路径
            StatFs statFs = new StatFs(getSDCardBaseDir());
            //有多少块
            long blockCount = statFs.getBlockCountLong();
            //每块多大
            long blockSize = statFs.getBlockCountLong();
            return blockCount * blockSize / 1024 / 1024;
        }
        return 0;
    }

    //获得SDCard空闲空间的大小
    public static long getSDCardFreeSize() {
        if (isSDCardMounted()) {
            //获得SD卡的根目录的路径
            StatFs statFs = new StatFs(getSDCardBaseDir());
            //有多少块
            long freeBlockCount = statFs.getBlockCountLong();
            //每块有多大
            long blockSize = statFs.getBlockSizeLong();
            //返回大小
            return freeBlockCount * blockSize / 1024 / 1024;
        }
        return 0;
    }

    //获得SDCard可用空间的大小
    public static long getSDCardAvailSize() {
        if (isSDCardMounted()) {
            //获得SD卡的根目录路径
            StatFs statFs = new StatFs(getSDCardBaseDir());
            //有多少块
            long availBlockCoun = statFs.getBlockCountLong();
            //每块有多少
            long blockSize = statFs.getBlockCountLong();
            //返回大小
            return availBlockCoun * blockSize / 1024 / 1024;
        }
        return 0;
    }

    //往SDCard公有目录下保存文件 (九大公有目录中的一个，具体由type指定) /storage/sdcard/{type}
    public static boolean saveData2SDCardPublicDir(byte[] data, String type, String filename) {
        if (isSDCardMounted()) {
            BufferedOutputStream bos = null;
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + type);
            try {
                bos = new BufferedOutputStream(new FileOutputStream(new File(file, filename)));
                bos.write(data);
                bos.flush();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    //：往SDCard的自定义目录中保存数据 /storage/sdcard/{dir}
    public static boolean saveData2SDCardCustomDir(byte[] data, String dir, String filename) {
        if (isSDCardMounted()) {
            BufferedOutputStream bos = null;

            try {
                File file = new File(getSDCardBaseDir() + File.separator + dir);
                //判断目录是否存在，如果不存在就创建
                if (!file.exists()) {
                    file.mkdirs();
                }
                bos = new BufferedOutputStream(new FileOutputStream(new File(file, filename)));
                bos.write(data);
                bos.flush();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        //关闭流
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    //：往SDCard的私有File目录下保存文件
    public static boolean saveData2SDCardPrivateFileDir(byte[] data, String type, String filename, Context context) {
        if (isSDCardMounted()) {
            BufferedOutputStream bos = null;
            try {
                File file = context.getExternalFilesDir(type);
                if (!file.exists()) {
                    file.mkdirs();
                }
                bos = new BufferedOutputStream(new FileOutputStream(new File(file, filename)));
                bos.write(data);//写数据
                bos.flush();//刷新
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        //关闭流
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    //往SDCard的私有Cache目录下保存文件 /storage/sdcard/Android/data/包名/cache/{filename}
    public static boolean saveData2SDCardPrivateCacheDir(byte[] data, String filename, Context context) {
        if (isSDCardMounted()) {
            BufferedOutputStream bos = null;
            try {
                //获取SDCard的私有Cache目录的路径：：/storage/sdcard/Android/data/包名/cache
                File file = context.getExternalCacheDir();
                bos = new BufferedOutputStream(new FileOutputStream(new File(file, filename)));
                bos.write(data);//写数据
                bos.flush();//刷新
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        //关闭流
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }


    //往SDCard的私有Cache目录下保存图像 /storage/sdcard/Android/data/包名/cache/{filename}
    public static boolean saveBitmap2SDCardPrivateCacheDir(Bitmap bitmap, String filename, Context context) {
        if (isSDCardMounted()) {
            BufferedOutputStream bos = null;
            try {
                File file = context.getExternalCacheDir();
                bos = new BufferedOutputStream(new FileOutputStream(new File(file, filename)));
                //把bitmap 写进bos流中
                if (filename.endsWith(".png") || filename.endsWith(".PNG")) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                } else if (filename.endsWith(".jpg") || filename.endsWith(".JPG")) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                }
                bos.flush();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        //关闭流
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    //：从SDCard读取指定文件 /storage/sdcard/{filePath}
    public static byte[] loadFileFromSDCard(String filePath) {
        if (isSDCardMounted()) {
            BufferedInputStream bis = null;
            try {
                File file = new File(getSDCardBaseDir());
                bis = new BufferedInputStream(new FileInputStream(new File(file, filePath)));
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while (true) {
                    int hasRead = bis.read(buffer);
                    if (hasRead < 0) {
                        break;
                    }
                    baos.write(buffer, 0, hasRead);
                }
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    //：从SDCard读取Bitmap并返回 /storage/sdcard/{filePath}
    public static Bitmap loadBitmapFromSDCard(String filePath) {
        if (isSDCardMounted()) {
            BufferedInputStream bis = null;
            try {
                File file = new File(getSDCardBaseDir());
                bis = new BufferedInputStream(new FileInputStream(new File(file, filePath)));
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while (true) {
                    int hasRead = bis.read(buffer);
                    if (hasRead < 0) {
                        break;
                    }
                    baos.write(buffer, 0, hasRead);
                }
                byte[] data = baos.toByteArray();
                return BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    //：获取SD卡公有目录路径   /storage/sdcard/{type}
    public static String getSDCardPublicDir(String type) {
        if (isSDCardMounted()) {
            String path = Environment.getExternalStorageDirectory() + File.separator + type;
            return path;
        }
        return null;
    }

    //：获取SDCard私有Cache目录路径  /storage/sdcard/Android/data/包名/cache/
    public static String getSDCardPrivateCacheDir(Context context) {
        if (isSDCardMounted()) {
            String path = context.getExternalCacheDir().getAbsolutePath();
            return path;
        }
        return null;
    }

    //：获取SDCard私有File目录路径 /storage/sdcard/Android/data/包名/files/{type}
    public static String getSDCardPrivateFilesDir(Context context, String type) {
        if (isSDCardMounted()) {
            String path = context.getExternalFilesDir(type).getAbsolutePath();
            return path;
        }
        return null;
    }

    //：判断一个文件是否存在/storage/sdcard/{type}
    public static boolean isFileExists(String filePath) {
        if (isSDCardMounted()) {
            File file = new File(getSDCardBaseDir() + File.separator + filePath);
            return file.exists();
        }
        return false;
    }

    //：删除一个文件 /storage/sdcard/{type}
    public static boolean removeFileFromSDCard(String filePath) {
        if (isSDCardMounted()) {
            if (!isFileExists(filePath)) {
                return false;
            }
            File file = new File(getSDCardBaseDir() + File.separator + filePath);
            return file.delete();
        }
        return false;
    }
}
