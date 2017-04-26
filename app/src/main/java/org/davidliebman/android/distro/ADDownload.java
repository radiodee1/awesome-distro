package org.davidliebman.android.distro;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dave on 4/26/17.
 */

public class ADDownload {

    private String mUrl = "";
    private Date mDateOld = new Date();
    private Date mDateNew = new Date();
    private ArrayList<String> mList = new ArrayList<>();

    public ADDownload(String url, Date old) {
        mUrl = url;
        mDateOld = old;
        mList = downloadFile(mUrl);

    }

    public void setUrl(String url) {mUrl = url;}
    public String getUrl() {return mUrl;}
    public void setDateOld(Date date) {mDateOld = date;}
    public Date getDateOld() {return  mDateOld;}
    public ArrayList<String> getList() {return mList;}

    private ArrayList<String> downloadFile(String url) {

        ArrayList<String> list = new ArrayList<>();
        try {
            URL distro = new URL(url);
            URLConnection con = distro.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
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

}
