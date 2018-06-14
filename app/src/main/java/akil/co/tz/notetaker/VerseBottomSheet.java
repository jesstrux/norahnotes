package akil.co.tz.notetaker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import akil.co.tz.notetaker.R;

/**
 * Created by DevDept on 6/14/18.
 */

public class VerseBottomSheet extends BottomSheetDialogFragment {
    ListView chapterList, verseList, endVerseList;
    String[] verses = {"1" , "2", "3", "11" , "12", "13", "21" , "22", "23"};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chapter_bottom_sheet, container, false);
        chapterList = v.findViewById(R.id.chapterList);
        verseList = v.findViewById(R.id.verseList);
        endVerseList = v.findViewById(R.id.endVerseList);
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.number_view, R.id.num_text, verses);

        chapterList.setAdapter(arrayAdapter);
        verseList.setAdapter(arrayAdapter);
        endVerseList.setAdapter(arrayAdapter);

        return v;
    }
}
