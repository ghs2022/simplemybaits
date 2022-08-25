package com.jsoft;

import com.jsoft.dao.DeptDao;
import com.jsoft.dao.EmpDao;
import com.jsoft.entiry.Dept;
import com.jsoft.entiry.Emp;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Properties;


/**
 * 测试Dao
 */
public class Demo {

    @Test
    public void testFindAll() throws  Exception{
        EmpDao empDao = new EmpDao();
        List<Emp> all = empDao.findAll(Emp.class);
        for (Emp emp : all) {
            System.out.println(emp);
        }
    }

    @Test
    public void testFindOne() throws Exception {
        EmpDao empDao = new EmpDao();
        Emp empno = empDao.findOne(Emp.class, "empno", 7369);
        System.out.println(empno);
    }

    @Test
    public void testSave() throws  Exception {
        DeptDao deptDao = new DeptDao();
        deptDao.save(new Dept(60,"a","a"));
    }


    @Test
    public void testUpdate() throws Exception{
        DeptDao deptDao = new DeptDao();
        deptDao.update(new Dept(60,"学习部","长春"),"deptno",60);
    }

    @Test
    public void test() {
        Class<Demo> clazz = Demo.class;
        Type genericSuperclass = clazz.getGenericSuperclass();
    }
}
