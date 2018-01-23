package com.shows.getmoney.hanzicode;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yixinke.shows.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by junweiliu on 16/5/9.
 */
public class CodeView extends ImageView {
    /**
     * 干扰项
     */
    private final char[] CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    /**
     * 汉字画笔
     */
    private Paint hzPaint;
    /**
     * 干扰项画笔
     */
    private Paint dbPaint;
    /**
     * 汉字画笔颜色
     */
    private int hzColor;
    /**
     * 干扰项画笔颜色
     */
    private int dbColor;
    /**
     * 干扰项的个数,默认30个
     */
    private int DEFAULT_DBSIZE;
    /**
     * 默认画笔颜色
     */
    private int DEFAULT_DBCOLOR, DEFAULT_HZCOLOR;
    /**
     * 干扰项随机生成的位置
     */
    private float dbRandomX, dbRandomY;
    /**
     * 验证码文字
     */
    private List<String> codeList;
    /**
     * 随机
     */
    private Random random = new Random();
    /**
     * 矩形区域
     */
    private Rect mBounds;
    /**
     * 干扰文字大小
     */
    private int dbTextSize;
    /**
     * 验证码文字大小
     */
    private int hzTextSize;
    /**
     * 画布旋转度数,默认为6
     */
    private int rotate;


    public CodeView(Context context) {
        this(context, null);
    }

    public CodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    ;

    public CodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 获取自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CodeView);
        DEFAULT_DBSIZE = ta.getInteger(R.styleable.CodeView_disturbSize, 30);
        dbTextSize = DisplayUtil.sp2px(context, ta.getDimensionPixelSize(R.styleable.CodeView_disturbTextSize, 10));
        hzTextSize = DisplayUtil.sp2px(context, ta.getDimensionPixelSize(R.styleable.CodeView_codeTextSize, 20));
        DEFAULT_DBCOLOR = ta.getColor(R.styleable.CodeView_disturbTextColor, 0);
        DEFAULT_HZCOLOR = ta.getColor(R.styleable.CodeView_codeTextColor, 0);
        rotate = ta.getInteger(R.styleable.CodeView_rotate, 6);
        init();

    }

    /**
     * 初始化
     */
    private void init() {
        // 验证的文字颜色一致,只需要生成一次
        if (0 == DEFAULT_HZCOLOR) {
            hzColor = Util.randomDarkColor();
        } else {
            hzColor = DEFAULT_HZCOLOR;
        }
        mBounds = new Rect();
        codeList = new ArrayList<String>();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        Bitmap bp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bp);
        String dbCode = createDBCode();
        // 绘制干扰项
        for (int i = 0; i < dbCode.length(); i++) {
            randomDBStyele();
            randomDBPosition();
            c.drawText(dbCode.charAt(i) + "", dbRandomX, dbRandomY, dbPaint);
        }
        canvas.drawBitmap(bp, 0, 0, dbPaint);
        // 绘制验证文字
        if (null != codeList && codeList.size() > 0) {
            for (int i = 0; i < codeList.size(); i++) {
                randomHZStyle();
                canvas.save();
                canvas.rotate((int) (Math.floor(Math.random() * rotate + 1)
                        - Math.floor(Math.random() * rotate * 2 + 1)));
                canvas.drawText(codeList.get(i), mBounds.width() * (i + 1), getHeight() / 2 + mBounds.height() / 2 + rotate, hzPaint);
                canvas.restore();
            }
        }
//        super.onDraw(canvas);
    }

    /**
     * 重置
     */
    public void reSet(List<String> codes) {
        // 重置汉字画笔颜色
        if (0 == DEFAULT_HZCOLOR) {
            hzColor = Util.randomDarkColor();
        } else {
            hzColor = DEFAULT_HZCOLOR;
        }
        this.codeList.clear();
        this.codeList = codes;
        invalidate();
    }

    /**
     * 设置验证码文字
     *
     * @param codes
     */
    public void setCode(List<String> codes) {
        this.codeList = codes;
        invalidate();
    }

    /**
     * 随机生成汉字画笔样式
     */
    private void randomHZStyle() {
        hzPaint = new Paint();
//        hzPaint.setAntiAlias(true);
        hzPaint.setColor(hzColor);
        hzPaint.setFakeBoldText(random.nextBoolean());
        hzPaint.setTextSize(hzTextSize);
        hzPaint.getTextBounds("一", 0, "一".length(), mBounds);
    }


    /**
     * 随机生成干扰项画笔样式
     */
    private void randomDBStyele() {
        if (0 == DEFAULT_DBCOLOR) {
            dbColor = Util.randomDarkColor();
        } else {
            dbColor = DEFAULT_DBCOLOR;
        }
        dbPaint = new Paint();
//        dbPaint.setAntiAlias(true);
        dbPaint.setColor(dbColor);
        dbPaint.setTextSize(dbTextSize);
    }

    /**
     * 随机生成干扰项的显示位置
     */
    private void randomDBPosition() {
        dbRandomX = (float) Math.random() * getWidth();
        dbRandomY = (float) Math.random() * getHeight();
    }

    /**
     * 创建干扰项
     *
     * @return
     */
    private String createDBCode() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < DEFAULT_DBSIZE; i++) {
            buffer.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return buffer.toString();
    }

}
