package org.davidliebman.android.distro;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ADActivityList extends ListActivity
        implements ADDialogQuestion.ADDialogQuestionInterface {

    private long mDateOld = 0;
    private long mDateDownload = 0;
    private long mDateShowing = 0;
    private TextView text;
    private ArrayList<ADPackageInfo> listValues;
    //private ArrayAdapter<String> myAdapter;
    private ADListAdapter myAdapter;
    private Context mContext;
    private ADDownload download = null;
    private DownloadFilesTask down;

    private int mListType = ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB;

    public static final String PREFERENCES_FILE_KEY = "awesome_distro";
    public static final String PREFERENCES_DATE_OLD_KEY = "long_old_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adlist);
        mContext = this;

        text = (TextView) findViewById(R.id.mainText);

        listValues = new ArrayList<ADPackageInfo>();

        for(int i = 0; i < 1 ; i ++) {
            ADPackageInfo info = new ADPackageInfo();

            info.packageName = ("Android");
            //listValues.add("iOS");
            //listValues.add("Symbian");
            //listValues.add("Blackberry");
            //listValues.add("Windows Phone");

            listValues.add(info);
        }
        // initiate the listadapter

        //myAdapter = new ArrayAdapter <String>(this, R.layout.row_layout, R.id.listText, listValues);
        myAdapter = new ADListAdapter(this, listValues);

        // assign the list adapter
        setListAdapter(myAdapter);

        //myAdapter.setNotifyOnChange(true);

        readPreferences();

        Button checkButton = (Button) findViewById(R.id.main_button);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();

            }
        });

        Button browseButton = (Button) findViewById(R.id.browse_button);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListType == ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB && download == null) {
                    mListType = ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB;
                    down = new DownloadFilesTask();
                    down.execute(getDistroURL());
                }
                else if (mListType == ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB) {
                    mListType = ADDownload.ACTION_LIST_SHOW_SECTION_DEB;
                    down = new DownloadFilesTask();
                    down.execute(getDistroURL());
                }
                /*
                else if (mListType == ADDownload.ACTION_LIST_UPDATE_SECTION_DEB) {
                    mListType = ADDownload.ACTION_LIST_UPDATE_PACKAGE_DEB;
                    down = new DownloadFilesTask();
                    down.execute(getDistroURL());
                }
                */
                else if (mListType == ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB) {
                    mListType = ADDownload.ACTION_LIST_SHOW_SECTION_DEB;
                    down = new DownloadFilesTask();
                    down.execute(getDistroURL());
                }
                else {
                    mListType = ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB;
                    down = new DownloadFilesTask();
                    down.execute(getDistroURL());
                }
            }
        });

    }

    // when an item of the list is clicked
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);

        ADPackageInfo selectedItem = listValues.get(position);

        text.setText("You clicked " + selectedItem.packageName + " at position " + position);

        if ((mListType == ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB ||
                mListType == ADDownload.ACTION_LIST_SHOW_SECTION_DEB) &&
                down.getStatus() == AsyncTask.Status.FINISHED) {

            mListType = ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB;
            download.setSearchString(selectedItem.packageName);
            down = new DownloadFilesTask();
            down.execute(getDistroURL());

            //listValues = download.getList(mListType);
            //showList();
        }
        else if ((mListType == ADDownload.ACTION_GZIP_FILE_SHOW_PACKAGE_DEB ||
                mListType == ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB) &&
                down.getStatus() == AsyncTask.Status.FINISHED) {
            boolean test = selectedItem.packageIsNew;
            listValues.get(position).packageIsNew = ! test;

            //showList();
            View checkbox_green = (View) view.findViewById(R.id.list_image_check);
            View checkbox_red = (View) view.findViewById(R.id.list_image_uncheck);

            if(!test) {
                checkbox_green.setVisibility(View.VISIBLE);
                checkbox_red.setVisibility(View.GONE);

                mListType = ADDownload.ACTION_ADD_PACKAGE;

                down = new DownloadFilesTask();
                down.execute(getDistroURL(), Integer.toString(position));
            }
            else {
                checkbox_green.setVisibility(View.GONE);
                checkbox_red.setVisibility(View.VISIBLE);
                mListType = ADDownload.ACTION_REMOVE_PACKAGE;
                down = new DownloadFilesTask();
                down.execute(getDistroURL(), Integer.toString(position));
            }
        }


    }

    public void showDialog() {
        ADDialogQuestion dialog = new ADDialogQuestion();
        dialog.show(getFragmentManager(), "button_check");
    }



    public void showList() {
        if (mListType == ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB) {
            ArrayList<ADPackageInfo> chosen = download.getWatchedPackages(mContext);
            myAdapter = new ADListAdapter(this, listValues, chosen);

        }
        else {
            myAdapter = new ADListAdapter(this, listValues);

        }


        //myAdapter = new ADListAdapter(this, listValues);
        setListAdapter(myAdapter);
    }

    public String getDistroURL() {
        return "http://http.us.debian.org/debian/dists/testing/main/binary-amd64/" +"Packages.gz"; //+ "Release";
    }

    @Override
    protected void onDestroy() {
        download.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // check distro
        if (down == null || down.getStatus() == AsyncTask.Status.FINISHED) {
            mListType = ADDownload.ACTION_LIST_UPDATE_SECTION_DEB;
            down = new DownloadFilesTask();
            down.execute(getDistroURL());
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // exit
    }

    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
        // configure

        Intent config = new Intent(this, ADActivityConfig.class);
        startActivity(config);

        if (down == null || down.getStatus() == AsyncTask.Status.FINISHED) {
            mListType = ADDownload.ACTION_ACT_AS_UPDATE;
            down = new DownloadFilesTask();
            down.execute(getDistroURL());
        }
    }

    public void writePreferences() {
        //Context context = getActivity();
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(PREFERENCES_DATE_OLD_KEY, mDateDownload);
        editor.commit();

    }

    public void readPreferences() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        mDateOld = sharedPref.getLong(PREFERENCES_DATE_OLD_KEY, 0);
        System.out.println("date from preferences " + new Date(mDateOld));
    }

    ////////////////////////////////////////

    private class DownloadFilesTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onProgressUpdate(Void... progress) {
        }

        @Override
        protected Void doInBackground(String... params) {
            //System.out.println(params[0]);
            int num = 0;
            if (params[0].endsWith(".gz")) {
                //mListType = ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB;
            }
            else {
                mListType = ADDownload.ACTION_TEXT_FILE_SHOW_ALL;
            }
            //download = new ADDownload(params[0], new Date(), mListType);

            switch (mListType) {
                case ADDownload.ACTION_TEXT_FILE_SHOW_ALL:
                    download = new ADDownload(params[0], mDateOld, mListType);
                    listValues = download.getList(mListType);
                    mDateDownload = download.getDateDownload();
                    break;
                case ADDownload.ACTION_GZIP_FILE_SHOW_PACKAGE_DEB:
                    listValues = download.getList(mListType);
                    break;
                case ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB:
                    download = new ADDownload(params[0], mDateOld, mListType);
                    listValues = download.getList(mListType);
                    mDateDownload = download.getDateDownload();
                    break;
                case ADDownload.ACTION_GZIP_FILE_SHOW_ALL:
                    download = new ADDownload(params[0], mDateOld, mListType);
                    listValues = download.getList(mListType);
                    mDateDownload = download.getDateDownload();
                    break;
                case ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB:
                    listValues = download.getList(mListType);
                    break;
                case ADDownload.ACTION_LIST_SHOW_SECTION_DEB:
                    listValues = download.getList(mListType);
                    break;
                case ADDownload.ACTION_LIST_UPDATE_PACKAGE_DEB:
                    readPreferences();
                    if (download == null) download = new ADDownload(params[0], mDateOld, mListType);
                    download.setDateOld(mDateOld);
                    listValues = download.getDBList(mContext, mListType, mDateOld);
                    mDateDownload = download.getDateDownload();

                    break;
                case ADDownload.ACTION_LIST_UPDATE_SECTION_DEB:
                    readPreferences();
                    if (download == null) download = new ADDownload(params[0], mDateOld, mListType);
                    download.setDateOld(mDateOld);
                    listValues = download.getDBList(mContext, mListType, mDateOld);
                    mDateDownload = download.getDateDownload();

                    break;
                case ADDownload.ACTION_ACT_AS_UPDATE:
                    readPreferences();
                    if (download == null) download = new ADDownload(params[0], mDateOld, mListType);
                    download.setDateOld(mDateOld);
                    mDateDownload = download.getDateDownload();

                    download.setAsUpdate(mContext);
                    //listValues = download.getDBList(mContext, mListType, mDateOld);
                    listValues = new ArrayList<>();
                    writePreferences();
                    break;
                case ADDownload.ACTION_ADD_PACKAGE:
                    if (download == null) download = new ADDownload(params[0], mDateOld, mListType);
                    int position = Integer.parseInt(params[1]);
                    download.addPackage(mContext, listValues.get(position));
                    //mListType = ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB;
                    break;
                case ADDownload.ACTION_REMOVE_PACKAGE:
                    if (download == null) download = new ADDownload(params[0], mDateOld, mListType);
                    int remove = Integer.parseInt(params[1]);
                    download.removePackage(mContext, listValues.get(remove));
                    //mListType = ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB;
                    break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (mListType == ADDownload.ACTION_REMOVE_PACKAGE || mListType == ADDownload.ACTION_ADD_PACKAGE) {
                mListType = ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB;
            }
            else {
                showList();
            }
            if (download != null && !download.getToastMessage().isEmpty()) {
                Toast.makeText(mContext, download.getToastMessage(), Toast.LENGTH_LONG).show();
                download.setToastMessage("");
            }

        }
    }
}

