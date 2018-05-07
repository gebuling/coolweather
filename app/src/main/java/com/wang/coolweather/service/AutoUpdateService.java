package com.wang.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.wang.coolweather.gson.Weather;
import com.wang.coolweather.util.HttpUtil;
import com.wang.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=8*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent intent1=new Intent(this,AutoUpdateService.class);
        PendingIntent pendingIntent=PendingIntent.getService(this,0,intent1,0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingPic() {
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.SendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responsePic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",responsePic);
                editor.apply();
            }
        });
    }

    private void updateWeather() {
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=pref.getString("weather",null);
        if (weatherString!=null){
            //有缓存时直接解析天气数据
            Weather weather= Utility.handleWeatherResponse(weatherString);
            String weatherId=weather.basic.weatherId;
            String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=110ae80cf3a5496680f0a608c95a9835";
            HttpUtil.SendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText=response.body().string();
                    Weather weather1=Utility.handleWeatherResponse(responseText);
                    if (weather1!=null && "ok".equals(weather1.status)){
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
