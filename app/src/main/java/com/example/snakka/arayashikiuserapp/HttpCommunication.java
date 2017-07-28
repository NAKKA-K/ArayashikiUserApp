package com.example.snakka.arayashikiuserapp;

import android.os.AsyncTask;
import android.os.storage.StorageManager;
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
import java.util.ArrayList;

/**
 * Created by morikei on 2017/07/03.
 */

public class HttpCommunication {

    public static ArrayList<String> sensorList = new ArrayList<String>();

    private static final String PROTOCOL = "http";
    private static final String HOST = "59.106.210.231";
    private static final int PORT = 3000;
    private static final String FILEPATHGET = "/mana/";
    private static final String FILEPATHPOST = "/mana/route";
    private static final int NUMNULL = 0;

    private static String block = "false";
    private static String currentNum = "null";
    private String fourWayNumberN = "null";
    private String fourWayNumberS = "null";
    private String fourWayNumberW = "null";
    private String fourWayNumberE = "null";

    //TODO:同じNoでPOSTしないようにするためのフラグ
    private static boolean isIdenticalNumber = true;

    //private String trafficLightAddress = "";

    /**
     * asyncTaskToGet()を始めると、自動的にasyncTaskToPost()を始める
     */
    public void asyncTaskToGet() {
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground (Void...params){
                String resultJson = "";
                HttpURLConnection getCon = null;
                int resCode = 0;

                if( getSensorList(currentNum) == null ) return false;
                currentNum = getSensorList();//センサー情報取得
                try {
                    URL url = new URL(PROTOCOL, HOST, PORT, FILEPATHGET + currentNum);
                    getCon = (HttpURLConnection) url.openConnection();
                    getCon.connect();
                    resCode = getCon.getResponseCode();
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
                Log.d("http通信", "doInBackground終了");

                return new Boolean(isResponseCode(resCode));
            }

            /**
             * doInBackgroundの後に行う
             * <p>
             * SensorNumberに値をセットする
             */
            @Override
            protected void onPostExecute (Boolean isRes) {
                if(isRes) {
                    SensorNumber.setNum(numStringToInt(currentNum));
                    SensorNumber.setNextNorth(numStringToInt(fourWayNumberN));
                    SensorNumber.setNextEast(numStringToInt(fourWayNumberE));
                    SensorNumber.setNextSouth(numStringToInt(fourWayNumberS));
                    SensorNumber.setNextWest(numStringToInt(fourWayNumberW));
                    isIdenticalNumber = false;
                    Log.d("http通信","GET成功");
                }
                Log.d("http通信", "Get終了");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //ここでPOSTをします
                asyncTaskToPost();
            }
        }.execute();
    }

    private void asyncTaskToPost() {
        new AsyncTask<Void, Void, Integer>(){
            protected Integer doInBackground(Void... param){
                int resCode;


                if( isIdenticalNumber ) return -1;
                resCode = AccountManager.httpDataPost(HOST, PORT, FILEPATHPOST,
                        getUserJson(currentNum, AccountManager.getUserName()));
                return new Integer(resCode);
            }

            protected void onPostExecute (Integer resCode) {
                boolean isRes;
                isRes = (isResponseCode(resCode));
                if(!isRes){//POSTができなかった場合
                    if( resCode == -1 ) {
                        Log.d("http通信", "同じNoなのでPOSTしない");
                    }
                    else {
                        Log.e("http通信", "POST失敗");
                    }
                }else{//POST成功したとき
                    Log.d("http通信","POST成功");
                    //POSTしたのでsensorList(0)を消去
                    sensorList.remove(0);
                    Log.d("http通信","sensorListのサイズ" + Integer.toString(sensorList.size()));
                }
                isIdenticalNumber = true;
                //ここでまた、
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                asyncTaskToGet();
            }

        }.execute();
    }

    /** ユーザ名とセンサーナンバーから、JSON形式のString型を作成して返す */
    private String getUserJson(String sensorNo, String userName){
        return "{\"sensorNo\":\"" + sensorNo + "\", \"userName\":\"" + userName + "\"}";
    }

    /**InputStreamをString型に変換*/
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
            block = jsonObject.getString("sensorType");
            fourWayNumberN = jsonObject.getString("nothNo");
            fourWayNumberS = jsonObject.getString("southNo");
            fourWayNumberW = jsonObject.getString("westNo");
            fourWayNumberE = jsonObject.getString("eastNo");
            //trafficLightAddress = jsonObject.getJSONObject("TrafficLight").getString("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //レスポンスコードからHTTP通信の成否を判断
    private boolean isResponseCode(int responseCode){
        switch (responseCode){
            case HttpURLConnection.HTTP_OK:
                return true;
            default:
                return false;
        }
    }

    private int numStringToInt(String num){
        return num == "null" ? NUMNULL : Integer.parseInt(num);
    }
    /**trueで進行ブロック、falseで停止ブロック*/
    public static boolean getBlock() {
        return Boolean.parseBoolean(block);
    }

    /*public String getTrafficLightAddress() {
        return trafficLightAddress;
    }*/

    /**現在ナンバーと同じならnullを返す*/
    private static String getSensorList(String currentNum) {
        if(!sensorList.isEmpty())
            Log.d("http通信","get(0)" + sensorList.get(0) + "currentNum" + currentNum);
        if (sensorList == null || sensorList.isEmpty() || sensorList.get(0) == currentNum) return null;
        return sensorList.get(0);
    }

    /**実際に値を入れる時はこっちを使う*/
    private static String getSensorList() {
        if (sensorList == null || sensorList.isEmpty()) return null;
        return sensorList.get(0);
    }


    public static void setSensorList(String sensorStr) {
        if(sensorList.isEmpty() && sensorStr != currentNum){
            Log.d("http通信", "add成功２" + Integer.toString(sensorList.size()));
            sensorList.add(sensorStr);
            return;
        }
        if(sensorList.isEmpty() || sensorList.get(sensorList.size()-1) == sensorStr) {
            Log.d("http通信", "add失敗" + Integer.toString(sensorList.size()));
            return; //連続して同じセンサーは受け付けない
        }
        Log.d("http通信", sensorList.get(sensorList.size()-1) + ":" + sensorStr);
        sensorList.add(sensorStr);
        Log.d("http通信", sensorList.get(sensorList.size()-1) + ":" + sensorStr);
        Log.d("http通信","add成功" + Integer.toString(sensorList.size()));
    }

}
