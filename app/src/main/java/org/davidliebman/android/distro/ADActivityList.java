package org.davidliebman.android.distro;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
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

public class ADActivityList extends ListActivity
        implements ADDialogQuestion.ADDialogQuestionInterface {

    private long mDateOld = 0;
    private long mDateDownload = 0;
    private long mDateShowing = 0;
    private TextView text;
    private List<String> listValues;
    private ArrayAdapter<String> myAdapter;
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

        listValues = new ArrayList<String>();

        for(int i = 0; i < 1 ; i ++) {
            listValues.add("Android");
            listValues.add("iOS");
            listValues.add("Symbian");
            listValues.add("Blackberry");
            listValues.add("Windows Phone");
        }
        // initiate the listadapter

        myAdapter = new ArrayAdapter <String>(this,
                R.layout.row_layout, R.id.listText, listValues);

        // assign the list adapter
        setListAdapter(myAdapter);

        myAdapter.setNotifyOnChange(true);

        readPreferences();

        Button checkButton = (Button) findViewById(R.id.main_button);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();

            }
        });

    }

    // when an item of the list is clicked
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);

        String selectedItem = (String) getListView().getItemAtPosition(position);

        text.setText("You clicked " + selectedItem + " at position " + position);

        if ((mListType == ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB ||
                mListType == ADDownload.ACTION_LIST_SHOW_SECTION_DEB) &&
                down.getStatus() == AsyncTask.Status.FINISHED) {

            mListType = ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB;
            download.setSearchString(selectedItem);
            down = new DownloadFilesTask();
            down.execute(getDistroURL());

            //listValues = download.getList(mListType);
            //showList();
        }
    }

    public void showDialog() {
        ADDialogQuestion dialog = new ADDialogQuestion();
        dialog.show(getFragmentManager(), "button_check");
    }

    public void showList() {
        myAdapter = new ArrayAdapter <String>(mContext,
                R.layout.row_layout, R.id.listText, listValues);
        myAdapter.notifyDataSetChanged();
        myAdapter.setNotifyOnChange(true);
        setListAdapter(myAdapter);
        mDateShowing = mDateDownload;
    }

    public String getDistroURL() {
        return "http://http.us.debian.org/debian/dists/testing/main/binary-amd64/" +"Packages.gz"; //+ "Release";
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // check distro
        //DownloadFilesTask
        if (mListType == ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB && download == null) {
            mListType = ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB;
            down = new DownloadFilesTask();
            down.execute(getDistroURL());
        }
        else if (true) {
            mListType = ADDownload.ACTION_LIST_SHOW_SECTION_DEB;
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
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            writePreferences();
            showList();

        }
    }
}

