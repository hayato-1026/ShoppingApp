package com.example.shopping.entity;

import java.math.BigDecimal;

public class Department {
	private Integer code;
	private String name;
	private BigDecimal discountRate;
	private BigDecimal taxRate;
	private BigDecimal departmentSales;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(BigDecimal discountRate) {
		this.discountRate = discountRate;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public BigDecimal getDepartmentSales() {
		return departmentSales;
	}

	public void setDepartmentSales(BigDecimal departmentSales) {
		this.departmentSales = departmentSales;
	}

}
