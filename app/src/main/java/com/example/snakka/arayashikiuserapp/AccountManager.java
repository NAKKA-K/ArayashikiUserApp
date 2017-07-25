package com.example.snakka.arayashikiuserapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountManager{
    private static Context accountCreateContext;
    private static String userName;
    private static String guardianMail;
    private static final String USER_NAME_KEY = "user_name_key";
    private static final String GUARDIAN_MAIL_KEY = "guardian_mail_key";

    //private static final String URL = "http://59.106.210.231:3000/mana/userCreate";
    private static final String PROTOCOL = "http";
    private static final String HOST = "59.106.210.231";
    private static final int PORT = 3000;
    private static final String FILEPATH = "mana/userCleate/";


    private static boolean postResCode = false;
    private static String postResStr;
    private static String userJson;

    public AccountManager(Context context, final String userName, final String guardianMail){
        accountCreateContext = context;
        this.userName = userName;
        this.guardianMail = guardianMail;
    }


    /** アプリにログイン情報が残っているか */
    public static boolean loginedAccount(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        //両方が存在した場合のみ、アカウント登録ができていると判定する
        if((userName = preferences.getString(USER_NAME_KEY, null)) == null) return false;
        if((guardianMail = preferences.getString(GUARDIAN_MAIL_KEY, null)) == null) return false;

        //return true;
        return false; //HACK:テスト実装で常にfalseにしている
    }


    /** 入力されたユーザー名の論理チェック */
    public boolean isLogicalCheckName(String userName){
        //TODO:userNameに使ってはいけない文字が入っていないか検出。OKならtrue
        Pattern pattern = Pattern.compile("[ .,]"); //弾くべき文字パターン
        Matcher matcher = pattern.matcher(userName);

        if(matcher.find()){
            return false; //認められない
        }

        return true; //認める
    }


    /** サーバにアカウント登録のPOSTをする */
    public void postAccountToServer(final String userName, final String guardianMail){
        postResCode = false; //初期化

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                PrintStream outputServer = null;
                HttpURLConnection httpConnector = null;
                int resCode = 0;

                Log.d("HTTP:", "doInBackground通過");
                try {
                    //URL url = new URL(URL + getUserJson(userName, guardianMail));
                    URL url = new URL(PROTOCOL, HOST, PORT, FILEPATH);
                    httpConnector = (HttpURLConnection) url.openConnection();
                    httpConnector.setRequestMethod("POST");
                    httpConnector.setDoOutput(true);
                    httpConnector.setRequestProperty("Content-Type", "application/json");
                    httpConnector.setRequestProperty("Accept", "application/json");

                    httpConnector.connect();

                    //JSONをString型で送信
                    outputServer = new PrintStream(httpConnector.getOutputStream());
//                    outputServer = new PrintStream(httpConnector.getOutputStream());
                    outputServer.print(getUserJson(userName, guardianMail));
                    outputServer.flush();

                    resCode = httpConnector.getResponseCode();
                } catch (MalformedURLException e) {
                    Log.e("例外発生", "URLが不正です", e);
                } catch (IOException e) {
                    Log.e("例外発生", "接続失敗", e);
                } finally {
                    if (outputServer != null) outputServer.close();
                    if (httpConnector != null) httpConnector.disconnect();
                }

                Log.d("ResCode", "HTTP通信で返ってきた値は" + resCode);


                //TODO:既存、登録、その他の3種類くらいで、条件分けする予定
                switch (resCode) {
                    case HttpURLConnection.HTTP_OK:
                        postResStr = "OK!新規アカウント登録に成功しました";
                        postResCode = true;
                        break;
                    case 404:
                        postResStr = "ページが存在しない";
                        break;
                    default:
                        postResStr = "NG!新規アカウント登録に失敗しました";
                        postResCode = false;
                }

                return new Boolean(postResCode);
            }


            @Override
            protected void onPostExecute(Boolean isResCode) {
                Log.d("HTTP", "onPostExecute通過 => " + postResStr);
                if (isResCode == false) {
                    return;
                }

                loginAccount();

                Log.d("End", "終了！！");
            }

        }.execute();

    }

    /** ユーザ名と保護者メールから、JSON形式のString型を作成して返す */
    private String getUserJson(String userName, String guardianMail){
        return "{\"userName\":\"" + userName + "\", \"guardianAdd\":\"" + guardianMail + "\"}";
        //return "{userName:" + userName + ", guardianAdd:" + guardianMail + "}";
    }


    /** ログインすると同時に、アカウント情報をアプリに記憶させる */
    public void loginAccount(){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(accountCreateContext).edit();
        editor.putString(USER_NAME_KEY, userName);
        editor.putString(GUARDIAN_MAIL_KEY, guardianMail);
        editor.commit();
    }



    /** アカウント作成 */
    private static void setUserName(String userName) { AccountManager.userName = userName; }
    private static void setGuardianMail(String guardianMail) { AccountManager.guardianMail = guardianMail; }


    public static String getUserName() { return userName; }
    public static String getGuardianMail() { return guardianMail; }
}
