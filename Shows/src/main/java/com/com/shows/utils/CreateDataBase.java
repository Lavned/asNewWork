package com.com.shows.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class CreateDataBase{

	private static CreateDataBase mInstance = null;
	static SQLiteDatabase db;
	public synchronized static CreateDataBase getInstance() { 
		if (mInstance == null) { 
		    mInstance = new CreateDataBase(); 
		 } 
	 return mInstance; 
	};
	public synchronized void creatable(File file, String str) {
		
		 db = SQLiteDatabase.openOrCreateDatabase(file, null);
		try {
             if(!file.exists()){
            	 file.createNewFile();
             }
			db.execSQL(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.close();
	}

	public synchronized void savedata(File file, String del, String insert) {
		 db = SQLiteDatabase.openOrCreateDatabase(file, null);
		try {
			if (del != null) {
				db.execSQL(del);
			}else {
				db.execSQL(insert);
			}
			Log.i("sql-----insert", del+"\n"+insert);
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("sql--error---insert", del+"\n"+insert);
		}
		db.close();
	}

	public  synchronized void delededata(File file, String del) {
		if (!file.exists()) {
			return;
		}
		 db= SQLiteDatabase.openOrCreateDatabase(file, null);
		if (del != null) {
			db.execSQL(del);
		}
		db.close();
	}

	public synchronized void creattabAndsavedata(File file, String creattab,String deldata,String savedata) {
		 db = SQLiteDatabase.openOrCreateDatabase(file, null);
		try {
             if(!file.exists()){
            	 file.createNewFile();
            	 db.execSQL(creattab);
              }
            if (deldata != null) {
				db.execSQL(deldata);
			}
			db.execSQL(savedata);
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.close();
	}
	
	public  synchronized int update(File file,String tablename,ContentValues values,String where){
		  db = SQLiteDatabase.openOrCreateDatabase(file, null);
		 int result = db.update(tablename, values, where, null);
		 db.close();
		return result;
	}
	/**
	 * table就是表名
	 * colummns是个
	 * String[],表示要查询哪几列，
	 * selection就是where后面的　比如上面的"id=1"
	 * groupBY就是按表中的某个字段进行分组
	 * having就是对分组的结果进行筛选
	 * orderby　按某个字段进行排序
	 * */
	 public synchronized Cursor queryOne(File file,String table,String[] columns,String selection,String[] selectionArgs,String groupBy,String having,String orderBy,String limit){	
		 db = SQLiteDatabase.openOrCreateDatabase(file,null);
		Cursor  cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy ,limit);
		return cursor;
		}
	 public static void closedb(){
		 if (db!=null) {
			 db.close();
		}
	 }
}
