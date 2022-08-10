## GlideWebpDecoder

Android 使用Glide加载webp动图 使其可以暂停在某一帧

在大佬的 项目上稍微做的修改
Original project [GlideWebpDecoder](https://github.com/zjupure/GlideWebpDecoder)

## Usage


```
Transformation<Bitmap> circleCrop = new CircleCrop();
GlideApp.with(mContext)
        .load(url)
        .optionalTransform(circleCrop).optionalTransform(WebpDrawable.class, new WebpDrawableTransformation(mBitmapTrans, new WebpDrawableTransformation.WebpDrawableCallBack() {
                    @Override
                    public void onAnimationStart(Drawable drawable) {

                    }

                    @Override
                    public void onAnimationEnd(Drawable drawable) {

                    }

                    @Override
                    public void onLoadSuccess(WebpDrawable webpDrawable) {
                        webpDrawable.stopFrameIndex(99);//设置想要暂停在哪一帧
                    }
                }))
        .into(imageView);
```
