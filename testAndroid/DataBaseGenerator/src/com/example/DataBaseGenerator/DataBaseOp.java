package com.example.DataBaseGenerator;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.example.DataBaseGenerator.info.RootPathInfo;
import com.example.DataBaseGenerator.info.SubPathInfo;
import com.example.DataBaseGenerator.info.TrashInfoDesc;
import com.example.DataBaseGenerator.tools.LocalFileParser;
import com.example.DataBaseGenerator.tools.Utils;
import com.qihoo.security.opti.appcacheclear.MD5EncryptUtils;
import com.qihoo.security.opti.appcacheclear.TrashClearDatabaseHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by wangjia-s on 14-11-4.
 */
public class DataBaseOp {

    private static final String DES_KEY = Utils.DES_decrypt("603d5c3d2bc1246c34a4a15abfd1a453", "jtest*confxy@1");

    public static List<String> getLines(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            List<String> list = new ArrayList<String>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = null;
                while((line = br.readLine()) != null) {
                    list.add(line);
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        }
        return null;
    }

    private DataBaseHelper mDataBaseHelper = null;

    private Context mContext;

    public void loadFromAssets() {

    }

    public void init() {
        mDataBaseHelper = new DataBaseHelper(mContext);
    }

    public void genLocalData() {
        SQLiteDatabase database = mDataBaseHelper.getWritableDatabase();

        // delete tables
        String[] deleteTables = DataBaseHelper.getDeleteTables();
        for (String tableName : deleteTables) {
            String sql = String.format("drop table %s ", tableName);
            database.execSQL(sql);
        }

        for (String sql : DataBaseHelper.getCreateTableSqls()) {
            database.execSQL(sql);
        }

        String base_path = "/storage/sdcard0/test/";

        // genRootData    with package data
        String root_path = base_path + DataBaseHelper.LOCAL_ROOT_PATH;
        genRootData(getLines(root_path), database);

        // genSubData     with description
        String sub_path = base_path + DataBaseHelper.LOCAL_SUB_PATH;
        genSubData(getLines(sub_path), database);

        // genSubPathDesc
        String sub_path_desc = base_path + DataBaseHelper.LOCAL_SUB_PATH_DESCRITPION;
        genSubPathDescription(getLines(sub_path_desc), database);

        // genAppInfo
        String app_info_path = base_path + DataBaseHelper.LOCAL_APP_NAME_PATH;
        genAppInfo(getLines(app_info_path), database);
    }

    protected void genAppInfo(List<String> lines, SQLiteDatabase database) {
        database.beginTransaction();

        for (String line : lines) {
            TrashInfoDesc info = LocalFileParser.parseTrashInfoDesc(line);
            if (info != null) {
                ContentValues values = new ContentValues();
                values.put(TrashClearDatabaseHelper.AP_COLUMN_APP_ID, info.id);
                values.put(TrashClearDatabaseHelper.AP_APP_NAME, info.text);
                database.insertOrThrow(TrashClearDatabaseHelper.TABLE_SUB_PATH_DESCRIPTION, null, values);
            }
        }

        database.setTransactionSuccessful();
        database.endTransaction();
    }

    protected void genSubPathDescription(List<String> lines, SQLiteDatabase database) {
        database.beginTransaction();

        for (String line : lines) {
            TrashInfoDesc info = LocalFileParser.parseTrashInfoDesc(line);
            if (info != null) {
                ContentValues values = new ContentValues();
                values.put(TrashClearDatabaseHelper.DESC_COLUMN_DESC_ID, info.id);
                values.put(TrashClearDatabaseHelper.DESC_COLUMN_SUP_PATH_DESCRIPTION, info.text);
                database.insertOrThrow(TrashClearDatabaseHelper.TABLE_SUB_PATH_DESCRIPTION, null, values);
            }
        }

        database.setTransactionSuccessful();
        database.endTransaction();
    }

    protected void genSubData(List<String> lines, SQLiteDatabase database) {
        database.beginTransaction();
        for (String line : lines) {
            SubPathInfo info = LocalFileParser.parseSubPath(line);
            if (info != null) {
                ContentValues values = new ContentValues();
                values.put(TrashClearDatabaseHelper.SUB_COLUMN_APP_ID, info.appId);
                values.put(TrashClearDatabaseHelper.SUB_COLUMN_SUB_ID, info.subId);
                values.put(TrashClearDatabaseHelper.SUB_COLUMN_SUB_PATH, getAllPathMD5(info.subPath));   //MD5 all
                values.put(TrashClearDatabaseHelper.SUB_COLUMN_SUB_PATH_DESCRIPTION, info.subPathDescId);
                values.put(TrashClearDatabaseHelper.SUB_COLUMN_FILE_TYPE, info.fileType);
                values.put(TrashClearDatabaseHelper.SUB_COLUMN_CLEAR_TYPE, info.clearType);
                values.put(TrashClearDatabaseHelper.SUB_COLUMN_CLEAR_SUGGESTION, info.clearSuggestion);
                values.put(TrashClearDatabaseHelper.SUB_COLUMN_CLEAR_RESIDUAL_TYPE, info.clearResidualType);
                database.insertOrThrow(TrashClearDatabaseHelper.TABLE_SUB_PATH, null, values);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public String getAllPathMD5(String path) {
        String[] paths = path.split("\\/");
        String md5 = "";
        for (String p : paths) {
            if (TextUtils.isEmpty(p)) {
                continue;
            }

            if (!TextUtils.isEmpty(md5)) {
                md5 += "+";
            }
            md5 += MD5EncryptUtils.encrypt(p, DES_KEY);
        }
        return md5;
    }

    /**
     * gen TABLE_ROOT_PATH
     * gen TABLE_APP_PKGNAME
     * */
    public void genRootData(Collection<String> lines, SQLiteDatabase sqliteDatabase) {
        sqliteDatabase.beginTransaction();
        for(String line : lines) {
            RootPathInfo info = LocalFileParser.parseRootPathLine(line);
            if (info == null) {
                continue;
            }

            // insert into root_table
            ContentValues values = new ContentValues();
            values.put(TrashClearDatabaseHelper.ROOT_COLUMN_APP_ID, info.appId);
            values.put(TrashClearDatabaseHelper.ROOT_COLUMN_APP_NAME, Utils.DES_encrypt(info.appName, DES_KEY));   // 加密
            values.put(TrashClearDatabaseHelper.ROOT_COLUMN_ROOT_PATH, MD5EncryptUtils.encrypt(info.appRootPath, DES_KEY));   // MD5
            values.put(TrashClearDatabaseHelper.ROOT_COLUMN_APP_TYPE, info.appType);
            values.put(TrashClearDatabaseHelper.ROOT_COLUMN_FILE_TYPE, info.fileType);
            values.put(TrashClearDatabaseHelper.ROOT_COLUMN_CLEAR_TYPE, info.clearType);
            values.put(TrashClearDatabaseHelper.ROOT_COLUMN_CLEAR_SUGGESTION, info.clearSuggestion);
            values.put(TrashClearDatabaseHelper.ROOT_COLUMN_ROOT_FIST_PATH, MD5EncryptUtils.encrypt(getRootPath(info.appRootPath), DES_KEY));

            sqliteDatabase.insertOrThrow(TrashClearDatabaseHelper.TABLE_ROOT_PATH, null, values);

            // insert into package infos
            for (String pkg : info.pkgs) {
                ContentValues pkgValues = new ContentValues();
                pkgValues.put(TrashClearDatabaseHelper.PKG_COLUMN_APP_ID, info.appId);
                pkgValues.put(TrashClearDatabaseHelper.PKG_COLUMN_APP_PKGNAME, pkg);

                sqliteDatabase.insertOrThrow(TrashClearDatabaseHelper.TABLE_APP_PKGNAME, null, pkgValues);
            }
        }
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
    }

    public String getRootPath(String path) {
        // 如果... 处理全部计算
        // 如果... 并且子路径大于... 取 ...
        // 其他去首个子路径
        String[] condition1 = new String[] {
                    "android/data/",
                    "data/data/",
                    "dcim/camera/",
                };
        String[] condition2 = new String[] {
                    "dcim/",
                    "pictures/",
                    "download/",
                    "ringtones/",
                    "data/",
                    "tencent/",
                    "miui/",
                    "baidu/",
                    "sogou/",
                    "sina/",
                    "youku/",
                    "youtube/",
                    "osmdroid/",
                    "hjapp/",
                    "youdao/",
                };

        for (String path_head : condition1) {
            if (path.startsWith(path_head)) {
                return path;
            }
        }

        for (String path_head : condition2) {
            if (path.startsWith(path_head)) {
                String[] paths = path.split("/");
                String prefixPath;
                if (paths.length <= 1) {
                    prefixPath = path;
                } else {
                    prefixPath = paths[0] + "/" + paths[1];
                }
                return prefixPath;
            }
        }

        // 取首个目录
        String[] paths = path.split("\\/");
        return paths[0];
    }




}
