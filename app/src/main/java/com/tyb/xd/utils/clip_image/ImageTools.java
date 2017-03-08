package com.tyb.xd.utils.clip_image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by peiyuWang on 2016/6/27.
 */
public class ImageTools {

    public static void savePhotoToSDCard(Bitmap bitmap, String path, String photoName) {
        if (checkSDCardIsAvailable()) {
            File photo = new File(path, photoName);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photo);
                if (bitmap != null) {
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveBitmapToSDCard(Bitmap bitmap, String path) {

        if (checkSDCardIsAvailable()) {
            File file = new File(path);
            if (!file.exists())
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                if (bitmap != null) {
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        }
    }

    public static Bitmap convertToBitmap(String path, int w, int h) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置为true，只获取图片的大小
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //返回为空，获取的大小在options中
        BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        int height = options.outHeight;
        float scaleWidth = 1.0f, scaleHeight = 1.0f;
        /*******************************dailijie*************************************/
        if (width > w || height > h) {
            scaleWidth = (float) width / w;
            scaleHeight = (float) height / h;
        }
        float scale = Math.max(scaleHeight, scaleWidth);
        //设置不单单返回大小
        options.inJustDecodeBounds = false;
        options.inSampleSize = (int) scale;
        WeakReference<Bitmap> weakReference =
                new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, options));
        Bitmap bitmap = Bitmap.createBitmap(weakReference.get(), 0, 0, weakReference.get().getWidth(),
                weakReference.get().getHeight(), null, true);
        if (bitmap != null) {
            return bitmap;
        } else
            return null;
    }

    public static boolean checkSDCardIsAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
