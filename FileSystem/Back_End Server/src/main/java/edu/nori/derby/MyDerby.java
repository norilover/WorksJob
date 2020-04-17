package edu.nori.derby;

import edu.nori.entity.FileInfo;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

/***
 * Derby数据库类
 */
public class MyDerby {
    private static Queue<FileInfo> alfi = new ArrayDeque<>(10);
    private static Statement statement;
    private static final String SCHEMA_NAME = "NoriDB";
    private static final String TABLE_NAME = "file_info";
    private static Connection conn;

    //【测试】
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, SQLException {
        connectSchema();
        createTable();
        FileInfo fileInfo = new FileInfo();
        fileInfo.fillData();
        //clearDataFromTable();
        //statement.execute("INSERT INTO file_system(raw_name, UUID_name, saved_dir, saved_time, encry_data, file_size, file_type) VALUES('nori','norilover', '/etc/sys/', CURRENT_TIMESTAMP, 'encryPart', '100M', 'file'");
        selectAll();
        insertFileInfo(fileInfo);
        selectAll();
    }

    /***
     * 向表中插入数据
     * @param fileInfo
     * @throws SQLException
     */
    public static void insertFileInfo(FileInfo fileInfo) throws SQLException {
        String sql = "INSERT INTO "+ TABLE_NAME +"(raw_name, UUID_name, saved_dir, saved_time, encry_data, file_size, file_type) VALUES(?,?,?, CURRENT_TIMESTAMP ,?,?,?)";
        //防止发生数据缺失
        synchronized (MyDerby.class){
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            //依次插入
            preparedStatement.setString(1,fileInfo.getRaw_name());
            preparedStatement.setString(2,fileInfo.getUUID_name());
            preparedStatement.setString(3,fileInfo.getSaved_dir());
            preparedStatement.setString(4,fileInfo.getEncry_data());
            preparedStatement.setString(5,fileInfo.getFile_size());
            preparedStatement.setString(6,fileInfo.getFile_type());

            //执行
            preparedStatement.execute();

            //【测试】
            System.out.println("数据库现有数据： ");
            System.out.println(fileInfo.toString());

            return;
        }
    }

    /***
     * 查询所有元数据
     * @return
     * @throws SQLException
     */
    public static List<FileInfo> selectAll() throws SQLException {
        List<FileInfo> fileInfoList = null;
        if(statement != null){
            String parameters = "id, raw_name, UUID_name, saved_dir, saved_time, encry_data, file_size, file_type";
            ResultSet resultSet = statement.executeQuery("SELECT " + parameters + " FROM " + TABLE_NAME);
            fileInfoList = traverseOutcome(resultSet);
        }

        return fileInfoList;
    }

    /***
     * 将结果存储
     * @param rs
     * @return
     * @throws SQLException
     */
    public static List<FileInfo> traverseOutcome(ResultSet rs) throws SQLException {
        List<FileInfo> fileInfoList = new ArrayList<>();
        //防止发生数据缺失
        synchronized (MyDerby.class){
            while (rs.next()) {
                FileInfo fileInfo = new FileInfo();
                fileInfoList.add(fileInfo);

                //得到数据
                int ind = 1;
                fileInfo.setId(rs.getInt(ind++));
                fileInfo.setRaw_name(rs.getString(ind++));
                fileInfo.setUUID_name(rs.getString(ind++));
                fileInfo.setSaved_dir(rs.getString(ind++));
                fileInfo.setSaved_time(rs.getTimestamp(ind++).toString());
                fileInfo.setEncry_data(rs.getString(ind++));
                fileInfo.setFile_size(rs.getString(ind++));
                fileInfo.setFile_type(rs.getString(ind++));
            }
        }

        return fileInfoList;
    }

    /***
     * 按UUID进行搜索
     * @param uuid
     */
    public static FileInfo selectByUUID(String uuid) throws SQLException {
        FileInfo fileInfo = new FileInfo();
        //防止发生数据缺失
        synchronized (MyDerby.class){
            if(statement != null){
                String parameters = "id, raw_name, UUID_name, saved_dir, saved_time, encry_data, file_size, file_type";
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT " + parameters + " FROM " + TABLE_NAME + " WHERE UUID_name = ?" );
                preparedStatement.setString(1, uuid);
                ResultSet resultSet = preparedStatement.executeQuery();
                fileInfo = traverseOutcome(resultSet).get(0);
            }
        }

        return fileInfo;
    }

    /***
     * 通过id进行搜索
     * @param id
     * @return
     * @throws SQLException
     */
    public static FileInfo selectById(String id) throws SQLException {
        FileInfo fileInfo = new FileInfo();
        //防止发生数据缺失
        synchronized (MyDerby.class){
            if(statement != null){
                String parameters = "id, raw_name, UUID_name, saved_dir, saved_time, encry_data, file_size, file_type";
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT " + parameters + " FROM " + TABLE_NAME + " WHERE id = ?" );
                preparedStatement.setString(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                fileInfo = traverseOutcome(resultSet).get(0);
            }
        }

        return fileInfo;
    }

    /***
     * 连接数据库
     * @throws SQLException
     */
    public static void connectSchema() throws SQLException {
        conn = DriverManager.getConnection(
                "jdbc:derby:" + SCHEMA_NAME +";create=true");
        statement = conn.createStatement();
    }

    /***
     * 创建表
     * @throws SQLException
     */
    public static void createTable() throws SQLException {
        synchronized (MyDerby.class){
            if(statement != null){
                String sql = "CREATE TABLE "+ TABLE_NAME +"(id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)primary key,raw_name VARCHAR(225) NOT NULL,UUID_name VARCHAR(225) NOT NULL,saved_dir VARCHAR(30) NOT NULL,saved_time TIMESTAMP NOT NULL,encry_data VARCHAR(225) NOT NULL,file_size VARCHAR(20) NOT NULL,file_type VARCHAR(20) NOT NULL)";
                statement.execute(sql);
            }
        }
    }

    /***
     *清空表中数据
     * @throws SQLException
     */
    public static void clearDataFromTable() throws SQLException {
        if(statement != null){
            statement.execute("DELETE FROM " + TABLE_NAME);
        }
    }

    /***
     * 删除表结构
     * @throws SQLException
     */
    public static void deleteTable() throws SQLException {
        if(statement != null){
            statement.execute("DROP TABLE " + TABLE_NAME);
        }
    }

}