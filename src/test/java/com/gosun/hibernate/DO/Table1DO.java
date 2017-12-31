package com.gosun.hibernate.DO;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="table1")
public class Table1DO {
	@Id
	@GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
	@Column(name="id")
	private Integer id;
	
	@Column(name="varchar_value")
	private String varcharValue;
	
	@Column(name="int_value")
	private Integer intValue;
	
	@Transient
	private List<Table2DO> table2s;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getVarcharValue() {
		return varcharValue;
	}

	public void setVarcharValue(String varcharValue) {
		this.varcharValue = varcharValue;
	}

	public Integer getIntValue() {
		return intValue;
	}

	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}
	
	public List<Table2DO> getTable2s() {
		return table2s;
	}

	public void setTable2s(List<Table2DO> table2s) {
		this.table2s = table2s;
	}

	@Override
	public String toString() {
		return "Table1DO [id=" + id + ", varcharValue=" + varcharValue + ", intValue=" + intValue + ", table2s="
				+ table2s + "]";
	}
}
