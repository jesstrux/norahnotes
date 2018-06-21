package akil.co.tz.mzikii.ui.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import akil.co.tz.mzikii.R;

/**
 * Created by DevDept on 6/6/18.
 */

public class PostTitleDialog extends DialogFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.post_title_dialog, container, false);

        // Do all the stuff to initialize your custom view

        return v;
    }
}
