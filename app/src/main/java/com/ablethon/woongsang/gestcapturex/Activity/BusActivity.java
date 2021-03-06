package com.ablethon.woongsang.gestcapturex.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ablethon.woongsang.gestcapturex.API.DownloadTask;
import com.ablethon.woongsang.gestcapturex.API.TouchInterface;
import com.ablethon.woongsang.gestcapturex.ProcessGesture.ProcessBusGesture;
import com.ablethon.woongsang.gestcapturex.R;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class BusActivity extends Activity implements TextToSpeech.OnInitListener {

    public static TextToSpeech myTTS;

    public static ArrayList<String> options = new ArrayList<String>();
    ListView listview;
    public static int selector;
    Context context = this;
    //private static final String appid = "&appid=1c07e40d403816de4991116b22488b29";

    static DownloadTask task = null;
    private static String station1 = "";
    private static String station2 = "";
    private static String station3 = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);
        station1 = "07775:한국바이오협회";
        station2 = "07781:우주공원";
        station3 = "05206:신미주아파트";
        options.clear();
        options.add(station1);
        options.add(station2);
        options.add(station3);

        selector = 0;
        myTTS = new TextToSpeech(this, this);

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.bigfont_item, options);
        listview = (ListView) findViewById(R.id.BusListView);
        listview.setAdapter(adapter);

        listview.setOnTouchListener(scrollChecker);
    }


    AdapterView.OnTouchListener scrollChecker = new  AdapterView.OnTouchListener() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions (new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant

                return false;
            }

            return TI.gestureInterface(event);
        }

        ProcessBusGesture pg= new ProcessBusGesture();                               //to prcessing gesture
        TouchInterface TI = new TouchInterface((Activity) context,context,pg);       //to prcessing gesture
    };

    public static String getNextOption(int operator){
        if(operator==1){
            if(selector < options.size()-1 ) {
                selector++;
            }else {
                selector = 0;
            }
        }else{
            if(selector > 0 ) {
                selector--;
            }else {
                if(selector==-1){
                    selector=0;
                }else {
                    selector = options.size() - 1;
                }
            }
        }
        return options.get(selector);
    }

    @Override
    public void onInit(int status) {

    }

    public static void getBusInfo(double latitude, double longitude, String selected_option){

        String[] parts = station1.split(":");
        String station1_id = parts[0];
        String station1_name = parts[1];

        parts = station2.split(":");
        String station2_id = parts[0];
        String station2_name = parts[1];

        parts = station3.split(":");
        String station3_id = parts[0];
        String station3_name = parts[1];

        String msg = "";
        if (selected_option.equals(options.get(0)) ){
            msg = "73-1 (봇들육교 방향) 11분 뒤 도착합니다.";
            voiceMessage(station1_id, station1_name, msg);
        }
        else if (selected_option.equals(options.get(1))){
            msg = "602-1 (판교역서편 방면) 잠시후 도착합니다.";
            voiceMessage(station2_id, station2_name, msg);
        }
        else if (selected_option.equals(options.get(2))) { // 3 day weather
            msg = "390 (판교고교, 송현초교 방향) 8분 뒤 도착합니다." +
                    "602 (판교고교, 송현초교 방향) 14분 뒤 도착합니다.";
            voiceMessage(station3_id, station3_name, msg);

        }
    }

    private static void voiceMessage(String station_id, String station_name, String msg) {
        myTTS.speak("정류장 아이디 " + station_id + ", "+ station_name + "의 버스 정보입니다.", TextToSpeech.QUEUE_FLUSH, null);
        myTTS.speak(msg, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    protected void onDestroy() {


        //Close the Text to Speech Library
        if(myTTS != null) {

            myTTS.stop();
            myTTS.shutdown();
            Log.d(TAG, "TTS Destroyed");
        }
        super.onDestroy();
    }
}
