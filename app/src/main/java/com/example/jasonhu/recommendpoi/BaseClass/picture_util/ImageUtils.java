package com.example.jasonhu.recommendpoi.BaseClass.picture_util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    //加载本地图片
    public static void loadLocalSmallPic(Context context, String path, ImageView imageView) {
        //这个地方可以调清晰度，数值越大 清晰度越高
        loadLocalPic(context, path, imageView, 320, 320);
    }

    //加载本地图片
    public static void loadLocalVerySmallPic(Context context, String path, ImageView imageView,int width, int height ) {
        //这个地方可以调清晰度，数值越大 清晰度越高
        loadLocalPic(context, path, imageView, width, height);
    }
    //加入缓存，解决闪烁问题
    public static void loadLocalPic(Context context, String path, ImageView imageView, int width, int height) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Glide.with(context)
                .load(path)
                .override(width, height)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
    //本地原图
    public static void loadLocalPicNoOverride(Context context, String path, ImageView imageView) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        /**
         * Glide 带有内存缓存机制，每次进来会优先从内存中加载图片，然后并从本地，最后从网络
         */
        Glide.with(context)
                .load(path)
                .skipMemoryCache(false)
                .into(imageView);
    }
    public static void loadLocalPicNoOverride_for_see(Context context, String path, ImageView imageView) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        /**
         * Glide 带有内存缓存机制，每次进来会优先从内存中加载图片，然后并从本地，最后从网络
         * 查看更改图片时，不应该调用缓存，应当立即刷新
         */
        Glide.with(context)
                .load(path)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }

    /**
     * 读取一个缩放后的图片，限定图片大小，避免OOM
     * @param uri       图片uri，支持“file://”、“content://”
     * @param maxWidth  最大允许宽度
     * @param maxHeight 最大允许高度
     * @return  返回一个缩放后的Bitmap，失败则返回null
     */
    public static Bitmap decodeUri(Context context, Uri uri, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //只读取图片尺寸
        resolveUri(context, uri, options);

        //计算实际缩放比例
        int scale = 1;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if ((options.outWidth / scale > maxWidth &&
                    options.outWidth / scale > maxWidth * 1.4) ||
                    (options.outHeight / scale > maxHeight &&
                            options.outHeight / scale > maxHeight * 1.4)) {
                scale++;
            } else {
                break;
            }
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;//读取图片内容
        options.inPreferredConfig = Bitmap.Config.RGB_565; //根据情况进行修改
        Bitmap bitmap = null;
        try {
            bitmap = resolveUriForBitmap(context, uri, options);
//            if(bitmap.getHeight()<bitmap.getWidth())
//                return adjustPhotoRotation(bitmap,90);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    /**
     * 获取位图
     * @param path
     * @return
     */
    public static Bitmap getBitmap(String path){
        FileInputStream fis;
        try {
            fis = new FileInputStream(path);
            Bitmap bitmap  = BitmapFactory.decodeStream(fis);
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 解析图片中的文件名，不带后缀
     * @param image_addr 图片地址
     * @return 返回图片名
     */
    public static String parse_picName(String image_addr){
        if(image_addr != null){
            int length = image_addr.split("/").length;
            return image_addr.split("/")[length -1];
        }
        else{
            return null;
        }


    }
    /**
     * @param
     * @param bytes
     * @param opts
     * @return Bitmap
     */
    public static Bitmap getPicFromBytes(byte[] bytes,
                                         BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    private static void resolveUri(Context context, Uri uri, BitmapFactory.Options options) {
        if (uri == null) {
            return;
        }

        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) ||
                ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
                Log.w("resolveUri", "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.w("resolveUri", "Unable to close content: " + uri, e);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            Log.w("resolveUri", "Unable to close content: " + uri);
        } else {
            Log.w("resolveUri", "Unable to close content: " + uri);
        }
    }

    private static Bitmap resolveUriForBitmap(Context context, Uri uri, BitmapFactory.Options options) {
        if (uri == null) {
            return null;
        }

        Bitmap bitmap = null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) ||
                ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
                Log.w("resolveUriForBitmap", "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.w("resolveUriForBitmap", "Unable to close content: " + uri, e);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            Log.w("resolveUriForBitmap", "Unable to close content: " + uri);
        } else {
            Log.w("resolveUriForBitmap", "Unable to close content: " + uri);
        }

        return bitmap;
    }
    /**
     * @param bmp 获取的bitmap数据
     * @param picName 自定义的图片名
     */
    public static void saveBmp2Gallery(String galleryPath,Bitmap bmp, String picName,Context context) {

        String fileName;
        File file = new File(galleryPath);
        //判断文件夹是否存在,如果不存在则创建文件夹
        if (!file.exists()) {
            file.mkdir();
        }
        // 声明输出流
        FileOutputStream outStream = null;
        try {
            // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
            if(picName!= null)
            {
                if (!picName.contains(".jpg"))
                    picName =picName+ ".jpg";
                    file = new File(galleryPath, picName);
            }
            else{
                file = new File(galleryPath);
            }

            // 获得文件相对路径
            fileName = file.toString();
            // 获得输出流，如果文件中有内容，追加内容
            outStream = new FileOutputStream(fileName);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

        } catch (Exception e) {
            e.getStackTrace();
        }finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //通知相册更新
//        MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                bmp, fileName, null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
//        Toast.makeText(context,"头像保存在"+galleryPath+picName,Toast.LENGTH_LONG).show();

    }
    public static  Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {

        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth(), (float) bm.getHeight());

        try {
            return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);

        } catch (OutOfMemoryError ignored) {
        }
        return null;
    }

}
