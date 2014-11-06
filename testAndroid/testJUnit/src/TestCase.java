import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;
import org.junit.Test;
import sqlite.SqliteDatabase;
import tools.MD5EncryptUtils;
import tools.Utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by wangjia-s on 14-11-5.
 */
public class TestCase {

    public static final String DES_KEY = DataBaseOp.getDesKey();

    @Test
    public void checkDES() {
        // 测试DES 加密算法

        // 测试加密
        assertEquals("455f4a25c622132ed3fc9de34efab26f27e7359753a6fd5473bb6db4cfead0ab7e352885f7b4645af92b165539eddfcf686c86a6bf005361", Utils.DES_encrypt("en@iFishing Lite|zh-CN@我钓鱼|zh-TW@我釣魚", DES_KEY));
        assertEquals("b056eead060466ae372c7fe86fbe565d7b26a5e2a52bdbedab62b1f09d4f38cf932397b55651f14cfb102e5a3852beda75530304d7a37ff3efa212b57850c994b7be75e7390b9e9f", Utils.DES_encrypt("en@Game Image Cache|zh-CN@游戏图片缓存|zh-TW@遊戲圖片緩存", DES_KEY));

        // 测试解密
        assertEquals("en@iFishing Lite|zh-CN@我钓鱼|zh-TW@我釣魚", Utils.DES_decrypt("455f4a25c622132ed3fc9de34efab26f27e7359753a6fd5473bb6db4cfead0ab7e352885f7b4645af92b165539eddfcf686c86a6bf005361", DES_KEY));
        assertEquals("en@MCam|zh-CN@好相机|zh-TW@好相機", Utils.DES_decrypt("d05faa30102f837c348747f4be24b4afac308fdcbf0e7a060158444e04943ffc51fda829cfc39c2a", DES_KEY));

    }

    @Test
    public void checkFirstPath() {
        /// 测试首行选取规则
        /// 测试多种情况

        // ID = 2
        assertEquals("12bda202183963a61093e23c26c3f692d2d3c3886d13b793"
                , MD5EncryptUtils.encrypt(DataBaseOp.getRootPath("easyme/bodybeauty")));

        // ID = 9
        assertEquals("121ba482ff3c03d01253f53326b7f87262d0e3bf6693849e"
                , MD5EncryptUtils.encrypt(DataBaseOp.getRootPath("android/data/com.elevenst"), DES_KEY));

        // ID = 1779
        assertEquals("52ccad52503493b71073ad3be6aff91236dad33e6173769a"
                , MD5EncryptUtils.encrypt(DataBaseOp.getRootPath("tencent/qzone")));

        // 仅仅测试了很少的情况
        // TODO

    }

    @Test
    public void checkMD5Path() {
        // ID = 2
        assertEquals("12bda202183963a61093e23c26c3f692d2d3c3886d13b793+2274aa222430835d1fa3123aa664fe321fd9433469f3c59b"
                , DataBaseOp.getAllPathMD5("easyme/bodybeauty"));

        // ID = 9
        assertEquals("8284afe28f3cf3cc1633cf3a4659feb2cfd8536d69f33f9d+623ba7520830436a1ae3d539a6c4f1d214d143b564a3dd9a+0272adf2b53383141a93463fe642f192dad1334f6c634e9c"
                , DataBaseOp.getAllPathMD5("android/data/com.elevenst"));

        // ID = 1779
        assertEquals("0291a522b03f93581e33063d26a0fbf20cd3030d6dc31998+9259a0b20a3ba30113f3753c36def392ebd583f468c3a891"
                , DataBaseOp.getAllPathMD5("tencent/qzone"));

    }

    @Test
    public void checkDataBase() {
        // 测试数据库数据是否一致
        // 1. load database
        // 2. check tables and create table sql
        // 3. check line count
        // 4. check row


        String oldDBPath = "";
        String newDBPath = "";

        SqliteDatabase base_database = DataBaseHelper.getWritableDatabase(oldDBPath);
        SqliteDatabase new_database = DataBaseHelper.getWritableDatabase(newDBPath);

        Connection conOld = base_database.getConnection();
        Connection conNew = new_database.getConnection();
        assertNotNull("old database connection should not null", conOld);
        assertNotNull("new database connection should not null", conNew);

        try {
            // check =create table sql
            checkTableInfo(conOld, conNew);

            // 判断表相等
            // 准则 先判断 count 是否相等， 然后从OLD取ID在New里面去查找 如果查找不到就报错
            // 计算必须表的row count
            checkTableRowCount(conOld, conNew, TrashClearDatabaseHelper.TABLE_ROOT_PATH);
            checkTableRowCount(conOld, conNew, TrashClearDatabaseHelper.TABLE_SUB_PATH);
            checkTableRowCount(conOld, conNew, TrashClearDatabaseHelper.TABLE_SUB_PATH_DESCRIPTION);
            checkTableRowCount(conOld, conNew, TrashClearDatabaseHelper.TABLE_APP_PKGNAME);
            checkTableRowCount(conOld, conNew, TrashClearDatabaseHelper.TABLE_APP_INFO);

            // 再从OLD表里面逐条比对

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkTableRowCount(Connection oldCon, Connection newCon, String tableName) throws SQLException {
        assertEquals(getTotalRowCount(oldCon, tableName)
                , getTotalRowCount(newCon, tableName));
    }


    public void checkTableInfo(Connection oldCon, Connection newCon) throws SQLException {
        Map<String, String> tableMapOld = loadTableInfo(oldCon);
        Map<String, String> tableMapNew = loadTableInfo(newCon);

        assertEquals(tableMapOld.size(), tableMapNew.size());

        for (String tableName : tableMapOld.keySet()) {
            assertTrue(tableMapNew.containsKey(tableName));
            assertEquals(tableMapOld.get(tableName), tableMapNew.get(tableName));
        }
    }

    public int getTotalRowCount(Connection con, String tableName) throws SQLException {
        String sql = String.format("select count(*) num from %s", tableName);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        int num = -1;
        if (rs.next()) {
            num =  rs.getInt("num");
        }
        rs.close();
        stmt.close();
        return num;
    }



    public Map<String, String> loadTableInfo(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String sql = "select * from sqlite_master where type = 'table'";
        ResultSet rs = stmt.executeQuery(sql);
        Map<String, String> map = new HashMap<String, String>();
        while (rs.next()) {
            String name = rs.getString("name");
            String create_sql = rs.getString("sql");
            map.put(name, create_sql);
        }
        rs.close();
        stmt.close();
        return map;
    }

}
