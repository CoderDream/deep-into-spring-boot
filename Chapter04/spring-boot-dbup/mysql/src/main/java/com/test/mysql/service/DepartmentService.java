package com.test.mysql.service;

import com.test.dbexpand.jpa.parameter.LinkEnum;
import com.test.dbexpand.jpa.parameter.Operator;
import com.test.dbexpand.jpa.parameter.PredicateBuilder;
import com.test.mysql.entity.Department;
import com.test.mysql.model.DepartmentQo;
import com.test.mysql.redis.DepartmentRedis;
import com.test.mysql.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private DepartmentRedis departmentRedis;


    @Cacheable(value = "mysql:findById:department", keyGenerator = "simpleKey")
    public Department findById(Long id) {
        return departmentRepository.findOne(id);
    }

    @CachePut(value = "mysql:findById:department", keyGenerator = "objectId")
    public Department create(Department department) {
        return departmentRepository.save(department);
    }

    @CachePut(value = "mysql:findById:department", keyGenerator = "objectId")
    public Department update(Department role) {
        return departmentRepository.save(role);
    }

    @CacheEvict(value = "mysql:findById:department", keyGenerator = "simpleKey")
    public void delete(Long id) {
        departmentRepository.delete(id);
    }

    public List<Department> findAll(){
        List<Department> departments = departmentRedis.getList("mysql:findAll:department");
        if(departments == null) {
            departments = departmentRepository.findAll();
            if(departments != null)
                departmentRedis.add("mysql:findAll:department", 5L, departments);
        }
        return departments;
    }

    public Page<Department> findPage(DepartmentQo departmentQo){
       Pageable pageable = new PageRequest(departmentQo.getPage(), departmentQo.getSize(), new Sort(Sort.Direction.ASC, "id"));

        PredicateBuilder pb  = new PredicateBuilder();

        if (!StringUtils.isEmpty(departmentQo.getName())) {
            pb.add("name","%" + departmentQo.getName() + "%", LinkEnum.LIKE);
        }

        Page<Department> pages = departmentRepository.findAll(pb.build(), Operator.AND, pageable);
        return pages;
    }

}
