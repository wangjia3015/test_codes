package com.example.DataBaseGenerator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.DataBaseGenerator.tools.Utils;
import com.qihoo.security.opti.appcacheclear.TrashClearDatabaseHelper;

/**
 * Created by wangjia-s on 14-11-4.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String LOCAL_ROOT_PATH = "local_root_path_data";
    public static final String LOCAL_APP_NAME_PATH = "local_app_name_data"; //
    public static final String LOCAL_SUB_PATH = "local_sub_path_data";
    public static final String LOCAL_SUB_PATH_DESCRITPION = "local_sub_path_description";


//    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }

    public static final String DB_NAME = "t_c_c_d";
    private static final int DB_VERSION = 1;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
/**
 * 现在五张表
 * 1. t_r_p     root_path 根路径信息
 * 2. t_s_p     sub_path  子路径信息
 * 3. t_a_p     app_package 软件 ID 包名对应表
 * 4. t_s_p_d   sub_path_description 子路径说明信息表
 * 5. t_a_i     app_info app信息表 现在保存app名称
 * */


    /**
     * *************************************************
     */

    public static String[] getDeleteTables() {
        return new String[]{
                TrashClearDatabaseHelper.TABLE_ROOT_PATH,
                TrashClearDatabaseHelper.TABLE_SUB_PATH,
                TrashClearDatabaseHelper.TABLE_APP_PKGNAME,
                TrashClearDatabaseHelper.TABLE_SUB_PATH_DESCRIPTION,
                TrashClearDatabaseHelper.TABLE_APP_INFO,
        };
    }

    public static String[] getCreateTableSqls () {
        String SQL_TABLE_ROOT_PATH =
                String.format("CREATE TABLE IF NOT EXISTS %s "
                            + "(%s INTEGER PRIMARY KEY, "
                            + "%s VARCHAR NOT NULL , "
                            + "%s VARCHAR NOT NULL , "
                            + "%s VARCHAR NOT NULL , "
                            + "%s INTERGER DEFAULT 0, "
                            + "%s INTERGER DEFAULT 0, "
                            + "%s INTERGER DEFAULT 0, "
                            + "%s INTERGER DEFAULT 0) ",
                            TrashClearDatabaseHelper.TABLE_ROOT_PATH,
                            TrashClearDatabaseHelper.ROOT_COLUMN_APP_ID,
                            TrashClearDatabaseHelper.ROOT_COLUMN_APP_NAME,
                            TrashClearDatabaseHelper.ROOT_COLUMN_ROOT_PATH,
                            TrashClearDatabaseHelper.ROOT_COLUMN_ROOT_FIST_PATH,
                            TrashClearDatabaseHelper.ROOT_COLUMN_APP_TYPE,
                            TrashClearDatabaseHelper.ROOT_COLUMN_FILE_TYPE,
                            TrashClearDatabaseHelper.ROOT_COLUMN_CLEAR_TYPE,
                            TrashClearDatabaseHelper.ROOT_COLUMN_CLEAR_SUGGESTION);
        String SQL_TABLE_SUB_PATH =
                String.format("CREATE TABLE IF NOT EXISTS %s"
                            + " (%s INTEGER, "
                            + "%s INTEGER NOT NULL , "
                            + "%s VARCHAR NOT NULL , "
                            + "%s INTEGER NOT NULL ,"
                            + "%s INTERGER DEFAULT 0, "
                            + "%s INTERGER DEFAULT 0, "
                            + "%s INTERGER DEFAULT 0, "
                            + "%s INTERGER DEFAULT 0 )",
                            TrashClearDatabaseHelper.TABLE_SUB_PATH,
                            TrashClearDatabaseHelper.SUB_COLUMN_APP_ID,
                            TrashClearDatabaseHelper.SUB_COLUMN_SUB_ID,
                            TrashClearDatabaseHelper.SUB_COLUMN_SUB_PATH,
                            TrashClearDatabaseHelper.SUB_COLUMN_SUB_PATH_DESCRIPTION,
                            TrashClearDatabaseHelper.SUB_COLUMN_FILE_TYPE,
                            TrashClearDatabaseHelper.SUB_COLUMN_CLEAR_TYPE,
                            TrashClearDatabaseHelper.SUB_COLUMN_CLEAR_SUGGESTION,
                            TrashClearDatabaseHelper.SUB_COLUMN_CLEAR_RESIDUAL_TYPE);

        String SQL_TABLE_APP_PKGNAME =
                String.format("CREATE TABLE IF NOT EXISTS %s"
                            + " (%s INTEGER, "
                            + "%s VARCHAR NOT NULL)",
                        TrashClearDatabaseHelper.TABLE_APP_PKGNAME,
                        TrashClearDatabaseHelper.PKG_COLUMN_APP_ID,
                        TrashClearDatabaseHelper.PKG_COLUMN_APP_PKGNAME);

        String SQL_TABLE_SUB_PATH_DESCRIPTION =
                String.format("CREATE TABLE IF NOT EXISTS %s"
                            + " (%s INTEGER PRIMARY KEY, "
                            + "%s VARCHAR NOT NULL )",
                        TrashClearDatabaseHelper.TABLE_SUB_PATH_DESCRIPTION,
                        TrashClearDatabaseHelper.DESC_COLUMN_DESC_ID,
                        TrashClearDatabaseHelper.DESC_COLUMN_SUP_PATH_DESCRIPTION);

        String SQL_TABLE_APP_INFO =
                String.format("CREATE TABLE IF NOT EXISTS %s"
                            + " (%s INTEGER, "
                            + "%s VARCHAR NOT NULL)",
                        TrashClearDatabaseHelper.TABLE_APP_INFO,
                        TrashClearDatabaseHelper.AP_COLUMN_APP_ID,
                        TrashClearDatabaseHelper.AP_APP_NAME);

        String[] sqls = new String[] {
                SQL_TABLE_ROOT_PATH,
                SQL_TABLE_SUB_PATH,
                SQL_TABLE_SUB_PATH_DESCRIPTION,
                SQL_TABLE_APP_PKGNAME,
                SQL_TABLE_APP_INFO,
        };
        return sqls;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

/*
    private static final String ADD_ROOT_PATH = "add_root_path_data";
    private static final String ADD_APP_NAME_PATH = "add_app_name_data"; //
    private static final String ADD_SUB_PATH = "add_sub_path_data";
    private static final String ADD_SUB_PATH_DESCRITPION = "add_sub_path_description";
    private static final String DELETE_ROOT_PATH = "delete_root_path_data";
    private static final String DELETE_APP_NAME_PATH = "delete_app_name_data"; //
    private static final String DELETE_SUB_PATH = "delete_sub_path_data";
    private static final String DELETE_SUB_PATH_DESCRITPION = "delete_sub_path_description";
    private static final String UPDATE_ROOT_PATH = "update_root_path_data";
    private static final String UPDATE_APP_NAME_PATH = "update_app_name_data"; //
    private static final String UPDATE_SUB_PATH = "update_sub_path_data";
    private static final String UPDATE_SUB_PATH_DESCRITPION = "update_sub_path_description";
*/



}
