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

public class HttpCommunication {
    private static final String PROTOCOL = "http";
    private static final String HOST = "59.106.210.231";
    private static final int PORT = 3000;
    private static final String FILEPATH = "/mana/";
    private static final String NONULL = "0";
    private static final int HTTP_OK = 200;
    private static final int HTTP_ERR = 400;

    private String currentNum;

    private String block = "false";
    private String fourWayNumberN = "null";
    private String fourWayNumberS = "null";
    private String fourWayNumberW = "null";
    private String fourWayNumberE = "32";


    private String trafficLightAddress = "";
    //TODO:信号機の状態今のところ実装なし
    //private String trafficLightSignal = "false";

    private int responseCode = 0;


    //このスレッドの処理が終わるまで、trueにしておく
    //private boolean startFlg = false;

    public HttpCommunication(String currentNum){
        this.currentNum = currentNum;
    }


    public void asysncTaskToGet() {
        new AsyncTask<Void, Void, Boolean>(){
            //TODO:doInBackground前処理、今のところ必要ない
            /*@Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.d("http通信","log1");
                // doInBackground前処理
            }*/
            //非同期処理、HTTP通信とjsonのパースを行う
            @Override
            protected Boolean doInBackground (Void...params){
                String resultJson = "";
                HttpURLConnection getCon = null;
                try {
                    URL url = new URL(PROTOCOL, HOST, PORT, FILEPATH + currentNum);
                    getCon = (HttpURLConnection) url.openConnection();
                    //TODO：初期値で設定されているから必要ない？
                    //getCon.setRequestMethod("GET");
                    //getCon.setDoInput(true);
                    getCon.connect();
                    //TODO:ResponseCodeは今の所使ってないので
                    responseCode = getCon.getResponseCode();
                    resultJson = jsonStreamToString(getCon.getInputStream());
                    Log.d("http通信", resultJson);
                    jsonanalysis(resultJson);
                } catch (Exception ex) {
                    System.out.println(ex);
                } finally {
                    if (getCon != null) {
                        Log.d("http通信", "finally通過");
                        getCon.disconnect();
                    }
                }
                //startFlg = false;
                Log.d("http通信", "doInBackground終了");

                return new Boolean(isResponseCode(responseCode));

            }

            /**
             * doInBackgroundの後に行う
             * <p>
             * SensorNumberに値をセットする
             */
            @Override
            protected void onPostExecute (Boolean isRes) {
                if(isRes) {
                    SensorNumber sn = new SensorNumber();
                    sn.setNextNorth(getFourWayNumberN());
                    sn.setNextEast(getFourWayNumberE());
                    sn.setNextSouth(getFourWayNumberS());
                    sn.setNextWest(getFourWayNumberW());
                }
                Log.d("http通信", "終了");
            }
        }.execute();
    }

    //InputStreamをString型に変換
    private String jsonStreamToString(InputStream stream) throws IOException, UnsupportedEncodingException {
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

    //jsonの解析を実行する
    private void jsonanalysis(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            //block = jsonObject.getString("type");
            fourWayNumberN = jsonObject.getString("nothNo");
            fourWayNumberS = jsonObject.getString("southNo");
            fourWayNumberW = jsonObject.getString("westNo");
            fourWayNumberE = jsonObject.getString("eastNo");
            //trafficLightAddress = jsonObject.getJSONObject("TrafficLight").getString("address");
            //trafficLightSignal = jsonObject.getJSONObject("TrafficLight").getString("signal");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //レスポンスコードからHTTP通信の成否を判断
    private boolean isResponseCode(int responseCode){
        switch (responseCode){
            case HTTP_OK:
                return true;
            default:
                return false;
        }
    }

    public boolean getBlock() {
        return Boolean.parseBoolean(block);
    }

    public int getFourWayNumberN() {

        fourWayNumberN = fourWayNumberN == "null" ?  NONULL : fourWayNumberN;
        return Integer.parseInt(fourWayNumberN);
    }

    public int getFourWayNumberS() {
        fourWayNumberS = fourWayNumberS == "null" ? NONULL : fourWayNumberS;
        return Integer.parseInt(fourWayNumberS);
    }

    public int getFourWayNumberW() {
        fourWayNumberW = fourWayNumberW == "null" ? NONULL : fourWayNumberW;
        return Integer.parseInt(fourWayNumberW);
    }

    public int getFourWayNumberE() {
        fourWayNumberE = fourWayNumberE == "null" ? NONULL : fourWayNumberE;
        return Integer.parseInt(fourWayNumberE);
    }

    public String getTrafficLightAddress() {
        return trafficLightAddress;
    }

    public int getResponseCode(){
        return responseCode;
    }
    /*public boolean getTrafficLightSignal(){
        return Boolean.parseBoolean(trafficLightSignal);
    }*/
    /*public boolean isStartFlg(){
        return startFlg;
    }*/
}

