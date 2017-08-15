package com.egkhan.instagramclonewithfirebase.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.egkhan.instagramclonewithfirebase.R;

/**
 * Created by EgK on 8/15/2017.
 */

public class ConfirmPasswordDialog extends DialogFragment {
    private static final String TAG = "ConfirmPasswordDialog";

    public interface OnConfirmPasswordListener {
        void onConfirmPassword(String password);
    }

    OnConfirmPasswordListener mOnConfirmPasswordListener;
    TextView mPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_password, container, false);
        Log.d(TAG, "onCreateView: started");
        mPassword = (TextView) view.findViewById(R.id.confirm_password);

        TextView cancelDialog = (TextView) view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the dialog");
                getDialog().dismiss();
            }
        });
        TextView confirmDialog = (TextView) view.findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: captured password and confirming");

                String password = mPassword.getText().toString();
                if (!password.equals("")) {
                    mOnConfirmPasswordListener.onConfirmPassword(password);
                    getDialog().dismiss();
                } else
                    Toast.makeText(getActivity(), "You must enter a passwrod", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnConfirmPasswordListener = (OnConfirmPasswordListener) getTargetFragment();
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: ClassCastException" + e.getMessage());
        }
    }
}
