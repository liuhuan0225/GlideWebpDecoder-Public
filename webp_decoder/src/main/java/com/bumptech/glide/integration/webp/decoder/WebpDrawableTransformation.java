package com.bumptech.glide.integration.webp.decoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.util.Preconditions;

import java.security.MessageDigest;

/**
 * An {@link com.bumptech.glide.load.Transformation} that wraps a transformation for a
 * {@link Bitmap} and can apply it to every frame of any
 * {@link com.bumptech.glide.integration.webp.decoder.WebpDrawable}.
 *
 * @author liuchun
 */
public class WebpDrawableTransformation implements Transformation<WebpDrawable> {
    private final Transformation<Bitmap> wrapped;
    private WebpDrawableCallBack callBack;

    public WebpDrawableTransformation(Transformation<Bitmap> wrapped) {
        this.wrapped = Preconditions.checkNotNull(wrapped);
    }
    public WebpDrawableTransformation(Transformation<Bitmap> wrapped,WebpDrawableCallBack callBack) {
        this.wrapped = Preconditions.checkNotNull(wrapped);
        this.callBack = callBack;
    }

    @Override
    public Resource<WebpDrawable> transform(Context context, Resource<WebpDrawable> resource, int outWidth, int outHeight) {
        WebpDrawable drawable = resource.get();

        // The drawable needs to be initialized with the correct width and height in order for a view
        // displaying it to end up with the right dimensions. Since our transformations may arbitrarily
        // modify the dimensions of our GIF, here we create a stand in for a frame and pass it to the
        // transformation to see what the final transformed dimensions will be so that our drawable can
        // report the correct intrinsic width and height.
        if (callBack != null){
            callBack.onLoadSuccess(drawable);
            drawable.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                @Override
                public void onAnimationStart(Drawable drawable) {
                    super.onAnimationStart(drawable);
                    callBack.onAnimationStart(drawable);
                }

                @Override
                public void onAnimationEnd(Drawable drawable) {
                    super.onAnimationEnd(drawable);
                    callBack.onAnimationEnd(drawable);
                }
            });
        }
        BitmapPool bitmapPool = Glide.get(context).getBitmapPool();
        Bitmap firstFrame = drawable.getFirstFrame();
        Resource<Bitmap> bitmapResource = new BitmapResource(firstFrame, bitmapPool);
        Resource<Bitmap> transformed = wrapped.transform(context, bitmapResource, outWidth, outHeight);
        if (!bitmapResource.equals(transformed)) {
            bitmapResource.recycle();
        }
        Bitmap transformedFrame = transformed.get();

        drawable.setFrameTransformation(wrapped, transformedFrame);
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WebpDrawableTransformation) {
            WebpDrawableTransformation other = (WebpDrawableTransformation) o;
            return wrapped.equals(other.wrapped);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        wrapped.updateDiskCacheKey(messageDigest);
    }

    public interface WebpDrawableCallBack{
        public void onAnimationStart(Drawable drawable);
        public void onAnimationEnd(Drawable drawable);
        public void onLoadSuccess(WebpDrawable webpDrawable);
    }
}
