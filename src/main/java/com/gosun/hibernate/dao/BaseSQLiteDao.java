package com.gosun.hibernate.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gosun.hibernate.query.param.DataBaseQueryParam;
import com.gosun.hibernate.query.param.MySQLQueryParam;
import com.gosun.hibernate.query.param.PostgreSQLQueryParam;
import com.gosun.util.ReflectUtils;
import com.gosun.hibernate.query.param.DataBaseQueryParam.FormatType;

/**
 * SQLite数据通道基类 针对写操作，进行类级读写锁
 * 
 * @author caixiaopeng
 *
 * @param <T>
 */
public class BaseSQLiteDao<T> extends BaseDao<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseSQLiteDao.class);
	protected static final ReadWriteLock LOCK = new ReentrantReadWriteLock();
	
	/**
	 * 保存 返回主键值
	 * 
	 */
	@Override
	public Object save(T t) {
		LOCK.writeLock().lock();
		Object result = super.save(t);
		LOCK.writeLock().unlock();
		return result;
	}

	/**
	 * 批量保存
	 */
	@Override
	public void saveAll(Collection<T> ts) {
		LOCK.writeLock().lock();
		super.saveAll(ts);
		LOCK.writeLock().unlock();
	}
	
	/**
	 * 原子保存 只插入not null字段
	 * 
	 */
	public void atomSave(T t) {
		StringBuilder hql = new StringBuilder("insert into " + tClass.getSimpleName());
		StringBuilder fieldNames=new StringBuilder("(");
		StringBuilder values=new StringBuilder(" values(");
		Map<String, Object> params = new HashMap<String, Object>();
		Field[] fields = tClass.getDeclaredFields();
		boolean isInsert = false; // 是否需要插入
		for (Field field : fields) {
			try {
				Method getter = ReflectUtils.obtainGetter(tClass, field);
				Object value = getter.invoke(t);
				if (value != null) {
					String fieldName = field.getName();
					fieldNames.append(fieldName).append(",");
					values.append("?").append(",");
					params.put(fieldName, value);
					isInsert = true;
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				LOGGER.error("原子更新，在解析实体类时出现异常",e);
				return ;
			}
		}
		
		if (isInsert) {
			hql.append(fieldNames.substring(0, fieldNames.length()-1)+")");
			hql.append(values.substring(0, values.length()-1)+")");
			LOCK.writeLock().lock();
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			Query query=session.createQuery(hql.toString());
			for (Entry<String, Object> param : params.entrySet()) {
				if (param.getValue() instanceof Object[]) {
					// 数组参数
					query.setParameterList(param.getKey(), (Object[]) param.getValue());
				} else {
					query.setParameter(param.getKey(), param.getValue());
				}
			}
			query.executeUpdate();
			session.getTransaction().commit();
			session.close();
			LOCK.writeLock().unlock();
		}
	}

	/**
	 * 更新
	 * 
	 */
	@Override
	public void update(T t) {
		LOCK.writeLock().lock();
		super.update(t);
		LOCK.writeLock().unlock();
	}

	/**
	 * 原子更新 只更新not null字段
	 * 
	 */
	public void atomUpdate(T t) {
		StringBuilder hql = new StringBuilder("update " + tClass.getSimpleName() + " set ");
		Map<String, Object> params = new HashMap<String, Object>();
		Field[] fields = tClass.getDeclaredFields();
		boolean isUpdate = false; // 是否需要更新
		for (Field field : fields) {
			try {
				Method getter = ReflectUtils.obtainGetter(tClass, field);
				Object value = getter.invoke(t);
				if (value != null) {
					String fieldName = field.getName();
					hql.append(fieldName).append(" = :").append(fieldName).append(",");
					params.put(fieldName, value);
					isUpdate = true;
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				LOGGER.error("原子更新，在解析实体类时出现异常",e);
				return ;
			}
		}
		
		if (isUpdate) {
			LOCK.writeLock().lock();
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			Query query=session.createQuery(hql.substring(0, hql.length()-1));
			for (Entry<String, Object> param : params.entrySet()) {
				if (param.getValue() instanceof Object[]) {
					// 数组参数
					query.setParameterList(param.getKey(), (Object[]) param.getValue());
				} else {
					query.setParameter(param.getKey(), param.getValue());
				}
			}
			query.executeUpdate();
			session.getTransaction().commit();
			session.close();
			LOCK.writeLock().unlock();
		}
	}

	/**
	 * 批量更新
	 * 
	 */
	@Override
	public void updateAll(Collection<T> ts) {
		LOCK.writeLock().lock();
		super.updateAll(ts);
		LOCK.writeLock().unlock();
	}

	/**
	 * 保存或者更新
	 * 
	 */
	public void saveOrUpdate(T t) {
		LOCK.writeLock().lock();
		super.saveOrUpdate(t);
		LOCK.writeLock().unlock();
	}

	/**
	 * 批量保存或者更新
	 * 
	 */
	public void saveOrUpdateAll(Collection<T> ts) {
		LOCK.writeLock().lock();
		super.saveOrUpdateAll(ts);
		LOCK.writeLock().unlock();
	}

	/**
	 * 查询所有数据
	 * 
	 */
	public List<T> list() {
		LOCK.readLock().lock();
		List<T> result = super.list();
		LOCK.readLock().unlock();
		return result;
	}

	/**
	 * 单表基础查询
	 * 
	 * @param queryParam
	 * @return
	 */
	public List<T> listByHQL(DataBaseQueryParam queryParam) {
		LOCK.readLock().lock();
		List<T> result = super.listByHQL(queryParam);
		LOCK.readLock().unlock();
		return result;
	}

	/**
	 * 单表查询，指定视图 必须由select子句，并且使用别名
	 * 
	 * @param queryParam
	 * @param vClass
	 * @return
	 */
	public <V> List<V> listByHQL(DataBaseQueryParam queryParam, Class<V> vClass) {
		LOCK.readLock().lock();
		List<V> result = super.listByHQL(queryParam, vClass);
		LOCK.readLock().unlock();
		return result;
	}

	/**
	 * 单表基础查询
	 * 
	 * @param queryParam
	 * @return
	 */
	public List<Map<String, Object>> listByHQLToMap(DataBaseQueryParam queryParam) {
		LOCK.readLock().lock();
		List<Map<String, Object>> result = super.listByHQLToMap(queryParam);
		LOCK.readLock().unlock();
		return result;
	}

	/**
	 * 单表基础查询
	 * 
	 * @param queryParam
	 * @return
	 */
	public List<List<Object>> listByHQLToList(DataBaseQueryParam queryParam) {
		LOCK.readLock().lock();
		List<List<Object>> result = super.listByHQLToList(queryParam);
		LOCK.readLock().unlock();
		return result;
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
		LOCK.readLock().lock();
		List<V> result = super.listMultitableByHQL(hql, params, vClass);
		LOCK.readLock().unlock();
		return result;
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
		LOCK.readLock().lock();
		List<Map<String, Object>> result = super.listMultitableByHQLToMap(hql, params);
		LOCK.readLock().unlock();
		return result;
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
		LOCK.readLock().lock();
		List<List<Object>> result = super.listMultitableByHQLToList(hql, params);
		LOCK.readLock().unlock();
		return result;
	}

	/**
	 * 单表基础查询
	 * 
	 */
	public T getByHQL(DataBaseQueryParam queryParam) {
		LOCK.readLock().lock();
		T result = super.getByHQL(queryParam);
		LOCK.readLock().unlock();
		return result;
	}

	/**
	 * 单表查询，指定视图 必须由select子句，并且使用别名
	 * 
	 * @param queryParam
	 * @param vClass
	 * @return
	 */
	public <V> V getByHQL(DataBaseQueryParam queryParam, Class<V> vClass) {
		LOCK.readLock().lock();
		V result = super.getByHQL(queryParam, vClass);
		LOCK.readLock().unlock();
		return result;
	}

	/**
	 * 单表基础查询
	 * 
	 */
	public Map<String, Object> getByHQLToMap(DataBaseQueryParam queryParam) {
		LOCK.readLock().lock();
		Map<String, Object> result = super.getByHQLToMap(queryParam);
		LOCK.readLock().unlock();
		return result;
	}

	/**
	 * 单表基础查询
	 * 
	 */
	public List<Object> getByHQLToList(DataBaseQueryParam queryParam) {
		LOCK.readLock().lock();
		List<Object> result = super.getByHQLToList(queryParam);
		LOCK.readLock().unlock();
		return result;
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
		LOCK.readLock().lock();
		V result = super.getMultitableByHQL(hql, params, vClass);
		LOCK.readLock().unlock();
		return result;
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
		LOCK.readLock().lock();
		Map<String, Object> result = super.getMultitableByHQLToMap(hql, params);
		LOCK.readLock().unlock();
		return result;
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
		LOCK.readLock().lock();
		List<Object> result = super.getMultitableByHQLToList(hql, params);
		LOCK.readLock().unlock();
		return result;
	}

	/**
	 * 通过id查询
	 * 
	 */
	public T getById(Serializable id) {
		LOCK.readLock().lock();
		T result = super.getById(id);
		LOCK.readLock().unlock();
		return result;
	}

	/**
	 * 单表计数 对于group by支持可能有异常
	 * 
	 */
	public long countByHQL(DataBaseQueryParam queryParam) {
		LOCK.readLock().lock();
		long result = super.countByHQL(queryParam);
		LOCK.readLock().unlock();
		return result;
	}

	/**
	 * 多表计数 对于group by支持可能有异常
	 * 
	 * @param hql
	 *            不能带select子句
	 * @param params
	 * @return
	 */
	public long countByHQL(String hql, Map<String, Object> params) {
		LOCK.readLock().lock();
		long result = super.countByHQL(hql, params);
		LOCK.readLock().unlock();
		return result;
	}
}
