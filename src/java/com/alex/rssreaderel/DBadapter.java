package com.alex.rssreaderel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 27.10.2016.
 */
public class DBadapter {
    private List<MenuItem> menuList = new ArrayList<MenuItem>();
    private List<RssItem> rssList = new ArrayList<RssItem>();
    public static final String KEY_ROWID = BaseColumns._ID;
    public static final String KEY_TITLE = "title";
    public static final String KEY_LINK = "imagelink";
    public static final String KEY_ID = BaseColumns._ID;
    public static final String KEY_GUID = "guid";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_RTITLE = "title";
    public static final String KEY_READ = "read";
    public static final String KEY_OFFLINE = "offline";
    public static final String KEY_DELETE = "ddelete";
    public static final String KEY_URL = "url";
    public static final String KEY_COUNTER = "counter";
    public static final String KEY_URL2 = "url2";
    public static final String KEY_GROUP = "groupp";
    public static final String KEY_URLL = "url";
    public static final String KEY_DATE = "date";
    public static final String KEY_UPDATE = "udate";
    public static final String KEY_FAVORITE = "favorite";


    private static final String DATABASE_NAME = "blogposts";
    private static final String DATABASE_TABLE = "menulist";
    private static final String DATABASE_TABLE_2 = "blogpostlist";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_LIST_TABLE = "create table " + DATABASE_TABLE + " (" +
            KEY_ROWID + " integer primary key autoincrement, " +
            KEY_LINK + " text not null, " +
            KEY_URLL + " text not null, " +
            KEY_COUNTER + " text not null, " +
            KEY_GROUP + " text not null, " +
            KEY_UPDATE + " text not null, " +
            KEY_TITLE + " text not null);";


    private static final String DATABASE_CREATE_LIST_TABLE_2 = "create table " + DATABASE_TABLE_2 + " (" +
            KEY_ID +" integer primary key autoincrement, "+
            KEY_GUID + " text not null, " +
            KEY_DESCRIPTION + " text not null, " +
            KEY_RTITLE + " text not null, " +
            KEY_READ + " boolean not null, " +
            KEY_URL + " text not null, " +
            KEY_URL2 + " text not null, " +
            KEY_OFFLINE + " boolean not null, " +
            KEY_DELETE + " boolean not null, " +
            KEY_FAVORITE + " boolean not null, " +
            KEY_DATE + " text not null);";

    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    public DBadapter(Context c) {
        context = c;
    }

    public DBadapter openToRead() throws android.database.SQLException {
        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        return this;
    }

    public DBadapter openToWrite() throws android.database.SQLException {
        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        sqLiteHelper.close();
    }

    public class SQLiteHelper extends SQLiteOpenHelper {
        public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_LIST_TABLE);
            db.execSQL(DATABASE_CREATE_LIST_TABLE_2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_2);
            onCreate(db);
        }
    }

    public long insertBlogListing(String title, String link,String url,String update,String counter) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_LINK, link);
        initialValues.put(KEY_URLL, url);
        initialValues.put(KEY_UPDATE, update);
        initialValues.put(KEY_COUNTER,counter);
        initialValues.put(KEY_GROUP, "group");
        return sqLiteDatabase.insert(DATABASE_TABLE, null, initialValues);
    }

    public long insertArticleListing(String guid,String description, String title, String date,String url, String url2) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_GUID, guid);
        initialValues.put(KEY_DESCRIPTION, description);
        initialValues.put(KEY_RTITLE, title);
        initialValues.put(KEY_READ, false);
        initialValues.put(KEY_DELETE,false);
        initialValues.put(KEY_OFFLINE, false);
        initialValues.put(KEY_FAVORITE, false);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_URL, url);
        initialValues.put(KEY_URL2, url2);
        return sqLiteDatabase.insert(DATABASE_TABLE_2, null, initialValues);

    }

    public boolean SetGroup(int title,String group) {
        ContentValues args = new ContentValues();
        args.put(KEY_GROUP, group);
        return sqLiteDatabase.update(DATABASE_TABLE, args, KEY_ROWID + "='" + title+"'", null) > 0;
    }

    public boolean SetDateUpdate(String title,String update) {
        ContentValues args = new ContentValues();
        args.put(KEY_UPDATE,update);
        return sqLiteDatabase.update(DATABASE_TABLE, args, KEY_TITLE + "='" + title+"'", null) > 0;
    }

    public int makeCounter(String url,String counter) {
        ContentValues args = new ContentValues();
        args.put(KEY_COUNTER, counter);
        return sqLiteDatabase.update(DATABASE_TABLE, args, KEY_URLL + "='" + url+"'", null);
    }

    public boolean markAsFavorite(String title) {
        ContentValues args = new ContentValues();
        args.put(KEY_FAVORITE, true);
        return sqLiteDatabase.update(DATABASE_TABLE_2, args, KEY_TITLE + "='" + title+"'", null) > 0;
    }

    public int chaneldel(String title) {

        return sqLiteDatabase.delete(DATABASE_TABLE,  KEY_TITLE + "='" + title+"'", null) ;
    }

    public int lentadel(String title) {

        return sqLiteDatabase.delete(DATABASE_TABLE_2,  KEY_TITLE + "='" + title+"'", null) ;
    }

    public boolean markAsDelete(String title) {
        ContentValues args = new ContentValues();
        Log.e("del",title);
        args.put(KEY_DELETE, true);
        return sqLiteDatabase.update(DATABASE_TABLE_2, args, KEY_TITLE + "='" + title+"'", null) > 0;
    }

    public boolean markAsRead(String title) {
        ContentValues args = new ContentValues();
        args.put(KEY_READ, true);
        return sqLiteDatabase.update(DATABASE_TABLE_2, args, KEY_TITLE + "='" + title+"'", null) > 0;
    }
    public boolean markAsunFavorite(String title) {
        ContentValues args = new ContentValues();
        args.put(KEY_FAVORITE, false);
        return sqLiteDatabase.update(DATABASE_TABLE_2, args, KEY_TITLE + "='" + title+"'", null) > 0;
    }
    public boolean markAsunRead(String title) {
        ContentValues args = new ContentValues();
        args.put(KEY_READ, false);
        return sqLiteDatabase.update(DATABASE_TABLE_2, args, KEY_TITLE + "='" + title+"'", null) > 0;
    }

    public int updatetitle(String url,String title) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        return sqLiteDatabase.update(DATABASE_TABLE, args, KEY_URLL + "='" + url+"'", null);
    }
    public int updatedescr(String desc,String title) {
        ContentValues args = new ContentValues();
        args.put(KEY_DESCRIPTION, desc);
        return sqLiteDatabase.update(DATABASE_TABLE_2, args, KEY_TITLE + "='" + title+"'", null);
    }

    public int updateurl(String url,String urlnew) {
        ContentValues args = new ContentValues();
        args.put(KEY_URLL, urlnew);
        return sqLiteDatabase.update(DATABASE_TABLE, args, KEY_URLL + "='" + url+"'", null);
    }

    public List<MenuItem> getBlogListing() throws SQLException {

        Cursor mCursor;
        mCursor = sqLiteDatabase.query(true, DATABASE_TABLE, new String[]{
                        KEY_ROWID,
                        KEY_LINK,
                        KEY_URLL,
                        KEY_GROUP,
                        KEY_COUNTER,
                        KEY_UPDATE,
                        KEY_TITLE
                },
                null,
                null,
                null,
                null,
                null,
                null);
        if (mCursor.moveToFirst()) {
            do {

                MenuItem a = new MenuItem();
                a.setDateUpdate(mCursor.getString(mCursor.getColumnIndex(KEY_UPDATE)));
                a.setTitle(mCursor.getString(mCursor.getColumnIndex(KEY_TITLE)));
                a.setImageLink(mCursor.getString(mCursor.getColumnIndex(KEY_LINK)));
                a.setDbId(mCursor.getInt(mCursor.getColumnIndex(KEY_ROWID)));
                a.setGroup(mCursor.getString(mCursor.getColumnIndex(KEY_GROUP)));
                a.setUrl(mCursor.getString(mCursor.getColumnIndex(KEY_URLL)));
                a.setCounter(mCursor.getString(mCursor.getColumnIndex(KEY_COUNTER)));
                menuList.add(a);
            } while (mCursor.moveToNext());
            return menuList;
        }
        return null;
    }

    public MenuItem getMenuItem(String url) throws SQLException {

        Cursor mCursor;
        mCursor = sqLiteDatabase.query(true, DATABASE_TABLE, new String[]{
                        KEY_ROWID,
                        KEY_LINK,
                        KEY_URLL,
                        KEY_GROUP,
                        KEY_UPDATE,
                        KEY_COUNTER,
                        KEY_TITLE
                },
                KEY_URLL + "= '" + url+ "'",
                null,
                null,
                null,
                null,
                null);
        if (mCursor.moveToFirst()) {

                MenuItem a = new MenuItem();
                a.setDateUpdate(mCursor.getString(mCursor.getColumnIndex(KEY_UPDATE)));
                a.setTitle(mCursor.getString(mCursor.getColumnIndex(KEY_TITLE)));
                a.setImageLink(mCursor.getString(mCursor.getColumnIndex(KEY_LINK)));
                a.setDbId(mCursor.getInt(mCursor.getColumnIndex(KEY_ROWID)));
                a.setGroup(mCursor.getString(mCursor.getColumnIndex(KEY_GROUP)));
                a.setUrl(mCursor.getString(mCursor.getColumnIndex(KEY_URLL)));
                a.setCounter(mCursor.getString(mCursor.getColumnIndex(KEY_COUNTER)));


            return a;
        }
        return null;
    }

    public RssItem getArticleListing(String title) throws SQLException {
        Cursor mCursor;
        mCursor = sqLiteDatabase.query(true, DATABASE_TABLE_2, new String[] {
                        KEY_ROWID,
                        KEY_GUID,
                        KEY_DESCRIPTION,
                        KEY_TITLE,
                        KEY_READ,
                        KEY_OFFLINE,
                        KEY_DATE,
                        KEY_FAVORITE,
                        KEY_URL,
                        KEY_URL2
                },
                KEY_TITLE + "= '" + title+ "'",
                null,
                null,
                null,
                null,
                null);
        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            RssItem a = new RssItem();
            a.setGuid(mCursor.getString(mCursor.getColumnIndex(KEY_GUID)));
            a.setDescription(mCursor.getString(mCursor.getColumnIndex(KEY_DESCRIPTION)));
            a.setTitle(mCursor.getString(mCursor.getColumnIndex(KEY_TITLE)));
            a.setRead(mCursor.getInt(mCursor.getColumnIndex(KEY_READ)) > 0);
            a.setDbId(mCursor.getLong(mCursor.getColumnIndex(KEY_ROWID)));
            a.setOffline(mCursor.getInt(mCursor.getColumnIndex(KEY_OFFLINE)) > 0);

            a.setFavoritee(mCursor.getInt(mCursor.getColumnIndex(KEY_FAVORITE)) > 0);
            a.setDate(mCursor.getString(mCursor.getColumnIndex(KEY_DATE)));
            a.setLink(mCursor.getString(mCursor.getColumnIndex(KEY_URL)));
            a.setUrl(mCursor.getString(mCursor.getColumnIndex(KEY_URL2)));
            return a;
        }
        return null;
    }

    public List<RssItem> getFavorite() throws SQLException {
        Cursor mCursor;
        mCursor = sqLiteDatabase.query(true, DATABASE_TABLE_2, new String[] {
                        KEY_ROWID,
                        KEY_GUID,
                        KEY_DESCRIPTION,
                        KEY_TITLE,
                        KEY_READ,
                        KEY_OFFLINE,
                        KEY_DATE,
                        KEY_FAVORITE,
                        KEY_URL
                },
                KEY_FAVORITE+ "= '" +1+ "'",
                null,
                null,
                null,
                null,
                null);
        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            do {
                RssItem a = new RssItem();
                a.setGuid(mCursor.getString(mCursor.getColumnIndex(KEY_GUID)));
                a.setDescription(mCursor.getString(mCursor.getColumnIndex(KEY_DESCRIPTION)));
                a.setTitle(mCursor.getString(mCursor.getColumnIndex(KEY_TITLE)));
                a.setRead(mCursor.getInt(mCursor.getColumnIndex(KEY_READ)) > 0);
                a.setDbId(mCursor.getLong(mCursor.getColumnIndex(KEY_ROWID)));
                a.setOffline(mCursor.getInt(mCursor.getColumnIndex(KEY_OFFLINE)) > 0);
                a.setFavoritee(mCursor.getInt(mCursor.getColumnIndex(KEY_FAVORITE)) > 0);
                a.setDate(mCursor.getString(mCursor.getColumnIndex(KEY_DATE)));

                a.setLink(mCursor.getString(mCursor.getColumnIndex(KEY_URL)));

               rssList.add(a);
            } while (mCursor.moveToNext());
            return rssList;
        }
        return null;
    }



    public List<RssItem> getRead() throws SQLException {
        Cursor mCursor;
        mCursor = sqLiteDatabase.query(true, DATABASE_TABLE_2, new String[] {
                        KEY_ROWID,
                        KEY_GUID,
                        KEY_DESCRIPTION,
                        KEY_TITLE,
                        KEY_READ,
                        KEY_OFFLINE,
                        KEY_DATE,
                        KEY_FAVORITE,
                        KEY_URL
                },
                KEY_READ+ "= '" +1+ "'",
                null,
                null,
                null,
                null,
                null);
        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            do {
                RssItem a = new RssItem();
                a.setGuid(mCursor.getString(mCursor.getColumnIndex(KEY_GUID)));
                a.setDescription(mCursor.getString(mCursor.getColumnIndex(KEY_DESCRIPTION)));
                a.setTitle(mCursor.getString(mCursor.getColumnIndex(KEY_TITLE)));
                a.setRead(mCursor.getInt(mCursor.getColumnIndex(KEY_READ)) > 0);

                a.setDbId(mCursor.getLong(mCursor.getColumnIndex(KEY_ROWID)));
                a.setOffline(mCursor.getInt(mCursor.getColumnIndex(KEY_OFFLINE)) > 0);
                a.setFavoritee(mCursor.getInt(mCursor.getColumnIndex(KEY_FAVORITE)) > 0);
                a.setDate(mCursor.getString(mCursor.getColumnIndex(KEY_DATE)));
                a.setLink(mCursor.getString(mCursor.getColumnIndex(KEY_URL)));

                rssList.add(a);
            } while (mCursor.moveToNext());
            return rssList;
        }
        return null;
    }


    public List<RssItem> getRssListing(String feed) throws SQLException {
        Cursor mCursor;
        mCursor = sqLiteDatabase.query(true, DATABASE_TABLE_2, new String[] {
                        KEY_ROWID,
                        KEY_GUID,
                        KEY_DESCRIPTION,
                        KEY_TITLE,
                        KEY_READ,
                        KEY_OFFLINE,
                        KEY_DATE,
                        KEY_FAVORITE,
                        KEY_URL,
                        KEY_DELETE,
                        KEY_URL2
                },
                KEY_URL2+ "= '" +feed+ "'",
                null,
                null,
                null,
                null,
                null);
        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            do {
                RssItem a = new RssItem();
                a.setGuid(mCursor.getString(mCursor.getColumnIndex(KEY_GUID)));
                a.setDescription(mCursor.getString(mCursor.getColumnIndex(KEY_DESCRIPTION)));
                a.setTitle(mCursor.getString(mCursor.getColumnIndex(KEY_TITLE)));
                a.setRead(mCursor.getInt(mCursor.getColumnIndex(KEY_READ)) > 0);
                a.setDbId(mCursor.getLong(mCursor.getColumnIndex(KEY_ROWID)));
                a.setDelete(mCursor.getInt(mCursor.getColumnIndex(KEY_DELETE)) > 0);
                a.setOffline(mCursor.getInt(mCursor.getColumnIndex(KEY_OFFLINE)) > 0);
                a.setFavoritee(mCursor.getInt(mCursor.getColumnIndex(KEY_FAVORITE)) > 0);
                a.setDate(mCursor.getString(mCursor.getColumnIndex(KEY_DATE)));
                a.setLink(mCursor.getString(mCursor.getColumnIndex(KEY_URL)));
                a.setUrl(mCursor.getString(mCursor.getColumnIndex(KEY_URL2)));
                rssList.add(a);
            } while (mCursor.moveToNext());
            return rssList;
        }
        return null;
    }

    public List<RssItem> getAllRss() throws SQLException {
        Cursor mCursor;
        mCursor = sqLiteDatabase.query(true, DATABASE_TABLE_2, new String[] {
                        KEY_ROWID,
                        KEY_GUID,
                        KEY_DESCRIPTION,
                        KEY_TITLE,
                        KEY_READ,
                        KEY_OFFLINE,
                        KEY_DATE,
                        KEY_FAVORITE,
                        KEY_URL,
                        KEY_URL2
                },
                null,
                null,
                null,
                null,
                null,
                null);
        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            do {
                RssItem a = new RssItem();
                a.setGuid(mCursor.getString(mCursor.getColumnIndex(KEY_GUID)));
                a.setDescription(mCursor.getString(mCursor.getColumnIndex(KEY_DESCRIPTION)));
                a.setTitle(mCursor.getString(mCursor.getColumnIndex(KEY_TITLE)));
                a.setRead(mCursor.getInt(mCursor.getColumnIndex(KEY_READ)) > 0);

                a.setDbId(mCursor.getLong(mCursor.getColumnIndex(KEY_ROWID)));
                a.setOffline(mCursor.getInt(mCursor.getColumnIndex(KEY_OFFLINE)) > 0);
                a.setFavoritee(mCursor.getInt(mCursor.getColumnIndex(KEY_FAVORITE)) > 0);
                a.setDate(mCursor.getString(mCursor.getColumnIndex(KEY_DATE)));
                a.setLink(mCursor.getString(mCursor.getColumnIndex(KEY_URL)));

                rssList.add(a);
            } while (mCursor.moveToNext());
            return rssList;
        }
        return null;
    }

    public RssItem getDescription(String title) throws SQLException {
        Cursor mCursor;
        mCursor = sqLiteDatabase.query(true, DATABASE_TABLE_2, new String[] {

                        KEY_DESCRIPTION,
                        KEY_TITLE,
                        KEY_DATE
                },
                KEY_TITLE + "= '" + title + "'",
                null,
                null,
                null,
                null,
                null);
        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            RssItem a = new RssItem();

            a.setDescription(mCursor.getString(mCursor.getColumnIndex(KEY_DESCRIPTION)));
            a.setTitle(mCursor.getString(mCursor.getColumnIndex(KEY_TITLE)));
            a.setDate(mCursor.getString(mCursor.getColumnIndex(KEY_DATE)));
            return a;
        }
        return null;
    }

    public RssItem getLink(String title) throws SQLException {

        Cursor mCursor;
        mCursor = sqLiteDatabase.query(true, DATABASE_TABLE_2, new String[]{
                        KEY_ID,
                        KEY_DESCRIPTION,
                        KEY_GUID,
                        KEY_TITLE,
                        KEY_DATE,
                        KEY_URL
                },
                KEY_TITLE + "= '" + title + "'",
                null,
                null,
                null,
                null,
                null);
        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            RssItem a = new RssItem();
            a.setGuid(mCursor.getString(mCursor.getColumnIndex(KEY_GUID)));
            a.setTitle(mCursor.getString(mCursor.getColumnIndex(KEY_ID)));
            a.setDescription(mCursor.getString(mCursor.getColumnIndex(KEY_DESCRIPTION)));
            a.setTitle(mCursor.getString(mCursor.getColumnIndex(KEY_TITLE)));
            a.setDate(mCursor.getString(mCursor.getColumnIndex(KEY_DATE)));
            a.setLink(mCursor.getString(mCursor.getColumnIndex(KEY_URL)));
            return a;

        }
        return null;
    }

    public List<MenuItem> getGroup(String group) throws SQLException {

        Cursor mCursor;
        mCursor = sqLiteDatabase.query(true, DATABASE_TABLE, new String[]{
                        KEY_ROWID,
                        KEY_LINK,
                        KEY_URLL,
                        KEY_GROUP,
                        KEY_UPDATE,
                        KEY_TITLE
                },
                KEY_GROUP + "= '" + group + "'",
                null,
                null,
                null,
                null,
                null);
        if (mCursor.moveToFirst()) {
            do {

                MenuItem a = new MenuItem();
                a.setDateUpdate(mCursor.getString(mCursor.getColumnIndex(KEY_UPDATE)));
                a.setTitle(mCursor.getString(mCursor.getColumnIndex(KEY_TITLE)));
                a.setImageLink(mCursor.getString(mCursor.getColumnIndex(KEY_LINK)));
                a.setDbId(mCursor.getInt(mCursor.getColumnIndex(KEY_ROWID)));
                a.setGroup(mCursor.getString(mCursor.getColumnIndex(KEY_GROUP)));
                a.setUrl(mCursor.getString(mCursor.getColumnIndex(KEY_URLL)));

                menuList.add(a);
            } while (mCursor.moveToNext());
            return menuList;
        }
        return null;
    }

    public MenuItem getLast(String title) throws SQLException {
        Cursor mCursor;
        mCursor = sqLiteDatabase.query(true, DATABASE_TABLE, new String[] {

                        KEY_ROWID,
                        KEY_LINK,
                        KEY_URLL,
                        KEY_UPDATE,
                        KEY_GROUP,
                        KEY_TITLE
                },
                KEY_TITLE + "= '" + title + "'",
                null,
                null,
                null,
                null,
                null);
        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToLast();
            MenuItem a = new MenuItem();


            a.setTitle(mCursor.getString(mCursor.getColumnIndex(KEY_TITLE)));
            a.setDbId(mCursor.getInt(mCursor.getColumnIndex(KEY_ROWID)));

            return a;
        }
        return null;
    }

}



