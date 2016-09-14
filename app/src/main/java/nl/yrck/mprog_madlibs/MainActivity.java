package nl.yrck.mprog_madlibs;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public final static String STORY = "nl.yrck.mprog_madlibs.STORY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final LinearLayoutCompat lm = (LinearLayoutCompat) findViewById(R.id.linearMain);

        final Story story = new Story(getListOfRawTexts());

        int i = 0;
        for (String placeholder : story.getPlaceHolders()) {

            // Create TextView
            TextView wordTitle = new TextView(this);
            wordTitle.setText(placeholder);
            wordTitle.setAllCaps(true);
            lm.addView(wordTitle);

            // Create EditText
            EditText word = new EditText(this);
            word.setHint(placeholder);
            word.setTag(i);
            lm.addView(word);

            i++;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener((View v) -> {
            ArrayList<EditText> editTexts = new ArrayList<>();
            for (int j = 0; j < lm.getChildCount(); j++) {
                if (lm.getChildAt(j) instanceof EditText) {
                    editTexts.add(((EditText) lm.getChildAt(j)));
                }
            }

            // Sort based on tag
            Collections.sort(editTexts, (EditText editText, EditText t1) ->
                    ((int) editText.getTag() - (int) t1.getTag()));

            if (allPlaceHolderFilled(editTexts)) {
                for (EditText editText : editTexts) {
                    String placeHolder = editText.getText().toString();
                    story.fillInPlaceholder(placeHolder);
                }
                // Launch story activity
                Intent intent = new Intent(MainActivity.this, ShowStoryActivity.class);
                intent.putExtra(STORY, story.toString());
                startActivity(intent);
            } else {
                Snackbar snackbar = Snackbar.make(lm, "Not all placeholders are filled!",
                        Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private boolean allPlaceHolderFilled(ArrayList<EditText> editTexts) {
        for (EditText editText : editTexts) {
            String placeHolder = editText.getText().toString();
            if (placeHolder.equals("")) {
                return true;
            }
        }
        return true;
    }

    private InputStream getListOfRawTexts() {
        Field[] fields = R.raw.class.getFields();
        Random random = new Random();
        int randomInt = random.nextInt(fields.length);
        try {
            int resourceID = fields[randomInt].getInt(fields[randomInt]);
            return getResources().openRawResource(resourceID);
        } catch (IllegalAccessException e) {
            Log.e("Illegal Access", "E: " + e);
        }
        return null;
    }

}
