package sqlite;

import info.RootPathInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by wangjia-s on 14-11-5.
 */
public class SqliteDatabase {

    private Connection mConnection;

    public SqliteDatabase(Connection conn) {
        mConnection = conn;
    }

    public boolean executeSqls(List<String> sqls) {
        try {
            Statement stmt = mConnection.createStatement();
            for (String sql : sqls) {
                stmt.execute(sql);
            }
            mConnection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Connection getConnection() {
        return mConnection;
    }


}
