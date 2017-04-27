package org.davidliebman.android.distro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by dave on 4/26/17.
 */

public class ADDialogQuestion extends DialogFragment {

    ADDialogQuestionInterface mListener;

    public interface ADDialogQuestionInterface {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
        public void onDialogNeutralClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ADDialogQuestionInterface) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.main_dialog_message)
                .setPositiveButton(R.string.main_dialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // check distro
                        mListener.onDialogPositiveClick(ADDialogQuestion.this);
                    }
                })
                .setNegativeButton(R.string.main_dialog_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // exit
                        mListener.onDialogNegativeClick(ADDialogQuestion.this);
                    }
                })
                .setNeutralButton(R.string.main_dialog_neutral, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // configure app
                        mListener.onDialogNeutralClick(ADDialogQuestion.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
