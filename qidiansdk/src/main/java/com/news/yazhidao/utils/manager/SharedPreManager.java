package com.news.yazhidao.utils.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.entity.HistoryEntity;
import com.news.yazhidao.entity.UploadLogDataEntity;
import com.news.yazhidao.entity.UploadLogEntity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Created by fengjigang on 15/5/6.
 */
public final class SharedPreManager {

    private static Context mContext;
    private static SharedPreManager mInstance;

    private SharedPreManager(Context context) {
        mContext = context;
    }

    public static SharedPreManager mInstance(Context mContext) {
        if (mInstance == null) {
            mInstance = new SharedPreManager(mContext.getApplicationContext());
        }
        return mInstance;
    }

    public static SharedPreferences getSettings(String name, int mode) {
        return mContext.getSharedPreferences(name, mode);
    }

    public void saveUserIdAndPlatform(String spName, String keyUserIdAndPlatform, String userId, String platform) {
        SharedPreferences.Editor e = getSettings(spName, Context.MODE_MULTI_PROCESS).edit();
        e.putString(keyUserIdAndPlatform, userId + "," + platform);
        e.commit();
    }

    @Deprecated
    public void saveJPushId(String value) {
        SharedPreferences.Editor e = getSettings(CommonConstant.FILE_JPUSH, Context.MODE_MULTI_PROCESS).edit();
        e.putString(CommonConstant.KEY_JPUSH_ID, value);
        e.commit();
    }

    public void saveUUID() {
        if (TextUtil.isEmptyString(getUUID())) {
            SharedPreferences.Editor e = getSettings(CommonConstant.FILE_USER, Context.MODE_MULTI_PROCESS).edit();
            e.putString(CommonConstant.KEY_UUID, UUID.randomUUID().toString());
            e.commit();
        }
    }

    public String getUUID() {
        return get(CommonConstant.FILE_USER, CommonConstant.KEY_UUID);
    }

    public String getJPushId() {
        return get(CommonConstant.FILE_JPUSH, CommonConstant.KEY_JPUSH_ID);
    }

    public void saveUmengId(String value) {
        SharedPreferences.Editor e = getSettings(CommonConstant.FILE_UMENG, Context.MODE_MULTI_PROCESS).edit();
        e.putString(CommonConstant.KEY_UMENG_ID, value);
        e.commit();
    }

    public String getUmengId() {
        return get(CommonConstant.FILE_UMENG, CommonConstant.KEY_UMENG_ID);
    }

    /**
     * 保存搜索词
     */
    public void saveSearchWord(String pKeyWord) {
        SharedPreferences sharedPreferences = getSettings(CommonConstant.FILE_SEARCH_WORDS, Context.MODE_MULTI_PROCESS);
        String oldWords = sharedPreferences.getString(CommonConstant.KEY_SEARCH_WORDS, "");
        if (!TextUtil.isEmptyString(oldWords)) {
            ArrayList<String> oldList = new ArrayList<>(Arrays.asList(oldWords.split(",")));
            if (oldList.contains(pKeyWord)) {
                oldList.remove(pKeyWord);
            }
            oldList.add(0, pKeyWord);
            StringBuilder sb = new StringBuilder();
            for (String item : oldList) {
                sb.append(item + ",");
            }
            oldWords = sb.deleteCharAt(sb.length() - 1).toString();
        } else {
            oldWords = pKeyWord;
        }
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(CommonConstant.KEY_SEARCH_WORDS, oldWords);
        e.commit();
    }

    /**
     * 获取搜索词列表
     */
    public List<String> getSearchWord() {
        SharedPreferences sharedPreferences = getSettings(CommonConstant.FILE_SEARCH_WORDS, Context.MODE_MULTI_PROCESS);
        String oldWords = sharedPreferences.getString(CommonConstant.KEY_SEARCH_WORDS, "");
        if (!TextUtil.isEmptyString(oldWords)) {
            return Arrays.asList(oldWords.split(","));
        }
        return new ArrayList<>();
    }


    /**
     * 保存User json
     *
     * @param user
     */
    public void saveUser(User user) {
        save(CommonConstant.FILE_USER, CommonConstant.KEY_USER_INFO, user.toJsonString());
    }

    /**
     * 删除保存的user json
     */
    public void deleteUser(Context mContext) {
        User user = getUser(mContext);
        if (user != null) {
//            if (!"meizu".equals(user.getPlatformType())){
//                ShareSDK.getPlatform(mContext, user.getPlatformType()).removeAccount();
//            }
        }
        user.setUtype("2");
        saveUser(user);
//        remove(CommonConstant.FILE_USER, CommonConstant.KEY_USER_INFO);
    }

    /**
     * 从 sp 中获取用户对象
     *
     * @return
     */
    public User getUser(Context mContext) {
//        ShareSDK.initSDK(mContext);
        String userJson = get(CommonConstant.FILE_USER, CommonConstant.KEY_USER_INFO);
        if (TextUtils.isEmpty(userJson)) {
            return null;
        }

        User user = User.parseUser(userJson);
//        if ("meizu".equals(user.getPlatformType())){
//            if (System.currentTimeMillis() - user.getExpiresTime() > 0){
//                remove(CommonConstant.FILE_USER, CommonConstant.KEY_USER_INFO);
//                return null;
//            }else {
//                return user;
//            }
//        }
        if (user != null) {
            return user;
//            if (user.isVisitor()){
//                return user;
//            }else {
//                if (ShareSDK.getPlatform(mContext,user.getPlatformType()).isValid()){
//                    return user;
//                }else {
//                    remove(CommonConstant.FILE_USER, CommonConstant.KEY_USER_INFO);
//                }
//            }
        }
        return null;
    }

    public void save(String spName, String key, long value) {
        Logger.d("SettingsManager", "SettingsManager : " + spName + ":" + "key : " + key + "value : " + value);
        SharedPreferences.Editor e = getSettings(spName, Context.MODE_MULTI_PROCESS).edit();
        e.putLong(key, value);
        e.commit();
    }

    public void save(String spName, String key, int value) {
        Logger.d("SettingsManager", "SettingsManager : " + spName + ":" + "key : " + key + "value : " + value);
        SharedPreferences.Editor e = getSettings(spName, Context.MODE_MULTI_PROCESS).edit();
        e.putInt(key, value);
        e.commit();
    }

    public void save(String spName, String key, boolean value) {
        SharedPreferences.Editor e = getSettings(spName, Context.MODE_MULTI_PROCESS).edit();
        e.putBoolean(key, value);
        e.commit();
    }

    public static void save(String spName, String key, String value) {
        SharedPreferences.Editor e = getSettings(spName, Context.MODE_MULTI_PROCESS).edit();
        e.putString(key, value);
        boolean result = e.commit();
        Logger.d("jigang", "save result = " + result + ",value=" + value);
    }

    public void clear(String spName) {
        SharedPreferences.Editor editor = getSettings(spName, Context.MODE_MULTI_PROCESS).edit();
        editor.clear();
        editor.commit();
    }

    public static String get(String spName, String key) {
        return getSettings(spName, Context.MODE_MULTI_PROCESS).getString(key, "");
    }

    public int getInt(String spName, String key) {
        return getSettings(spName, Context.MODE_MULTI_PROCESS).getInt(key, 0);
    }

    public boolean getBoolean(String spName, String key) {
        return getSettings(spName, Context.MODE_MULTI_PROCESS).getBoolean(key, false);
    }

    public boolean getBoolean(String spName, String key, boolean defaultValue) {
        return getSettings(spName, Context.MODE_MULTI_PROCESS).getBoolean(key, defaultValue);
    }

    public long getLong(String spName, String key) {
        return getSettings(spName, Context.MODE_MULTI_PROCESS).getLong(key, 0);
    }

    public void remove(String spName, String... keys) {
        if (keys == null) return;
        SharedPreferences.Editor e = getSettings(spName, Context.MODE_MULTI_PROCESS).edit();
        for (String key : keys) {
            e.remove(key);
        }
        e.commit();
    }

    /**
     * /**
     * 收藏本地版
     *
     * @param mUserId
     * @param key
     * @param locationJsonString
     * @param uploadLogDataEntity
     * @return
     */

    public int upLoadLogSave(String mUserId, String key, String locationJsonString, UploadLogDataEntity uploadLogDataEntity) {

        String mReadData = upLoadLogGet(key);
        if (mReadData.indexOf("city") != -1) {
            remove(CommonConstant.UPLOAD_LOG, key);
        }
        Gson gson = new Gson();
        JSONArray array = null;
        ArrayList<UploadLogDataEntity> data = new ArrayList<UploadLogDataEntity>();
        try {
            array = new JSONArray(mReadData);
            for (int i = 0; i < array.length(); i++) {
                String str1 = array.getString(i);
                UploadLogDataEntity bean = gson.fromJson(str1, UploadLogDataEntity.class);
                data.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        data.add(uploadLogDataEntity);

        upLoadLogDelter(key);
        save(CommonConstant.UPLOAD_LOG, key, gson.toJson(data));
        return data.size();


    }

    public int upLoadLogSaveList(String mUserId, String key, List<UploadLogDataEntity> list) {

        String mReadData = upLoadLogGet(key);
        Gson gson = new Gson();
        UploadLogEntity uploadLogEntity = new UploadLogEntity();
        if (mReadData != null && mReadData.length() != 0) {
            uploadLogEntity = gson.fromJson(mReadData, UploadLogEntity.class);

        }

        uploadLogEntity.getData().addAll(list);

        uploadLogEntity.setUid(mUserId);
        uploadLogEntity.setClas(CommonConstant.UPLOAD_LOG_DETAIL.equals(key) ? 0 : 1);

        upLoadLogDelter(key);
        save(CommonConstant.UPLOAD_LOG, key, gson.toJson(uploadLogEntity));
        return uploadLogEntity.getData().size();
    }

    public String upLoadLogGet(String key) {

        return get(CommonConstant.UPLOAD_LOG, key);
    }

    public void upLoadLogDelter(String key) {

        remove(CommonConstant.UPLOAD_LOG, key);
    }

/** 梁帅： 因为收藏才注释*/
//    /**
//     * 收藏本地版
//     * @param bean
//     */
//    public  void myFavoriteSaveList(NewsFeed bean){
//        ArrayList<NewsFeed> list = new ArrayList<NewsFeed>();
//        try {
//            list = myFavoriteGetList();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        list.add(bean);
//        Gson gson = new Gson();
//        String str = gson.toJson(list);
//        save(CommonConstant.MY_FAVORITE, CommonConstant.MY_FAVORITE, str);
//    }
//    public  boolean myFavoriteisSame(String newsID){
//        ArrayList<NewsFeed> list = new ArrayList<NewsFeed>();
//        try {
//            list = myFavoriteGetList();
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return false;
//        }
//        Logger.d("bbb", "newsID==" + newsID);
//        Logger.e("aaa","收藏的数据======"+list.toString());
//        for(int i = 0; i < list.size(); i++){
////            Logger.d("bbb", "list.get(i).getUrl()======="+i+"============" + list.get(i).getNid());
//            if(newsID.equals(list.get(i).getNid()+"")){
//                return true;
//            }
//        }
//        return false;
//    }
//    public  ArrayList<NewsFeed> myFavoriteGetList() throws JSONException {
//        Gson gson = new Gson();
//        String mf = get(CommonConstant.MY_FAVORITE, CommonConstant.MY_FAVORITE);
//        JSONArray array;
//        ArrayList<NewsFeed> list = new ArrayList<NewsFeed>();
//        array = new JSONArray(mf);
//        for (int i = 0; i < array.length(); i++) {
//            String str = array.getString(i);
//            NewsFeed bean = gson.fromJson(str, NewsFeed.class);
//            list.add(bean);
//
//        }
//        return list;
//
//    }
//    public  void myFavoritRemoveItem(String newsID){
//        ArrayList<NewsFeed> list = new ArrayList<NewsFeed>();
//        try {
//            list = myFavoriteGetList();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        for(int i = 0; i < list.size(); i++){
//            if (newsID.equals(list.get(i).getNid() + "")) {
//                list.remove(i);
//                Gson gson = new Gson();
//                String str = gson.toJson(list);
//                save(CommonConstant.MY_FAVORITE, CommonConstant.MY_FAVORITE, str);
//            }
//        }
//    }
//
//    public  ArrayList<NewsFeed> myFavoritRemoveList(ArrayList<NewsFeed> deleteList){
//        ArrayList<NewsFeed> list = new ArrayList<NewsFeed>();
//        try {
//            list = myFavoriteGetList();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
////        Logger.e("aaa","1111111111111"+list.size());
////        list.removeAll(deleteList);
////        Logger.e("aaa","2222222222222"+list.size());
////        Gson gson = new Gson();
////        String str = gson.toJson(list);
////        save(CommonConstant.MY_FAVORITE, CommonConstant.MY_FAVORITE, str);
//        for(NewsFeed bean : deleteList){
//            myFavoritRemoveItem(bean.getNid()+"");
//        }
//        try {
//            list = myFavoriteGetList();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }


    /**
     * 搜索历史的添加，获得，删除
     *
     * @param content
     */
    public void HistorySave(String content) {
        HistoryEntity historyEntity = new HistoryEntity(content);
        ArrayList<HistoryEntity> historyEntities = new ArrayList<HistoryEntity>();
        try {
            historyEntities = HistoryGetList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        historyEntities.add(historyEntity);
        save(CommonConstant.SEARCH_HISTORY, CommonConstant.SEARCH_HISTORY, gson.toJson(historyEntities));
    }

    public ArrayList<HistoryEntity> HistoryGetList() throws JSONException {
        ArrayList<HistoryEntity> historyEntities = new ArrayList<HistoryEntity>();
        String str = get(CommonConstant.SEARCH_HISTORY, CommonConstant.SEARCH_HISTORY);
        Gson gson = new Gson();
        JSONArray array = new JSONArray(str);
        for (int i = 0; i < array.length(); i++) {
            String str1 = array.getString(i);
            HistoryEntity bean = gson.fromJson(str1, HistoryEntity.class);
            historyEntities.add(bean);
        }
        return historyEntities;
    }

    public void Historyremove() {
        remove(CommonConstant.SEARCH_HISTORY, CommonConstant.SEARCH_HISTORY);
    }

}
