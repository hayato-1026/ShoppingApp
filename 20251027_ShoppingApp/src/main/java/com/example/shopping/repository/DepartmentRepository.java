package com.example.shopping.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.example.shopping.entity.Department;

public interface DepartmentRepository {
    List<Department> findByCodes(List<Integer> codes);
    int[] incrementDepartmentSales(Map<Integer, BigDecimal> increments);
}