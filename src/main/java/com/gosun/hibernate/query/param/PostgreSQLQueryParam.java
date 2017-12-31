package com.gosun.hibernate.query.param;

import org.hibernate.Query;

public class PostgreSQLQueryParam extends DataBaseQueryParam {
	/**
	 * 分页页面大小 取值为负时，忽略这个参数
	 */
	private int limit = -1;
	/**
	 * 分页偏移量，从0开始算起 取值为负时，忽略这个参数
	 */
	private int offset;

	@Override
	public void setQuery(Query query) {
		super.setQuery(query);
		if (limit >= 0) {
			query.setMaxResults(limit);
		}
		if (offset >= 0) {
			query.setFirstResult(offset);
		}
	}

	public int getLimit() {
		return limit;
	}

	public PostgreSQLQueryParam setLimit(int limit) {
		this.limit = limit;
		return this;
	}

	public int getOffset() {
		return offset;
	}

	public PostgreSQLQueryParam setOffset(int offset) {
		this.offset = offset;
		return this;
	}
}
