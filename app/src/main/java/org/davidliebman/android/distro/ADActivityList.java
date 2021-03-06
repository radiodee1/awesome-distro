package org.davidliebman.android.distro;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuInflater;

public class ADActivityList extends ListActivity
        implements ADDialogQuestion.ADDialogQuestionInterface, SearchView.OnQueryTextListener {

    private boolean mDebug = true;
    private long mDateOld = 0;
    private long mDateDownload = 0;
    private long mDateShowing = 0;
    private TextView text;
    private TextView date;
    private ArrayList<ADPackageInfo> listValues;
    //private ArrayAdapter<String> myAdapter;
    private ADListAdapter myAdapter;
    private Context mContext;
    private ADDownload download = null;
    private DownloadFilesTask down;
    private boolean bool_del_db;
    private String string_url = "";
    private String mSearchStringFed = "";
    private SearchView searchView ;
    private MenuItem searchMenuItem;
    private boolean mIsSearching = false;

    private int mListType = ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB;

    public static final String PREFERENCES_FILE_KEY = "awesome_distro";
    public static final String PREFERENCES_DATE_OLD_KEY = "long_old_date";
    public static final String PREFERENCES_URL_KEY = "string_url";

    public static final int INTENT_CONFIGURE = 9001;

    public static final int WRITE_PREFERENCES_ALL = 1;
    public static final int WRITE_PREFERENCES_URL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adlist);
        mContext = this;

        text = (TextView) findViewById(R.id.mainText);
        date = (TextView) findViewById(R.id.textview_date);

        listValues = new ArrayList<ADPackageInfo>();

        for(int i = 0; i < 1 ; i ++) {
            ADPackageInfo info = new ADPackageInfo();

            info.packageName = ("Awesome Distro Watcher");

            listValues.add(info);
        }

        myAdapter = new ADListAdapter(this, listValues);

        setListAdapter(myAdapter);

        readPreferences();
        text.setText(getDistroURL());
        date.setText(new Date(mDateDownload).toString());

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
                if (mIsSearching) {
                    myAdapter.resetCompleteData();
                    mIsSearching = false;
                    //return;
                }
                if (mListType == ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB && download == null) {
                    mListType = ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB;
                    checkUrlForFed();
                    down = new DownloadFilesTask();
                    down.execute(getDistroURL());
                }
                else if (mListType == ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB) {
                    mListType = ADDownload.ACTION_LIST_SHOW_SECTION_DEB;
                    checkUrlForFed();
                    down = new DownloadFilesTask();
                    down.execute(getDistroURL());
                }

                else if (mListType == ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB) {
                    mListType = ADDownload.ACTION_LIST_SHOW_SECTION_DEB;
                    checkUrlForFed();
                    down = new DownloadFilesTask();
                    down.execute(getDistroURL());
                }
                else if (mListType == ADDownload.ACTION_LIST_UPDATE_SECTION_FED ||
                        mListType == ADDownload.ACTION_LIST_UPDATE_PACKAGE_FED) {
                    mListType = ADDownload.ACTION_LIST_SHOW_SECTION_FED;
                    //mListType = ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_FED;
                    checkUrlForFed();
                    down = new DownloadFilesTask();
                    down.execute(getDistroURL());
                }

                else {
                    mListType = ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB;
                    checkUrlForFed();
                    down = new DownloadFilesTask();
                    down.execute(getDistroURL());
                }
            }
        });

        Button urlButton = (Button) findViewById(R.id.check_button);
        urlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (down == null || down.getStatus() == AsyncTask.Status.FINISHED) {
                    mListType = ADDownload.ACTION_LIST_UPDATE_SECTION_DEB;
                    checkUrlForFed();
                    down = new DownloadFilesTask();
                    down.execute(getDistroURL());
                }

            }
        });

    }

    public void checkUrlForFed() {
        String BASEURL = "baseurl=";
        String METALINK = "metalink=";
        if (string_url.startsWith(BASEURL) || string_url.startsWith(METALINK)) {
            if (mListType == ADDownload.ACTION_LIST_SHOW_SECTION_DEB) mListType = ADDownload.ACTION_LIST_SHOW_SECTION_FED;
            if (mListType == ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB) mListType = ADDownload.ACTION_LIST_SHOW_PACKAGE_FED;
            if (mListType == ADDownload.ACTION_LIST_UPDATE_PACKAGE_DEB) mListType = ADDownload.ACTION_LIST_UPDATE_PACKAGE_FED;
            if (mListType == ADDownload.ACTION_LIST_UPDATE_SECTION_DEB) mListType = ADDownload.ACTION_LIST_UPDATE_SECTION_FED;
            if (mListType == ADDownload.ACTION_GZIP_FILE_SHOW_PACKAGE_DEB) mListType = ADDownload.ACTION_GZIP_FILE_SHOW_PACKAGE_FED;
            if (mListType == ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB) mListType = ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_FED;

            //mListType = ADDownload.ACTION_GZIP_FILE_GET_URL_FED;
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        readPreferences();
        text.setText(getDistroURL());
    }

    // when an item of the list is clicked
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        ADPackageInfo selectedItem = null;

        if (mIsSearching == false) {
            selectedItem = listValues.get(position);
        }
        else {
            selectedItem = myAdapter.getList().get(position);
        }

        //text.setText("You clicked " + selectedItem.packageName + " at position " + position);
        if ((mListType == ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_FED ||
                mListType == ADDownload.ACTION_LIST_SHOW_SECTION_FED) &&
                down != null &&
                down.getStatus() == AsyncTask.Status.FINISHED) {

            mListType = ADDownload.ACTION_GZIP_FILE_SHOW_PACKAGE_FED;
            //mListType = ADDownload.ACTION_LIST_SHOW_PACKAGE_FED;
            checkUrlForFed();
            download.setSearchString(selectedItem.packageName);
            mSearchStringFed = selectedItem.packageName;
            down = new DownloadFilesTask();
            down.execute(getDistroURL());
        }
        else if ((mListType == ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB ||
                mListType == ADDownload.ACTION_LIST_SHOW_SECTION_DEB) &&
                down != null &&
                down.getStatus() == AsyncTask.Status.FINISHED) {

            mListType = ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB;
            checkUrlForFed();
            download.setSearchString(selectedItem.packageName);
            down = new DownloadFilesTask();
            down.execute(getDistroURL());

            //listValues = download.getList(mListType);
            //showList();
        }
        else if ((mListType == ADDownload.ACTION_GZIP_FILE_SHOW_PACKAGE_DEB ||
                mListType == ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB ||
                mListType == ADDownload.ACTION_GZIP_FILE_SHOW_PACKAGE_FED ||
                mListType == ADDownload.ACTION_LIST_SHOW_PACKAGE_FED) &&
                down != null &&
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
        else if(selectedItem == null || true) {
            //do nothing... simple list
        }


    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if ((keycode == KeyEvent.KEYCODE_BACK) && listValues.size() > 0) {

            //System.out.println("keycode " + mListType);

            if (mListType == ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB) {
                mListType = ADDownload.ACTION_LIST_SHOW_SECTION_DEB;
                down = new DownloadFilesTask();
                down.execute(getDistroURL());
            }
            else if (mListType == ADDownload.ACTION_GZIP_FILE_SHOW_PACKAGE_DEB) {
                mListType = ADDownload.ACTION_LIST_SHOW_SECTION_DEB;
                down = new DownloadFilesTask();
                down.execute(getDistroURL());
            }
            else if (mListType == ADDownload.ACTION_LIST_SHOW_PACKAGE_FED) {
                mListType = ADDownload.ACTION_LIST_SHOW_SECTION_FED;
                down = new DownloadFilesTask();
                down.execute(getDistroURL());
            }
            else if (mListType == ADDownload.ACTION_GZIP_FILE_SHOW_PACKAGE_FED) {
                mListType = ADDownload.ACTION_LIST_SHOW_SECTION_FED;
                down = new DownloadFilesTask();
                down.execute(getDistroURL());
            }
            else if (true && mListType == ADDownload.ACTION_LIST_UPDATE_SECTION_FED) {
                mListType = ADDownload.ACTION_LIST_SHOW_SECTION_FED;
                down = new DownloadFilesTask();
                down.execute(getDistroURL());
            }
            else {
                //return true; // super.onKeyDown(keycode,event);
            }
            System.out.println("back key " + mListType);
        }
        else return true;//super.onKeyDown(keycode, event);

        return true; //super.onKeyDown(keycode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    public void showDialog() {
        ADDialogQuestion dialog = new ADDialogQuestion();
        dialog.show(getFragmentManager(), "button_check");
    }



    public void showList() {
        if ((mListType == ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB ||
                mListType == ADDownload.ACTION_GZIP_FILE_SHOW_PACKAGE_FED ||
                mListType == ADDownload.ACTION_LIST_SHOW_PACKAGE_FED) &&
                download != null) {

            //if (download == null) return;
            ArrayList<ADPackageInfo> chosen = download.getWatchedPackages(mContext);
            myAdapter = new ADListAdapter(this, listValues, chosen);

        }
        else {
            myAdapter = new ADListAdapter(this, listValues);

        }
        setListAdapter(myAdapter);

        text.setText(getDistroURL());
        date.setText(new Date(mDateDownload).toString());

        System.out.println("show list "+ mListType);
    }

    public String getDistroURL() {
        if (string_url == null || string_url.isEmpty()) {
            return "http://http.us.debian.org/debian/dists/testing/main/binary-amd64/" +"Packages.gz"; //+ "Release";

        }
        else return string_url;

    }

    @Override
    protected void onDestroy() {
        if (download != null) download.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //finish();
        down = null;
        //listValues = new ArrayList<>();
        //mListType = ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB;
        //checkUrlForFed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        String old_url = string_url;

        boolean bool_do_as_upgrade = false;
        if (requestCode == INTENT_CONFIGURE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                string_url = data.getStringExtra(ADActivityConfig.RETURN_URL);
                bool_del_db = data.getBooleanExtra(ADActivityConfig.RETURN_CLEAR_DB, false);
                bool_do_as_upgrade = data.getBooleanExtra(ADActivityConfig.RETURN_AS_UPGRADE,false);
                text.setText(getDistroURL());

                listValues = new ArrayList<>();
                mListType = ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB;
                checkUrlForFed();

                if ( !string_url.contentEquals(old_url)){
                    down = null;
                }

                if(string_url != null && ! string_url.isEmpty()) {
                    writePreferences(WRITE_PREFERENCES_URL);

                    mListType = ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB;
                    checkUrlForFed();
                    download = null;
                    listValues = new ArrayList<>();
                    showList();
                }

                if ((down == null || down.getStatus() == AsyncTask.Status.FINISHED) && bool_do_as_upgrade) {
                    mListType = ADDownload.ACTION_ACT_AS_UPDATE;
                    down = new DownloadFilesTask();
                    down.execute(getDistroURL());
                }
                if ((down == null || down.getStatus() == AsyncTask.Status.FINISHED) && bool_del_db) {
                    mListType = ADDownload.ACTION_CLEAR_DB;
                    down = new DownloadFilesTask();
                    down.execute(getDistroURL());
                }
                showList();
            }
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // check distro
        if (down == null || down.getStatus() == AsyncTask.Status.FINISHED) {
            mListType = ADDownload.ACTION_LIST_UPDATE_SECTION_DEB;
            checkUrlForFed();
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
        //startActivity(config);
        startActivityForResult(config, INTENT_CONFIGURE);

    }

    public void writePreferences(int write) {
        //Context context = getActivity();
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (write == WRITE_PREFERENCES_ALL && mDateDownload != 0) {
            editor.putLong(PREFERENCES_DATE_OLD_KEY, mDateDownload);
        }
        editor.putString(PREFERENCES_URL_KEY, string_url);
        editor.commit();

    }

    public void readPreferences() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        mDateOld = sharedPref.getLong(PREFERENCES_DATE_OLD_KEY, 0);
        string_url = sharedPref.getString(PREFERENCES_URL_KEY, "");
        //System.out.println("date from preferences " + new Date(mDateOld));
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        myAdapter.getFilter().filter(s);
        mIsSearching = true;
        return true;
    }

    ////////////////////////////////////////

    private class DownloadFilesTask extends AsyncTask<String, Void, Void> {

        ProgressDialog progressBar = new ProgressDialog(ADActivityList.this);

        public DownloadFilesTask() {
            if (mListType == ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_DEB ||
                    mListType == ADDownload.ACTION_LIST_UPDATE_PACKAGE_DEB ||
                    mListType == ADDownload.ACTION_LIST_UPDATE_SECTION_DEB ||
                    mListType == ADDownload.ACTION_GZIP_FILE_GET_URL_FED ||
                    mListType == ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_FED ||
                    mListType == ADDownload.ACTION_GZIP_FILE_SHOW_PACKAGE_FED ||
                    mListType == ADDownload.ACTION_LIST_UPDATE_PACKAGE_FED ||
                    mListType == ADDownload.ACTION_LIST_UPDATE_SECTION_FED) {
                progressBar.setMessage("Please wait");
                progressBar.show();
                progressBar.setIndeterminate(true);
            }
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
        }

        @Override
        protected Void doInBackground(String... params) {
            //System.out.println(params[0]);
            int num = 0;
            if (download == null) {
                download = new ADDownload(params[0], mDateOld, mListType);
            }
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
                case ADDownload.ACTION_LIST_SHOW_PACKAGE_FED:
                    download.setListType(mListType);
                    listValues = download.getListFed();
                    break;
                case ADDownload.ACTION_LIST_SHOW_SECTION_FED:
                    download.setListType(mListType);
                    listValues = download.getListFed();
                    break;
                case ADDownload.ACTION_LIST_UPDATE_PACKAGE_DEB:
                case ADDownload.ACTION_LIST_UPDATE_PACKAGE_FED:
                    readPreferences();
                    if (download == null) download = new ADDownload(params[0], mDateOld, mListType);
                    download.setDateOld(mDateOld);
                    listValues = download.getDBList(mContext, mListType, mDateOld);
                    mDateDownload = download.getDateDownload();

                    break;
                case ADDownload.ACTION_LIST_UPDATE_SECTION_DEB:
                case ADDownload.ACTION_LIST_UPDATE_SECTION_FED:
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
                    writePreferences(WRITE_PREFERENCES_ALL);
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
                case ADDownload.ACTION_CLEAR_DB:
                    if (download == null) download = new ADDownload(params[0], mDateOld, mListType);
                    download.deleteDB(ADActivityList.this);
                    break;
                case ADDownload.ACTION_GZIP_FILE_GET_URL_FED:
                    //System.out.println("url fed");
                    if (download == null) download = new ADDownload(params[0], mDateOld, mListType);
                    listValues = download.getListFed();
                    mDateDownload = download.getDateDownload();
                    break;
                case ADDownload.ACTION_GZIP_FILE_SHOW_PACKAGE_FED:
                    //System.out.println("package fed");
                    if (download == null ) download = new ADDownload(params[0], mDateOld, mListType);
                    download.setListType(mListType);
                    download.setSearchString(mSearchStringFed);
                    download.setDateOld(mDateOld);
                    listValues = download.getListFed();
                    mDateDownload = download.getDateDownload();
                    break;
                case ADDownload.ACTION_GZIP_FILE_SHOW_SECTION_FED:
                    //System.out.println("section fed");
                    if (download == null) download = new ADDownload(params[0], mDateOld, mListType);
                    listValues = download.getListFed();
                    mDateDownload = download.getDateDownload();
                    break;
            }
            if (listValues.size() == 0
                    //&&
                    //mListType != ADDownload.ACTION_LIST_UPDATE_SECTION_FED &&
                    //mListType != ADDownload.ACTION_LIST_UPDATE_PACKAGE_FED
                    ) {
                download = null;
                // does this work??
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.hide();
            if (mListType == ADDownload.ACTION_REMOVE_PACKAGE ||
                    mListType == ADDownload.ACTION_ADD_PACKAGE) {
                mListType = ADDownload.ACTION_LIST_SHOW_PACKAGE_DEB;
                checkUrlForFed();
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

