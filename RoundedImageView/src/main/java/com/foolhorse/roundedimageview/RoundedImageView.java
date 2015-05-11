package com.foolhorse.roundedimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by ID_MARR on 2015/1/4.
 */
public class RoundedImageView extends ImageView {

    // TODO
    private int mShape;
    private static final int SHAPE_CIRCLE = 0;
    private static final int SHAPE_ROUND = 1;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;

    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();
    private BitmapShader mBitmapShader;


    private Bitmap mBitmap;

    private int mDrawableRadius;
    private int mBorderRadius;

    public RoundedImageView(Context context) {
        super(context);

        init();
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.RoundedImageView_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.RoundedImageView_border_color, DEFAULT_BORDER_COLOR);

        a.recycle();

        init();
    }


    @Override
    public void setScaleType(ImageView.ScaleType scaleType) {
        if (scaleType != ScaleType.CENTER_CROP) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Log.e("MARR", "drawableStateChanged");
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        Log.e("MARR","setImageBitmap");
        mBitmap = bm;
        invalidate();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        Log.e("MARR","setImageDrawable");
        mBitmap = ImageUtils.getBitmapFromDrawable(drawable);
        invalidate();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        Log.e("MARR","setImageResource");
        mBitmap = ImageUtils.getBitmapFromDrawable(getDrawable());
        invalidate();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        Log.e("MARR","setImageURI");
        mBitmap = ImageUtils.getBitmapFromDrawable(getDrawable());
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e("MARR", "onSizeChanged");
//        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("MARR", "onDraw");
        if (getDrawable() == null) {
            return;
        }
        setup();
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mBitmapPaint);
        if (mBorderWidth != 0) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);
        }
    }



    private void init() {
        super.setScaleType(ScaleType.CENTER_CROP);
        Log.e("MARR", "init");
    }

    private void setup() {
        if (mBitmap == null) {
            return;
        }
        setupBorder();
        setupBitmap();

//        不使用Xfermode的方式
//        mBorderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        invalidate();
    }

    private void setupBorder(){
        Rect viewRect =  new Rect(0, 0, RoundedImageView.this.getWidth(), RoundedImageView.this.getHeight());
        mBorderRadius = Math.min((viewRect.height() - mBorderWidth) / 2, (viewRect.width() - mBorderWidth) / 2);

        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
    }

    private void setupBitmap() {
        Rect viewRect =  new Rect(0, 0, RoundedImageView.this.getWidth(), RoundedImageView.this.getHeight());
        Rect drawableRect =  new Rect(mBorderWidth, mBorderWidth, viewRect.width() - mBorderWidth, viewRect.height() - mBorderWidth);
        mDrawableRadius = Math.min(drawableRect.height() / 2, drawableRect.width() / 2);

        setupBitmapShader(drawableRect);
        mBitmapPaint.setShader(mBitmapShader);
        mBitmapPaint.setAntiAlias(true);
    }

    private void setupBitmapShader(Rect drawableRect) {
        switch (getScaleType()){
            case CENTER_CROP:
                setupShaderCenterCrop(drawableRect);
                break ;
        }
    }

    private void setupShaderCenterCrop(Rect drawableRect) {
//        mBitmap = Bitmap.createScaledBitmap(mBitmap, min, min, false);

        Matrix shaderMatrix = new Matrix();
        shaderMatrix.set(null);

        int side = Math.min(drawableRect.height(), drawableRect.width()) ;
        float scale = Math.max( (float) side/(float) mBitmap.getWidth(),  (float) side/(float) mBitmap.getHeight());
        float dx = (drawableRect.width() -  mBitmap.getWidth() * scale) / 2;
        float dy = (drawableRect.height() - mBitmap.getHeight() * scale) / 2;

        shaderMatrix.setScale(scale, scale);
        shaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) + mBorderWidth);

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapShader.setLocalMatrix(shaderMatrix);
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }
        mBorderColor = borderColor;
        Log.e("MARR","setBorderColor");
        invalidate();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }
        mBorderWidth = borderWidth;
        Log.e("MARR","setBorderWidth");
        invalidate();
    }
}
