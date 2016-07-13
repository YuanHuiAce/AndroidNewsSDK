package com.news.yazhidao.widget.digger;//package com.news.yazhidao.widget.digger;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.RectF;
//import android.util.AttributeSet;
//import android.widget.ImageView;
//
//import com.news.yazhidao.R;
//
//
///**
// * Created by fengjigang on 15/7/20.
// */
//public class DigCircleImage extends ImageView {
//    private final Paint paint;
//    private int spinSpeed = 4;
//    private int progress = 0;
//    private boolean isPinning ;
//
//    public DigCircleImage(Context context) {
//        this(context, null);
//    }
//
//    public DigCircleImage(Context context, AttributeSet attrs) {
//        this(context, attrs, -1);
//    }
//
//    public DigCircleImage(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        this.paint = new Paint();
//        this.paint.setAntiAlias(true); //消除锯齿
//        this.paint.setStyle(Paint.Style.STROKE); //绘制空心圆
//    }
//
//    @Override
//    public void setBackgroundColor(int color) {
//        super.setBackgroundColor(color);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//
//        super.onDraw(canvas);
//        this.paint.setStrokeWidth(2);
//        this.paint.setColor(getResources().getColor(R.color.dig_title_doing));
//        if(isPinning){
//            canvas.drawArc(new RectF(2, 2, getWidth() - 2, getHeight() - 2), progress , 330, false, paint);
//            scheduleRedraw();
//        }
//    }
//
//    private void scheduleRedraw() {
//        progress += spinSpeed;
//        if (progress > 360) {
//            progress = 0;
//        }
//        postInvalidateDelayed(0);
//    }
//
//    /**
//     * 开始动画
//     */
//    public void startPin(){
//        this.isPinning = true;
//        postInvalidate();
//    }
//
//    /**
//     * 停止动画
//     */
//    public void stopPinning(){
//        this.isPinning = false;
//        postInvalidate();
//    }
//}
