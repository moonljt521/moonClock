package com.moon.samples.clockdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * author: moon
 * created on:
 * description:
 */
public class ClockView extends View {
    //View默认最小宽度
    private static final int DEFAULT_MIN_WIDTH = 200;
    //秒针长度
    private float secondPointerLength;
    //分针长度
    private float minutePointerLength;
    //时针长度
    private float hourPointerLength;
    //外圆边框宽度
    private static final float DEFAULT_BORDER_WIDTH = 6f;
    //表盘
    private static final float DEFAULT_PAN_WIDTH = 0f;
    //指针反向超过圆点的长度
    private static final float DEFAULT_POINT_BACK_LENGTH = 40f;
    //长刻度线
    private static final float DEFAULT_LONG_DEGREE_LENGTH = 40f;
    //短刻度线
    private static final float DEFAULT_SHORT_DEGREE_LENGTH = 30f;

    //表盘内圈
    private Paint paintCircle = new Paint();
    //画表盘外圈
    private Paint outCircle = new Paint();
    // 表盘上的阴影
    private Shader shader = new LinearGradient(100, 100, 500, 500, Color.parseColor("#E91E63"),
            Color.parseColor("#2196F3"), Shader.TileMode.CLAMP);
    // 表盘
    private Paint panPaint = new Paint();
    // 刻度
    private Paint paintDegree = new Paint();
    // 时针
    private Paint paintHour = new Paint();
    // 分针
    private Paint paintMinute = new Paint();
    // 秒针
    private Paint paintSecond = new Paint();
    // 圆心处
    private Paint paintCenter = new Paint();

    //数字字号
    private int degressNumberSize = 40;

    public boolean isPause;

    private Timer timer = new Timer();

    // 临时分针的角度，临时变量，用处计算时针便宜的角度
    private float minuteAngle;

    //系统日历
    private Calendar calendar = Calendar.getInstance();

    //只初始化一次时间，刷新的时候+1 ；  优化之前每次刷新view从系统时钟取值的性能瓶颈
    private int second, minute, hour;

    public ClockView(Context context) {
        super(context);
        init();
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void correctTime(Calendar calendar){
        this.calendar = calendar;
        second = this.calendar.get(Calendar.SECOND);
        minute = this.calendar.get(Calendar.MINUTE);
        hour = this.calendar.get(Calendar.HOUR_OF_DAY);
    }


    //启动时钟
    private void init() {
        // 初始化时分秒值，
        second = calendar.get(Calendar.SECOND);
        minute = calendar.get(Calendar.MINUTE);
        hour = calendar.get(Calendar.HOUR_OF_DAY);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isPause) {
                    postInvalidate();
                    refreshTime();
                }
            }
        }, 0, 1000);
    }

    // 更新时间
    private void refreshTime(){
        second ++ ;
        if (second == 60){
            second = 0;
            minute++;
        }

        if (minute == 60){
            minute = 0;
            hour ++ ;
        }

        if (hour == 24){
            hour = 0;
        }


//        Log.i("moon","刷新后的"+ hour + ": " + minute +":" + second);

    }


    /**
     * 暂停或启动
     */
    public void start() {
        if (isPause) {
            isPause = false;
        }
    }

    /**
     * 暂停或启动
     */
    public void pause() {
        if (!isPause) {
            isPause = true;
        }
    }

    /**
     *
     */
    public void adjust() {


    }

    /**
     * 计算时针、分针、秒针的长度
     */
    private void reset() {
        float r = (Math.min(getHeight() / 2, getWidth() / 2) - DEFAULT_BORDER_WIDTH / 2);
        secondPointerLength = r * 0.8f;
        minutePointerLength = r * 0.6f;
        hourPointerLength = r * 0.5f;
    }

    /**
     * 根据角度和长度计算线段的起点和终点的坐标
     *
     * @param angle
     * @param length
     * @return
     */
    private float[] calculatePoint(float angle, float length) {
        float[] points = new float[4];
        if (angle <= 90f) {
            points[0] = -(float) Math.sin(angle * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[1] = (float) Math.cos(angle * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[2] = (float) Math.sin(angle * Math.PI / 180) * length;
            points[3] = -(float) Math.cos(angle * Math.PI / 180) * length;
        } else if (angle <= 180f) {
            points[0] = -(float) Math.cos((angle - 90) * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[1] = -(float) Math.sin((angle - 90) * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[2] = (float) Math.cos((angle - 90) * Math.PI / 180) * length;
            points[3] = (float) Math.sin((angle - 90) * Math.PI / 180) * length;
        } else if (angle <= 270f) {
            points[0] = (float) Math.sin((angle - 180) * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[1] = -(float) Math.cos((angle - 180) * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[2] = -(float) Math.sin((angle - 180) * Math.PI / 180) * length;
            points[3] = (float) Math.cos((angle - 180) * Math.PI / 180) * length;
        } else if (angle <= 360f) {
            points[0] = (float) Math.cos((angle - 270) * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[1] = (float) Math.sin((angle - 270) * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[2] = -(float) Math.cos((angle - 270) * Math.PI / 180) * length;
            points[3] = -(float) Math.sin((angle - 270) * Math.PI / 180) * length;
        }
        return points;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        reset();
        //画表盘内圈
        float borderWidth = DEFAULT_BORDER_WIDTH;
        float r = Math.min(getHeight() / 2, getWidth() / 2) - borderWidth / 2;

        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setColor(Color.BLUE);  // 蓝色外盘边缘
        paintCircle.setAntiAlias(true);
        paintCircle.setStrokeWidth(borderWidth);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, r, paintCircle);


        outCircle.setStyle(Paint.Style.STROKE);
        outCircle.setColor(Color.GREEN);  // 外盘
        outCircle.setAntiAlias(true);
        outCircle.setStrokeWidth(borderWidth);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, r + 10, outCircle);


        //绘制表盘
        float panR = Math.min(getHeight() / 2, getWidth() / 2) - borderWidth / 2;
        panPaint.setStyle(Paint.Style.FILL);
        panPaint.setAntiAlias(true);
        panPaint.setStrokeWidth(DEFAULT_BORDER_WIDTH);

        panPaint.setShader(shader);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, panR, panPaint);


        //画刻度线
        float degreeLength = 0f;
        paintDegree.setColor(Color.BLUE);  // 蓝色刻度
        paintDegree.setAntiAlias(true);
        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) {
                paintDegree.setStrokeWidth(6);
                degreeLength = DEFAULT_LONG_DEGREE_LENGTH;
            } else {
                paintDegree.setStrokeWidth(3);
                degreeLength = DEFAULT_SHORT_DEGREE_LENGTH;
            }
            canvas.drawLine(getWidth() / 2, Math.abs(getHeight() / 2 - r), getWidth() / 2, Math.abs(getHeight() / 2 - r) + degreeLength, paintDegree);
            canvas.rotate(360 / 60, getWidth() / 2, getHeight() / 2);
        }

        //刻度数字
        canvas.translate(getWidth() / 2, getHeight() / 2);
        Paint paintDegreeNumber = new Paint();
        paintDegreeNumber.setTextAlign(Paint.Align.CENTER);
        paintDegreeNumber.setTextSize(degressNumberSize);
        paintDegreeNumber.setFakeBoldText(true);
        for (int i = 0; i < 12; i++) {
            float[] temp = calculatePoint((i + 1) * 30, r - DEFAULT_LONG_DEGREE_LENGTH - degressNumberSize / 2 - 15);
            canvas.drawText((i + 1) + "", temp[2], temp[3] + degressNumberSize / 2 - 6, paintDegreeNumber);
        }

        //画指针
        paintHour.setAntiAlias(true);
        paintHour.setStrokeWidth(15);
        paintMinute.setAntiAlias(true);
        paintMinute.setStrokeWidth(10);
        paintSecond.setColor(Color.RED);// 红色秒针
        paintSecond.setAntiAlias(true);
        paintSecond.setStrokeWidth(5);


        minuteAngle = minute / 60f * 360;

        float[] hourPoints = calculatePoint(hour % 12 / 12f * 360 + minuteAngle * 5 / 60,
                hourPointerLength);
        canvas.drawLine(hourPoints[0], hourPoints[1], hourPoints[2], hourPoints[3], paintHour);

        float[] minutePoints = calculatePoint(minute / 60f * 360, minutePointerLength);
        canvas.drawLine(minutePoints[0], minutePoints[1], minutePoints[2], minutePoints[3], paintMinute);

        // TODO: 17/8/16
        float[] secondPoints = calculatePoint(second / 60f * 360, secondPointerLength);
        canvas.drawLine(secondPoints[0], secondPoints[1], secondPoints[2], secondPoints[3], paintSecond);

        //画圆心
        paintCenter.setColor(Color.WHITE);
        canvas.drawCircle(0, 0, 2, paintCenter);
    }

    /**
     * 当布局为wrap_content时设置默认长宽
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int origin) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(origin);
        int specSize = MeasureSpec.getSize(origin);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
}
