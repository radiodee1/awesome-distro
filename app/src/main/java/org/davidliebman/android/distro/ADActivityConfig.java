package org.davidliebman.android.distro;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ADActivityConfig extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener{

    View debian_layout;
    View ubuntu_layout;
    View fedora_layout;

    String string_arch_deb;
    String string_base_url_deb;
    String string_release_deb;
    String string_mcnf_deb;
    String string_distro_deb;


    String string_arch_ubu;
    String string_base_url_ubu;
    String string_release_ubu;
    String string_mcnf_ubu;
    String string_distro_ubu;
    String string_proposed_ubu;
    String string_full_url;

    String string_arch_fed = "";
    String string_release_fed = "";
    String string_base_url_fed = "";

    int int_release_fed = 0;
    int int_arch_fed = 0;
    int int_base_url_fed = 0;

    String string_distro = "debian";

    boolean clear_db_on_exit = false;
    boolean confirm_url_change = false;
    boolean confirm_as_upgrade = false;

    public static final  String URLSLASH = "/";
    public static final  String RETURN_URL = "url";
    public static final  String RETURN_CLEAR_DB = "db";
    public static final  String RETURN_AS_UPGRADE = "upgrade";

    EditText text_custom_release = null;
    TextView text_view_url = null;
    Button button_readme = null;

    ////// specific to fedora /////
    ArrayList<String> listName = new ArrayList<>();
    ArrayList<String> listUrl = new ArrayList<>();
    ///////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adconfig);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_distro);
        makeListsFed();

        RadioButton radioButton = (RadioButton) findViewById(R.id.radio_deb);
        radioButton.setChecked(true);
        string_distro = "debian";
        string_proposed_ubu = "none";

        debian_layout = findViewById(R.id.layout_debian);
        ubuntu_layout = findViewById(R.id.layout_ubuntu);
        fedora_layout = findViewById(R.id.layout_fedora);

        ubuntu_layout.setVisibility(View.GONE);
        fedora_layout.setVisibility(View.GONE);

        Spinner spinner1 = (Spinner) findViewById(R.id.spinner_base_url_deb);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner_arch_deb);
        Spinner spinner3 = (Spinner) findViewById(R.id.spinner_release_deb);
        Spinner spinner4 = (Spinner) findViewById(R.id.spinner_mcnf_deb);

        Spinner spinner5 = (Spinner) findViewById(R.id.spinner_base_url_ubu);
        Spinner spinner6 = (Spinner) findViewById(R.id.spinner_arch_ubu);
        Spinner spinner7 = (Spinner) findViewById(R.id.spinner_release_ubu);
        Spinner spinner8 = (Spinner) findViewById(R.id.spinner_mcnf_ubu);
        Spinner spinner9 = (Spinner) findViewById(R.id.spinner_proposed_ubu);

        Spinner spinner10 = (Spinner) findViewById(R.id.spinner_arch_fed);
        Spinner spinner11 = (Spinner) findViewById(R.id.spinner_release_fed);
        Spinner spinner12 = (Spinner) findViewById(R.id.spinner_base_url_fed);


        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.spinner_base_url_deb, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener( this );

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.spinner_arch_deb, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener( this );

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.spinner_release_deb, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner3.setAdapter(adapter3);
        spinner3.setOnItemSelectedListener( this );

        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.spinner_mcnf_deb, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner4.setAdapter(adapter4);
        spinner4.setOnItemSelectedListener( this );

        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(this,
                R.array.spinner_base_url_ubu, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner5.setAdapter(adapter5);
        spinner5.setOnItemSelectedListener( this );

        ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(this,
                R.array.spinner_arch_ubu, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner6.setAdapter(adapter6);
        spinner6.setOnItemSelectedListener( this );

        ArrayAdapter<CharSequence> adapter7 = ArrayAdapter.createFromResource(this,
                R.array.spinner_release_ubu, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner7.setAdapter(adapter7);
        spinner7.setOnItemSelectedListener( this );

        ArrayAdapter<CharSequence> adapter8 = ArrayAdapter.createFromResource(this,
                R.array.spinner_mcnf_ubu, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner8.setAdapter(adapter8);
        spinner8.setOnItemSelectedListener( this );

        ArrayAdapter<CharSequence> adapter9 = ArrayAdapter.createFromResource(this,
                R.array.spinner_proposed_ubu, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner9.setAdapter(adapter9);
        spinner9.setOnItemSelectedListener( this );

        ArrayAdapter<CharSequence> adapter10 = ArrayAdapter.createFromResource(this,
                R.array.spinner_arch_fed, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter10.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner10.setAdapter(adapter10);
        spinner10.setOnItemSelectedListener( this );

        ArrayAdapter<CharSequence> adapter11 = ArrayAdapter.createFromResource(this,
                R.array.spinner_release_fed, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner11.setAdapter(adapter11);
        spinner11.setOnItemSelectedListener( this );

        ArrayAdapter<String> adapter12 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listName);
        // Specify the layout to use when the list of choices appears
        adapter12.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner12.setAdapter(adapter12);
        spinner12.setOnItemSelectedListener( this );


        text_custom_release = (EditText) findViewById(R.id.text_custom_release);
        text_custom_release.setVisibility(View.INVISIBLE);

        text_custom_release.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                buildUrl();
            }
        });

        text_view_url = (TextView) findViewById(R.id.text_show_url);

        button_readme = (Button) findViewById(R.id.button_readme);
        button_readme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ADActivityConfig.this,ADActivityReadme.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!confirm_url_change) string_full_url = "";
                Intent resultIntent = new Intent();
                resultIntent.putExtra(RETURN_URL, string_full_url);
                resultIntent.putExtra(RETURN_CLEAR_DB, clear_db_on_exit);
                resultIntent.putExtra(RETURN_AS_UPGRADE, confirm_as_upgrade);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();

            }
        });
    }
    public void onRadioButtonClick(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_deb:
                if (checked) {
                    debian_layout.setVisibility(View.VISIBLE);
                    ubuntu_layout.setVisibility(View.GONE);
                    fedora_layout.setVisibility(View.GONE);
                    string_distro = "debian";

                }
                break;
            case R.id.radio_ubu:
                if (checked) {
                    debian_layout.setVisibility(View.GONE);
                    ubuntu_layout.setVisibility(View.VISIBLE);
                    fedora_layout.setVisibility(View.GONE);
                    string_distro = "ubuntu";

                }
                break;
            case R.id.radio_fed:
                if (checked) {
                    debian_layout.setVisibility(View.GONE);
                    ubuntu_layout.setVisibility(View.GONE);
                    fedora_layout.setVisibility(View.VISIBLE);
                    string_distro = "fedora";

                }
                break;
        }
        buildUrl();
    }



    public void onCheckboxClick(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_custom_release:
                if (checked) {
                    text_custom_release.setVisibility(View.VISIBLE);
                    buildUrl();
                }
                else {
                    text_custom_release.setVisibility(View.INVISIBLE);
                    buildUrl();
                }
                break;
            case R.id.checkbox_clear_db:
                if (checked) {
                    clear_db_on_exit = true;
                }
                else {
                    clear_db_on_exit = false;
                }
                break;
            case R.id.checkbox_confirm:
                if (checked) {
                    confirm_url_change = true;
                }
                else {
                    confirm_url_change = false;
                }
                break;
            case R.id.checkbox_as_upgrade:
                if (checked) {
                    confirm_as_upgrade = true;
                }
                else {
                    confirm_as_upgrade = false;
                }
                break;

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_arch_deb:
                string_arch_deb = (String) parent.getItemAtPosition(position);
                break;
            case R.id.spinner_arch_ubu:
                string_arch_ubu = (String) parent.getItemAtPosition(position);
                break;
            case R.id.spinner_base_url_deb:
                string_base_url_deb = (String) parent.getItemAtPosition(position);
                break;
            case R.id.spinner_base_url_ubu:
                string_base_url_ubu = (String) parent.getItemAtPosition(position);
                break;
            case R.id.spinner_release_deb:
                string_release_deb = (String) parent.getItemAtPosition(position);
                break;
            case R.id.spinner_release_ubu:
                string_release_ubu = (String) parent.getItemAtPosition(position);
                break;
            case R.id.spinner_mcnf_deb:
                string_mcnf_deb = (String) parent.getItemAtPosition(position);
                break;
            case R.id.spinner_mcnf_ubu:
                string_mcnf_ubu = (String) parent.getItemAtPosition(position);
                break;
            case R.id.spinner_proposed_ubu:
                string_proposed_ubu = (String) parent.getItemAtPosition(position);
                break;
            case R.id.spinner_arch_fed:
                string_arch_fed = (String) parent.getItemAtPosition(position);
                int_arch_fed = position;
                break;
            case R.id.spinner_release_fed:
                string_release_fed = (String) parent.getItemAtPosition(position);
                int_release_fed = position;
                break;
            case R.id.spinner_base_url_fed:
                string_base_url_fed = (String) parent.getItemAtPosition(position);
                int_base_url_fed = position;
                break;
        }
        buildUrl();
    }

    public void stringsFromSpinnerId(int in) {


    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void buildUrl() {
        String release_ubu = "";
        String release_deb = "";

        if (text_custom_release.getVisibility() == View.VISIBLE &&
                !text_custom_release.getText().toString().isEmpty()) {
            release_ubu = text_custom_release.getText().toString().trim();
            release_deb = text_custom_release.getText().toString().trim();

        }
        else {
            release_deb = string_release_deb;
            release_ubu = string_release_ubu;
        }

        if ( string_proposed_ubu.equalsIgnoreCase("proposed")) {
            release_ubu = release_ubu + "-proposed";
        }

        if (string_distro.contentEquals("debian")) {
            string_full_url = string_base_url_deb + URLSLASH +
                    //string_distro + URLSLASH +
                    "dists" + URLSLASH +
                    release_deb + URLSLASH +
                    string_mcnf_deb + URLSLASH +
                    string_arch_deb + URLSLASH +
                    "Packages.gz";
        }
        else if (string_distro.contentEquals("ubuntu")) {
            string_full_url = string_base_url_ubu + URLSLASH +
                    string_distro + URLSLASH +
                    "dists" + URLSLASH +
                    release_ubu + URLSLASH +
                    string_mcnf_ubu + URLSLASH +
                    string_arch_ubu + URLSLASH +
                    "Packages.gz";
        }
        else if (string_distro.contentEquals("fedora")) {
            string_full_url = makeUrlFed(int_base_url_fed);
        }
        text_view_url.setText(string_full_url);


    }

    //////// fedora specific methods ///////////
    public void makeListsFed() {
        ArrayList<Integer> docs = new ArrayList<>();

        docs.add(R.raw.fedora_repo);
        docs.add(R.raw.fedora_updates_repo);
        docs.add(R.raw.fedora_updates_testing_repo);

        listName.clear();
        listUrl.clear();
        //listMetalink.clear();

        for (int i = 0; i < docs.size(); i ++) {
            String result;
            try {
                Resources res = getResources();
                InputStream is = res.openRawResource(docs.get(i));

                byte[] b = new byte[is.available()];
                is.read(b);
                result = new String(b);
            } catch (Exception e) {
                // e.printStackTrace();
                result = "Error: can't show file.";
            }

            //String base = new String();
            String url = new String();
            String name = new String();

            String line[] = result.split("\\r?\\n");
            for (int j = 0; j < line.length; j ++) {
                String fed = line[j];

                if(fed.startsWith("baseurl=")) {

                    url = fed;
                }
                else if (fed.startsWith("metalink=")) {

                    url = fed;
                }
                else if (fed.startsWith("name=")) {
                    name = fed.substring(("name=").length());
                    String list[] = name.split("[ ]+");
                    name = "";
                    for(int k = 0; k < list.length; k ++) {
                        if(!list[k].startsWith("$") && !list[k].contentEquals("-")) {
                            name = name + list[k] + " ";
                        }
                    }
                }
                if ((j == line.length - 1 || fed.trim().contentEquals("") || fed.startsWith("[")) && !name.isEmpty()) {
                    listName.add(name);
                    listUrl.add(url);
                    name = "";
                    url = "";
                }
            }
        }
    }

    public String makeUrlFed(int position) {
        String out = listUrl.get(position);
        String METALINK = "metalink=";
        String BASEURL = "baseurl=";
        String RELEASEVER = "$releasever";
        String BASEARCH = "$basearch";

        out = out.replace(RELEASEVER, string_release_fed);
        out = out.replace(BASEARCH, string_arch_fed);

        System.out.println(out + " here");
        return out;
    }
}
