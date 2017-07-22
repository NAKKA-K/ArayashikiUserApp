package com.example.snakka.arayashikiuserapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountManager extends Thread{
    private static String userName;
    private static String guardianMail;
    private static final String USER_NAME_KEY = "user_name_key";
    private static final String GUARDIAN_MAIL_KEY = "guardian_mail_key";
    private static final String URL = "http://59.106.210.231/mana/userCreate";

    private static boolean postResCode = false;
    private static String postResStr;
    private static String userJson;

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
        Pattern pattern = Pattern.compile(""); //弾くべき文字パターン
        Matcher matcher = pattern.matcher(userName);

        if(matcher.find()){
            return false; //認められない
        }

        return true; //認める
    }


    /** サーバにアカウント登録のPOSTをする */
    public boolean postAccountToServer(final String userName, final String guardianMail){
        AsyncTask<Void, Void, Integer> test = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                PrintStream outputServer = null;
                HttpURLConnection httpConnector = null;
                int resCode = 0;

                try{
                    URL url = new URL(URL);
                    httpConnector = (HttpURLConnection) url.openConnection();
                    httpConnector.setRequestMethod("POST");
                    httpConnector.setRequestProperty("Accept-Language", "jp");
                    httpConnector.connect();

                    resCode = httpConnector.getResponseCode();

                    outputServer = new PrintStream(httpConnector.getOutputStream());
                    outputServer.print(getUserJson(userName, guardianMail));
                }catch(MalformedURLException e){
                    //System.out.println("URLが不正です");
                }catch(IOException e){
                    //System.out.println("接続失敗");
                }finally{
                    if(outputServer != null) outputServer.close();
                    if(httpConnector != null) httpConnector.disconnect();
                }

                return new Integer(resCode);
            }

            @Override
            protected void onPostExecute(Integer resCode){
                //TODO:既存、登録、その他の3種類くらいで、条件分けする予定
                switch(resCode){
                    case HttpURLConnection.HTTP_OK:
                        postResStr = "OK!新規アカウント登録に成功しました";
                        postResCode = true;
                        break;
                    default:
                        postResStr = "NG!新規アカウント登録に失敗しました";
                        postResCode = false;
                }
            }
        };

        test.execute();

        return postResCode;
    }

    /** ユーザ名と保護者メールから、JSON形式のString型を作成して返す */
    private String getUserJson(String userName, String guardianMail){
        return "[{userName:" + userName + ", guardianMail:" + guardianMail + "}]";
    }


    /** ログインすると同時に、アカウント情報をアプリに記憶させる */
    public void loginAccount(Context context, String userName, String guardianMail){
        setUserName(userName);
        setGuardianMail(guardianMail);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
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
