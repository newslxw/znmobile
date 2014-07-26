package com.wlnet.mobile.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wlnet.mobile.common.BaseDao;
import com.wlnet.mobile.common.Dao;
import com.wlnet.mobile.pojo.Dev;

public class DevDao extends BaseDao implements Dao<Dev>{

	public DevDao(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Dev getById(Integer id) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(Dev.TABLE_NAME, null, Dev._ID + " = ?", new String[]{id.toString()}, null, null, null);
		Dev b = null;
		if(cursor!=null&&cursor.moveToNext()){
			b = new Dev();
			Integer _id = cursor.getInt(cursor.getColumnIndex(Dev._ID));
			b.setId(_id);
			b.setDevName(cursor.getString(cursor.getColumnIndex(Dev.DEV_NAME)));
			b.setDevType(cursor.getString(cursor.getColumnIndex(Dev.DEV_TYPE)));
			b.setDevUuid(cursor.getString(cursor.getColumnIndex(Dev.DEV_UUID)));
			b.setInetIp(cursor.getString(cursor.getColumnIndex(Dev.INET_IP)));
			b.setInetPort(cursor.getInt(cursor.getColumnIndex(Dev.INET_PORT)));
		}else{
		}
		cursor.close();
		db.close();
		return b;
	}

	@Override
	public List<Dev> query(Dev bean) {
		SQLiteDatabase db = null;
		List<Dev> list = new ArrayList<Dev>();
		Cursor cursor = null;
		String sel= null;String []selParam=null;
		if(bean!=null&&bean.getDevUuid()!=null){
			sel = Dev.DEV_UUID+" = ?";
			selParam = new String[]{bean.getDevUuid()};
		}
		try{
			db = helper.getReadableDatabase();
			cursor = db.query(Dev.TABLE_NAME, null, sel, selParam, null, null,null);
			if(cursor != null){
				while(cursor.moveToNext()){
					Dev b = new Dev();
					Integer _id = cursor.getInt(cursor.getColumnIndex(Dev._ID));
					b.setId(_id);
					b.setDevName(cursor.getString(cursor.getColumnIndex(Dev.DEV_NAME)));
					b.setDevType(cursor.getString(cursor.getColumnIndex(Dev.DEV_TYPE)));
					b.setAppType(cursor.getString(cursor.getColumnIndex(Dev.APP_TYPE)));
					b.setDevUuid(cursor.getString(cursor.getColumnIndex(Dev.DEV_UUID)));
					b.setInetIp(cursor.getString(cursor.getColumnIndex(Dev.INET_IP)));
					b.setInetPort(cursor.getInt(cursor.getColumnIndex(Dev.INET_PORT)));
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
	public void insert(Dev bean) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Dev.INET_IP, bean.getInetIp());
		values.put(Dev.INET_PORT, bean.getInetPort());
		values.put(Dev.DEV_TYPE, bean.getDevType());
		values.put(Dev.APP_TYPE, bean.getAppType());
		values.put(Dev.DEV_NAME, bean.getDevName());
		values.put(Dev.DEV_UUID, bean.getDevUuid());
		db.insert(Dev.TABLE_NAME, null, values);
		Cursor cursor = db.rawQuery("select last_insert_rowid()", null);
		if(cursor.moveToNext()){
			bean.setId(cursor.getInt(0));
		}
		cursor.close();
		db.close();
	}
	
	/**
	 * 
	 * @param list
	 */
	public void insertOrUpdate(List<Dev> list){
		Dev dev = null;
		List<Dev> tmp = null;
		for(int i=0; i<list.size(); i++){
			dev = list.get(i);
			if(dev.getDevUuid()==null)continue;
			tmp = this.query(dev);
			if(tmp!=null&&tmp.size()>0){
				this.update(dev);
			}else{
				this.insert(dev);
			}
		}
	}

	@Override
	public void update(Dev bean) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Dev.INET_IP, bean.getInetIp());
		values.put(Dev.INET_PORT, bean.getInetPort());
		values.put(Dev.DEV_TYPE, bean.getDevType());
		values.put(Dev.APP_TYPE, bean.getAppType());
		values.put(Dev.DEV_NAME, bean.getDevName());
		values.put(Dev.DEV_UUID, bean.getDevUuid());
		db.update(Dev.TABLE_NAME, values," dev_uuid=? ",new String[]{bean.getDevUuid()});
		db.close();
	}

	@Override
	public void delete(Integer id) {
		ContentValues values = new ContentValues();
		values.put(Dev._ID, id);
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(Dev.TABLE_NAME, "_id=?", new String[]{Integer.toString(id)});
		db.close();
		
	}
	
	

}
