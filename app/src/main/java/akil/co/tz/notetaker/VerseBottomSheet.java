package akil.co.tz.notetaker;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import akil.co.tz.notetaker.R;
import akil.co.tz.notetaker.models.Book;

/**
 * Created by DevDept on 6/14/18.
 */

public class VerseBottomSheet extends BottomSheetDialogFragment {
    ListView chapterList, verseList, endVerseList;
    ArrayList<String> verses = new ArrayList<>();

    private static final String DESCRIBABLE_KEY = "describable_key";
    private Book mBook ;

    public static VerseBottomSheet newInstance(Book book) {
        VerseBottomSheet bottomSheetFragment = new VerseBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DESCRIBABLE_KEY, book);
        bottomSheetFragment.setArguments(bundle);
        bottomSheetFragment.setCancelable(false);

        return bottomSheetFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chapter_bottom_sheet, container, false);
        mBook = (Book) getArguments().getSerializable(DESCRIBABLE_KEY);
        ImageButton closerBtn = v.findViewById(R.id.sheet_closer);
        final VerseBottomSheet sheet = this;
        closerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheet.dismiss();
            }
        });

        chapterList = v.findViewById(R.id.chapterList);
        verseList = v.findViewById(R.id.verseList);
        endVerseList = v.findViewById(R.id.endVerseList);

        ArrayAdapter chaptersAdapter = new ArrayAdapter<String>(getContext(), R.layout.number_view, R.id.num_text, mBook.getChapters());
        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.number_view, R.id.num_text, verses);

        chapterList.setAdapter(chaptersAdapter);
        verseList.setAdapter(arrayAdapter);
        endVerseList.setAdapter(arrayAdapter);

        chapterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                verses.clear();
                verses.addAll(Arrays.asList(mBook.getChapterVerses(position)));
                arrayAdapter.notifyDataSetChanged();

                Log.d("WOURA", "Chapters: " + mBook.getChapterVerses(position).length);
            }
        });

        return v;
    }
}
