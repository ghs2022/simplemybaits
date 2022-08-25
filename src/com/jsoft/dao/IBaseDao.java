package com.jsoft.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;


/**
 *
 *
 *使用本接口需要遵循的约定
 *  1.要操作的数据库表必须有实体类
 *      * 类名与表名相同(可忽略大小写)
 *      * 属性和表字段一一对应(名称、类型) 注意：不要使用基本数据类型，实体类中不要有其他属性
 *      * 实体类要有全参构造器
 *  2.实体类中必须要有无参构造器(反射需要)
 *
 *  注意；只能单表操作
 *
 * @param <T> 要操作表的类对象
 */
public interface IBaseDao<T> {

    /**
     * 获取连接的方法
     * @return
     * @throws Exception
     */
    Connection getConncetion() throws Exception;


    /**
     * 释放所有资源
     * @param conn
     * @param stmt
     * @param rt
     * @throws Exception
     */
    void closeAll(Connection conn, Statement stmt, ResultSet rt) throws Exception;


    /**
     * 查找表中所有记录
     * @param clazz 要查找表对应的类对象
     * @return  返回存储表中所有数据放的List集合
     * @throws Exception
     */
    List<T> findAll(Class<T> clazz) throws Exception;

    /**
     * 根据字段等值条件(fieldName=value)查找表中的某条记录
     * @param clazz 要查找表对应的类对象
     * @param fieldName 字段名称
     * @param value 字段值
     * @return 返回由记录封装成相应的实体对象
     * @throws Exception
     */
    T findOne(Class<T> clazz, String fieldName, Object value) throws Exception;


    /**
     * 将记录插入到表中
     * @param entity 要插入记录对应的实体类对象
     * @throws Exception
     */
    void save(Object entity) throws Exception;


    /**
     *
     * 根据字段等值条件(fieldName=value)修改记录
     * @param entity 要查找表对应的实力类对象
     * @param fieldName 字段名称
     * @param value 字段值
     * @return 影响记录条数
     * @throws Exception
     */
    int update(Object entity, String fieldName, Object value) throws Exception;


    /**
     * 根据字段等值条件(fieldName=value)删除记录
     * @param clazz 要删除表对应的类对象
     * @param fieldName 字段名称
     * @param value 字段值
     * @return 影响记录条数
     * @throws Exception
     */
    int delete(Class<T> clazz, String fieldName, Object value) throws Exception;




}
