package com.example.shopping.repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.example.shopping.entity.Department;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class JdbcDepartmentRepository implements DepartmentRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final RowMapper<Department> rowMapper = (rs, rowNum) -> {
        Department d = new Department();
        d.setCode(rs.getInt("code"));
        d.setName(rs.getString("name"));
        d.setDiscountRate(rs.getBigDecimal("discount_rate"));
        d.setTaxRate(rs.getBigDecimal("tax_rate"));
        d.setDepartmentSales(rs.getBigDecimal("department_sales"));
        return d;
    };

    public JdbcDepartmentRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Department> findByCodes(List<Integer> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        }

        // null を除去して空になったら空リストを返す
        List<Integer> cleaned = codes.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (cleaned.isEmpty()) {
            return Collections.emptyList();
        }

        String sql = "SELECT code, name, discount_rate, tax_rate, department_sales FROM t_department WHERE code IN (:codes)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("codes", cleaned);

        return jdbc.query(sql, params, rowMapper);
    }

    @Override
    public int[] incrementDepartmentSales(Map<Integer, BigDecimal> increments) {
        if (increments == null || increments.isEmpty()) {
            return new int[0];
        }

        // フィルタして無効なエントリを除去
        List<MapSqlParameterSource> batch = increments.entrySet().stream()
                .filter(e -> e.getKey() != null && e.getValue() != null && e.getValue().compareTo(BigDecimal.ZERO) != 0)
                .map(e -> new MapSqlParameterSource()
                		.addValue("code", e.getKey())
                        .addValue("amount", e.getValue()))
                .collect(Collectors.toList());

        if (batch.isEmpty()) {
            return new int[0];
        }

        String sql = "UPDATE t_department SET department_sales = COALESCE(department_sales, 0) + :amount WHERE code = :code";
        MapSqlParameterSource[] batchArray = batch.toArray(new MapSqlParameterSource[0]);
        return jdbc.batchUpdate(sql, batchArray);
    }
}