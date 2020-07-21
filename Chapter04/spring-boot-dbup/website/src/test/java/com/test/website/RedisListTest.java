package com.test.website;

import com.test.mysql.entity.Department;
import com.test.mysql.redis.DepartmentRedis;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RedisConfig.class, DepartmentRedis.class})
public class RedisListTest {
    private static Logger logger = LoggerFactory.getLogger(RedisListTest.class);

    @Autowired
    DepartmentRedis departmentRedis;

    @Before
    public void setup(){
        Department department = new Department();
        department.setName("开发部");

        List<Department> departments = new ArrayList<>();
        departments.add(department);

        departmentRedis.delete(this.getClass().getName()+":departmentAll:");
        departmentRedis.add(this.getClass().getName()+":departmentAll:", 10L, departments);
    }

    @Test
    public void get(){
        List<Department> departments = departmentRedis.getList(this.getClass().getName() + ":departmentAll:");
        Assert.notNull(departments);
        for(Department department : departments) {
            logger.info("======department====== name:{}",
                    department.getName());
        }
    }
}
