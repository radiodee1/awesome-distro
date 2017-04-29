package org.davidliebman.android.distro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.GZIPInputStream;

/**
 * Created by dave on 4/26/17.
 */

public class ADDownload {

    private String mUrl = "";
    private String mSearchString = "";
    private long mDateDownload = 0;
    private long mDateOld = 0;
    private ArrayList<String> mList = new ArrayList<>();
    private ArrayList<ADPackageInfo> mPackageList = new ArrayList<>();
    private int mListType = 0;

    public static final int ACTION_GZIP_FILE_SHOW_ALL = 1;
    public static final int ACTION_TEXT_FILE_SHOW_ALL = 2;
    public static final int ACTION_GZIP_FILE_SHOW_PACKAGE_DEB = 3;
    public static final int ACTION_GZIP_FILE_SHOW_SECTION_DEB = 4;
    public static final int ACTION_FILE_NO_DOWNLOAD = 5;
    public static final int ACTION_LIST_SHOW_PACKAGE_DEB = 6;
    public static final int ACTION_LIST_SHOW_SECTION_DEB = 7;

    public static final String STRING_PACKAGE = "Package: ";
    public static final String STRING_VERSION = "Version: ";
    public static final String STRING_FILENAME = "Filename: ";
    public static final String STRING_SECTION = "Section: ";


    public ADDownload(String url, long old, int action) {
        mUrl = url;
        mDateOld = old;
        mListType = action;
        //mList = downloadFile(mUrl);

        downloadDate(mUrl);
        System.out.println("date from download " + new Date(mDateDownload));

        if (mDateOld > mDateDownload || mDateOld == 0) {
            // list is newest list available
            action = ACTION_FILE_NO_DOWNLOAD;
        }

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
        }

    }

    public void setUrl(String url) {mUrl = url;}
    public String getUrl() {return mUrl;}
    //public void setDateOld(Date date) {mDateOld = date;}
    //public Date getDateOld() {return  mDateOld;}
    public ArrayList<String> getList() {return mList;}
    public void setSearchString(String s) {mSearchString = s;}
    public long getDateDownload() {return mDateDownload;}

    public ArrayList<String> getList(int type) {
        ArrayList<String> sublist = new ArrayList<>();

        if (mListType == ACTION_FILE_NO_DOWNLOAD) {
            sublist = mList;
            return sublist;
        }

        mListType = type;


        switch (mListType) {
            case ACTION_GZIP_FILE_SHOW_PACKAGE_DEB:
                for (int i = 0; i < mPackageList.size(); i ++ ) {
                    if (mPackageList.get(i).packageSection.trim().endsWith(mSearchString.trim())) {
                        sublist.add(mSearchString + " " + mPackageList.get(i).packageName);
                        //System.out.println(mSearchString);

                    }
                }
                break;
            case ACTION_GZIP_FILE_SHOW_SECTION_DEB:
                for (int i = 0; i < mList.size() ; i ++) {
                    if (mList.get(i).startsWith(STRING_SECTION)) {
                        String mSection = mList.get(i).substring((STRING_SECTION).length());
                        if (!sublist.contains(mSection)) {
                            sublist.add(mSection);
                        }
                    }
                }
                fillPackageList();
                break;
            case ACTION_LIST_SHOW_PACKAGE_DEB:
                for (int i = 0; i < mPackageList.size(); i ++ ) {
                    if (mPackageList.get(i).packageSection.trim().endsWith(mSearchString.trim())) {
                        sublist.add(mSearchString + " " + mPackageList.get(i).packageName);
                        //System.out.println(mSearchString);

                    }
                }
                break;
            case ACTION_LIST_SHOW_SECTION_DEB:
                for (int i = 0; i < mPackageList.size(); i ++) {
                    if (!sublist.contains(mPackageList.get(i).packageSection)) {
                        sublist.add(mPackageList.get(i).packageSection);
                    }
                }
                break;

        }

        return sublist;
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

            //ByteArrayInputStream bais = new ByteArrayInputStream(responseBytes);
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
    }

    public void processNew() {

    }
}
