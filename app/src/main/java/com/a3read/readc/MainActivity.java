package com.a3read.readc;


import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private static final String TAG = "MainActivity";
    private static final String TAG1 = "MainActivity";

    private List<String> class001List = new ArrayList<String>();
    private List<String> class002List = new ArrayList<String>();
    private List<String> class003List = new ArrayList<String>();
    private List<String> class004List = new ArrayList<String>();
    private List<String> class005List = new ArrayList<String>();
    private List<String> class006List = new ArrayList<String>();

    // 控件
    private Button butPre, butNext;
    private TextView textView;
    private ImageView imgRing;

    // 控制当前阅读列表
    private List<String> list = new ArrayList<String>();
    private int index = 0;

    // 文本朗读引擎
    private TextToSpeech textToSpeech; // TTS对象

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 控件
        initControl();
        // TTS引擎
        initTTS();
        // 加载中文文件
        initRaw();
    }

    public void initRaw() {
        InputStream inputStreamClass001 = getResources().openRawResource(R.raw.class001);
        putListByStr(class001List, getString(inputStreamClass001));
        InputStream inputStreamClass002 = getResources().openRawResource(R.raw.class002);
        putListByStr(class002List, getString(inputStreamClass002));
        InputStream inputStreamClass003 = getResources().openRawResource(R.raw.class003);
        putListByStr(class003List, getString(inputStreamClass003));
        InputStream inputStreamClass004 = getResources().openRawResource(R.raw.class004);
        putListByStr(class004List, getString(inputStreamClass004));
        InputStream inputStreamClass005 = getResources().openRawResource(R.raw.class005);
        putListByStr(class005List, getString(inputStreamClass005));
        InputStream inputStreamClass006 = getResources().openRawResource(R.raw.class006);
        putListByStr(class006List, getString(inputStreamClass006));

        // 设置1年级为默认单词
        setDefaultList(class001List);

    }

    private void setDefaultList(List<String> class001List) {
        list = class001List;
    }

    private void initControl() {
        butPre = findViewById(R.id.btnPer);
        butNext = findViewById(R.id.btnNext);
        textView = findViewById(R.id.txtView);
        imgRing = findViewById(R.id.imgRing);
        butPre.setOnClickListener(this);
        butNext.setOnClickListener(this);
        textView.setOnClickListener(this);
        imgRing.setOnClickListener(this);
    }

    private void initTTS() {
        // TTS
        textToSpeech = new TextToSpeech(this, this); // 参数Context,TextToSpeech.OnInitListener\
        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        textToSpeech.setPitch(1f);
        //设定语速 ，默认1.0正常语速
        textToSpeech.setSpeechRate(1f);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPer:
                String str1 = list.get(index = (index - 1 < 0 ? list.size() - 1 : index - 1));
                textView.setText(str1);

                speackText(str1);
                break;
            case R.id.btnNext:
                String str2 = list.get(index = (index + 1 >= list.size() ? 0 : index + 1));
                textView.setText(str2);
                speackText(str2);
                break;
            case R.id.txtView:
                speackText(textView.getText().toString());
                break;
            case R.id.imgRing:
                speackText(textView.getText().toString());
                break;
        }
    }

    private void speackText(String str) {
        Log.e(TAG, "speackText: " + textView.getText().toString());
        if (textToSpeech == null) {
            Log.e(TAG, "TTS is NULL: " + textView.getText().toString());
            // TTS
            textToSpeech = new TextToSpeech(this, this);
            // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            textToSpeech.setPitch(1f);
            //设定语速 ，默认1.0正常语速
            textToSpeech.setSpeechRate(1f);
        }
        //
//            while(true){
//                if( !textToSpeech.isSpeaking()){
//                    try {
//                        Thread.sleep(30);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                }
//            }
        //朗读，注意这里三个参数的added in API level 4   四个参数的added in API level 21
        textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null);

    }

    @Override
    public void onInit(int status) {
        Log.e(TAG, "onInit");
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.CHINA);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "数据丢失或不支持", Toast.LENGTH_SHORT).show();
            }
        }
        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        textToSpeech.setPitch(1f);
        //设定语速 ，默认1.0正常语速
        textToSpeech.setSpeechRate(1f);
        //朗读，注意这里三个参数的added in API level 4   四个参数的added in API level 21
        textToSpeech.speak("李沫言你好,欢迎学习汉字！", TextToSpeech.QUEUE_FLUSH, null);
    }


    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
        // textToSpeech.stop(); // 不管是否正在朗读TTS都被打断
        // textToSpeech.shutdown(); // 关闭，释放资源
    }


    private void putListByStr(List<String> class001List, String class001Str) {

        char[] chars = class001Str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            class001List.add(String.valueOf(chars[i]));
        }

    }

    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return sb.toString().replaceAll("、", "").replaceAll("#", "").replaceAll("#", "").replaceAll("\n", "");
    }


}