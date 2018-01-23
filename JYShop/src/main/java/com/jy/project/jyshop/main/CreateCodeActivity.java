package com.jy.project.jyshop.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.jy.project.jyshop.R;
import com.jy.project.jyshop.utils.T;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 用于创建和扫描二维码
 */
public class CreateCodeActivity extends Activity {

    IntentIntegrator integrator;

    /**
     * 将要生成二维码的内容
     */
    @ViewInject(R.id.code_edittext)
    private EditText codeEdit;

    /**
     * 生成二维码代码
     */
    private Button twoCodeBtn;
    /**
     * 用于展示生成二维码的imageView
     */
    @ViewInject(R.id.code_img)
    private ImageView codeImg;

    /**
     * 生成一维码按钮
     */
    private Button oneCodeBtn;

    /**
     * 界面的初始化事件
     *
     * @param savedInstanceState Bundle对象
     */

    @ViewInject(R.id.btn_code)
    Button ones;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_code);
        x.view().inject(this);//注解绑定
//        然后在点击事件中设置属性
        integrator = new IntentIntegrator(CreateCodeActivity.this);
        // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
//        integrator Scan();
        ones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T.showShort("one");

                //开启扫描界面
                Intent intent = new Intent(CreateCodeActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
            }
        });

    }



//Bitmap bitmap = EncodingHandler.createQRCode(str, 300);
//iv.setImageBitmap(bitmap);

    @Event(value =R.id.code_btn,type = View.OnClickListener.class)
    private void OnClickTestTwo(View view){
        T.showShort("two");
        String str = codeEdit.getText().toString().trim();
        Bitmap bmp = null;
        try {
            if (str != null && !"".equals(str)) {
                bmp = CreateTwoDCode(str);
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if (bmp != null) {
            codeImg.setImageBitmap(bmp);
        }
    }


    /**
     * 将指定的内容生成成二维码
     *
     * @param content 将要生成二维码的内容
     * @return 返回生成好的二维码事件
     * @throws WriterException WriterException异常
     */
    public Bitmap CreateTwoDCode(String content) throws WriterException {
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, 300, 300);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(0, resultCode, data);
        if (scanResult != null) {
            String result = scanResult.getContents();
            Log.e("HYN", result);
            Toast.makeText(CreateCodeActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }
}