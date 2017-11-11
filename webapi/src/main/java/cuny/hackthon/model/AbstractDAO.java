package cuny.hackthon.model;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cuny.hackthon.datautils.Column;
import cuny.hackthon.datautils.Table;

public abstract class AbstractDAO<T extends DataObject<PK>, PK> {
	
	protected Logger logger;
	
	protected QueryRunner runner;
	private static ObjectMapper mapper = new ObjectMapper();
	
	private static Pattern getterReg = Pattern.compile("^get(?!Class)[A-Z]\\w*$");
	
	public static Predicate<Method> isGetter = (p)-> getterReg.matcher(p.getName()).matches() &&
													 !Modifier.isVolatile(p.getModifiers());
	
	protected static final String INSERT_TEMPLATE = "INSERT INTO %s (%s) VALUES(%s);";
	protected static final String UPDATE_TEMPLATE = "UPDATE %s SET %s WHERE %s;";
	protected static final String DELETE_TEMPLATE = "DELETE FROM %s WHERE %s;";
	protected static final String SELECT_TEMPLATE = "SELECT %s FROM %s WHERE %s";
	
	private Class<T[]> arrayType;
	private Class<T> beanType;
	
	static {
		mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}
	
	@SuppressWarnings("unchecked")
	public AbstractDAO(DataSource ds) {
		runner = new QueryRunner(ds);
		beanType = getType();
		logger = LoggerFactory.getLogger(beanType);
		arrayType = (Class<T[]>) Array.newInstance(beanType, 0).getClass();
	}
	
	protected String getBeanTableName() {
		return beanType.getAnnotation(Table.class).value();
	}
	
	protected List<String> loadFields() {
		return loadFields(true);
	}
	
	protected List<String> loadFields(boolean includePK) {
		List<String> selectFields = new LinkedList<>();
		if(includePK) selectFields.add("id");
		Method[] methods = Arrays.stream(beanType.getMethods())
								 .filter(isGetter)
								 .filter(p->p.isAnnotationPresent(Column.class))
								 .toArray(Method[]::new);
		for(Method method : methods) {
			boolean isPK = method.getAnnotation(Column.class).primaryKey();
			//ignore pk
			if(isPK) continue;
			String colName = method.getAnnotation(Column.class).value();
			if(colName.isEmpty())
				selectFields.add(getterNameToName(method.getName()));
		}
		Field[] fields = Arrays.stream(beanType.getDeclaredFields())
							   .filter(p->p.isAnnotationPresent(Column.class))
							   .toArray(Field[]::new);
		for(Field f : fields) {
			boolean isPK = f.getAnnotation(Column.class).primaryKey();
			//ignore
			if(isPK) continue;
			selectFields.add(f.getName());
		}
		return selectFields;
	}
	
	protected void loadParams(Collection<String> fields, T bean, Object[] params) throws Exception {
		int index = 0;
		Class<?>[] sigParams = new Class<?>[0];
		for(String name : fields) {
			Object value = null;
			Field field = bean.getClass().getDeclaredField(name);
			if(field != null) {
				field.setAccessible(true);
				Column meta = field.getAnnotation(Column.class);
				if(meta != null) value = meta.autoVaule().newInstance().get();
				if(value == null) value = field.get(bean);
			}
			String methodName = nameToGetterName(name);
			Method method = bean.getClass().getMethod(methodName, sigParams);
			if(method != null) {
				Column meta = method.getAnnotation(Column.class);
				if(meta != null) value = meta.autoVaule().newInstance().get();
				if(value == null) value = method.invoke(bean, new Object[0]);
			}
			params[index++] = value;
		}
	}
	
	protected Object[] loadParamsForInsert(T bean) throws Exception {
		List<String> fields = loadFields(false);
		Object[] params = new Object[fields.size()];
		loadParams(fields, bean, params);
		return params;
	}
	
	protected Object[] loadParamsForUpdate(T bean) throws Exception {
		List<String> fields = loadFields(false);
		Object[] params = new Object[fields.size()+1];
		loadParams(fields, bean, params);
		params[fields.size()] = bean.getId();
		return params;
	}
	
	protected Map<String, Object> beanToMap(T bean) throws Exception {
		Map<String, Object> map = new HashMap<>();
		Method[] methods = Arrays.stream(bean.getClass().getMethods())
								 .filter(isGetter)
								 .filter(p->p.isAnnotationPresent(Column.class))
								 .toArray(Method[]::new);
		for(Method method : methods) {
			method.setAccessible(true);
			boolean isPK = method.getAnnotation(Column.class).primaryKey();
			//ignore pk
			if(isPK) continue;
			String colName = method.getAnnotation(Column.class).value();
			if(colName.isEmpty())
				colName = getterNameToName(method.getName());
			Object value = null;
			Column meta = method.getAnnotation(Column.class);
			if(meta != null)
				value = meta.autoVaule().newInstance().get();
			if(value == null) map.put(colName, method.invoke(bean, new Object[0]));
			else 			  map.put(colName, value);
		}
		Field[] fields = Arrays.stream(bean.getClass().getDeclaredFields())
							   .filter(p->p.isAnnotationPresent(Column.class))
							   .toArray(Field[]::new);
		for(Field f : fields) {
			f.setAccessible(true);
			boolean isPK = f.getAnnotation(Column.class).primaryKey();
			//ignore
			if(isPK) continue;
			Object value = null;
			Column meta = f.getAnnotation(Column.class);
			if(meta != null)
				value = meta.autoVaule().newInstance().get();
			if(value == null) map.put(f.getName(), f.get(bean));
			else 			  map.put(f.getName(), value);
		}		
		return map;
	}
	
	protected String equalsToId() {
		return "id = ?";
	}
	
	protected String buildInsertTemplate(Collection<String> keys) {
		boolean first = true;
		StringBuilder fields = new StringBuilder();
		StringBuilder placeHolder = new StringBuilder();
		for(String key : keys) {
			if(!first) {
				fields.append(',');
				placeHolder.append(',');
			}
			fields.append(key);
			placeHolder.append('?');
			first = false;
		}
		return String.format(INSERT_TEMPLATE, getBeanTableName(), fields.toString(), placeHolder.toString());
	}
	
	protected String buildUpdateTemplate(Collection<String> keys) {
		boolean first = true;
		StringBuilder fields = new StringBuilder();
		for(String key : keys) {
			if(!first) fields.append(", ");
			first = false;
			fields.append(key).append("=?");
		}
		return String.format(UPDATE_TEMPLATE, getBeanTableName(), fields.toString(), equalsToId());
	}
	
	protected String buildLoadTemplate(String where) {
		String fields = loadFields().stream().collect(Collectors.joining(", "));
		return String.format(SELECT_TEMPLATE, fields, getBeanTableName(), where);
	}
	
	private String nameToGetterName(String name) {
		return "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}
	
	private String getterNameToName(String name) {
		return Character.toLowerCase(name.charAt(3)) + name.substring(4); 
	}

	@SuppressWarnings("unchecked")
	private Class<T> getType() {
		Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];	
		return (Class<T>) type;
	}
	
	public T fromJson(String json) throws JsonParseException, JsonMappingException, IOException {
		return (T) mapper.readValue(json, beanType);
	}

	public T findOne(PK id) {
		try {
			String sql = buildLoadTemplate(equalsToId());
			logger.debug("SQL: {} PARAMS: {}", sql, id);
			return runner.query(sql, new BeanHandler<>(beanType), id);
		} catch ( IllegalArgumentException
				| SQLException e) {
			throw new RuntimeException(e);			
		}
	}
	
	public List<T> findAll() {
		try {
			String sql = buildLoadTemplate("1 = 1");
			logger.debug("SQL: {}", sql);
			return runner.query(sql, new BeanListHandler<>(beanType));
		} catch ( IllegalArgumentException
				| SQLException e) {
			throw new RuntimeException(e);			
		}
	}
	
	public int[] batchInsert(T[] beans) {
		Connection conn = null;
		try {
			conn = runner.getDataSource().getConnection();
			conn.setAutoCommit(false);
			List<String> fields = loadFields(false);
			String sql = buildInsertTemplate(fields);
			Object[][] params = new Object[beans.length][];
			for(int i=0; i<beans.length; i++)
				params[i] = loadParamsForInsert(beans[i]);
			List<String> paramsList = new LinkedList<>();
			Arrays.stream(params).forEach(param->paramsList.add(Arrays.toString(param)));
			logger.debug("BATCH INSERT SQL: {}, PARAMS: {}", sql, paramsList);
			int[] result = runner.batch(conn, sql, params);
			conn.commit();
			return result;
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.warn("rollback error");
			}
			throw new RuntimeException(e);
		} finally {
			if(conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	public int[] batchUpdate(T[] beans) {
		Connection conn = null;
		try {
			conn = runner.getDataSource().getConnection();
			conn.setAutoCommit(false);
			List<String> fields = loadFields(false);
			String sql = buildUpdateTemplate(fields);
			Object[][] params = new Object[beans.length][];
			for(int i=0; i<beans.length; i++)
				params[i] = loadParamsForUpdate(beans[i]);
			List<String> paramsList = new LinkedList<>();
			Arrays.stream(params).forEach(param->paramsList.add(Arrays.toString(param)));
			logger.debug("BATCH UPDATE SQL: {}, PARAMS: {}", sql, paramsList);
			int[] result = runner.batch(conn, sql, params);
			conn.commit();
			return result;
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.warn("rollback error");
			}
			throw new RuntimeException(e);
		} finally {
			if(conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	public T insert(T bean) {
		try {
			Map<String, Object> map = beanToMap(bean);
			String sql = buildInsertTemplate(map.keySet());
			Object[] params = map.values().toArray(new Object[map.size()]);
			logger.debug("SQL: {} PARAMS: {}", sql, map.values());
			runner.insert(sql, new PKResultHandler(bean), params);
			return bean;
		} catch (Exception e) {
			throw new RuntimeException(e);			
		}
	}
	
	public T update(T bean) {
		try {
			Map<String, Object> map = beanToMap(bean);
			String sql = buildUpdateTemplate(map.keySet());
			Object[] params = map.values().toArray(new Object[map.size()+1]);
			params[map.size()] = bean.getId();
			logger.debug("SQL: {} PARAMS: {}", sql, Arrays.toString(params));
			runner.update(sql, params);
			return bean;
		} catch (Exception e) {
			throw new RuntimeException(e);			
		}
	}
	
	public int[] batchInsert(String json) {
		try {
			T[] beans = mapper.readValue(json, arrayType);
			return batchInsert(beans);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public int[] batchUpdate(String json) {
		try {
			T[] beans = mapper.readValue(json, arrayType);
			return batchUpdate(beans);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public T insert(String json) {
		try {
			T bean = fromJson(json);
			return insert(bean);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 
	}
	
	public T update(String json) {
		try {
			T bean = fromJson(json);
			return update(bean);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 
	}
	
	protected class PKResultHandler implements ResultSetHandler<PK> {

		private T bean;
		
		public PKResultHandler(T bean) {
			super();
			this.bean = bean;
		}

		@Override
		public PK handle(ResultSet rs) throws SQLException {
			if(rs.next()) {
				bean.setId(bean.convert(rs.getObject(1)));
			}
			return null;
		}
		
	}
}
