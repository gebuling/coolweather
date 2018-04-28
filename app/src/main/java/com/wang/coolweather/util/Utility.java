package com.wang.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.wang.coolweather.db.City;
import com.wang.coolweather.db.County;
import com.wang.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    private static final String TAG = "Utility";

    //解析和处理服务器返回的省级数据
    public static boolean handelProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvince = new JSONArray(response);
                for (int i = 0; i < allProvince.length(); i++) {
                    JSONObject provinceJSONObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceJSONObject.getString("name"));
                    province.setProvinceCode(provinceJSONObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析和处理服务器返回的市级数据
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityJsonObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityJsonObject.getString("name"));
                    city.setCityCode(cityJsonObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                    Log.d(TAG, "allCities: "+cityJsonObject.getString("name"));
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析和处理服务器返回的县级数据
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyJsonObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyJsonObject.getString("name"));
                    county.setWeatherId(countyJsonObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                    Log.d(TAG, "allCounties: "+countyJsonObject.getString("name"));
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
