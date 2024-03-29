package com.news.yazhidao.adapter.abslistview;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.news.yazhidao.R;
import com.news.yazhidao.application.QiDianApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.ThemeManager;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.TextViewExtend;

/**
 * Created by fengjigang on 16/4/14.
 * ListView GridView 通用ViewHolder
 */
public class CommonViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private Context mContext;
    private View mConvertView;
    private int mLayoutId;
    private SharedPreferences mSharedPreferences;

    public CommonViewHolder(Context mContext, View mConvertView, ViewGroup mViewGroup, int mPosition) {
        this.mContext = mContext;
        this.mConvertView = mConvertView;
        this.mPosition = mPosition;
        this.mViews = new SparseArray<>();
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mConvertView.setTag(this);
    }

    public static CommonViewHolder get(Context mContext, View mConvertView, ViewGroup mViewGroup, int mLayoutId, int mPosition) {
        if (mConvertView == null) {
            View itemView = LayoutInflater.from(mContext).inflate(mLayoutId, mViewGroup, false);
            CommonViewHolder viewHolder = new CommonViewHolder(mContext, itemView, mViewGroup, mPosition);
            viewHolder.mLayoutId = mLayoutId;
            return viewHolder;
        } else {
            CommonViewHolder viewHolder = (CommonViewHolder) mConvertView.getTag();
            viewHolder.mPosition = mPosition;
            return viewHolder;
        }
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public ImageView getImageView(int ViewID) {
        ImageView image = getView(ViewID);
        return image;
    }

    public void setTextViewText(int ViewID, String content) {
        TextView text = getView(ViewID);
        text.setText(content);
    }

    public void setTextViewTextBackgroundResource(int ViewID, int resource) {
        TextView text = getView(ViewID);
        text.setBackgroundResource(resource);
    }

    public void setTextViewExtendText(int ViewID, String content) {
        TextViewExtend text = getView(ViewID);
        text.setText(content);
    }

    public void setTextViewExtendTextandTextSice(int ViewID, String content) {
        TextViewExtend text = getView(ViewID);
        text.setText(content);
        text.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
    }

    public void setTextViewExtendTextColor(int ViewID, int color) {
        TextViewExtend text = getView(ViewID);
        text.setTextColor(mContext.getResources().getColor(color));
    }

    public void setTextViewExtendTextBackground(int ViewID, int color) {
        TextViewExtend text = getView(ViewID);
        text.setBackgroundColor(mContext.getResources().getColor(color));
    }

    public void setTextViewExtendTextBackgroundResource(int ViewID, int resource) {
        TextViewExtend text = getView(ViewID);
        text.setBackgroundResource(resource);
    }


    public View getConvertView() {
        return mConvertView;
    }

    public int getLayoutId() {
        return mLayoutId;
    }

    public void setGlideDraweeViewURI(int draweeView, String strImg) {
        ImageView imageView = getView(draweeView);
        if (!TextUtil.isEmptyString(strImg)) {
            imageView.setImageURI(Uri.parse(strImg));
//            imageView.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0.4F));
        }
    }

    public void setGlideDraweeViewURI(int draweeView, String strImg, int width, int height, int rType) {
        ImageView imageView = getView(draweeView);
        if (!TextUtil.isEmptyString(strImg)) {
            if (SharedPreManager.mInstance(mContext).getBoolean(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES)) {
                imageView.setImageResource(R.drawable.bg_load_default_small);
//                imageView.setImageURI(Uri.parse("res://com.news.yazhidao/" + R.drawable.bg_load_default_small));
//                Glide.with(mContext).load(R.drawable.bg_load_default_small).into(imageView);
            } else {
                Uri uri;
                if (rType != 3 && rType != 4) {
                    String img = strImg.replace("bdp-", "pro-");
                    uri = Uri.parse(img + "@1e_1c_0o_0l_100sh_" + height + "h_" + width + "w_95q.jpg");
                } else {
                    uri = Uri.parse(strImg);
                }
                if (ThemeManager.getThemeMode() == ThemeManager.ThemeMode.NIGHT) {
                    imageView.setAlpha(0.5f);
                } else {
                    imageView.setAlpha(1.0f);
                }
                Glide.with(mContext).load(uri).placeholder(R.drawable.bg_load_default_small).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }
        }
    }


    public static class GlideTransform extends BitmapTransformation {

        private float radius = 1f;

        public GlideTransform(Context context, int px) {
            super(context);
            this.radius = px;
        }


        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
//            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(QiDianApplication.getAppContext().getResources().getColor(R.color.new_color4));
            paint.setStrokeWidth(radius);
            RectF rectF = new RectF(0, 0, source.getWidth(), source.getHeight());
            canvas.drawRect(rectF, paint);

            Paint paint1 = new Paint();
            paint1.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            RectF rectF1 = new RectF(radius / 2.0f, radius / 2.0f, source.getWidth() - radius / 2.0f, source.getHeight() - radius / 2.0f);
            canvas.drawRect(rectF1, paint1);
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }

    public static class GlideRoundTransform extends BitmapTransformation {
        private float radius = 1f;

        public GlideRoundTransform(Context context) {
            this(context, 4);
        }

        public GlideRoundTransform(Context context, int dp) {
            super(context);
            this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName() + Math.round(radius);
        }
    }

    public static class GlideCircleTransform extends BitmapTransformation {

        private Paint mBorderPaint;
        private float mBorderWidth;

        public GlideCircleTransform(Context context) {
            super(context);
        }

        public GlideCircleTransform(Context context, int borderWidth, int borderColor) {
            super(context);
            mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;

            mBorderPaint = new Paint();
            mBorderPaint.setDither(true);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(borderColor);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(mBorderWidth);
        }


        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            if (mBorderPaint != null) {
                float borderRadius = r - mBorderWidth / 2;
                canvas.drawCircle(r, r, borderRadius, mBorderPaint);
            }
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }


//    public void setIsShowImagesSimpleDraweeViewURI(Context mContext,int draweeView, String strImg) {
//        SimpleDraweeView imageView = (SimpleDraweeView)getView(draweeView);
//        if (!TextUtil.isEmptyString(strImg)) {
//            //梁帅：判断图片是不是  不显示
//            if(SharedPreManager.getBoolean(CommonConstant.FILE_USER,CommonConstant.TYPE_SHOWIMAGES)){
//                imageView.setImageURI(Uri.parse("res://com.news.yazhidao/" + R.drawable.bg_load_default_small));
//            }else{
//                imageView.setImageURI(Uri.parse(strImg));
//            }
//
//
//            imageView.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0.4F));
//        }
//    }
}
