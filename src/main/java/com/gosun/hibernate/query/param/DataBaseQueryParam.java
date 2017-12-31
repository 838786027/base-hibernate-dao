package com.gosun.hibernate.query.param;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;

/**
 * 单表查询参数
 * @author caixiaopeng
 *
 */
public class DataBaseQueryParam {
	/**
	 * select子句
	 */
	private String select;
	/**
	 * where子句
	 */
	private String where;
	/**
	 * 分组
	 */
	private String groupBy;
	/**
	 * having子句
	 */
	private String having;
	/**
	 * 排序
	 */
	private String orderBy;
	/**
	 * 预处理SQL的参数
	 */
	private final Map<String, Object> preprocessedParamMap = new HashMap<String, Object>();

	/**
	 * 格式化参数
	 * 
	 */
	public String formatParam(String tableName,FormatType formatType) {

		StringBuilder strBuilder = new StringBuilder("");
		if (StringUtils.isNotBlank(select)) {
			StringBuilder selectBuilder=new StringBuilder();
			switch (formatType) {
			case Alias:
				// 由于采用AliasToBeanResultTransformer进行结果集转换，所以select子句的字段必须使用alias别名
				for(String field:select.split(",")){
					if(selectBuilder.length()!=0){
						selectBuilder.append(",");
					}
					if(!field.contains("(")&&!field.contains(")")&&!field.contains(" as ")){
						// 对于包含as，函数的不操作
						String[] temp=field.split(".");
						if(temp.length==0){
							selectBuilder.append(field).append(" as ").append(field);
						}else{
							selectBuilder.append(field).append(" as ").append(temp[temp.length-1]);
						}
					}else{
						selectBuilder.append(field);
					}
				}
				break;
			case List:
				selectBuilder.append("new List(").append(select).append(")");
				break;
			case Map:
				for(String field:select.split(",")){
					if(selectBuilder.length()!=0){
						selectBuilder.append(",");
					}
					if(!field.contains("(")&&!field.contains(")")&&!field.contains(" as ")){
						// 对于包含as，函数的不操作
						String[] temp=field.split(".");
						if(temp.length==0){
							selectBuilder.append(field).append(" as ").append(field);
						}else{
							selectBuilder.append(field).append(" as ").append(temp[temp.length-1]);
						}
					}else{
						selectBuilder.append(field);
					}
				}
				selectBuilder.insert(0, "new Map(").append(")");
				break;
			case None:
				selectBuilder.append(select);
				break;
			}
			strBuilder.append(" select ").append(selectBuilder.toString());
		}
		strBuilder.append(" from ").append(tableName);
		if (StringUtils.isNotBlank(where)) {
			strBuilder.append(" where ").append(where);
		}
		if (StringUtils.isNotBlank(groupBy)) {
			strBuilder.append(" group by ").append(groupBy);
		}
		if (StringUtils.isNotBlank(having)) {
			strBuilder.append(" having ").append(having);
		}
		if (StringUtils.isNotBlank(orderBy)) {
			strBuilder.append(" order by ").append(orderBy);
		}
		return strBuilder.toString();
	}

	/**
	 * 添加预处理SQL的参数
	 * 
	 */
	public DataBaseQueryParam addParam(String paramName, Object paramValue) {
		assert StringUtils.isNotBlank(paramName) && paramValue != null;
		preprocessedParamMap.put(paramName, paramValue);
		return this;
	}
	
	public void setQuery(Query query){
		for (Entry<String, Object> param : preprocessedParamMap.entrySet()) {
			if (param.getValue() instanceof Object[]) {
				// 数组参数
				query.setParameterList(param.getKey(), (Object[]) param.getValue());
			} else {
				query.setParameter(param.getKey(), param.getValue());
			}
		}
	}
	
	public enum FormatType{
		/**
		 * 别名
		 */
		Alias
		/**
		 * 数组
		 */
		,List
		/**
		 * Map
		 */
		,Map
		/**
		 * 不作处理
		 */
		,None
	}
	
	public Map<String, Object> getPreprocessedParamMap() {
		return preprocessedParamMap;
	}

	public String getWhere() {
		return where;
	}

	public DataBaseQueryParam setWhere(String where) {
		this.where = where;
		return this;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public DataBaseQueryParam setGroupBy(String groupBy) {
		this.groupBy = groupBy;
		return this;
	}

	public String getHaving() {
		return having;
	}

	public DataBaseQueryParam setHaving(String having) {
		this.having = having;
		return this;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public DataBaseQueryParam setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	public String getSelect() {
		return select;
	}

	public DataBaseQueryParam setSelect(String select) {
		this.select = select;
		return this;
	}
}
