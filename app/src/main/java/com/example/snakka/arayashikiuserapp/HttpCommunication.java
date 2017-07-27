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
    private static final int HTTP_OK = 200;
    private static final int HTTP_ERR = 400;


    private String block = "false";
    private String currentNum = "null";
    private String fourWayNumberN = "null";
    private String fourWayNumberS = "null";
    private String fourWayNumberW = "null";
    private String fourWayNumberE = "null";


    private String trafficLightAddress = "";
    //TODO:信号機の状態今のところ実装なし
    //private String trafficLightSignal = "false";]


    //このスレッドの処理が終わるまで、trueにしておく
    //private boolean startFlg = false;

    /*public HttpCommunication(String currentNum){
        this.currentNum = currentNum;
    }*/

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

                if( getSensorList() == null ) return false;
                currentNum = getSensorList();//センサー情報取得
                try {
                    URL url = new URL(PROTOCOL, HOST, PORT, FILEPATHGET + currentNum);
                    getCon = (HttpURLConnection) url.openConnection();
                    //TODO：初期値で設定されているから必要ない？
                    //getCon.setRequestMethod("GET");
                    //getCon.setDoInput(true);
                    getCon.connect();
                    //TODO:ResponseCodeは今の所使ってないので
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
                //startFlg = false;
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
                }
                Log.d("http通信", "終了");
                //ここでPOSTをします
                asyncTaskToPost();
            }
        }.execute();
    }

    private void asyncTaskToPost() {
        new AsyncTask<Void, Void, Integer>(){
            protected Integer doInBackground(Void... param){
                int resCode;

                if( getSensorList() == null ) return -1;
                currentNum = getSensorList();
                resCode = AccountManager.httpDataPost(HOST, PORT, FILEPATHPOST,
                        getUserJson(currentNum, AccountManager.getUserName()));
                return new Integer(resCode);
            }

            protected void onPostExecute (Integer resCode) {
                boolean isRes;
                isRes = (isResponseCode(resCode));
                if(!isRes){//POSTができなかった場合
                    if( resCode == -1 ) {
                        Log.d("http通信", "ナンバーnull");
                    }
                    else {
                        Log.e("http通信", "失敗");
                    }
                }else{//POST成功したとき
                    Log.d("http通信","POST成功");
                    sensorList.remove(0);
                }
                //ここでまた、
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
            case HttpURLConnection.HTTP_OK:
                return true;
            default:
                return false;
        }
    }

    private int numStringToInt(String num){
        return num == "null" ? NUMNULL : Integer.parseInt(num);
    }

    public boolean getBlock() {
        return Boolean.parseBoolean(block);
    }

/*TODO:getメゾットは今は使わない
    public int getFourWayNumberN() {
        return fourWayNumberN == "null" ?  NUMNULL : Integer.parseInt(fourWayNumberN);
    }

    public int getFourWayNumberS() {
        return fourWayNumberS == "null" ? NUMNULL : Integer.parseInt(fourWayNumberS);
    }

    public int getFourWayNumberW() {
        return fourWayNumberW == "null" ? NUMNULL : Integer.parseInt(fourWayNumberW);
    }

    public int getFourWayNumberE() {
        return fourWayNumberE == "null" ? NUMNULL : Integer.parseInt(fourWayNumberE);
    }
*/

    /*public String getTrafficLightAddress() {
        return trafficLightAddress;
    }*/

    /*public int getResponseCode(){
        return responseCode;
    }*/
    /*public boolean getTrafficLightSignal(){
        return Boolean.parseBoolean(trafficLightSignal);
    }*/
    /*public boolean isStartFlg(){
        return startFlg;
    }*/

    /**Getの時は、Listを消さないのでこちらを使います*/
    public static String getSensorList() {
        if (HttpCommunication.sensorList == null || sensorList.isEmpty()) return null;
        return sensorList.get(0);
    }
    /**Postをする時は、Listを消すのでこちらを使う*/
/*    public static String getSensorListRemove() {
        if (HttpCommunication.sensorList == null || sensorList.isEmpty()) return null;
        String str = sensorList.get(0);
        //sensorList.remove(0);
        return str;
    }
*/

    public static void setSensorList(String sensorStr) {
        if(sensorList.get(sensorList.size() - 1) == sensorStr) return; //連続して同じセンサーは受け付けない
        HttpCommunication.sensorList.add(sensorStr);
    }
}

