package nl.yrck.mprog_madlibs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
    static final String STORY_RESOURCE_ID = "story_id";
    static final String EDITTEXT_VALUES = "edittext_values";

    LinearLayoutCompat lm;
    int resourceIdRawStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Recreate saved story
        if (savedInstanceState != null) {
            resourceIdRawStory = savedInstanceState.getInt(STORY_RESOURCE_ID);
        } else {
            resourceIdRawStory = getIdRandomRawText();
        }

        // Load story and edittext view
        final Story story = new Story(getResourceRandomText(resourceIdRawStory));
        createWidgets(story);

        // Recreate strings already put in to edit text
        if (savedInstanceState != null) {
            recreateEditTextStrings(savedInstanceState.getStringArrayList(EDITTEXT_VALUES));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener((View v) -> startShowStoryActivity(story));
    }

    /**
     * Dynamically create widgets for the input of placeholders
     *
     * @param story object
     */
    private void createWidgets(Story story) {
        lm = (LinearLayoutCompat) findViewById(R.id.linearMain);

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
    }

    /**
     * Get the text from all inputs and put to put to intent
     *
     * @param story object
     */
    private void startShowStoryActivity(Story story) {
        ArrayList<EditText> editTexts = getEditTexts();
        ArrayList<String> editTextsStrings = saveEditTextStrings(editTexts);
        if (allPlaceHolderFilled(editTexts)) {
            for (String editTextString : editTextsStrings) {
                story.fillInPlaceholder(editTextString);
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
    }

    /**
     * Check if all placeholders are filled
     *
     * @param editTexts arraylist of all edittexts
     * @return bool
     */
    private boolean allPlaceHolderFilled(ArrayList<EditText> editTexts) {
        for (EditText editText : editTexts) {
            String placeHolder = editText.getText().toString();
            if (placeHolder.equals("")) {
                return true;
            }
        }
        return true;
    }

    /**
     * Return a random id of a text
     *
     * @return random id
     */
    private int getIdRandomRawText() {
        Field[] fields = R.raw.class.getFields();
        Random random = new Random();
        int randomInt = random.nextInt(fields.length);
        try {
            return fields[randomInt].getInt(fields[randomInt]);
        } catch (IllegalAccessException e) {
            Log.e("Illegal Access", "E: " + e);
        }
        return 0;
    }

    /**
     * Return an input stream of random text
     *
     * @param resourceID id
     * @return InputStream
     */
    private InputStream getResourceRandomText(int resourceID) {
        return getResources().openRawResource(resourceID);
    }

    /**
     * @return Collect all edit texts on screen
     */
    private ArrayList<EditText> getEditTexts() {
        ArrayList<EditText> editTexts = new ArrayList<>();
        for (int j = 0; j < lm.getChildCount(); j++) {
            if (lm.getChildAt(j) instanceof EditText) {
                editTexts.add(((EditText) lm.getChildAt(j)));
            }
        }
        return editTexts;
    }

    /**
     * @param editTexts ArrayList
     * @return edittexts
     */
    private ArrayList<String> saveEditTextStrings(ArrayList<EditText> editTexts) {
        ArrayList<String> editTextStrings = new ArrayList<>();

        // Sort based on tag
        Collections.sort(editTexts, (EditText editText, EditText t1) ->
                ((int) editText.getTag() - (int) t1.getTag()));


        for (EditText editText : editTexts) {
            String placeHolder = editText.getText().toString();
            editTextStrings.add(placeHolder);
        }
        return editTextStrings;
    }

    /**
     * Create edit texts from arraylist of strings
     *
     * @param editTextStrings
     */
    private void recreateEditTextStrings(ArrayList<String> editTextStrings) {
        ArrayList<EditText> editTexts = getEditTexts();
        for (int i = 0; i < editTextStrings.size(); i++) {
            editTexts.get(i).setText(editTextStrings.get(i));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STORY_RESOURCE_ID, resourceIdRawStory);

        ArrayList<EditText> editTexts = getEditTexts();
        outState.putStringArrayList(EDITTEXT_VALUES, saveEditTextStrings(editTexts));

        super.onSaveInstanceState(outState);
    }
}
