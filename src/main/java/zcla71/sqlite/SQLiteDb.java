package zcla71.sqlite;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class SQLiteDb {
    private String path;

    public SQLiteDb(String path) {
        this.path = path;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + this.path);
    }

    public Collection<String> getTableNames(Connection conn) throws SQLException {
        Collection<String> result = new ArrayList<>();
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet tables = metaData.getTables(null, null, null, null);
        while (tables.next()) {
            result.add(tables.getString("TABLE_NAME"));
        }
        return result.stream().filter(tn -> !tn.startsWith("sqlite_")).toList();
    }

    public <T> Collection<T> getData(Connection conn, String tableName, Class<T> classe) throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException {
        Collection<T> result = new ArrayList<>();
        try (
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("select * from " + tableName);
        ) {
            ResultSetMetaData metaData = rs.getMetaData();
            while(rs.next()) {
                T object = classe.getDeclaredConstructor().newInstance();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String colName = metaData.getColumnName(i);
                    Object value = rs.getObject(colName);
                    Field field = classe.getDeclaredField(colName);
                    field.setAccessible(true);
                    field.set(object, value);
                }
                result.add(object);
            }
        }
        return result;
    }
}
