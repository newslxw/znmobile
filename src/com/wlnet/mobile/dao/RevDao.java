package com.wlnet.mobile.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wlnet.mobile.common.BaseDao;
import com.wlnet.mobile.common.Dao;
import com.wlnet.mobile.pojo.Rev;

public class RevDao extends BaseDao implements Dao<Rev>{

	public RevDao(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Rev getById(Integer id) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(Rev.TABLE_NAME, null, Rev._ID + " = ?", new String[]{id.toString()}, null, null, null);
		Rev b = null;
		if(cursor!=null&&cursor.moveToNext()){
			b = new Rev();
			Integer _id = cursor.getInt(cursor.getColumnIndex(Rev._ID));
			b.setId(_id);
			b.setDevUuid(cursor.getString(cursor.getColumnIndex(Rev.DEV_UUID)));
			//b.setMeasureValue(cursor.getString(cursor.getColumnIndex(Rev.MEASURE_VALUE)));
			b.setRevId(cursor.getLong(cursor.getColumnIndex(Rev.REV_ID)));
			b.setRevTime(cursor.getString(cursor.getColumnIndex(Rev.REV_TIME)));
		}else{
		}
		cursor.close();
		db.close();
		return b;
	}

	@Override
	public List<Rev> query(Rev bean) {
		SQLiteDatabase db = null;
		List<Rev> list = new ArrayList<Rev>();
		Cursor cursor = null;
		try{
			db = helper.getReadableDatabase();
			cursor = db.query(Rev.TABLE_NAME, null, Rev.DEV_UUID + " = ?", new String[]{bean.getDevUuid()}, null, null," rev_time desc");
			if(cursor != null){
				while(cursor.moveToNext()){
					Rev b = new Rev();
					Integer _id = cursor.getInt(cursor.getColumnIndex(Rev._ID));
					b.setId(_id);
					b.setDevUuid(cursor.getString(cursor.getColumnIndex(Rev.DEV_UUID)));
					b.setMeasureValue(cursor.getString(cursor.getColumnIndex(Rev.MEASURE_VALUE)));
					//b.setRevId(cursor.getLong(cursor.getColumnIndex(Rev.REV_ID)));
					b.setRevTime(cursor.getString(cursor.getColumnIndex(Rev.REV_TIME)));
					list.add( b);
				}
			}
		}finally{
			if(cursor!=null)cursor.close();
			if(db!=null)db.close();
		}
		return list;
	}

	@Override
	public void insert(Rev bean) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Rev.DEV_UUID, bean.getDevUuid());
		values.put(Rev.MEASURE_VALUE, bean.getMeasureValue());
		//values.put(Rev.REV_ID, bean.getRevId());
		values.put(Rev.REV_TIME, bean.getRevTime());
		db.insert(Rev.TABLE_NAME, null, values);
		Cursor cursor = db.rawQuery("select last_insert_rowid()", null);
		if(cursor.moveToNext()){
			bean.setId(cursor.getInt(0));
		}
		cursor.close();
		db.close();
		
	}

	@Override
	public void update(Rev bean) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Rev.DEV_UUID, bean.getDevUuid());
		values.put(Rev.MEASURE_VALUE, bean.getMeasureValue());
		//values.put(Rev.REV_ID, bean.getRevId());
		values.put(Rev.REV_TIME, bean.getRevTime());
		db.update(Rev.TABLE_NAME, values," dev_uuid=? ",new String[]{bean.getDevUuid()});
		db.close();
	}

	@Override
	public void delete(Integer id) {
		ContentValues values = new ContentValues();
		values.put(Rev._ID, id);
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(Rev.TABLE_NAME, "_id=?", new String[]{Integer.toString(id)});
		db.close();
	}
	
	

}
