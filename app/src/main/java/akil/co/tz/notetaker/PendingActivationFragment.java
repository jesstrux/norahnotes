package akil.co.tz.notetaker;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import akil.co.tz.notetaker.models.Memo;


public class PendingActivationFragment extends Fragment {

    public PendingActivationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String name = getArguments().getString("name");

        View rootView = inflater.inflate(R.layout.fragment_pending_activation, container, false);

        ((TextView) rootView.findViewById(R.id.hello_text)).setText("Hello " + name);
        return rootView;
    }
}
