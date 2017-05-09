package org.davidliebman.android.distro;

import android.app.Activity;
import android.content.Intent;
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

import java.net.URL;

public class ADActivityConfig extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener{

    View debian_layout;
    View ubuntu_layout;

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
    String string_full_url;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adconfig);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_distro);

        RadioButton radioButton = (RadioButton) findViewById(R.id.radio_deb);
        radioButton.setChecked(true);
        string_distro = "debian";

        debian_layout = findViewById(R.id.layout_debian);
        ubuntu_layout = findViewById(R.id.layout_ubuntu);

        ubuntu_layout.setVisibility(View.GONE);

        Spinner spinner1 = (Spinner) findViewById(R.id.spinner_base_url_deb);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner_arch_deb);
        Spinner spinner3 = (Spinner) findViewById(R.id.spinner_release_deb);
        Spinner spinner4 = (Spinner) findViewById(R.id.spinner_mcnf_deb);

        Spinner spinner5 = (Spinner) findViewById(R.id.spinner_base_url_ubu);
        Spinner spinner6 = (Spinner) findViewById(R.id.spinner_arch_ubu);
        Spinner spinner7 = (Spinner) findViewById(R.id.spinner_release_ubu);
        Spinner spinner8 = (Spinner) findViewById(R.id.spinner_mcnf_ubu);

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
                    string_distro = "debian";

                }
                break;
            case R.id.radio_ubu:
                if (checked) {
                    debian_layout.setVisibility(View.GONE);
                    ubuntu_layout.setVisibility(View.VISIBLE);
                    string_distro = "ubuntu";

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
        text_view_url.setText(string_full_url);


    }
}
