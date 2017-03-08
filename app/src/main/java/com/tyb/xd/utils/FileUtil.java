package com.tyb.xd.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.tyb.xd.R;
import com.tyb.xd.activity.XDApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangpeiyu on 2016/7/29.
 */
public class FileUtil {

    public static String FILE_IMAGE = ".png";

    public static Bitmap getBitmapFormPath(Context context, String temppath) {
        try {
            FileInputStream fis = new FileInputStream(temppath);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //默认返回XD照片
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.login_logo);
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 写入文件，
     * 需要制定文件的类型
     * 文件的名字
     *
     * @param context
     * @param userPhone
     * @param file
     * @param fileName
     * @param fileType
     */
    public static void writeFile(Context context, String userPhone, File file, String fileName, String fileType) {

        if (userPhone != null && (!TextUtils.isEmpty(userPhone))) //文件的目录
        {
            String path_directory = context.getExternalFilesDir(null).getAbsolutePath() + "/" + userPhone;
            File file_directory = new File(path_directory);
            if (!file_directory.exists()) {
                file_directory.mkdir();//创建目录
            }
            if (fileName != null && fileType != null && (!TextUtils.isEmpty(fileName)) && (!TextUtils.isEmpty(fileType))) {
                String path = path_directory + "/" + fileName + fileType;
                File OldFile = new File(path);
                if (OldFile.exists()) {
                    //如果原来已经存在了，则会将原来的删除
                    OldFile.delete();
                    OldFile = new File(path);//这时候的OldFile已经是新的了
                }
                if (file != null) {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        FileOutputStream fileOutputStream = new FileOutputStream(OldFile);
                        byte[] buffer = new byte[1024];
                        int lenght = 0;
                        while ((lenght = fileInputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, lenght);
                        }
                        fileInputStream.close();
                        fileOutputStream.flush();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
