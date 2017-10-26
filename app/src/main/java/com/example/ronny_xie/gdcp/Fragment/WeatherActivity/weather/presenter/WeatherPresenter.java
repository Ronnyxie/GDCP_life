package com.example.ronny_xie.gdcp.Fragment.WeatherActivity.weather.presenter;


import com.example.ronny_xie.gdcp.Fragment.WeatherActivity.weather.WeatherForecastInfo;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import java.io.IOException;
import okhttp3.Response;


/**
 * Created by HYH on 2017/8/30.
 */

public class WeatherPresenter {

    /**
     * 彩云天气接入key
     */
    private static final String CAIYUN_KEY="96Ly7wgKGq6FhllM";

    /**
     * 彩云天气地址
     */
    private static final String CAIYUN_URL="https://api.caiyunapp.com/v2/";

    private static Response response;

    /**
     * 加载预报天气信息
     */
    public static WeatherForecastInfo loadForecastWeather(double latitude, double longitude){

        String locationStr=longitude+","+latitude;
        final String realTimeWeatherUrl=CAIYUN_URL+CAIYUN_KEY+"/"
                +locationStr+"/forecast.json";

        try {
            response = OkGo.get(realTimeWeatherUrl).execute();
            if(response.code() == 200){
                String info = response.body().string();
                Gson gson = new Gson();
                WeatherForecastInfo json = gson.fromJson(info, WeatherForecastInfo.class);
                return json;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加载定位信息
     */
    public static String loadLocationAddress(double latitude, double longitude) {
        String URL = "http://api.map.baidu.com/geocoder/v2/?ak=pPGNKs75nVZPloDFuppTLFO3WXebPgXg&callback=renderReverse&location=" + latitude + "," + longitude + "&output=json&pois=0";

        try {
            response = OkGo.get(URL).execute();
            if(response.code() == 200){
                String info = response.body().string();
                if(info.contains("formatted_address")){
                    String b = info.substring(info.indexOf("_address") + 11);
                    String c = b.substring(0, b.indexOf("\""));
                    return c;
                }else{
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
