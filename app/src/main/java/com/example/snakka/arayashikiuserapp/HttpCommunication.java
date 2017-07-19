package com.example.snakka.arayashikiuserapp;

import android.util.Log;

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

public class HttpCommunication extends AsyncTask<void, void, String> {
    private static final String JSONURL = "";

    protected String doInBackground() {
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
    protected void onPostExecute (String jsonStr)

}
