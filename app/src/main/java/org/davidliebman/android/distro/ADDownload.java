package org.davidliebman.android.distro;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private Date mDateOld = new Date();
    private Date mDateNew = new Date();
    private ArrayList<String> mList = new ArrayList<>();
    private ArrayList<ADPackageInfo> mPackageList = new ArrayList<>();
    private int mListType = 0;

    public static final int ACTION_GZIP_FILE_SHOW_ALL = 1;
    public static final int ACTION_TEXT_FILE_SHOW_ALL = 2;
    public static final int ACTION_GZIP_FILE_SHOW_PACKAGE_DEB = 3;
    public static final int ACTION_GZIP_FILE_SHOW_SECTION_DEB = 4;



    public ADDownload(String url, Date old, int action) {
        mUrl = url;
        mDateOld = old;
        mListType = action;
        //mList = downloadFile(mUrl);

        switch (action) {
            case ACTION_GZIP_FILE_SHOW_ALL:
                mList = downloadGzipFile(mUrl);
                break;
            case ACTION_TEXT_FILE_SHOW_ALL:
                mList = downloadFile(mUrl);
                break;
            case ACTION_GZIP_FILE_SHOW_PACKAGE_DEB:
                mList = downloadGzipFile(mUrl);
                break;
            case ACTION_GZIP_FILE_SHOW_SECTION_DEB:
                mList = downloadGzipFile(mUrl);
                break;
        }

    }

    public void setUrl(String url) {mUrl = url;}
    public String getUrl() {return mUrl;}
    public void setDateOld(Date date) {mDateOld = date;}
    public Date getDateOld() {return  mDateOld;}
    public ArrayList<String> getList() {return mList;}
    public void setSearchString(String s) {mSearchString = s;}

    public ArrayList<String> getList(int type) {
        ArrayList<String> sublist = new ArrayList<>();
        mListType = type;


        switch (mListType) {
            case ACTION_GZIP_FILE_SHOW_PACKAGE_DEB:
                fillPackageList();

                for (int i = 0; i < mPackageList.size(); i ++ ) {
                    if (mPackageList.get(i).packageSection.trim().endsWith(mSearchString.trim())) {
                        sublist.add(mSearchString + " " + mPackageList.get(i).packageName);
                        //System.out.println(mSearchString);

                    }
                }
                break;
            case ACTION_GZIP_FILE_SHOW_SECTION_DEB:
                for (int i = 0; i < mList.size() ; i ++) {
                    if (mList.get(i).startsWith("Section")) {
                        String mSection = mList.get(i).substring(("Section:").length());
                        if (!sublist.contains(mSection)) {
                            sublist.add(mSection);
                        }
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

    private void fillPackageList() {
        int i = 0;
        ADPackageInfo packageInfo = new ADPackageInfo();
        while ( i < mList.size()) {
            if (mList.get(i).startsWith("Section:")) {
                packageInfo.packageSection = mList.get(i).substring("Section: ".length()).trim();
            }
            if (mList.get(i).startsWith("Package:")) {
                packageInfo.packageName = mList.get(i).substring("Package: ".length());

            }
            if (mList.get(i).startsWith("Version:")) {
                packageInfo.packageVersion = mList.get(i).substring("Version: ".length());
            }
            if (mList.get(i).startsWith("Filename:")) {
                packageInfo.packageFilename = mList.get(i).substring("Filename: ".length());
            }
            if (mList.get(i).isEmpty()) {
                mPackageList.add(packageInfo);
                packageInfo = new ADPackageInfo();
            }
            i++;
        }
        mList = null;
    }
}
