package org.davidliebman.android.distro;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dave on 11/21/17.
 */

public class ADDownloadXml {

    private static final String TAG_METADATA = "metadata";
    private static final String TAG_METALINK = "metalink";
    private static final String TAG_FILES = "files";
    private static final String TAG_FILE = "file";
    private static final String TAG_VERIFICATION = "verification";
    private static final String TAG_RESOURCES = "resources";
    private static final String TAG_URL = "url";
    private static final String TAG_REPOMD = "repomd";
    private static final String TAG_DATA = "data";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_PACKAGE = "package";
    private static final String TAG_NAME = "name";
    private static final String TAG_ARCH = "arch";
    private static final String TAG_VERSION = "version";
    private static final String TAG_FORMAT = "format";
    private static final String TAG_RPMGROUP = "rpm:group";

    private static final String ATTR_PROTOCOL = "protocol";
    private static final String ATTR_TYPE = "type";
    private static final String ATTR_PREFERENCE = "preference";
    private static final String ATTR_HREF = "href";
    private static final String VAL_HTTP = "http";
    private static final String VAL_HTTPS = "https";
    private static final String VAL_PRIMARY = "primary";

    private static final String URL_PART_ENDING = "repodata/repomd.xml";

    private XmlPullParser mXpp;
    public String url = "";
    public ArrayList<ADPackageInfo> list = new ArrayList<>();

    private static final String ns = null;
    boolean mDebug = true;
    int mCount = 0;

    public void parseFindUrl(InputStream in) throws XmlPullParserException, IOException {
        try {
            mXpp = Xml.newPullParser();
            mXpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            mXpp.setInput(in, null);
            mXpp.nextTag();
            //return
            //startRead(TAG_METALINK);
            metalink();
        } finally {
            if (in != null) in.close();
        }
    }

    public void parseFindPackagelist( InputStream in) throws XmlPullParserException, IOException {
        //List entries = new ArrayList();

        try {
            mXpp = Xml.newPullParser();
            mXpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            mXpp.setInput(in, null);
            mXpp.nextTag();
            //return
            //startRead(TAG_METALINK);
            repomd();
        } finally {
            if (in != null) in.close();
        }
    }

    public void parseFindAllPackages( InputStream in) throws XmlPullParserException, IOException {
        //List entries = new ArrayList();

        try {
            mXpp = Xml.newPullParser();
            mXpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            mXpp.setInput(in, null);
            mXpp.nextTag();
            //return
            //startRead(TAG_METALINK);
            metadata();
        } finally {
            if (in != null) in.close();
        }
    }

    private void skip() throws XmlPullParserException, IOException {
        if (mXpp.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (mXpp.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    /////////////////// tag consuming methods start //////////////////////////

    public int consumeStartTag(String tagname, String attributeName) {
        int mIndexNum = 0;
        boolean mAdvance = false;
        try {

            if (mXpp.getEventType() == XmlPullParser.END_DOCUMENT) {
                return 0;
            }

            if (attributeName != null &&
                    mXpp.getEventType() == XmlPullParser.START_TAG &&
                    mXpp.getName().equalsIgnoreCase(tagname)) {


                mIndexNum = Integer.valueOf(mXpp.getAttributeValue(null, attributeName));

                mAdvance = true;
                mXpp.next();

            } else if (mXpp.getEventType() == XmlPullParser.START_TAG &&
                    mXpp.getName().equalsIgnoreCase(tagname) ) {
                mAdvance = true;
                mXpp.next();
            }
            while (!(mXpp.getEventType() == XmlPullParser.START_TAG  &&
                    mXpp.getName().equalsIgnoreCase(tagname)) && !mAdvance) {
                mXpp.next();
            }

            if (mDebug) {

                System.out.println(" start at " + tagname + " " + mXpp.getName() + " " + mIndexNum);
            }
            //mXpp.next();


        }
        catch (Exception e) {
            System.out.println("consumeStartTag");
        }
        //eventType = mXpp.next();
        return mIndexNum;
    }

    public int consumeStartTag(String tagname) {

        return consumeStartTag(tagname, null);
    }

    public void consumeEndTag(String tagname)  {

        try {

            //int eventType = mXpp.getEventType();
            if (mXpp.getEventType() == XmlPullParser.END_DOCUMENT) {
                //mXpp.next();
                return;
            }

            while (!(mXpp.getEventType() == XmlPullParser.END_TAG &&
                    mXpp.getName().equalsIgnoreCase(tagname))) {
                mXpp.next();// remove me???
            }

            //mXpp.next();
            if (mDebug) {
                System.out.println(" end with " + mXpp.getName() + " as " + tagname);
            }




        }
        catch(Exception e) {
            System.out.println("consumeEndTag");
            e.printStackTrace();
        }
    }

    public String getText()  {
        try {

            while (mXpp.getEventType() != XmlPullParser.TEXT) {
                mXpp.next();
            }
            int eventType = mXpp.getEventType();
            if (eventType == XmlPullParser.END_DOCUMENT || eventType != XmlPullParser.TEXT) {
                return "";
            }
            String mReply = mXpp.getText();
            mXpp.next();

            return mReply;
        }
        catch(Exception e) {
            System.out.println("getText");
        }
        return "";
    }

    public void advanceToTag(String in) {
        try {
            if (in != null && mXpp.getName() != null) {
                boolean test = true;
                while (test && !mXpp.getName().contentEquals(in)) {
                    if (mDebug) System.out.println(mXpp.getPositionDescription());
                    mXpp.next();
                    if (mXpp.getName() == null) test = false;
                }
            }
            else {
                while (mXpp.getEventType() != XmlPullParser.START_TAG) {
                    mXpp.next();
                }
            }
        }
        catch (Exception e) {
            System.out.println("error at advance");
            e.printStackTrace();
        }
    }

    //////////////// tag consuming methods end /////////////////////////////

    /* metalink file */

    private void metalink() throws XmlPullParserException, IOException{
        //this.consumeStartTag(TAG_METALINK);

        this.advanceToTag(TAG_FILES);
        System.out.println("metalink");
        files();

        this.consumeEndTag(TAG_METALINK);
    }

    private void files() {
        this.consumeStartTag(TAG_FILES);
        System.out.println("files");
        file();
        this.consumeEndTag(TAG_FILES);
    }

    private void file() {
        this.consumeStartTag(TAG_FILE);
        this.advanceToTag(TAG_RESOURCES);
        resources();
        this.consumeEndTag(TAG_FILE);
    }

    private void resources()  {
        this.consumeStartTag(TAG_RESOURCES);
        try {

            /* technique for multiple identical tags */
            this.advanceToTag(TAG_URL);
            url();
            //mXpp.next();
            this.advanceToTag(TAG_URL);
            while (mXpp.getEventType() == XmlPullParser.END_TAG &&
                    mXpp.getName().equalsIgnoreCase(TAG_URL)) {

                mXpp.next();
                if (mXpp.getEventType() == XmlPullParser.START_TAG &&
                        mXpp.getName().equalsIgnoreCase(TAG_URL)) {
                    url();

                }
            }
        }
        catch (Exception e) {
            System.out.println("exception url pos");
        }
        this.consumeEndTag(TAG_RESOURCES);
    }

    private void url() {
        this.consumeStartTag(TAG_URL);
        String protocol = mXpp.getAttributeValue(null, ATTR_PROTOCOL);
        String type = mXpp.getAttributeValue(null, ATTR_TYPE);
        String preference = mXpp.getAttributeValue(null, ATTR_PREFERENCE);
        String newUrl = this.getText();
        if ((protocol.contentEquals(VAL_HTTP) || protocol.contentEquals(VAL_HTTPS)) &&
                (type.contentEquals(VAL_HTTPS) || type.contentEquals(VAL_HTTP)) &&
                url.contentEquals("")) {
            url = newUrl.trim();
        }
        System.out.println(url+ " new url ");
        this.consumeEndTag(TAG_URL);
    }

    /* repomd file */

    private void repomd() {
        this.consumeStartTag(TAG_REPOMD);
        this.advanceToTag(TAG_DATA);

        //data();
        try {

            /* technique for multiple identical tags */
            this.advanceToTag(TAG_DATA);
            data();
            //mXpp.next();
            this.advanceToTag(TAG_DATA);
            while (mXpp.getEventType() == XmlPullParser.END_TAG &&
                    mXpp.getName().equalsIgnoreCase(TAG_DATA)) {

                mXpp.next();
                if (mXpp.getEventType() == XmlPullParser.START_TAG &&
                        mXpp.getName().equalsIgnoreCase(TAG_DATA)) {
                    data();

                }
            }
        }
        catch (Exception e) {
            System.out.println("exception url pos");
        }
        this.consumeEndTag(TAG_REPOMD);
    }

    private void data() {
        this.advanceToTag(TAG_DATA);
        String mDataType = mXpp.getAttributeValue(null,ATTR_TYPE);
        //this.consumeStartTag(TAG_DATA);
        System.out.println(mDataType + " -- " + mXpp.getName());
        if (mDataType.contentEquals(VAL_PRIMARY)) {
            //this.consumeStartTag(TAG_DATA);
            this.advanceToTag(TAG_LOCATION);
            location();
        }

        this.consumeEndTag(TAG_DATA);
    }

    private void location() {
        this.consumeStartTag(TAG_LOCATION);
        String mLocation = mXpp.getAttributeValue(null,ATTR_HREF);
        System.out.println(mLocation);

        String mNewUrl = url.substring(0, url.length() - URL_PART_ENDING.length()) + mLocation;

        url = mNewUrl;
        System.out.println(url + " package url");

        this.consumeEndTag(TAG_LOCATION);
    }

    /* metadata package file */

    private void metadata() {
        this.consumeStartTag(TAG_METADATA);

        mCount = 0;
        try {

            /* technique for multiple identical tags */
            this.advanceToTag(TAG_PACKAGE);
            metadata_package();
            //mXpp.next();
            this.advanceToTag(TAG_PACKAGE);
            while (mCount < 10 && mXpp.getEventType() == XmlPullParser.END_TAG &&
                    mXpp.getName().equalsIgnoreCase(TAG_PACKAGE)) {

                mXpp.next();
                if (mXpp.getEventType() == XmlPullParser.START_TAG &&
                        mXpp.getName().equalsIgnoreCase(TAG_PACKAGE)) {
                    metadata_package();

                }
            }
        }
        catch (Exception e) {
            System.out.println("exception url pos");
        }
        this.consumeEndTag(TAG_METADATA);
    }

    private void metadata_package() {
        this.consumeStartTag(TAG_PACKAGE);
        mCount ++;
        try {
            while (mXpp.getName() != null || (mXpp.getEventType() != XmlPullParser.END_TAG &&
                    !mXpp.getName().equalsIgnoreCase(TAG_PACKAGE))) {
                if (mXpp.getName().equalsIgnoreCase(TAG_NAME)) {
                    package_name();
                }
                else if (mXpp.getName().equalsIgnoreCase(TAG_ARCH)) {
                    package_arch();
                }
                else if (mXpp.getName().equalsIgnoreCase(TAG_VERSION)) {
                    package_version();
                }
                else if (mXpp.getName().equalsIgnoreCase(TAG_FORMAT)) {
                    package_format();
                }
                else {
                    mXpp.next();
                }
            }
        }
        catch (Exception e){e.printStackTrace();}


        this.consumeEndTag(TAG_PACKAGE);
    }

    private void package_name() {
        this.consumeStartTag(TAG_NAME);
        String name = this.getText();
        System.out.println(mCount + " " + name);
        this.consumeEndTag(TAG_NAME);
    }
    private void package_arch() {
        this.consumeStartTag(TAG_ARCH);

        this.consumeEndTag(TAG_ARCH);

    }
    private void package_version() {
        this.consumeStartTag(TAG_VERSION);

        this.consumeEndTag(TAG_VERSION);

    }
    private void package_format() {
        this.consumeStartTag(TAG_FORMAT);

        this.consumeEndTag(TAG_FORMAT);
    }
}
