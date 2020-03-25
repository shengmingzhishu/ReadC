package com.a3read.readc;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.a3read.readc.utils.Cn2Spell;
import com.a3read.readc.utils.SharedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
    private ImageView imgRing, imgDel;
    private Toolbar toolbar;
    private View headerView;
    private DrawerLayout drawerLayout;
    private TextView tv_spell;
    private LinearLayout ll_txt;
    private LinearLayout ll_setting;
    private TextView tv_sav_all;
    private RelativeLayout rl_container;
    private Button btn_next_grade;
    private LinearLayout ll_bottom;

    // 控制当前阅读列表
    private List<String> list = new ArrayList<String>();
//    private List<List<String>> levList = new ArrayList<>();

    private int levIndex = -1;
    private int index = -1;

    // 文本朗读引擎
    private TextToSpeech textToSpeech; // TTS对象

    private Set<String> stringSet = new HashSet<>();

    private Map<String,List<String>> txtMap = new HashMap<>();

    private String mapKey = "level";
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

        stringSet = SharedUtils.getWord(MainActivity.this);

        removeTxt(class001List);
        txtMap.put(mapKey+0,class001List);
        removeTxt(class002List);
        txtMap.put(mapKey+1,class002List);
        removeTxt(class003List);
        txtMap.put(mapKey+2,class003List);
        removeTxt(class004List);
        txtMap.put(mapKey+3,class004List);
        removeTxt(class005List);
        txtMap.put(mapKey+4,class005List);
        removeTxt(class006List);
        txtMap.put(mapKey+5,class006List);

        // 设置1年级为默认单词
//        setDefaultList(txtMap.get(mapKey+levIndex));
    }

    private void removeTxt(List<String> stringList){
        List<String> list = new ArrayList<>();
        for(int i = 0;i<stringList.size();i++){
            if(stringSet.contains(stringList.get(i))){
                list.add(stringList.get(i));
            }
        }
        stringList.removeAll(list);
    }

    private void setDefaultList(List<String> class001List) {
        index = -1;
        list = class001List;
        tv_spell.setText("");
    }

    private void initControl() {
        butPre = findViewById(R.id.btnPer);
        butNext = findViewById(R.id.btnNext);
        textView = findViewById(R.id.txtView);
        imgRing = findViewById(R.id.imgRing);
        imgDel = findViewById(R.id.imgDel);
        drawerLayout = findViewById(R.id.drawerLayout);
        tv_spell = findViewById(R.id.tv_spell);
        toolbar = findViewById(R.id.id_toolbar);
        ll_txt = findViewById(R.id.ll_txt);
        ll_setting = findViewById(R.id.ll_setting);
        tv_sav_all = findViewById(R.id.tv_sav_all);
        rl_container = findViewById(R.id.rl_container);
        btn_next_grade = findViewById(R.id.btn_next_grade);
        ll_bottom = findViewById(R.id.ll_bottom);
        setSupportActionBar(toolbar);
        //显示返回按钮
        getSupportActionBar().setHomeButtonEnabled(true);
        //设置返回按钮可点击
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置返回按钮图标
//        toolbar.setNavigationIcon(R.drawable.icon_main_male);
        //去掉原标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //设置LOGO
//        toolbar.setLogo(R.mipmap.logo);
        //设置主标题
        toolbar.setTitle("Title");
        //设置副标题
        toolbar.setSubtitle("SubTitle");
//        toolbar.setOnMenuItemClickListener(menuItemClickListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        //在这里处理item的点击事件
                        return true;
                    }
                });
        butPre.setOnClickListener(this);
        butNext.setOnClickListener(this);
        textView.setOnClickListener(this);
        imgRing.setOnClickListener(this);
        imgDel.setOnClickListener(this);
        ll_txt.setOnClickListener(this);
        btn_next_grade.setOnClickListener(this);
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
                pervice();
                break;
            case R.id.btnNext:
                next();
                break;
            case R.id.txtView:
                speackText(textView.getText().toString());
                break;
            case R.id.imgRing:
                speackText(textView.getText().toString());
                break;
            case R.id.ll_txt:
                speackText(textView.getText().toString());
                break;
            case R.id.imgDel:
                stringSet.add(list.get(index));
                SharedUtils.putWord(stringSet, MainActivity.this);
                list.remove(index);
                index -= 1;
                next();
                break;
            case R.id.btn_next_grade:
                if(levIndex < 6-1){
                    levIndex += 1;
                }else{
                    levIndex = 0;
                }
                setDefaultList(txtMap.get(mapKey+levIndex));
                next();
                break;
        }
    }

    private void pervice() {
        tv_spell.setVisibility(View.VISIBLE);
        ll_setting.setVisibility(View.VISIBLE);
        if (list!=null && list.size() > 0) {
            String str = list.get(index = (index - 1 < 0 ? list.size() - 1 : index - 1));
            textView.setText(str);
            tv_spell.setText(Cn2Spell.getPinYin(str));
            speackText(str);
        }
    }

    private void next() {
        tv_spell.setVisibility(View.VISIBLE);
        ll_setting.setVisibility(View.VISIBLE);
        if(list !=null && list.size()>0){
            rl_container.setVisibility(View.VISIBLE);
            ll_bottom.setVisibility(View.VISIBLE);
            btn_next_grade.setVisibility(View.GONE);
            tv_sav_all.setVisibility(View.GONE);
            String str = list.get(index = (index + 1 >= list.size() ? 0 : index + 1));
            textView.setText(str);
            tv_spell.setText(Cn2Spell.getPinYin(str));
            speackText(str);
        }else {
            rl_container.setVisibility(View.GONE);
            tv_sav_all.setVisibility(View.VISIBLE);
            ll_bottom.setVisibility(View.GONE);
            btn_next_grade.setVisibility(View.VISIBLE);
        }
        btn_next_grade.setText("next grade");
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
        textToSpeech.speak("语音宝,欢迎学习汉字！", TextToSpeech.QUEUE_FLUSH, null);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.title, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String msg = "";
        switch (item.getItemId()) {
            case R.id.action_edit:
                msg += "Click edit";
                break;
            case R.id.action_share:
                msg += "Click share";
                break;
            case R.id.action_level1:
                levIndex = 0;
                setDefaultList(txtMap.get(mapKey+levIndex));
                next();
                break;
            case R.id.action_level2:
                levIndex = 1;
                setDefaultList(txtMap.get(mapKey+levIndex));
                next();
                break;
            case R.id.action_level3:
                levIndex = 2;
                setDefaultList(txtMap.get(mapKey+levIndex));
                next();
                break;
            case R.id.action_level4:
                levIndex = 3;
                setDefaultList(txtMap.get(mapKey+levIndex));
                next();
                break;
            case R.id.action_level5:
                levIndex = 4;
                setDefaultList(txtMap.get(mapKey+levIndex));
                next();
                break;
            case R.id.action_level6:
                levIndex = 5;
                setDefaultList(txtMap.get(mapKey+levIndex));
                next();
                break;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}