import info.AppInfo;
import info.RootPathInfo;
import info.SubPathInfo;
import info.TrashInfoDesc;
import sqlite.SqliteDatabase;
import tools.LocalFileParser;
import tools.MD5EncryptUtils;
import tools.TextUtils;
import tools.Utils;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.prefs.PreferenceChangeEvent;

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
                while ((line = br.readLine()) != null) {
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

    private DataBaseHelper mDataBaseHelper = new DataBaseHelper();

    public void genLocalData() throws SQLException {

        SqliteDatabase database = mDataBaseHelper.getWritableDatabase();

        // delete tables
        List<String> sqls = new ArrayList<String>();
        String[] deleteTables = DataBaseHelper.getDeleteTables();
        for (String tableName : deleteTables) {
            String sql = String.format("drop table if exists %s ", tableName);
            sqls.add(sql);
        }

        boolean flag = database.executeSqls(sqls);

        flag = database.executeSqls(Arrays.asList(DataBaseHelper.getCreateTableSqls()));

        String base_path = "/data/working/data/clear-data/trunk/tools/PreClearData/out/20141105/";

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

    protected void genAppInfo(List<String> lines, SqliteDatabase database) throws SQLException {

        Connection connection = database.getConnection();
        String sql = String.format("insert into %s values(?, ?)", TrashClearDatabaseHelper.TABLE_APP_INFO);
        PreparedStatement stmt = connection.prepareStatement(sql);
        for (String line : lines) {
            AppInfo info = LocalFileParser.parseAppInfo(line);
            if (info != null) {
                stmt.setInt(1, info.appId);
                stmt.setString(2, Utils.DES_encrypt(info.appName, DES_KEY));
                stmt.addBatch();
            }
        }
        stmt.executeBatch();
        connection.commit();
    }

    protected void genSubPathDescription(List<String> lines, SqliteDatabase database) throws SQLException {
        Connection connection = database.getConnection();
        String sql = String.format("insert into %s values(?, ?)", TrashClearDatabaseHelper.TABLE_SUB_PATH_DESCRIPTION);

        PreparedStatement stmt = connection.prepareStatement(sql);

        for (String line : lines) {
            TrashInfoDesc info = LocalFileParser.parseTrashInfoDesc(line);
            if (info != null) {
                stmt.setInt(1, info.id);
                stmt.setString(2, Utils.DES_encrypt(info.text, DES_KEY));
                stmt.addBatch();
            }
        }
        stmt.executeBatch();
        connection.commit();
    }

    protected void genSubData(List<String> lines, SqliteDatabase database) throws SQLException {
        Connection connection = database.getConnection();
        String sql = String.format("insert into %s values(?, ?, ?, ?, ?, ?, ?, ?)", TrashClearDatabaseHelper.TABLE_SUB_PATH);
        PreparedStatement stmt = connection.prepareStatement(sql);

        for (String line : lines) {
            SubPathInfo info = LocalFileParser.parseSubPath(line);
            if (info != null) {
                stmt.setInt(1, info.appId);
                stmt.setInt(2, info.subId);
                stmt.setString(3, getAllPathMD5(info.subPath));
                stmt.setInt(4, info.subPathDescId);
                stmt.setInt(5, info.fileType);
                stmt.setInt(6, info.clearType);
                stmt.setInt(7, info.clearSuggestion);
                stmt.setInt(8, info.clearResidualType);
                stmt.addBatch();
            }
        }
        stmt.executeBatch();
        connection.commit();
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
    public void genRootData(Collection<String> lines, SqliteDatabase sqliteDatabase) throws SQLException {

        Map<Integer, String[]> idPkgsMap = new HashMap<Integer, String[]>();

        Connection connection = sqliteDatabase.getConnection();

        String sql = String.format("insert into %s(%s, %s, %s, %s, %s, %s, %s, %s) " +
                                   "values(?, ?, ?, ?, ?, ?, ?, ?)",
                TrashClearDatabaseHelper.TABLE_ROOT_PATH,
                TrashClearDatabaseHelper.ROOT_COLUMN_APP_ID,
                TrashClearDatabaseHelper.ROOT_COLUMN_APP_NAME,
                TrashClearDatabaseHelper.ROOT_COLUMN_ROOT_PATH,
                TrashClearDatabaseHelper.ROOT_COLUMN_APP_TYPE,
                TrashClearDatabaseHelper.ROOT_COLUMN_FILE_TYPE,
                TrashClearDatabaseHelper.ROOT_COLUMN_CLEAR_TYPE,
                TrashClearDatabaseHelper.ROOT_COLUMN_CLEAR_SUGGESTION,
                TrashClearDatabaseHelper.ROOT_COLUMN_ROOT_FIST_PATH);

        PreparedStatement pre_stmt = connection.prepareStatement(sql);

        for(String line : lines) {
            RootPathInfo info = LocalFileParser.parseRootPathLine(line);
            if (info == null) {
                continue;
            }

            pre_stmt.setInt(1, info.appId);
            pre_stmt.setString(2, Utils.DES_encrypt(info.appName, DES_KEY));
            pre_stmt.setString(3, MD5EncryptUtils.encrypt(info.appRootPath, DES_KEY));
            pre_stmt.setInt(4, info.appType);
            pre_stmt.setInt(5, info.fileType);
            pre_stmt.setInt(6, info.clearType);
            pre_stmt.setInt(7, info.clearSuggestion);
            pre_stmt.setString(8, MD5EncryptUtils.encrypt(getRootPath(info.appRootPath), DES_KEY));

            pre_stmt.addBatch();

            // insert into package infos
            idPkgsMap.put(info.appId, info.pkgs);

        }

        pre_stmt.executeBatch();

        // insert into package infos
        sql = String.format("insert into %s values(?, ?)", TrashClearDatabaseHelper.TABLE_APP_PKGNAME);
        pre_stmt = connection.prepareStatement(sql);

        for (Map.Entry<Integer, String[]> entry: idPkgsMap.entrySet()) {
            pre_stmt.setInt(1,  entry.getKey());
            for (String pkg : entry.getValue()) {
                pre_stmt.setString(2, pkg);
                pre_stmt.addBatch();
            }
        }
        pre_stmt.executeBatch();
        connection.commit();
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
