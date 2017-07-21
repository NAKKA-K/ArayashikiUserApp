package com.example.snakka.arayashikiuserapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by morikei on 2017/07/03.
 */

public class HttpCommunication extends AsyncTask<Void, Void, String> {
    private static final String JSONURL = "/mana/";

    private String block = "false";

    private String fourWayNomberN = "null";
    private String fourWayNomberS = "null";
    private String fourWayNomberW = "null";
    private String fourWayNomberE = "null";

    private String trafficLightAddress = "";
    //TODO:信号機の状態今のところ実装なし
    //private String trafficLightSignal = "false";

    protected String doInBackground(Void...v) {
        String resultJson = "";
        HttpURLConnection getCon = null;
        try {
            URL url = new URL(JSONURL);
            getCon = (HttpURLConnection) url.openConnection();
            //TODO：初期値で設定されているから必要ない？
            //getCon.setRequestMethod("GET");
            //getCon.setDoInput(true);
            //getCon.connect();
            //int response = getCon.getResponseCode();
            resultJson = jsonStreamToString(getCon.getInputStream());
        } catch (Exception ex) {
            System.out.println(ex);

        } finally {
            if(getCon != null) {
                getCon.disconnect();
            }
        }
        return resultJson;
    }

    //InputStreamをString型に変換
    public String jsonStreamToString(InputStream stream) throws IOException, UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        try {
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /* doInBackgroundの処理が終わった後で処理を開始する
        ・Jsonの取り扱いを行う*/
    protected void onPostExecute (String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            block = jsonObject.getString("type");
            fourWayNomberN = jsonObject.getJSONObject("FourWayNomber").getString("nothNo");
            fourWayNomberS = jsonObject.getJSONObject("FourWayNomber").getString("southNo");
            fourWayNomberW = jsonObject.getJSONObject("FourWayNomber").getString("westNo");
            fourWayNomberE = jsonObject.getJSONObject("FourWayNomber").getString("eastNo");
            trafficLightAddress = jsonObject.getJSONObject("TrafficLight").getString("address");
            //trafficLightSignal = jsonObject.getJSONObject("TrafficLight").getString("signal");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean getBlock() {
        return Boolean.parseBoolean(block);
    }

    public int getFourWayNomberN() {
        return Integer.parseInt(fourWayNomberN);
    }

    public int getFourWayNomberS() {
        return Integer.parseInt(fourWayNomberS);
    }

    public int getFourWayNomberW() {
        return Integer.parseInt(fourWayNomberW);
    }

    public int getFourWayNomberE() {
        return Integer.parseInt(fourWayNomberE);
    }

    public String getTrafficLightAddress() {
        return trafficLightAddress;
    }

    /*public boolean getTrafficLightSignal(){
        return Boolean.parseBoolean(trafficLightSignal);
    }*/
}
