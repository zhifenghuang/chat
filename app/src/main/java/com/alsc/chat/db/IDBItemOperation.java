package com.alsc.chat.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * 所有数据库表对应的类的基类
 * 
 * @author shuguang.wen
 * 
 */
public abstract class IDBItemOperation implements Serializable {

	// public abstract void setValues(ContentValues contentValues);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 返回主键的字段名，如：id
	 * 
	 * @return
	 */
	public abstract String getPrimaryKeyName();

	/**
	 * 返回这个类对应的表名
	 * 
	 * @return
	 */
	public abstract String getTableName();

	/**
	 * 这里方法里通过递归给成员变量及父类的成员就是赋值
	 * @param cursor
	 * @param cls
	 */
	private void setDataByCursor(Cursor cursor, Class<?> cls) {
		int columnIndex = 0;
		Class<?> clz = null;
		for (Field field : cls.getDeclaredFields()) {// 遍历成员变量
			columnIndex = cursor.getColumnIndex(field.getName());
			if (columnIndex != -1) {// 如果cursor里包含这个字段
				clz = field.getType();
				try {
					Object value = null;
					if (clz == String.class) {
						value = cursor.getString(columnIndex);
					} else if (clz == Integer.class || clz == int.class) {
						value = cursor.getInt(columnIndex);
					} else if (clz == Long.class || clz == long.class) {
						value = cursor.getLong(columnIndex);
					} else if (clz == Float.class || clz == float.class) {
						value = cursor.getFloat(columnIndex);
					} else if (clz == Boolean.class || clz == boolean.class) {
						value = cursor.getInt(columnIndex) > 0;
					} else if (clz == Short.class || clz == short.class) {
						value = cursor.getShort(columnIndex);
					} else if (clz == Double.class || clz == double.class) {
						value = cursor.getDouble(columnIndex);
					} else if (clz == byte[].class || clz == Byte[].class) {
						value = cursor.getBlob(columnIndex);
					} else if (clz == byte.class || clz == Byte.class) {
						value = cursor.getInt(columnIndex);
					}
					if (value != null) {
						//Log.v(getClass().getSimpleName(),"key:" + field.getName() + "------value:"+ value);
						field.setAccessible(true);
						field.set(this, value);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (cls.getSuperclass() != IDBItemOperation.class) {
			setDataByCursor(cursor, cls.getSuperclass());
		}
	}

	/**
	 * 通过cursor给对象的各个成员变量赋值
	 * 
	 * @param cursor
	 */
	protected void setDataByCursor(Cursor cursor) {

		setDataByCursor(cursor, getClass());
	}

	/**
	 * 把所有可以写入数据库的成员变量的值都放进一个ContentValues里
	 * 
	 * @return
	 */
	public ContentValues getValues() {

		ContentValues values = new ContentValues();
		getValues(getClass(), values);
		return values;
	}

	/**
	 * 这个方法通过递归，把成员变量及父类的成员变量的值放入到ContentValues
	 * @param cls
	 * @param values
	 */
	private void getValues(Class<?> cls, ContentValues values) {
		Class<?> clz = null;
		for (Field field : cls.getDeclaredFields()) {// 遍历成员变量
			field.setAccessible(true);
			clz = field.getType();
			try {
				if (clz == String.class) {
					values.put(field.getName(), (String) field.get(this));
				} else if (clz == Integer.class || clz == int.class) {
					values.put(field.getName(), field.getInt(this));
				} else if (clz == Long.class || clz == long.class) {
					if (!"serialVersionUID".equals(field.getName())) {
						values.put(field.getName(), field.getLong(this));
					}
				} else if (clz == Float.class || clz == float.class) {
					values.put(field.getName(), field.getFloat(this));
				} else if (clz == Boolean.class || clz == boolean.class) {
					values.put(field.getName(), field.getBoolean(this));
				} else if (clz == Short.class || clz == short.class) {
					values.put(field.getName(), field.getShort(this));
				} else if (clz == Double.class || clz == double.class) {
					values.put(field.getName(), field.getDouble(this));
				} else if (clz == byte[].class || clz == Byte[].class) {
					values.put(field.getName(), (byte[]) field.get(this));
				} else if (clz == byte.class || clz == Byte.class) {
					values.put(field.getName(), field.getByte(this));
				}
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (cls.getSuperclass() != IDBItemOperation.class) {
			getValues(cls.getSuperclass(), values);
		}
	}
}
