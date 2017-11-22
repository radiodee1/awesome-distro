package org.davidliebman.android.distro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by dave on 4/26/17.
 */

public class ADDownload {

    private String mUrl = "";
    private String mSearchString = "";
    private String mToastMessage = "";
    private long mDateDownload = 0;
    private long mDateOld = 0;
    private ArrayList<String> mList = new ArrayList<>();
    private ArrayList<ADPackageInfo> mPackageList = new ArrayList<>();
    private ArrayList<ADPackageInfo> mDBList = new ArrayList<>();
    private int mListType = 0;
    private PackageDbHelper mDbHelper;
    private boolean mTestingDisable = true;
    private ADDownloadXml mFedXml;

    public static final int ACTION_GZIP_FILE_SHOW_ALL = 1;
    public static final int ACTION_TEXT_FILE_SHOW_ALL = 2;
    public static final int ACTION_GZIP_FILE_SHOW_PACKAGE_DEB = 3;
    public static final int ACTION_GZIP_FILE_SHOW_SECTION_DEB = 4;
    public static final int ACTION_FILE_NO_DOWNLOAD = 5;
    public static final int ACTION_LIST_SHOW_PACKAGE_DEB = 6;
    public static final int ACTION_LIST_SHOW_SECTION_DEB = 7;
    public static final int ACTION_LIST_UPDATE_PACKAGE_DEB = 8;
    public static final int ACTION_LIST_UPDATE_SECTION_DEB = 9;
    public static final int ACTION_ACT_AS_UPDATE = 10;
    public static final int ACTION_ADD_PACKAGE = 11;
    public static final int ACTION_REMOVE_PACKAGE = 12;
    public static final int ACTION_CLEAR_DB = 13;
    public static final int ACTION_GZIP_FILE_SHOW_PACKAGE_FED = 14;
    public static final int ACTION_GZIP_FILE_SHOW_SECTION_FED = 15;
    public static final int ACTION_GZIP_FILE_GET_URL_FED = 16;


    public static final String STRING_PACKAGE = "Package: ";
    public static final String STRING_VERSION = "Version: ";
    public static final String STRING_FILENAME = "Filename: ";
    public static final String STRING_SECTION = "Section: ";

    public static class Entry implements BaseColumns {
        public static final String TABLE_NAME = "package";
        public static final String COLUMN_NAME_NAME = "package_name";
        public static final String COLUMN_NAME_FILENAME = "package_filename";
        public static final String COLUMN_NAME_SECTION = "package_section";
        public static final String COLUMN_NAME_DATE = "package_date";
        public static final String COLUMN_NAME_VERSION = "package_version";
        //public static final String COLUMN_NAME_ID = "id";
    }


    public ADDownload(String url, long old, int action) {
        mUrl = url;
        mDateOld = old;
        mListType = action;
        //mList = downloadFile(mUrl);
        mFedXml = new ADDownloadXml();
        String mFedUrl = "";

        String mDateUrl = mUrl;
        if(mUrl.trim().startsWith("baseurl=")) {
            mDateUrl = mUrl.trim().substring("baseurl=".length()) + "repodata/repomd.xml";
        }
        if(mUrl.trim().startsWith("metalink=")) {
            mDateUrl = mUrl.trim().substring("metalink=".length());
        }

        if (true || mDateUrl.contentEquals(mUrl)) {
            downloadDate(mDateUrl);
        }
        System.out.println("date from download " + new Date(mDateDownload));

        switch (action) {
            case ACTION_GZIP_FILE_SHOW_ALL:
                mList = downloadGzipFile(mUrl);
                break;
            case ACTION_TEXT_FILE_SHOW_ALL:
                mList = downloadFile(mUrl);
                break;
            case ACTION_GZIP_FILE_SHOW_PACKAGE_DEB:
                mList = downloadGzipFile(mUrl);
                fillPackageList();

                break;
            case ACTION_GZIP_FILE_SHOW_SECTION_DEB:
                mList = downloadGzipFile(mUrl);
                //fillPackageList();

                break;
            case ACTION_FILE_NO_DOWNLOAD:
                mList = new ArrayList<>();
                mList.add("newest list is loaded");
                break;
            case ACTION_LIST_SHOW_PACKAGE_DEB:
                break;
            case ACTION_LIST_SHOW_SECTION_DEB:
                break;
            case ACTION_ACT_AS_UPDATE:
                mList = downloadGzipFile(mUrl);

                fillPackageList();
                break;
            case ACTION_LIST_UPDATE_PACKAGE_DEB:
                mList = downloadGzipFile(mUrl);

                fillPackageList();
                break;
            case ACTION_LIST_UPDATE_SECTION_DEB:
                mList = downloadGzipFile(mUrl);

                fillPackageList();
                break;
            case ACTION_GZIP_FILE_SHOW_PACKAGE_FED:
                mFedUrl = getFedUrl();

                break;
            case ACTION_GZIP_FILE_SHOW_SECTION_FED:
                mFedUrl = getFedUrl();

                break;
            case ACTION_GZIP_FILE_GET_URL_FED:
                try {
                    mFedXml = new ADDownloadXml();
                    mFedUrl = getFedUrl();
                    mFedXml = new ADDownloadXml();
                    mFedXml.parseFindPackagelist(inputStreamXmlFile(mFedUrl));
                    mFedUrl = mFedXml.url;
                    mFedXml = new ADDownloadXml();
                    System.out.println(mFedUrl + " test url");
                    mFedXml.parseFindAllPackages(inputStreamGzipFile(mFedUrl));
                    //System.out.println(mFedXml.url);
                }
                catch (Exception e) {e.printStackTrace();}

                break;
        }

    }

    //public void setUrl(String url) {mUrl = url;}
    //public String getUrl() {return mUrl;}
    public void setDateOld(long date) {mDateOld = date;}
    //public Date getDateOld() {return  mDateOld;}
    //public ArrayList<ADPackageInfo> getList() {return mList;}
    public void setSearchString(String s) {mSearchString = s;}
    public long getDateDownload() {return mDateDownload;}
    public String getToastMessage() {return mToastMessage;}
    public void setToastMessage(String in) {mToastMessage = in;}

    public String  getFedUrl() {
        try {

            String METALINK = "metalink=";
            String BASEURL = "baseurl=";

            if(mUrl.trim().startsWith(BASEURL)) {
                mUrl = mUrl.trim().substring(BASEURL.length())
                        + "repodata/repomd.xml";
                return mUrl;
            }
            if(mUrl.trim().startsWith(METALINK)) mUrl = mUrl.trim().substring(METALINK.length());

            mFedXml.parseFindUrl(inputStreamXmlFile(mUrl));

            System.out.println(mFedXml.url + " debug");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return mFedXml.url;
    }

    public ArrayList<ADPackageInfo> getList(int type) {
        ArrayList<ADPackageInfo> sublist = new ArrayList<>();

        if (mListType == ACTION_FILE_NO_DOWNLOAD) {
            //sublist = mList;
            //return sublist;
        }

        mListType = type;


        switch (mListType) {
            case ACTION_GZIP_FILE_SHOW_PACKAGE_DEB:
                for (int i = 0; i < mPackageList.size(); i ++ ) {
                    if (mPackageList.get(i).packageSection.trim().endsWith(mSearchString.trim())) {
                        //ADPackageInfo info = new ADPackageInfo();

                        sublist.add(mPackageList.get(i));
                        //System.out.println(mSearchString);

                    }
                }
                break;
            case ACTION_GZIP_FILE_SHOW_SECTION_DEB:
                ArrayList<String> repeats = new ArrayList<>();

                for (int i = 0; i < mList.size() ; i ++) {
                    if (mList.get(i).startsWith(STRING_SECTION)) {
                        String mSection = mList.get(i).substring((STRING_SECTION).length());
                        if (!repeats.contains(mSection.trim())) {
                            ADPackageInfo info = new ADPackageInfo();
                            info.packageName = mSection;
                            repeats.add(mSection.trim());
                            sublist.add(info);
                        }
                    }
                }
                fillPackageList();
                break;
            case ACTION_LIST_SHOW_PACKAGE_DEB:
                for (int i = 0; i < mPackageList.size(); i ++ ) {
                    if (mPackageList.get(i).packageSection.trim().endsWith(mSearchString.trim())) {
                        sublist.add(mPackageList.get(i));
                        //System.out.println(mSearchString);

                    }
                }
                break;
            case ACTION_LIST_SHOW_SECTION_DEB:
                ArrayList<String> repeats_also = new ArrayList<>();

                for (int i = 0; i < mPackageList.size(); i ++) {
                    if (!repeats_also.contains(mPackageList.get(i).packageSection)) {
                        ADPackageInfo info = new ADPackageInfo();
                        info.packageName = mPackageList.get(i).packageSection;
                        repeats_also.add(mPackageList.get(i).packageSection);
                        sublist.add(info);
                    }
                }
                break;

        }

        return sublist;
    }


    public ArrayList<ADPackageInfo> getDBList(Context context, int action, long date) {

        mListType = action;
        mDateOld = date;

        switch (mListType) {
            case ACTION_LIST_UPDATE_PACKAGE_DEB:
                processNew(context);
                break;
            case ACTION_LIST_UPDATE_SECTION_DEB:
                processNew(context);
                break;
        }
        return mDBList;
    }

    private ArrayList<String> downloadFile(String url) {

        ArrayList<String> list = new ArrayList<>();
        try {
            URL distro = new URL(url);
            URLConnection con = distro.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                //System.out.println(inputLine);
                list.add(inputLine);
            }
            in.close();

        } catch (MalformedURLException me) {
            System.out.println("MalformedURLException: " + me);
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe);
        }

        return list;
    }

    private ArrayList<String> downloadGzipFile(String url) {
        ArrayList<String> list = new ArrayList<>();
        try {
            URL distro = new URL(url);
            URLConnection con = distro.openConnection();

            GZIPInputStream gzis = new GZIPInputStream(con.getInputStream());
            InputStreamReader reader = new InputStreamReader(gzis);
            BufferedReader in = new BufferedReader(reader);

            String readed;
            while ((readed = in.readLine()) != null) {
                //System.out.println(readed);
                list.add(readed.trim());
            }
        } catch (MalformedURLException me) {
            System.out.println("MalformedURLException: " + me);
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe);
        }


        return list;
    }

    public InputStream inputStreamXmlFile(String url) {
        InputStream is = null;
        try {
            URL distro = new URL(url);
            URLConnection con = distro.openConnection();

            //GZIPInputStream
            is = con.getInputStream();


        } catch (MalformedURLException me) {
            System.out.println("MalformedURLException: " + me);
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe);
        }
        return is;
    }

    public InputStream inputStreamGzipFile(String url) {
        GZIPInputStream gzis = null;
        try {



            URL distro = new URL(url);
            URLConnection con = distro.openConnection();

            //GZIPInputStream
            gzis = new GZIPInputStream(con.getInputStream());
            //InputStreamReader reader = new InputStreamReader(gzis);
            //BufferedReader in = new BufferedReader(reader);

            //return gzis;

        } catch (MalformedURLException me) {
            System.out.println("MalformedURLException: " + me);
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe);
        }
        return gzis;
    }

    public void downloadDate(String url) {
        try {
            URL distro = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) distro.openConnection();
            mDateDownload = connection.getLastModified();
            connection.disconnect();
            //mDateNew = new Date(mDateDownload);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillPackageList() {
        int i = 0;
        ADPackageInfo packageInfo = new ADPackageInfo();
        while ( i < mList.size()) {
            if (mList.get(i).startsWith(STRING_SECTION)) {
                packageInfo.packageSection = mList.get(i).substring(STRING_SECTION.length()).trim();
            }
            if (mList.get(i).startsWith(STRING_PACKAGE)) {
                packageInfo.packageName = mList.get(i).substring(STRING_PACKAGE.length());

            }
            if (mList.get(i).startsWith(STRING_VERSION)) {
                packageInfo.packageVersion = mList.get(i).substring(STRING_VERSION.length());
            }
            if (mList.get(i).startsWith(STRING_FILENAME)) {
                packageInfo.packageFilename = mList.get(i).substring(STRING_FILENAME.length());
            }
            if (mList.get(i).isEmpty()) {
                mPackageList.add(packageInfo);
                packageInfo = new ADPackageInfo();
            }
            i++;
        }
        mList = new ArrayList<>();
        System.out.println("total packages from download " + mPackageList.size());
    }

    public void onDestroy() {
        if (mDbHelper != null) mDbHelper.close();
    }

    public void addPackage(Context context, ADPackageInfo info) {
        if (mDbHelper == null) {
            mDbHelper = new PackageDbHelper(context);
        }

        ADPackageInfo record = getPackageRecord(info.packageName);
        if (record.packageName.isEmpty()) savePackageRecord(info);

        //ArrayList<ADPackageInfo> test = getAllPackageRecords();
        //for(int i = 0; i < test.size(); i ++ ) System.out.println("saved " + test.get(i));
        //System.out.println("done list saved");

    }

    public void removePackage(Context context, ADPackageInfo info) {
        if (mDbHelper == null) {
            mDbHelper = new PackageDbHelper(context);
        }
        deletePackage(info);

    }
    public ArrayList<ADPackageInfo> getWatchedPackages(Context context) {
        if (mDbHelper == null) {
            mDbHelper = new PackageDbHelper(context);
        }
        return getAllPackageRecords();
    }

    public void processNew(Context context) {
        mDBList = new ArrayList<>();

        System.out.println("time long " + new Date(mDateOld) + " == " + new Date(mDateDownload));

        if ((mDateOld >= mDateDownload || mDateOld == 0 ) && !mTestingDisable) {
            mToastMessage = "The Package file is already the newest";
            return;
        }
        //mPackageList = new ArrayList<>();

        if (mDbHelper == null) {
            mDbHelper = new PackageDbHelper(context);
        }
        ArrayList<ADPackageInfo> test = getAllPackageRecords();

        for (int j = 0; j < test.size(); j ++) {
            // check if file is new!
            for (int i = 0; i < mPackageList.size(); i++) {
                //display packages that are not the same version number
                ADPackageInfo info = test.get(j);// getPackageRecord(mPackageList.get(i).packageName);
                if (info.packageName.contentEquals(mPackageList.get(i).packageName) &&
                        !info.packageVersion.contentEquals(mPackageList.get(i).packageVersion)) {
                    mDBList.add(info);
                    System.out.println("check update ---- " + info);

                }
            }
        }
        System.out.println("done loop");

    }

    public void setAsUpdate(Context context) {

        System.out.println("time long " + new Date(mDateOld) + " == " + new Date(mDateDownload));

        if (mDateOld >= mDateDownload && mDateOld != 0) {
            mDBList = new ArrayList<>();
            mToastMessage = "listings are already newest!";
            return;
        }

        if (mDbHelper == null) {
            mDbHelper = new PackageDbHelper(context);
        }

        ArrayList<ADPackageInfo> test = getAllPackageRecords();

        for (int j = 0; j < test.size(); j ++ ) {
            for (int i = 0; i < mPackageList.size(); i++) {
                //check for each package -- update version num
                ADPackageInfo info = test.get(j);
                ADPackageInfo pack = mPackageList.get(i);
                pack.packageDate = mDateDownload;

                if (!info.packageVersion.contentEquals(mPackageList.get(i).packageVersion)) {

                    updatePackageRecord(pack);
                }
            }
        }

    }

    public void deleteDB(Context context) {
        if (mDbHelper == null) {
            mDbHelper = new PackageDbHelper(context);
        }
        String SQL_DELETE_ENTRIES = "DELETE FROM " + Entry.TABLE_NAME +" ;";
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        //System.out.println("delete database here -------------------");

    }

    private void deletePackage(ADPackageInfo info) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define 'where' part of query.
        String selection = Entry.COLUMN_NAME_NAME + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { info.packageName };
        // Issue SQL statement.
        db.delete(Entry.TABLE_NAME, selection, selectionArgs);

    }

    private ArrayList<ADPackageInfo> getAllPackageRecords() {
        ArrayList<ADPackageInfo> list = new ArrayList<>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        /*
        String[] projection = {
                Entry._ID,
                Entry.COLUMN_NAME_NAME,
                Entry.COLUMN_NAME_FILENAME,
                Entry.COLUMN_NAME_SECTION,
                Entry.COLUMN_NAME_VERSION,
                Entry.COLUMN_NAME_DATE
        };
        */

        String mRawQuery = "SELECT * FROM " + Entry.TABLE_NAME ;

        Cursor cursor = db.rawQuery(mRawQuery, null);

        while(cursor.moveToNext()) {
            ADPackageInfo record = new ADPackageInfo();

            record.packageName = cursor.getString(cursor.getColumnIndexOrThrow(Entry.COLUMN_NAME_NAME));
            record.packageSection = cursor.getString(cursor.getColumnIndexOrThrow(Entry.COLUMN_NAME_SECTION));
            record.packageFilename = cursor.getString(cursor.getColumnIndexOrThrow(Entry.COLUMN_NAME_FILENAME));
            record.packageVersion = cursor.getString(cursor.getColumnIndexOrThrow(Entry.COLUMN_NAME_VERSION));
            record.packageDate = cursor.getLong(cursor.getColumnIndexOrThrow(Entry.COLUMN_NAME_DATE));
            //record.packageIsNew = true;
            list.add(record);

        }
        cursor.close();
        return list;
    }

    private ADPackageInfo getPackageRecord(String pName) {
        ADPackageInfo record = new ADPackageInfo();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                Entry._ID,
                Entry.COLUMN_NAME_NAME,
                Entry.COLUMN_NAME_FILENAME,
                Entry.COLUMN_NAME_SECTION,
                Entry.COLUMN_NAME_VERSION,
                Entry.COLUMN_NAME_DATE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = Entry.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { pName };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = Entry.COLUMN_NAME_NAME + " DESC";

        Cursor cursor = db.query(
                Entry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        //List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {

            record.packageName = cursor.getString(cursor.getColumnIndexOrThrow(Entry.COLUMN_NAME_NAME));
            record.packageSection = cursor.getString(cursor.getColumnIndexOrThrow(Entry.COLUMN_NAME_SECTION));
            record.packageFilename = cursor.getString(cursor.getColumnIndexOrThrow(Entry.COLUMN_NAME_FILENAME));
            record.packageVersion = cursor.getString(cursor.getColumnIndexOrThrow(Entry.COLUMN_NAME_VERSION));
            record.packageDate = cursor.getLong(cursor.getColumnIndexOrThrow(Entry.COLUMN_NAME_DATE));
            record.packageIsNew = true;
        }
        cursor.close();

        return record;
    }

    private void savePackageRecord(ADPackageInfo record) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Entry.COLUMN_NAME_NAME, record.packageName);
        values.put(Entry.COLUMN_NAME_FILENAME, record.packageFilename);
        values.put(Entry.COLUMN_NAME_SECTION, record.packageSection);
        values.put(Entry.COLUMN_NAME_VERSION, record.packageVersion);
        values.put(Entry.COLUMN_NAME_DATE, record.packageDate);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(Entry.TABLE_NAME, null, values);
    }

    private void updatePackageRecord (ADPackageInfo record) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(Entry.COLUMN_NAME_VERSION, record.packageVersion);

        // Which row to update, based on the title
        String selection = Entry.COLUMN_NAME_NAME + " LIKE ?";
        String[] selectionArgs = { record.packageName };

        int count = db.update(
                Entry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

    }
    ///////////////////////////////////////////////

    public class PackageDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Distro.db";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Entry.TABLE_NAME + " (" +
                        Entry._ID + " INTEGER PRIMARY KEY," +
                        Entry.COLUMN_NAME_NAME + " TEXT," +
                        Entry.COLUMN_NAME_FILENAME + " TEXT, "+
                        Entry.COLUMN_NAME_VERSION + " TEXT, " +
                        Entry.COLUMN_NAME_SECTION + " TEXT, " +
                        Entry.COLUMN_NAME_DATE + " INTEGER " +
                        ")";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Entry.TABLE_NAME;

        public PackageDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
