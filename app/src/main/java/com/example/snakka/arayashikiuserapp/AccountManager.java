package com.example.snakka.arayashikiuserapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountManager {
    private static String userName;
    private static String guardianMail;
    private static final String USER_NAME_KEY = "user_name_key";
    private static final String GUARDIAN_MAIL_KEY = "guardian_mail_key";



    //アプリにログイン情報が残っているか
    public static boolean loginedAccount(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        //両方が存在した場合のみ、アカウント登録ができていると判定する
        if((userName = preferences.getString(USER_NAME_KEY, null)) == null) return false;
        if((guardianMail = preferences.getString(GUARDIAN_MAIL_KEY, null)) == null) return false;

        //return true;
        return false; //HACK:テスト実装で常にfalseにしている
    }


    //入力されたユーザー名の論理チェック
    public boolean isLogicalCheckName(String userName){
        //TODO:userNameに使ってはいけない文字が入っていないか検出。OKならtrue
        Pattern pattern = Pattern.compile(""); //弾くべき文字パターン
        Matcher matcher = pattern.matcher(userName);

        if(matcher.find()){
            return false; //認められない
        }

        return true; //認める
    }


    //ログインすると同時に、アカウント情報をアプリに記憶させる
    public void loginAccount(Context context, String userName, String guardianMail){
        setUserName(userName);
        setGuardianMail(guardianMail);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(USER_NAME_KEY, userName);
        editor.putString(GUARDIAN_MAIL_KEY, guardianMail);
        editor.commit();
    }



    /*アカウント作成*/
    private static void setUserName(String userName) { AccountManager.userName = userName; }
    private static void setGuardianMail(String guardianMail) { AccountManager.guardianMail = guardianMail; }


    public static String getUserName() { return userName; }
    public static String getGuardianMail() { return guardianMail; }
}
