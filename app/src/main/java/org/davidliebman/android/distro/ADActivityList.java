package org.davidliebman.android.distro;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.util.ArrayList;
        import java.util.List;

        import android.os.Bundle;
        import android.view.View;
        import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
        import android.widget.TextView;
        import android.app.ListActivity;

public class ADActivityList extends ListActivity {

    private TextView text;
    private List<String> listValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adlist);

        text = (TextView) findViewById(R.id.mainText);

        listValues = new ArrayList<String>();

        for(int i = 0; i < 1; i ++) {
            listValues.add("Android");
            listValues.add("iOS");
            listValues.add("Symbian");
            listValues.add("Blackberry");
            listValues.add("Windows Phone");
        }
        // initiate the listadapter
        ArrayAdapter<String> myAdapter = new ArrayAdapter <String>(this,
                R.layout.row_layout, R.id.listText, listValues);

        // assign the list adapter
        setListAdapter(myAdapter);

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
        //String selectedItem = (String) getListAdapter().getItem(position);

        text.setText("You clicked " + selectedItem + " at position " + position);
    }

    public void showDialog() {
        ADDialogQuestion dialog = new ADDialogQuestion();
        dialog.show(getFragmentManager(), "button_check");
    }

}

