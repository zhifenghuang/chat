package com.alsc.chat.db;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 数据库操作工具类
 */
public class DBOperate {

    private SQLiteDatabase db;

    public DBOperate(SQLiteDatabase db) {
        this.db = db;
    }

	/*public boolean isDBOpen() {
        boolean flag = false;
		if (db != null) {
			flag = db.isOpen();
		}
		return flag;
	}

	public void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}*/

    /**
     * 事务开启
     */
    public void beginTransaction() {
        db.beginTransaction();

    }

    /**
     * 事务操作成功
     */
    public void setTransactionSuccessful() {
        db.setTransactionSuccessful();

    }

    /**
     * 事务结束
     */
    public void endTransaction() {

        db.endTransaction();
    }

    @SuppressWarnings("finally")
    public int getCursorCount(String strSql) {
        Cursor cursor = db.rawQuery(strSql, null);
        int count = 0;
        try {
            count = cursor.getCount();
        } catch (Exception e) {
            Log.e("getCursorCount()", " encount an ecception");
        } finally {
            cursor.close();
            return count;
        }
    }

    public Cursor getCursor(String strSql) {
        Cursor cursor = db.rawQuery(strSql, null);
        return cursor;
    }

    /**
     * 通过查询数据库获取一组ContentValues
     *
     * @param strSql
     * @return
     */
    public List<ContentValues> getValuesList(String strSql) {
        Cursor cursor = db.rawQuery(strSql, null);
        if (cursor == null) {
            return null;
        }
        List<ContentValues> valuesList = new ArrayList<ContentValues>();
        try {
            ContentValues values;
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                        .moveToNext()) {
                    values = cursorToValues(cursor);
                    valuesList.add(values);
                }
            }
            cursor.close();
            return valuesList;

        } finally {
            cursor.close();
        }
    }

    /**
     * 通过查询数据库获取一组ContentValues 类似getValuesList，只是ContentValues存放的key和value都是string
     *
     * @param strSql
     * @return
     */
    public List<ContentValues> getStringValuesList(String strSql) {
        Cursor cursor = db.rawQuery(strSql, null);
        if (cursor == null) {
            return null;
        }
        List<ContentValues> valuesList = new ArrayList<ContentValues>();
        try {
            ContentValues values;
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                        .moveToNext()) {
                    values = cursorToStringValues(cursor);
                    valuesList.add(values);
                }
            }
            cursor.close();
            return valuesList;

        } finally {
            cursor.close();
        }
    }

    @SuppressLint("NewApi")
    public static ContentValues cursorToStringValues(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("cursor不能为空");
        }
        if (cursor instanceof AbstractWindowedCursor) {
            ContentValues values = new ContentValues();
            AbstractWindowedCursor abs = (AbstractWindowedCursor) cursor;
            String[] names = abs.getColumnNames();
            for (int i = 0; i < names.length; i++) {


                int nType = abs.getType(i);
                switch (nType) {
                    case Cursor.FIELD_TYPE_INTEGER:
                        values.put(names[i], String.format("%d", abs.getLong(i)));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        values.put(names[i], String.format("%f", abs.getDouble(i)));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        values.put(names[i], abs.getString(i));
                        break;

                    case Cursor.FIELD_TYPE_NULL:
                    case Cursor.FIELD_TYPE_BLOB:
                    default:
                        values.put(names[i], "not support blod data type");

                }
            }
            return values;
        } else {
            return null;
        }
    }

    /**
     * 把cursor当前组的数据转换成ContentValues
     *
     * @param cursor
     * @return
     */

    @SuppressWarnings("deprecation")
    public static ContentValues cursorToValues(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("cursor不能为空");
        }
        if (cursor instanceof AbstractWindowedCursor) {
            ContentValues values = new ContentValues();
            AbstractWindowedCursor abs = (AbstractWindowedCursor) cursor;
            String[] names = abs.getColumnNames();
            for (int i = 0; i < names.length; i++) {
                if (abs.isBlob(i)) {
                    values.put(names[i], abs.getBlob(i));
                } else if (abs.isFloat(i)) {
                    //values.put(names[i], abs.getFloat(i));
                    values.put(names[i], abs.getDouble(i));
                } else if (abs.isLong(i)) {
                    values.put(names[i], abs.getLong(i));
                } else if (abs.isString(i)) {
                    values.put(names[i], abs.getString(i));
                }
            }
            return values;
        } else {
            return null;
        }
    }

    /**
     * 通过查询数据库得到一个字段
     *
     * @param strSql sql语句
     * @return
     */
    public <T extends Object> T getOneKey(String strSql, Class<T> clz) {
        Cursor cursor = db.rawQuery(strSql, null);
        if (cursor == null) {
            return null;
        }
        try {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                Object value = null;
                if (clz == String.class) {
                    value = cursor.getString(0);
                } else if (clz == Integer.class || clz == int.class) {
                    value = cursor.getInt(0);
                } else if (clz == Long.class || clz == long.class) {
                    value = cursor.getLong(0);
                } else if (clz == Float.class || clz == float.class) {
                    value = cursor.getFloat(0);
                } else if (clz == Boolean.class || clz == boolean.class) {
                    value = cursor.getInt(0) > 0;
                } else if (clz == Short.class || clz == short.class) {
                    value = cursor.getShort(0);
                } else if (clz == Double.class || clz == double.class) {
                    value = cursor.getDouble(0);
                } else if (clz == byte[].class || clz == Byte[].class) {
                    value = cursor.getBlob(0);
                } else if (clz == byte.class || clz == Byte.class) {
                    value = cursor.getInt(0);
                }
                return (T) value;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return null;
    }

    /**
     * 通过查询数据库得到一个对象
     *
     * @param strSql sql语句
     * @param clz    要得到的对象的class,如：IDBItemOperation.class
     * @return
     */
    public <T extends IDBItemOperation> T getOne(String strSql, Class<T> clz) {
        Cursor cursor = db.rawQuery(strSql, null);
        if (cursor == null) {
            return null;
        }
        T dbObject;
        try {
            dbObject = clz.newInstance();
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
//				dbObject.setValues(DBOperate.cursorToValues(cursor));
                dbObject.setDataByCursor(cursor);
                return dbObject;
            }
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return null;
    }

    /**
     * 通过查询数据库得到一组对象
     *
     * @param strSql
     * @param clz    要得到的对象的class,如：IDBItemOperation.class
     * @return
     */
    public <T extends IDBItemOperation> ArrayList<T> getList(String strSql,
                                                             Class<T> clz) {
        Cursor cursor = db.rawQuery(strSql, null);
        if (cursor == null) {
            return null;
        }
        ArrayList<T> dbObjectList = new ArrayList<T>();
        try {
            T dbObject;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                dbObject = clz.newInstance();
                dbObject.setDataByCursor(cursor);
                dbObjectList.add(dbObject);
            }
            if (dbObjectList.size() > 0) {
                return dbObjectList;
            }
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return null;
    }

    /**
     * 执行一条SQL语句
     *
     * @param strSql
     * @return 是否执行成功
     */
    public boolean execSQL(String strSql) {
        try {
            db.execSQL(strSql);
        } catch (SQLException sqlExc) {
            Log.v("DBManager", sqlExc.getMessage());
            return false;
        }
        return true;
    }


    /**
     * 替换掉特殊符号
     * /   ->    //
     * '   ->    ''
     * [   ->    /[
     * ]   ->    /]
     * %   ->    /%
     * &   ->    /&
     * _   ->    /_
     * (   ->    /(
     * )   ->    /)
     *
     * @param keyWord
     * @return
     */
    public static String sqliteEscape(String keyWord) {
        keyWord = keyWord.replace("/", "//");
        keyWord = keyWord.replace("'", "''");
        keyWord = keyWord.replace("[", "/[");
        keyWord = keyWord.replace("]", "/]");
        keyWord = keyWord.replace("%", "/%");
        keyWord = keyWord.replace("&", "/&");
        keyWord = keyWord.replace("_", "/_");
        keyWord = keyWord.replace("(", "/(");
        keyWord = keyWord.replace(")", "/)");
        return keyWord;
    }


    /**
     * 插入或更新 IDBItemOperation
     *
     * @return
     */
    public long insertOrUpdate(IDBItemOperation dbItem) {
        return db.replace(dbItem.getTableName(), null, dbItem.getValues());
    }

    /**
     * 插入或更新 IDBItemOperation
     *
     * @return
     */
    public long insertOrUpdate(String tableName, ContentValues contentValue) {
        return db.replace(tableName, null, contentValue);
    }

    /**
     * 插入一条数据 IDBItemOperation
     *
     * @return
     */
    public long insert(IDBItemOperation dbItem) {

        return db.insert(dbItem.getTableName(), null, dbItem.getValues());
    }

    /**
     * 插入或修改一条数据 IDBItemOperation(数据库中没设置主键时用)
     *
     * @return
     */
    public long insertOrUpdate2(IDBItemOperation dbItem) {
        if (update(dbItem) > 0) {
            return 1;
        }
        return db.insert(dbItem.getTableName(), null, dbItem.getValues());
    }

    /**
     * 插入一条数据(没有主建,因为主键是自增的)
     *
     * @return
     */
    public long insertWithNoPrimaryKey(IDBItemOperation dbItem) {
        String primaryKey = dbItem.getPrimaryKeyName();
        ContentValues values = dbItem.getValues();
        values.remove(primaryKey);
        return db.insert(dbItem.getTableName(), null, values);
    }


    /**
     * 插入一条数据
     *
     * @return
     */
    public long insert(String strTableName, ContentValues contentValue) {

        return db.insert(strTableName, null, contentValue);
    }


    /**
     * 更新一条数据
     *
     * @param tbName
     * @param values
     * @param idName
     * @return
     */
    public int update(String tbName, ContentValues values, String idName) {
        return db.update(tbName, values, idName + "=?",
                new String[]{(String) values.get(idName)});
    }

    /**
     * 根据Primarykey更新一条数据
     *
     * @return
     */
    public int update(IDBItemOperation dbItem) {
        ContentValues values = dbItem.getValues();
        String idName = dbItem.getPrimaryKeyName();


        String value = "";
        Object obj = values.get(idName);
        Class<?> clz = obj.getClass();
        if (clz != String.class) {
            value = String.valueOf(obj);
        } else {
            value = (String) obj;
        }

        return db.update(dbItem.getTableName(), values, idName + "=?",
                new String[]{value});
    }

    /**
     * 根据Primarykey更新一条数据
     *
     * @return
     */
    public int update(IDBItemOperation dbItem, ContentValues values) {
        String idName = dbItem.getPrimaryKeyName();
        String value = "";
        Object obj = values.get(idName);
        Class<?> clz = obj.getClass();
        if (clz != String.class) {
            value = String.valueOf(obj);
        } else {
            value = (String) obj;
        }
        return db.update(dbItem.getTableName(), values, idName + "=?",
                new String[]{value});
    }

    /**
     * 根据指定的条件，update一个对象
     *
     * @param tableName   asd
     *                    表名
     * @param whereClause ： （例如: "name=? AND age=?")
     * @param whereArgs   (例如: new Object{"xiadong", 20} )
     * @return
     */
    public int updateObjectByWhereClause(String tableName,
                                         ContentValues values, String whereClause, String[] whereArgs) {
        return db.update(tableName, values, whereClause, whereArgs);
    }

    /**
     * 根据主键删除一条数据
     *
     * @return
     */
    public int delete(IDBItemOperation dbItem) {
        String strTBName = dbItem.getTableName();
        String strKey = dbItem.getPrimaryKeyName();
        String strValue = dbItem.getPrimaryKeyName();
        return db.delete(strTBName, strKey + "=?", new String[]{strValue});
    }

    /**
     * 根据指定的条件，update一个对象
     *
     * @param whereClause ： （例如: "name = ? AND age = ?")
     * @param whereArgs   (例如: new Object{"xiadong", 20} )
     * @return
     */
    public int deleteObjectByWhereClause(String strTableName,
                                         String whereClause, String[] whereArgs) {
        return db.delete(strTableName, whereClause, whereArgs);
    }

}