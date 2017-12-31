package com.gosun.hibernate.DO;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="table2")
public class Table2DO {
	@Id
	@Column(name="id")
	private Integer id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="table1_id")
	private Integer table1Id;
	
	@Transient
	private Table1DO table1;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Table1DO getTable1() {
		return table1;
	}

	public void setTable1(Table1DO table1) {
		this.table1 = table1;
	}

	public Integer getTable1Id() {
		return table1Id;
	}

	public void setTable1Id(Integer table1Id) {
		this.table1Id = table1Id;
	}

	@Override
	public String toString() {
		return "Table2DO [id=" + id + ", name=" + name + ", table1Id=" + table1Id + ", table1=" + table1 + "]";
	}
}
