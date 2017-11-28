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
    private static final String TAG_TAGS = "tags";

    private static final String ATTR_PROTOCOL = "protocol";
    private static final String ATTR_TYPE = "type";
    private static final String ATTR_PREFERENCE = "preference";
    private static final String ATTR_HREF = "href";
    private static final String ATTR_PACKAGES = "packages";
    private static final String ATTR_EPOCH = "epoch";
    private static final String ATTR_VER = "ver";
    private static final String ATTR_REL = "rel";

    private static final String VAL_HTTP = "http";
    private static final String VAL_HTTPS = "https";
    private static final String VAL_PRIMARY = "primary";

    private static final String URL_PART_ENDING = "repodata/repomd.xml";

    private XmlPullParser mXpp;
    public String url = "";
    private String baseUrl = "";
    public ArrayList<ADPackageInfo> list = new ArrayList<>();
    private ADPackageInfo package_info = null;
    private int total_packages = 0;
    private String mSearchString = "";
    private int mListType = 0;
    private String latestArch = "";

    private static final String ns = null;
    boolean mDebug = false;
    int mCount = 0;
    int mCountLimit = -1;
    boolean mFoundGzUrl = false;

    public ADDownloadXml (String baseUrl) {
        this.baseUrl = baseUrl;// getFedUrl(baseUrl);
    }

    public String  parseFindUrl(InputStream in) throws XmlPullParserException, IOException {
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
        return url;
    }

    public String  parseFindPackagelist( InputStream in) throws XmlPullParserException, IOException {
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
        return url;
    }

    public String  parseFindAllPackages( InputStream in) throws XmlPullParserException, IOException {
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
        return url;
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
        advanceToTag(in, true);
    }

    public void advanceToTag(String in, boolean checkDepth) {
        try {
            int depth = 0;

            if (in != null ) {
                if ((mXpp.getName() != null && mXpp.getName().contentEquals(in)) ||
                        mXpp.getEventType() == XmlPullParser.END_DOCUMENT) {
                    return;
                }


                boolean test = true;
                while (test ){
                    //if (mDebug) System.out.println(mXpp.getPositionDescription());
                    mXpp.next();
                    //if (mXpp.getName() == null ) mXpp.next();

                    if(mXpp.getEventType() == XmlPullParser.END_TAG) depth --;
                    if(mXpp.getEventType() == XmlPullParser.START_TAG) depth ++;
                    if(checkDepth && depth < 0) {
                        if (mDebug) System.out.println("depth bad "+ in);
                        return;
                    }

                    if ((mXpp.getEventType() == XmlPullParser.START_TAG ||
                        mXpp.getEventType() == XmlPullParser.END_TAG) &&
                            mXpp.getName() != null &&
                            mXpp.getName().contentEquals(in)) {
                        test = false;
                    }
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

    public String  getFedUrl(String url) {
        try {

            String METALINK = "metalink=";
            String BASEURL = "baseurl=";

            if(url.trim().startsWith(BASEURL)) {
                url = url.trim().substring(BASEURL.length())
                        + "repodata/repomd.xml";
                return url;
            }
            if(url.trim().startsWith(METALINK)) url = url.trim().substring(METALINK.length());

            System.out.println(url + " debug");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public ArrayList<ADPackageInfo> getList() {return list;}
    public int getTotalPackages() {return total_packages;}
    public void setSearchString(String s) {mSearchString = s;}
    public void setListType(int lt) {mListType = lt;}

    ////////////////////////////////////////////////////////////////////////
    /* metalink file ----------------------------- */

    private void metalink() throws XmlPullParserException, IOException{
        //this.consumeStartTag(TAG_METALINK);

        this.advanceToTag(TAG_FILES);
        if (mDebug)System.out.println("metalink");
        files();

        this.consumeEndTag(TAG_METALINK);
    }

    private void files() {
        this.consumeStartTag(TAG_FILES);
        if (mDebug) System.out.println("files");
        file();
        this.consumeEndTag(TAG_FILES);
    }

    private void file() {
        this.consumeStartTag(TAG_FILE);
        this.advanceToTag(TAG_RESOURCES,true);
        resources();
        this.consumeEndTag(TAG_FILE);
    }

    private void resources()  {
        this.consumeStartTag(TAG_RESOURCES);
        try {

            /* technique for multiple identical tags */
            this.advanceToTag(TAG_URL);
            url();
            mXpp.next();
            this.advanceToTag(TAG_URL);
            while (mXpp.getEventType() == XmlPullParser.END_TAG &&
                    mXpp.getName().equalsIgnoreCase(TAG_URL)) {

                mXpp.next();
                this.advanceToTag(TAG_URL,false);
                if (mXpp.getEventType() == XmlPullParser.START_TAG &&
                        mXpp.getName().equalsIgnoreCase(TAG_URL)) {
                    url();

                }
            }
        }
        catch (Exception e) {
            System.out.println("exception url pos");
            e.printStackTrace();
        }
        this.consumeEndTag(TAG_RESOURCES);
    }

    private void url() {
        this.consumeStartTag(TAG_URL);
        String protocol = mXpp.getAttributeValue(null, ATTR_PROTOCOL);
        String type = mXpp.getAttributeValue(null, ATTR_TYPE);
        String preference = mXpp.getAttributeValue(null, ATTR_PREFERENCE);
        String newUrl = this.getText();
        if (true || (protocol.contentEquals(VAL_HTTP) || protocol.contentEquals(VAL_HTTPS)) &&
                (type.contentEquals(VAL_HTTPS) || type.contentEquals(VAL_HTTP)) &&
                (url.contentEquals("") )) {
            url = getFedUrl( newUrl.trim());
        }
        if (mDebug) System.out.println(url+ " new url ");
        this.consumeEndTag(TAG_URL);
    }

    /* repomd file ------------------------------- */

    private void repomd() {
        this.consumeStartTag(TAG_REPOMD);
        //this.advanceToTag(TAG_DATA); // <-------------

        mFoundGzUrl = false;
        //data();
        try {

            /* technique for multiple identical tags */
            this.advanceToTag(TAG_DATA);
            data();
            //mXpp.next();
            //this.advanceToTag(TAG_DATA);
            while (mXpp.getEventType() == XmlPullParser.END_TAG &&
                    mXpp.getName().equalsIgnoreCase(TAG_DATA)) {

                mXpp.next();
                this.advanceToTag(TAG_DATA,true);
                if (mXpp.getEventType() == XmlPullParser.START_TAG &&
                        mXpp.getName().equalsIgnoreCase(TAG_DATA) ) {
                    data();

                }
            }

            if (mDebug) System.out.println("new url " + url);

        }
        catch (Exception e) {
            System.out.println("exception url pos");
        }
        this.consumeEndTag(TAG_REPOMD);
    }

    private void data() {
        try {
            this.advanceToTag(TAG_DATA);
            String mDataType = mXpp.getAttributeValue(null, ATTR_TYPE);

            if (mDebug) System.out.println(mDataType + " -- " + mXpp.getName());
            this.consumeStartTag(TAG_DATA);


            if (mDataType.contentEquals(VAL_PRIMARY)) {
                //this.consumeStartTag(TAG_DATA);
                this.advanceToTag(TAG_LOCATION);
                //mXpp.next();
                if (mDebug) System.out.println("content equals " + VAL_PRIMARY);
                mFoundGzUrl = true;
                location();
            }
            //this.advanceToTag(TAG_DATA);
            this.consumeEndTag(TAG_DATA);
        }
        catch (Exception e) {e.printStackTrace();}
    }

    private void location() {
        this.consumeStartTag(TAG_LOCATION);
        String mLocation = mXpp.getAttributeValue(null,ATTR_HREF);
        if (mDebug) System.out.println(mLocation);

        String mBase = baseUrl;
        String mNewUrl = mBase.substring(0, mBase.length() - URL_PART_ENDING.length()) + mLocation;
        url = mNewUrl;
        //if (url.contentEquals("")) url = mNewUrl;
        if (mDebug) System.out.println(url + " package url");

        this.consumeEndTag(TAG_LOCATION);
    }

    /* metadata package file ----------------------------- */

    private void metadata() {
        //String packages = mXpp.getAttributeValue(null,ATTR_PACKAGES);
        total_packages = this.consumeStartTag(TAG_METADATA, ATTR_PACKAGES);

        //System.out.println(total_packages + " tot <------------");

        mCount = 0;
        try {

            /* technique for multiple identical tags */
            this.advanceToTag(TAG_PACKAGE);
            metadata_package();
            //mXpp.next();
            //this.advanceToTag(TAG_PACKAGE);
            while ((mCount < mCountLimit || mCountLimit == -1) &&
                    mXpp.getEventType() == XmlPullParser.END_TAG &&
                    //mXpp.getName() != null &&
                    mXpp.getName().equalsIgnoreCase(TAG_PACKAGE)
                    ) {
                //System.out.println(total_packages + " tot loop <------------");



                mXpp.next();
                this.advanceToTag(TAG_PACKAGE,true);
                if (mXpp.getEventType() == XmlPullParser.START_TAG &&
                        mXpp.getName().contentEquals(TAG_PACKAGE)) {
                    metadata_package();
                    //this.advanceToTag(TAG_PACKAGE);
                }


                //this.advanceToTag(TAG_PACKAGE);
                //System.out.println(total_packages + " tot loop 2 <------------");

                //mXpp.next();
            }
        }
        catch (Exception e) {
            System.out.println("exception ");
            e.printStackTrace();
        }
        //this.advanceToTag(TAG_METADATA);
        this.consumeEndTag(TAG_METADATA);
    }

    private void metadata_package() {
        this.consumeStartTag(TAG_PACKAGE);
        mCount ++;
        package_info = new ADPackageInfo();
        try {
            //System.out.println(mCount + " <--" );
            boolean loop = true;
            int checkRun = 0;
            while (loop ){
                if (mXpp.getName() == null) {
                    //mXpp.next();
                }
                else if (checkRun > 10 || mXpp.getEventType() == XmlPullParser.END_DOCUMENT
                        || (mXpp.getEventType() == XmlPullParser.END_TAG &&
                     mXpp.getName().equalsIgnoreCase(TAG_PACKAGE))) {
                    loop = false;
                    break;
                }
                else if (mXpp.getEventType() == XmlPullParser.START_TAG &&
                        mXpp.getName().equalsIgnoreCase(TAG_NAME)) {
                    package_name();
                    checkRun ++;
                    //System.out.println("package_name");
                }
                else if (mXpp.getEventType() == XmlPullParser.START_TAG &&
                        mXpp.getName().equalsIgnoreCase(TAG_ARCH)) {
                    package_arch();
                    checkRun ++;
                    //continue;
                    //System.out.println("package_arch");
                }
                else if (mXpp.getEventType() == XmlPullParser.START_TAG &&
                        mXpp.getName().equalsIgnoreCase(TAG_VERSION)) {
                    package_version();
                    checkRun ++;
                    //System.out.println("package_version");
                    //continue;
                }
                else if (mXpp.getEventType() == XmlPullParser.START_TAG &&
                        mXpp.getName().equalsIgnoreCase(TAG_FORMAT)) {
                    package_format();
                    checkRun ++;
                    //continue;
                    //loop = false;

                    if (mDebug) System.out.println("package_format");
                }

                else {
                    mXpp.next();
                    //continue;
                }
                if (true ) mXpp.next();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        if ( (!package_info.packageVersion.contentEquals("") &&
                !package_info.packageName.contentEquals("") &&
                !package_info.packageSection.contentEquals("")) || true) {

            list.add(package_info);
        }
        //this.advanceToTag(TAG_PACKAGE);

        this.consumeEndTag(TAG_PACKAGE);
    }

    private void package_name() {
        this.consumeStartTag(TAG_NAME);
        String name = this.getText();
        //System.out.println(mCount + " " + name);
        package_info.packageName = name;
        this.consumeEndTag(TAG_NAME);
    }
    private void package_arch() {
        this.consumeStartTag(TAG_ARCH);
        latestArch = this.getText();
        //
        this.consumeEndTag(TAG_ARCH);

    }
    private void package_version() {
        String epoch = mXpp.getAttributeValue(null,ATTR_EPOCH);
        String release = mXpp.getAttributeValue(null, ATTR_REL);
        String version = mXpp.getAttributeValue(null, ATTR_VER);
        int ep = Integer.valueOf(epoch) ;
        this.advanceToTag(TAG_VERSION);
        this.consumeStartTag(TAG_VERSION);
        //String version = this.getText();
        package_info.packageVersion = version + "-" + release + "." + latestArch;
        if (mDebug) System.out.println(package_info.packageVersion +" " + mCount);
        this.consumeEndTag(TAG_VERSION);

    }
    private void package_format() {
        //this.advanceToTag(TAG_FORMAT);
        this.consumeStartTag(TAG_FORMAT);

        this.advanceToTag(TAG_RPMGROUP);
        format_group();
        if (mDebug) System.out.println("group "+ mCount);
        //this.advanceToTag(TAG_FORMAT);
        this.consumeEndTag(TAG_FORMAT);
    }
    private void format_group() {
        this.consumeStartTag(TAG_RPMGROUP);
        String section = this.getText();
        if (mDebug) System.out.println(section + " section");
        package_info.packageSection = section;
        if (package_info.packageSection.trim().contentEquals("")) {
            package_info.packageSection = "[default]";
            //System.exit(0);
        }
        this.consumeEndTag(TAG_RPMGROUP);
    }
}
