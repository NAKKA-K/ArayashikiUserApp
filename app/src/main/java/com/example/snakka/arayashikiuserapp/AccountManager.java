package com.example.snakka.arayashikiuserapp;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountManager{
    private static Activity accountCreateContext;
    private static String userName;
    private static String guardianMail;
    private static final String USER_NAME_KEY = "user_name_key";
    private static final String GUARDIAN_MAIL_KEY = "guardian_mail_key";

    private static final String PROTOCOL = "http";
    private static final String HOST = "59.106.210.231";
    private static final int PORT = 3000;
    private static final String FILEPATH = "mana/userCleate/";


    public AccountManager(Activity context, final String userName, final String guardianMail){
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

        return true;
    }


    /** 入力されたユーザー名の論理チェック */
    public boolean isLogicalCheckName(String userName){
        //TODO:userNameに使ってはいけない文字が入っていないか検出。OKならtrue
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9_-]"); //これ以外の文字が含まれると弾く
        Matcher matcher = pattern.matcher(userName);

        if(matcher.find()){
            return false; //認められない
        }
        return true; //認める
    }


    /** サーバにアカウント登録のPOSTをする */
    public void postAccountToServer(final String userName, final String guardianMail){
        new AsyncTask<Void, String, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                //http通信でデータを送信して、レスポンスコードを受け取っている
                int resCode = httpDataPost(HOST, PORT, FILEPATH, getUserJson(userName, guardianMail));

                return new Boolean(isSuccess(resCode));
            }

            /** HTTPのレスポンスコードを受け取って、成功か失敗かを画面に表示しつつ、booleanを返す */
            private boolean isSuccess(int resCode){ //TODO:既存、登録、その他の3種類くらいで、条件分けする予定
                Log.d("ResCode", "HTTP通信で返ってきた値は" + resCode);

                resCode /= 100;
                if(resCode == 2){
                    publishProgress("新規アカウントの登録に成功しました"); //onProgressUpdateを呼ぶためのメソッド
                    return true;
                }else if(resCode == 5){
                    publishProgress("サーバー側のエラーです");
                }else{
                    publishProgress("新規アカウントの登録に失敗しました");
                }
                return false;
            }

            @Override
            protected void onProgressUpdate(String... params){
                Toast.makeText(accountCreateContext, params[0], Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(Boolean isResCode) {
                if (isResCode == false) return; //TODO:HACK:isResCode.FALSE == falseにしなければいけないのではないか

                loginAccount();
                accountCreateContext.finish();
            }

        }.execute();
    }


    /** ユーザ名と保護者メールから、JSON形式のString型を作成して返す */
    private String getUserJson(String userName, String guardianMail){
        return "{\"userName\":\"" + userName + "\", \"guardianAdd\":\"" + guardianMail + "\"}";
    }


    /** ログインすると同時に、アカウント情報をアプリに記憶させる */
    public void loginAccount(){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(accountCreateContext).edit();
        editor.putString(USER_NAME_KEY, userName);
        editor.putString(GUARDIAN_MAIL_KEY, guardianMail);
        editor.commit();
    }



    /** HTTP通信でPOSTをするときに、JSONのデータを送るメソッド */
    public static int httpDataPost(final String HOST, final int PORT, final String PATH, String postData){
        PrintStream outputServer = null;
        HttpURLConnection httpConnector = null;
        int resCode = 0;

        try {
            URL url = new URL(PROTOCOL, HOST, PORT, PATH);
            httpConnector = (HttpURLConnection) url.openConnection();
            httpConnector.setRequestMethod("POST");
            httpConnector.setDoOutput(true);
            httpConnector.setRequestProperty("Content-Type", "application/json");
            httpConnector.setRequestProperty("Accept", "application/json");

            httpConnector.connect();

            //JSONをString型で送信
            outputServer = new PrintStream(httpConnector.getOutputStream());
            outputServer.print(postData);
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

        return resCode;
    }


    /** アカウント作成 */
    private static void setUserName(String userName) { AccountManager.userName = userName; }
    private static void setGuardianMail(String guardianMail) { AccountManager.guardianMail = guardianMail; }

    public static String getUserName() { return userName; }
    public static String getGuardianMail() { return guardianMail; }
}
