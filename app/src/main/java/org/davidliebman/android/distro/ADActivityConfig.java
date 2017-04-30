package org.davidliebman.android.distro;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class ADActivityConfig extends AppCompatActivity {

    View debian_layout;
    View ubuntu_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adconfig);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_distro);

        RadioButton radioButton = (RadioButton) findViewById(R.id.radio_deb);
        radioButton.setChecked(true);

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

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.spinner_arch_deb, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner2.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.spinner_release_deb, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner3.setAdapter(adapter3);

        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.spinner_mcnf_deb, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner4.setAdapter(adapter4);

        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(this,
                R.array.spinner_base_url_ubu, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner5.setAdapter(adapter5);

        ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(this,
                R.array.spinner_arch_ubu, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner6.setAdapter(adapter6);

        ArrayAdapter<CharSequence> adapter7 = ArrayAdapter.createFromResource(this,
                R.array.spinner_release_ubu, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner7.setAdapter(adapter7);

        ArrayAdapter<CharSequence> adapter8 = ArrayAdapter.createFromResource(this,
                R.array.spinner_mcnf_ubu, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner8.setAdapter(adapter8);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    public void onRadioButtonClick(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_deb:
                if (checked)
                    debian_layout.setVisibility(View.VISIBLE);
                    ubuntu_layout.setVisibility(View.GONE);

                    break;
            case R.id.radio_ubu:
                if (checked)
                    debian_layout.setVisibility(View.GONE);
                    ubuntu_layout.setVisibility(View.VISIBLE);

                break;
        }
    }
}
