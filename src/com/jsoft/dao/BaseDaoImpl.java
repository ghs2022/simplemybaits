package com.jsoft.dao;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class BaseDaoImpl<T> implements IBaseDao<T> {

    private static final DataSource DATA_SOURCE;
    static {
        Properties pros = new Properties();
        try {
            pros.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("druid.properties"));
            DATA_SOURCE = DruidDataSourceFactory.createDataSource(pros);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public Connection getConncetion() {
        try {
            return DATA_SOURCE.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public void closeAll(Connection conn, Statement stmt, ResultSet rt) {
        if(Objects.nonNull(rt)) {
            try {
                rt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(Objects.nonNull(stmt)) {
            try {
                stmt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(Objects.nonNull(conn)) {
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    @Override
    public List<T> findAll(Class<T> clazz) {
        // select * from tname;
        List<T> list = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder sqlStr = new StringBuilder();
        // 在开发中不建议使用"*"
        sqlStr.append("SELECT * FROM ");
        sqlStr.append(clazz.getSimpleName().toLowerCase());

        try(Connection conn = DATA_SOURCE.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlStr.toString());) {

            try (ResultSet rt = ps.executeQuery()) {
                while (rt.next()) {
                    T o = clazz.getDeclaredConstructor().newInstance();
                    for (Field field : fields) {
                        // 打破封装
                        field.setAccessible(true);
                        field.set(o,rt.getObject(field.getName().toLowerCase()));
                    }
                    // 将实体对象添加到list集合中
                    list.add(o);
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                throw new RuntimeException();
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }

        } catch (SQLException | IllegalAccessException | NoSuchMethodException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException();
        }

        return list;
    }

    @Override
    public T findOne(Class<T> clazz, String fieldName, Object value) {
        // select * from tname where fieldName=? limit 1;

        Field[] fields = clazz.getDeclaredFields();
        StringBuilder sqlStr = new StringBuilder();
        sqlStr.append("SELECT * FROM ");
        sqlStr.append(clazz.getSimpleName().toLowerCase());
        sqlStr.append(" WHERE ");
        sqlStr.append(fieldName);
        sqlStr.append("=");
        sqlStr.append("?");

        try(Connection conn = DATA_SOURCE.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlStr.toString());) {

            ps.setObject(1,value);
            // 如果存在将该记录封装的实体对象返回
            ResultSet rt = ps.executeQuery();
            if (rt.next()) {
                T o = clazz.getDeclaredConstructor().newInstance();
                for (Field field : fields) {
                    field.setAccessible(true);
                    field.set(o,rt.getObject(field.getName()));
                }
              return o;
            }

        } catch (SQLException | NoSuchMethodException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        // 如果不存在返回null
        return null;
    }

    @Override
    public void save(Object entity) {
        // insert into tname(f1,f2,f3) values(?,?,?);
        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder sqlStr = new StringBuilder();
        sqlStr.append("INSERT INTO ");
        sqlStr.append(clazz.getSimpleName().toLowerCase());
        sqlStr.append("(");
        for (Field field : fields) {
            sqlStr.append(field.getName().toLowerCase()).append(",");
        }
        sqlStr.deleteCharAt(sqlStr.length()-1);
        sqlStr.append(") ");
        sqlStr.append("VALUES(");
        for (Field field : fields) {
            sqlStr.append("?").append(",");
        }
        sqlStr.deleteCharAt(sqlStr.length()-1);
        sqlStr.append(")");

        try(Connection conn = DATA_SOURCE.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlStr.toString());) {

            for (int i = 0; i < fields.length; i++) {
                // 打破封装
                fields[i].setAccessible(true);
                ps.setObject(i+1,fields[i].get(entity));
            }
            ps.executeUpdate();

        } catch (SQLException | IllegalAccessException throwables) {
            throwables.printStackTrace();
        }


    }

    @Override
    public int update(Object entity, String fieldName, Object value) {
        // update tname set f1=v1,f2=v2 where fieldName=value;
        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder sqlStr = new StringBuilder();
        sqlStr.append("UPDATE ");
        sqlStr.append(clazz.getSimpleName().toLowerCase());
        sqlStr.append(" SET ");
        for (Field field : fields) {
            // 打破封装
            field.setAccessible(true);
            sqlStr.append(field.getName().toLowerCase());
            sqlStr.append("=?");
            sqlStr.append(",");
        }
        sqlStr.deleteCharAt(sqlStr.length()-1);
        sqlStr.append(" WHERE ");
        sqlStr.append(fieldName).append("=").append("?");
        System.out.println(sqlStr);

        try(Connection conn = DATA_SOURCE.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlStr.toString());) {

            // 给set后的占位符赋值
            for (int i = 0; i < fields.length; i++) {
                ps.setObject(i+1,fields[i].get(entity));
            }
            // 给where后的占位符赋值
            int index = ps.getParameterMetaData().getParameterCount()-1;
            ps.setObject(index+1,value);
            // 执行sql并返回结果
            return ps.executeUpdate();
        } catch (SQLException | IllegalAccessException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException();
        }

    }

    @Override
    public int delete(Class<T> clazz, String fieldName, Object value) throws Exception {
        return 0;
    }
}
