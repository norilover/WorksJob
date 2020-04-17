package edu.nori.entity;

import java.io.File;
import java.io.Serializable;

/**
 * 文件元数据类
 */
public class FileInfo implements Serializable {
    // 数据库保存数据
    private int id;
    private String raw_name;
    private String UUID_name;
    private String saved_dir;
    private String saved_time;
    private String encry_data;  // 数字信封
    private String file_size;
    private String file_type;

    // 源文件
    private File file;

    public FileInfo() {

    }

    public FileInfo(int id, String raw_name, String UUID_name, String saved_dir, String saved_time, String encry_data, String file_size, String file_type) {
        this.id = id;
        this.raw_name = raw_name;
        this.UUID_name = UUID_name;
        this.saved_dir = saved_dir;
        this.saved_time = saved_time;
        this.encry_data = encry_data;
        this.file_size = file_size;
        this.file_type = file_type;
        fillData();
    }

    // test function
    public void fillData() {
        this.id = -1;
        String test = "TEST";
        this.raw_name = test;
        this.UUID_name = test;
        this.saved_dir = test;
        this.saved_time = test;
        this.encry_data = test;
        this.file_size = test;
        this.file_type = test;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRaw_name() {
        return raw_name;
    }

    public void setRaw_name(String raw_name) {
        this.raw_name = raw_name;
    }

    public String getUUID_name() {
        return UUID_name;
    }

    public void setUUID_name(String UUID_name) {
        this.UUID_name = UUID_name;
    }

    public String getSaved_dir() {
        return saved_dir;
    }

    public void setSaved_dir(String saved_dir) {
        this.saved_dir = saved_dir;
    }

    public String getSaved_time() {
        return saved_time;
    }

    public void setSaved_time(String saved_time) {
        this.saved_time = saved_time;
    }

    public String getEncry_data() {
        return encry_data;
    }

    public void setEncry_data(String encry_data) {
        this.encry_data = encry_data;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "FileSystem{" +
                "id=" + id +
                ", raw_name='" + raw_name + '\'' +
                ", UUID_name='" + UUID_name + '\'' +
                ", saved_dir='" + saved_dir + '\'' +
                ", saved_time='" + saved_time + '\'' +
                ", encry_data='" + encry_data + '\'' +
                ", file_size='" + file_size + '\'' +
                ", file_type='" + file_type + '\'' +
                '}';
    }
}
