package com.gosun.hibernate.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gosun.hibernate.query.param.DataBaseQueryParam;
import com.gosun.hibernate.query.param.DataBaseQueryParam.FormatType;
import com.gosun.hibernate.query.param.MySQLQueryParam;
import com.gosun.hibernate.query.param.PostgreSQLQueryParam;
import com.gosun.hibernate.server.SessionFactorySingleton;

/**
 * 数据库数据通道基类
 * 注意：该类不能用在SQLite上，请使用BaseSQLiteDao。因为在写数据库时，会有The database file is locked异常。
 * 
 * @author caixiaopeng
 *
 * @param <T>
 */
public class BaseDao<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseDao.class);
	/**
	 * 会话工厂
	 */
	protected SessionFactory sessionFactory = SessionFactorySingleton.getInstance();
	/**
	 * 实体类对应的class
	 */
	protected Class<T> tClass;

	public BaseDao() {
		// 获取泛型Class
		if (tClass == null) {
			Type genType = getClass().getGenericSuperclass();
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
			tClass = (Class<T>) params[0];
		}
	}

	/**
	 * 保存
	 * 返回主键值
	 * 
	 */
	public Object save(T t) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Object result = session.save(t);
		session.getTransaction().commit();
		session.close();
		return result;
	}

	/**
	 * 批量保存
	 * 
	 */
	public void saveAll(Collection<T> ts) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		for (T t : ts) {
			session.save(t);
		}
		session.getTransaction().commit();
		session.close();
	}
	
	/**
	 * 更新
	 * 
	 */
	public void update(T t){
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(t);
		session.getTransaction().commit();
		session.close();
	}
	
	/**
	 * 批量更新
	 * 
	 */
	public void updateAll(Collection<T> ts){
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		for(T t:ts){
			session.update(t);
		}
		session.getTransaction().commit();
		session.close();
	}
	
	/**
	 * 保存或者更新
	 * 
	 */
	public void saveOrUpdate(T t){
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.saveOrUpdate(t);
		session.getTransaction().commit();
		session.close();
	}
	
	/**
	 * 批量保存或者更新
	 * 
	 */
	public void saveOrUpdateAll(Collection<T> ts){
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		for(T t:ts){
			session.saveOrUpdate(t);
		}
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * 查询所有数据
	 * 
	 */
	public List<T> list() {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from " + tClass.getSimpleName());
		List<T> ts = query.list();
		session.close();
		return ts;
	}

	/**
	 * 单表基础查询
	 * 
	 * @param queryParam
	 * @return
	 */
	public List<T> listByHQL(DataBaseQueryParam queryParam) {
		String sql = queryParam.formatParam(tClass.getSimpleName(), FormatType.Alias);
		LOGGER.debug("BaseDao.listByHQL");
		LOGGER.debug("SQL：" + sql);
		LOGGER.debug("参数值：" + queryParam.getPreprocessedParamMap());

		Session session = sessionFactory.openSession();
		Query query = null;
		if (StringUtils.isBlank(queryParam.getSelect())) {
			// 不包含select子句
			query = session.createQuery(sql);
		} else {
			query = session.createQuery(sql).setResultTransformer(Transformers.aliasToBean(tClass));
		}
		queryParam.setQuery(query);
		List<T> ts = query.list();
		session.close();

		LOGGER.debug("查询结果：" + ts);
		return ts;
	}

	/**
	 * 单表查询，指定视图 必须由select子句，并且使用别名
	 * 
	 * @param queryParam
	 * @param vClass
	 * @return
	 */
	public <V> List<V> listByHQL(DataBaseQueryParam queryParam, Class<V> vClass) {
		assert vClass != null && StringUtils.isNotBlank(queryParam.getSelect());

		String sql = queryParam.formatParam(tClass.getSimpleName(), FormatType.None);
		LOGGER.debug("BaseDao.listByHQL");
		LOGGER.debug("SQL：" + sql);
		LOGGER.debug("参数值：" + queryParam.getPreprocessedParamMap());

		Session session = sessionFactory.openSession();
		Query query = session.createQuery(sql).setResultTransformer(Transformers.aliasToBean(vClass));
		queryParam.setQuery(query);
		List<V> vs = query.list();
		session.close();

		LOGGER.debug("查询结果：" + vs);
		return vs;
	}

	/**
	 * 单表基础查询
	 * 
	 * @param queryParam
	 * @return
	 */
	public List<Map<String, Object>> listByHQLToMap(DataBaseQueryParam queryParam) {
		String sql = queryParam.formatParam(tClass.getSimpleName(), FormatType.Map);
		LOGGER.debug("BaseDao.listByHQLToMap");
		LOGGER.debug("SQL：" + sql);
		LOGGER.debug("参数值：" + queryParam.getPreprocessedParamMap());

		Session session = sessionFactory.openSession();
		Query query = session.createQuery(sql);
		queryParam.setQuery(query);
		List<Map<String, Object>> ts = query.list();
		session.close();

		LOGGER.debug("查询结果：" + ts);
		return ts;
	}

	/**
	 * 单表基础查询
	 * 
	 * @param queryParam
	 * @return
	 */
	public List<List<Object>> listByHQLToList(DataBaseQueryParam queryParam) {
		String sql = queryParam.formatParam(tClass.getSimpleName(), FormatType.List);
		LOGGER.debug("BaseDao.listByHQLToList");
		LOGGER.debug("SQL：" + sql);
		LOGGER.debug("参数值：" + queryParam.getPreprocessedParamMap());

		Session session = sessionFactory.openSession();
		Query query = session.createQuery(sql);
		queryParam.setQuery(query);
		List<List<Object>> ts = query.list();
		session.close();

		LOGGER.debug("查询结果：" + ts);
		return ts;
	}

	/**
	 * 多表联结查询 指定视图，hql的select子句必须使用别名
	 * 
	 * @param hql
	 *            （必须）
	 * @param params
	 *            （可选）
	 * @param vClass
	 *            （必须）
	 * @return
	 */
	public <V> List<V> listMultitableByHQL(String hql, Map<String, Object> params, Class<V> vClass) {
		assert StringUtils.isNotBlank(hql) && hql.startsWith("select") && vClass != null;
		LOGGER.debug("BaseDao.listMultitableByHQL");
		LOGGER.debug("SQL：" + hql);
		LOGGER.debug("参数值：" + params);

		Session session = sessionFactory.openSession();
		Query query = session.createQuery(hql).setResultTransformer(Transformers.aliasToBean(vClass));
		setParams(query, params);
		List<V> vs = query.list();
		session.close();

		LOGGER.debug("查询结果：" + vs);
		return vs;
	}

	/**
	 * 多表联结查询
	 * 
	 * @param hql
	 *            格式：select new Map(a as aKey,b as bKey,c as cKey) ... （必须）
	 * @param params
	 *            （可选）
	 * @param vClass
	 *            （必须）
	 * @return
	 */
	public List<Map<String, Object>> listMultitableByHQLToMap(String hql, Map<String, Object> params) {
		assert StringUtils.isNotBlank(hql) && hql.startsWith("select");
		LOGGER.debug("BaseDao.listMultitableByHQLToMap");
		LOGGER.debug("SQL：" + hql);
		LOGGER.debug("参数值：" + params);

		Session session = sessionFactory.openSession();
		Query query = session.createQuery(hql);
		setParams(query, params);
		List<Map<String, Object>> vs = query.list();
		session.close();

		LOGGER.debug("查询结果：" + vs);
		return vs;
	}

	/**
	 * 多表联结查询
	 * 
	 * @param hql
	 *            格式：select new List(a,b,c) ... （必须）
	 * @param params
	 *            （可选）
	 * @param vClass
	 *            （必须）
	 * @return
	 */
	public List<List<Object>> listMultitableByHQLToList(String hql, Map<String, Object> params) {
		assert StringUtils.isNotBlank(hql) && hql.startsWith("select");
		LOGGER.debug("BaseDao.listMultitableByHQLToList");
		LOGGER.debug("SQL：" + hql);
		LOGGER.debug("参数值：" + params);

		Session session = sessionFactory.openSession();
		Query query = session.createQuery(hql);
		setParams(query, params);
		List<List<Object>> vs = query.list();
		session.close();

		LOGGER.debug("查询结果：" + vs);
		return vs;
	}

	/**
	 * 单表基础查询
	 * 
	 */
	public T getByHQL(DataBaseQueryParam queryParam) {
		String sql = queryParam.formatParam(tClass.getSimpleName(), FormatType.Alias);
		LOGGER.debug("BaseDao.getByHQL");
		LOGGER.debug("SQL：" + sql);
		LOGGER.debug("参数值：" + queryParam.getPreprocessedParamMap());

		Session session = sessionFactory.openSession();
		Query query = null;
		if (StringUtils.isBlank(queryParam.getSelect())) {
			// 不包含select子句
			query = session.createQuery(sql);
		} else {
			query = session.createQuery(sql).setResultTransformer(Transformers.aliasToBean(tClass));
		}
		queryParam.setQuery(query);
		T t = (T) query.uniqueResult();
		session.close();

		LOGGER.debug("查询结果：" + t);
		return t;
	}

	/**
	 * 单表查询，指定视图 必须由select子句，并且使用别名
	 * 
	 * @param queryParam
	 * @param vClass
	 * @return
	 */
	public <V> V getByHQL(DataBaseQueryParam queryParam, Class<V> vClass) {
		assert vClass != null && StringUtils.isNotBlank(queryParam.getSelect());

		String sql = queryParam.formatParam(tClass.getSimpleName(), FormatType.None);
		LOGGER.debug("BaseDao.getByHQL");
		LOGGER.debug("SQL：" + sql);
		LOGGER.debug("参数值：" + queryParam.getPreprocessedParamMap());

		Session session = sessionFactory.openSession();
		Query query = session.createQuery(sql).setResultTransformer(Transformers.aliasToBean(vClass));
		queryParam.setQuery(query);
		V v = (V) query.uniqueResult();
		session.close();

		LOGGER.debug("查询结果：" + v);
		return v;
	}

	/**
	 * 单表基础查询
	 * 
	 */
	public Map<String, Object> getByHQLToMap(DataBaseQueryParam queryParam) {
		String sql = queryParam.formatParam(tClass.getSimpleName(), FormatType.Map);
		LOGGER.debug("BaseDao.getByHQLToMap");
		LOGGER.debug("SQL：" + sql);
		LOGGER.debug("参数值：" + queryParam.getPreprocessedParamMap());

		Session session = sessionFactory.openSession();
		Query query = session.createQuery(sql);
		queryParam.setQuery(query);
		Map<String, Object> map = (Map<String, Object>) query.uniqueResult();
		session.close();

		LOGGER.debug("查询结果：" + map);
		return map;
	}

	/**
	 * 单表基础查询
	 * 
	 */
	public List<Object> getByHQLToList(DataBaseQueryParam queryParam) {
		String sql = queryParam.formatParam(tClass.getSimpleName(), FormatType.List);
		LOGGER.debug("BaseDao.getByHQLToList");
		LOGGER.debug("SQL：" + sql);
		LOGGER.debug("参数值：" + queryParam.getPreprocessedParamMap());

		Session session = sessionFactory.openSession();
		Query query = session.createQuery(sql);
		queryParam.setQuery(query);
		List<Object> list = (List<Object>) query.uniqueResult();
		session.close();

		LOGGER.debug("查询结果：" + list);
		return list;
	}

	/**
	 * 多表联结查询 指定视图，hql的select子句必须使用别名
	 * 
	 * @param hql
	 *            （必须）
	 * @param params
	 *            （可选）
	 * @param vClass
	 *            （必须）
	 * @return
	 */
	public <V> V getMultitableByHQL(String hql, Map<String, Object> params, Class<V> vClass) {
		assert StringUtils.isNotBlank(hql) && hql.startsWith("select") && vClass != null;
		LOGGER.debug("BaseDao.listMultitableByHQL");
		LOGGER.debug("SQL：" + hql);
		LOGGER.debug("参数值：" + params);

		Session session = sessionFactory.openSession();
		Query query = session.createQuery(hql).setResultTransformer(Transformers.aliasToBean(vClass));
		setParams(query, params);
		V v = (V) query.uniqueResult();
		session.close();

		LOGGER.debug("查询结果：" + v);
		return v;
	}

	/**
	 * 多表联结查询
	 * 
	 * @param hql
	 *            格式 select new Map(a as aKey,b as bKey,c as cKey) ... （必须）
	 * @param params
	 *            （可选）
	 * @param vClass
	 *            （必须）
	 * @return
	 */
	public Map<String, Object> getMultitableByHQLToMap(String hql, Map<String, Object> params) {
		assert StringUtils.isNotBlank(hql) && hql.startsWith("select");
		LOGGER.debug("BaseDao.listMultitableByHQL");
		LOGGER.debug("SQL：" + hql);
		LOGGER.debug("参数值：" + params);

		Session session = sessionFactory.openSession();
		Query query = session.createQuery(hql);
		setParams(query, params);
		Map<String, Object> map = (Map<String, Object>) query.uniqueResult();
		session.close();

		LOGGER.debug("查询结果：" + map);
		return map;
	}

	/**
	 * 多表联结查询
	 * 
	 * @param hql
	 *            格式 select new List(a,b,c) ... （必须）
	 * @param params
	 *            （可选）
	 * @param vClass
	 *            （必须）
	 * @return
	 */
	public List<Object> getMultitableByHQLToList(String hql, Map<String, Object> params) {
		assert StringUtils.isNotBlank(hql) && hql.startsWith("select");
		LOGGER.debug("BaseDao.listMultitableByHQL");
		LOGGER.debug("SQL：" + hql);
		LOGGER.debug("参数值：" + params);

		Session session = sessionFactory.openSession();
		Query query = session.createQuery(hql);
		setParams(query, params);
		List<Object> list = (List<Object>) query.uniqueResult();
		session.close();

		LOGGER.debug("查询结果：" + list);
		return list;
	}
	
	/**
	 * 通过id查询
	 * 
	 */
	public T getById(Serializable id){
		Session session = sessionFactory.openSession();
		T result=session.get(tClass, id);
		session.close();
		return result;
	}

	/**
	 * 单表计数
	 * 对于group by支持可能有异常
	 * 
	 */
	public long countByHQL(DataBaseQueryParam queryParam) {
		// 参数预处理
		queryParam.setSelect("count(*)");
		if (queryParam instanceof MySQLQueryParam) {
			((MySQLQueryParam) queryParam).setLimit(-1);
			((MySQLQueryParam) queryParam).setOffset(-1);
		} else if (queryParam instanceof PostgreSQLQueryParam) {
			((PostgreSQLQueryParam) queryParam).setLimit(-1);
			((PostgreSQLQueryParam) queryParam).setOffset(-1);
		}

		String sql = queryParam.formatParam(tClass.getSimpleName(), FormatType.None);
		LOGGER.debug("BaseDao.count");
		LOGGER.debug("SQL：" + sql);
		LOGGER.debug("参数值：" + queryParam.getPreprocessedParamMap());

		Session session = sessionFactory.openSession();
		Query query = session.createQuery(sql);
		queryParam.setQuery(query);
		Long count = null;
		if (StringUtils.isBlank(queryParam.getGroupBy())) {
			// 不包含group by
			count = (Long) query.iterate().next();
		} else {
			// 包含group by
			count = (long) query.list().size();
		}
		session.close();

		LOGGER.debug("查询结果：" + count);
		return count;
	}

	/**
	 * 多表计数
	 * 对于group by支持可能有异常
	 * 
	 * @param hql
	 *            不能带select子句
	 * @param params
	 * @return
	 */
	public long countByHQL(String hql, Map<String, Object> params) {
		assert StringUtils.isNotBlank(hql) && !hql.startsWith("select");

		hql = "select count(*) " + hql;
		LOGGER.debug("BaseDao.countByHQL");
		LOGGER.debug("SQL：" + hql);
		LOGGER.debug("参数值：" + params);

		Session session = sessionFactory.openSession();
		Query query = session.createQuery(hql);
		setParams(query, params);
		Long count = null;
		if (!hql.contains("group by")) {
			// 不包含group by
			count = (Long) query.iterate().next();
		} else {
			// 包含group by
			count = (long)query.list().size();
		}
		session.close();

		LOGGER.debug("查询结果：" + count);
		return count;
	}

	private void setParams(Query query, Map<String, Object> params) {
		if (params == null) {
			return;
		}
		for (Entry<String, Object> param : params.entrySet()) {
			if (param.getValue() instanceof Object[]) {
				// 数组参数
				query.setParameterList(param.getKey(), (Object[]) param.getValue());
			} else {
				query.setParameter(param.getKey(), param.getValue());
			}
		}
	}

}
