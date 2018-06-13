package com.ct.shadeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by ChenTao on 2018/4/17.
 */

public class ShadeImageView extends ImageView {

    private ViewTreeObserver viewTreeObserver = null;
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener = null;
    private Bitmap bm = null;
    private Context context;
    private int screenHeight = 0;//屏幕高度

    private float scale = 1;//图片将要的缩放比例,用于画圆时中心点的实际位置

    private float oriYAbs = 0,oriXAbs = 0,oriRAbs = 0;//记录比例放大后的圆的圆点和半径，用于判断点击位置

    public final static int CLICK_SPACE = 0;//点击空白处
    public final static int CLICK_RANGE = 1;//点击在圆上
    private int where_click = 0;//用于返回点击在哪里

    public ShadeImageView(Context context) {
        super(context);
        this.context = context;
        this.setScaleType(ScaleType.CENTER_CROP);
    }

    public ShadeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setScaleType(ScaleType.CENTER_CROP);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        // TODO Auto-generated method stub
        super.setImageBitmap(bm);//会调用setImageDrawable函数
    }
    @Override
    public void setImageResource(int resId) {
        // TODO Auto-generated method stub
        Drawable drawable = ContextCompat.getDrawable(context,resId);
        setImageDrawable(drawable);//会调用setImageDrawable函数
    }
    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Drawable drawable = getDrawable();
        if(drawable!=null)
        {
            bm = drawableToBitmap(drawable);//获取设置的图片
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenHeight = dm.heightPixels;

        viewTreeObserver = getViewTreeObserver();
        mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
               drawLocation();
            }
        };
        viewTreeObserver.addOnScrollChangedListener(mOnScrollChangedListener);
        //view加载完成调用
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                drawLocation();//画出初始的时候样子
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(bm!=null)
         scale = getScale(bm,getMeasuredWidth(),getMeasuredHeight());

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                float x = e.getX();
                float y = e.getY();
                where_click = Math.sqrt((x-oriXAbs)*(x-oriXAbs)+(y-oriYAbs)*(y-oriYAbs))>oriRAbs?0:1;//确定点击范围
                break;
        }
        return super.onTouchEvent(e);//继续往下传
    }

    @Override
    protected void onDetachedFromWindow() {

        viewTreeObserver.removeOnScrollChangedListener(mOnScrollChangedListener);
        super.onDetachedFromWindow();
    }

    public int getWhere_click() {
        return where_click;
    }

    private void drawLocation()//根据位置进行画图
    {
        if(bm!=null)
        {
            int[] location = new int[2];
            getLocationOnScreen(location);//获取当前view左上点在屏幕中的坐标
            float locationY = (float) location[1];
            //oriXAbs ,oriYAbs为图片按屏幕比例放大后的抽象原点，实际原点需要比例缩放
            oriXAbs = (bm.getWidth()*scale-getMeasuredWidth())/2+getMeasuredHeight()/4;//由于绘制的时候是根据画布来进行坐标计算，
            // 所以需要计算圆的原点。
            oriYAbs = (bm.getHeight()*scale-getMeasuredHeight())/2+getMeasuredHeight()/4;
            //oriX,oriY实际在画布里的原点
            int oriX = (int) (oriXAbs/scale);
            int oriY = (int) (oriYAbs/scale);

            //计算半径
            //中心位置
            float centerY = screenHeight/2;
            float startY = screenHeight/2-getMeasuredHeight();
            float startY2 = screenHeight/2+getMeasuredHeight();
            //计算当控件滑到中心以下时，圆全部覆盖后的半径。
            float endX = getMeasuredHeight()*3/4;
            float endY = getMeasuredWidth() - getMeasuredHeight()/4;
            float endR = (float) Math.sqrt(endX*endX+endY*endY)/scale;
            if(locationY<centerY && locationY>startY) {
                //开始绘画位置
                int r = (int) ((locationY-startY)*endR/(centerY-startY));
                oriRAbs = r*scale;//记录控件你显示的半径范围，用于实际点击
                Bitmap shadeBm = getShadeBitmap(bm, r, oriX, oriY);
                setImageBitmap(shadeBm);
            }
            else if(locationY<=startY)
            {
                Bitmap shadeBm = getShadeBitmap(bm, 0, oriX, oriY);
                oriRAbs = 0;//记录控件你显示的半径范围，用于实际点击
                setImageBitmap(shadeBm);
            }
            else
            {
                Bitmap shadeBm = getShadeBitmap(bm, (int) endR, oriX, oriY);
                oriRAbs = endR*scale;//记录控件你显示的半径范围，用于实际点击
                setImageBitmap(shadeBm);
            }
        }
    }

    public Bitmap getShadeBitmap(Bitmap bitmap, int r, int oriX, int oriY) {//获取遮罩图片,r为遮罩圆的半径,上滑时,oriX为原点的xy值，这是相对于控件的

        Paint paint = new Paint(); //创建笔
        paint.setAntiAlias(true);//给Paint加上抗锯齿标志

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output); //创建一个画布,以画布的坐标为标准
        Rect rect = new Rect(oriX-r, oriY-r, r+oriX, r+oriY); //构造一个矩形，前面两个参数代表的是左上点
        RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF, r, r, paint);
        canvas.save();

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//取两层绘制交集。显示上层。遮罩，先画的在上面，后画的在下面
        //要先画圆再设置

        Rect rect2 = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); //构造一个矩形
        canvas.drawBitmap(bitmap, rect2, rect2, paint);//第一个Rect 代表要绘制的bitmap 区域，第二个 Rect 代表的是要将bitmap 绘制在画布的什么地方，绘制的时候不会改变图片本身

        return output;
    }

    /**
     * Drawable转化为Bitmap
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();//获得图片实际宽度
        int height = drawable.getIntrinsicHeight();//获得图片实际高度
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);//创建bitmap，用于然后创建它的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    private float getScale(Bitmap bitmap,float vw,float vh)//获得，图片centerCrop时将获得的缩放比例
    {
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        float scale = 1;
        scale = vw/width>vh/height?vw/width:vh/height;
        return scale;
    }
}
