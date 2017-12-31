package com.gosun.hibernate.dao;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.gosun.hibernate.DO.Table1DO;
import com.gosun.hibernate.DO.Table1View;
import com.gosun.hibernate.DO.Table2View;
import com.gosun.hibernate.query.param.DataBaseQueryParam;
import com.gosun.hibernate.query.param.MySQLQueryParam;

/**
 * @version 1
 * @author caixiaopeng
 *
 */
public class BaseDaoTestCase {
	private Table1Dao table1Dao = new Table1Dao();

	@Test
	public void testSave() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testList() {
		List<Table1DO> table1s = table1Dao.list();
		boolean result = true;
		if (table1s.size() != 6) {
			result = false;
		}
		assertTrue(result);
	}

	/**
	 * 测试单表查询
	 */
	@Test
	public void testListByHQLDataBaseQueryParam() {
		MySQLQueryParam queryParam = new MySQLQueryParam();
		queryParam.setLimit(2)
				.setOffset(1)
				.setSelect("id,varcharValue")
				.setWhere("intValue=:intValue")
				.setOrderBy("id desc")
				.addParam("intValue", 1);
		List<Table1DO> table1s = table1Dao.listByHQL(queryParam);
		boolean result = true;
		if (table1s.size() == 2) {
			if (!table1s.get(0).getId().equals(2) || !table1s.get(1).getId().equals(1)) {
				result = false;
			}
		} else {
			result = false;
		}
		assertTrue(result);
	}

	/**
	 * 测试单表查询，指定视图
	 */
	@Test
	public void testListByHQLDataBaseQueryParamClassOfV() {
		MySQLQueryParam queryParam = new MySQLQueryParam();
		queryParam.setSelect("max(id) as maxId,intValue as intValue")
				.setGroupBy("intValue")
				.setOrderBy("id desc");
		List<Table1View> table1Views=table1Dao.listByHQL(queryParam, Table1View.class);
		boolean result = true;
		if (table1Views.size() == 2) {
			if (!table1Views.get(0).getMaxId().equals(6) || !table1Views.get(1).getMaxId().equals(3)) {
				result = false;
			}
		} else {
			result = false;
		}
		assertTrue(result);
	}

	
	@Test
	public void testListByHQLToMap() {
		MySQLQueryParam queryParam = new MySQLQueryParam();
		queryParam.setLimit(2)
				.setOffset(1)
				.setSelect("id,varcharValue")
				.setWhere("intValue=:intValue")
				.setOrderBy("id desc")
				.addParam("intValue", 1);
		List<Map<String, Object>> table1s = table1Dao.listByHQLToMap(queryParam);
		boolean result = true;
		if (table1s.size() == 2) {
			if (!table1s.get(0).get("id").equals(2) || !table1s.get(1).get("id").equals(1)) {
				result = false;
			}
		} else {
			result = false;
		}
		assertTrue(result);
	}

	@Test
	public void testListByHQLToList() {
		MySQLQueryParam queryParam = new MySQLQueryParam();
		queryParam.setLimit(2)
				.setOffset(1)
				.setSelect("id,varcharValue")
				.setWhere("intValue=:intValue")
				.setOrderBy("id desc")
				.addParam("intValue", 1);
		List<List<Object>> table1s = table1Dao.listByHQLToList(queryParam);
		boolean result = true;
		if (table1s.size() == 2) {
			if (!table1s.get(0).get(0).equals(2) || !table1s.get(1).get(0).equals(1)) {
				result = false;
			}
		} else {
			result = false;
		}
		assertTrue(result);
	}

	@Test
	public void testListMultitableByHQL() {
		String hql="select  table2.id as id,table2.name as name,table2.table1Id as table1Id,table1.varcharValue as table1VarcharValue "
				+ "from Table2DO as table2,Table1DO as table1 "
				+ "where table2.table1Id=table1.id";
		List<Table2View> table2View=table1Dao.listMultitableByHQL(hql, null, Table2View.class);
		boolean result = true;
		if(!"NAME1".equals(table2View.get(0).getName())||!"a1".equals(table2View.get(0).getTable1VarcharValue())){
			result=false;
		}
		assertTrue(result);
	}

	@Test
	public void testListMultitableByHQLToMap() {
		String hql="select  new Map(table2.id as id,table2.name as name,table2.table1Id as table1Id,table1.varcharValue as table1VarcharValue) "
				+ "from Table2DO as table2,Table1DO as table1 "
				+ "where table2.table1Id=table1.id";
		List<Map<String,Object>> table2View=table1Dao.listMultitableByHQLToMap(hql, null);
		boolean result = true;
		if(!"NAME1".equals(table2View.get(0).get("name"))||!"a1".equals(table2View.get(0).get("table1VarcharValue"))){
			result=false;
		}
		assertTrue(result);
	}

	@Test
	public void testListMultitableByHQLToList() {
		String hql="select  new List(table2.id,table2.name,table2.table1Id,table1.varcharValue) "
				+ "from Table2DO as table2,Table1DO as table1 "
				+ "where table2.table1Id=table1.id";
		List<List<Object>> table2View=table1Dao.listMultitableByHQLToList(hql, null);
		boolean result = true;
		if(!"NAME1".equals(table2View.get(0).get(1))||!"a1".equals(table2View.get(0).get(3))){
			result=false;
		}
		assertTrue(result);
	}

	@Test
	public void testGetByHQLDataBaseQueryParam() {
		MySQLQueryParam queryParam=new MySQLQueryParam();
		queryParam.setLimit(1)
			.setOffset(1)
			.setSelect("id,varcharValue")
			.setWhere("intValue=:intValue")
			.setOrderBy("id desc")
			.addParam("intValue", 1);
		Table1DO table1=table1Dao.getByHQL(queryParam);
		boolean result = true;
		if(!table1.getId().equals(2)){
			result=false;
		}
		assertTrue(result);
	}

	@Test
	public void testGetByHQLDataBaseQueryParamClassOfV() {
		MySQLQueryParam queryParam = new MySQLQueryParam();
		queryParam.setLimit(1)
				.setOffset(1)
				.setSelect("max(id) as maxId,intValue as intValue")
				.setGroupBy("intValue")
				.setOrderBy("id desc");
		List<Table1View> table1Views=table1Dao.listByHQL(queryParam, Table1View.class);
		boolean result = true;
		if (table1Views.size() == 1) {
			if (!table1Views.get(0).getMaxId().equals(3)){
				result = false;
			}
		} else {
			result = false;
		}
		assertTrue(result);
	}

	@Test
	public void testGetByHQLToMap() {
		MySQLQueryParam queryParam=new MySQLQueryParam();
		queryParam.setLimit(1)
			.setOffset(1)
			.setSelect("id,varcharValue")
			.setWhere("intValue=:intValue")
			.setOrderBy("id desc")
			.addParam("intValue", 1);
		Map<String,Object> table1=table1Dao.getByHQLToMap(queryParam);
		boolean result = true;
		if(!table1.get("id").equals(2)){
			result=false;
		}
		assertTrue(result);
	}

	@Test
	public void testGetByHQLToList() {
		MySQLQueryParam queryParam=new MySQLQueryParam();
		queryParam.setLimit(1)
			.setOffset(1)
			.setSelect("id,varcharValue")
			.setWhere("intValue=:intValue")
			.setOrderBy("id desc")
			.addParam("intValue", 1);
		List<Object> table1=table1Dao.getByHQLToList(queryParam);
		boolean result = true;
		if(!table1.get(0).equals(2)){
			result=false;
		}
		assertTrue(result);
	}

	@Test
	public void testGetMultitableByHQL() {
		String hql="select  table2.id as id,table2.name as name,table2.table1Id as table1Id,table1.varcharValue as table1VarcharValue "
				+ "from Table2DO as table2,Table1DO as table1 "
				+ "where table2.table1Id=table1.id";
		Table2View table2View=table1Dao.getMultitableByHQL(hql, null, Table2View.class);
		boolean result = true;
		if(!"NAME1".equals(table2View.getName())||!"a1".equals(table2View.getTable1VarcharValue())){
			result=false;
		}
		assertTrue(result);
	}

	@Test
	public void testGetMultitableByHQLToMap() {
		String hql="select  new Map(table2.id as id,table2.name as name,table2.table1Id as table1Id,table1.varcharValue as table1VarcharValue) "
				+ "from Table2DO as table2,Table1DO as table1 "
				+ "where table2.table1Id=table1.id";
		Map<String,Object> table2View=table1Dao.getMultitableByHQLToMap(hql, null);
		boolean result = true;
		if(!"NAME1".equals(table2View.get("name"))||!"a1".equals(table2View.get("table1VarcharValue"))){
			result=false;
		}
		assertTrue(result);
	}

	@Test
	public void testGetMultitableByHQLToList() {
		String hql="select  new List(table2.id as id,table2.name as name,table2.table1Id as table1Id,table1.varcharValue as table1VarcharValue) "
				+ "from Table2DO as table2,Table1DO as table1 "
				+ "where table2.table1Id=table1.id";
		List<Object> table2View=table1Dao.getMultitableByHQLToList(hql, null);
		boolean result = true;
		if(!"NAME1".equals(table2View.get(1))||!"a1".equals(table2View.get(3))){
			result=false;
		}
		assertTrue(result);
	}

	@Test
	public void testCountByHQLDataBaseQueryParam() {
		MySQLQueryParam queryParam=new MySQLQueryParam();
		queryParam.setSelect("max(id) as maxId,intValue as intValue")
		.setGroupBy("intValue")
		.setOrderBy("id desc");
		Long count=table1Dao.countByHQL(queryParam);
		boolean result = true;
		if(count!=2){
			result=false;
		}
		assertTrue(result);
	}

	@Test
	public void testCountByHQLStringMapOfStringObject() {
		String hql="from Table2DO as table2,Table1DO as table1 "
				+ "where table2.table1Id=table1.id ";
		long count = table1Dao.countByHQL(hql, null);
		boolean result = true;
		if(count!=1){
			result=false;
		}
		assertTrue(result);
		
	}

}
