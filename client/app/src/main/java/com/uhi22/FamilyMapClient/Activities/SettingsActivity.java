/**
 * This is the Settings Activity that is opened when the user selectsthe settings icon on the
 * map fragment from the Main Activity
 * This activity shows the status of the filters and allows the user to toggle filters on and off
 * This activity also hosts the logout option, which returns the main activity to a state without an authToken
 * and requires login again
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.uhi22.FamilyMapClient.R;

public class SettingsActivity extends AppCompatActivity {

//    Declare the variables that are used in the UI
    private ConstraintLayout logoutLayout;
    private Switch lifeStorySwitch, familyTreeLineSwitch, spouseLineSwitch, fatherSwitch, motherSwitch, maleSwitch, femaleSwitch;

//    Declare variables that will be used to denote whether filters are enabled or not
    private boolean lifeStoryEnabled, familyTreeEnabled, spouseLineEnabled, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled;

    /**
     * This method runs when the Activity is first created
     * On the Settings Activity, that defines the variables that will be used and the UI elements
     *
     * @param savedInstanceState is the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Define the activity layout and instance state
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
//        Populate the UI
        lifeStorySwitch = (Switch) findViewById(R.id.life_story_switch);
        familyTreeLineSwitch = (Switch) findViewById(R.id.family_tree_switch);
        spouseLineSwitch = (Switch) findViewById(R.id.spouse_switch);
        fatherSwitch = (Switch) findViewById(R.id.father_switch);
        motherSwitch = (Switch) findViewById(R.id.mother_switch);
        maleSwitch = (Switch) findViewById(R.id.male_switch);
        femaleSwitch = (Switch) findViewById(R.id.female_switch);
        logoutLayout = (ConstraintLayout) findViewById(R.id.logout_layout);

//        Get information regarding filter settings
        Intent intent = getIntent();
        lifeStoryEnabled = intent.getExtras().getBoolean("lifeStoryEnabled");
        familyTreeEnabled = intent.getExtras().getBoolean("familyTreeEnabled");
        spouseLineEnabled = intent.getExtras().getBoolean("spouseLineEnabled");
        fatherEnabled = intent.getExtras().getBoolean("fatherEnabled");
        motherEnabled = intent.getExtras().getBoolean("motherEnabled");
        maleEnabled = intent.getExtras().getBoolean("maleEnabled");
        femaleEnabled = intent.getExtras().getBoolean("femaleEnabled");

//        Initialize the switches and listeners for User Interaction
        initializeSwitches();
        initializeListeners();
    }

    /**
     * This method toggles the switches on or off depending on whether the filters were enabled in the previous activity
     * that called this one
     */
    private void initializeSwitches() {
//        Set switch status based on whether the filter is enabled
        lifeStorySwitch.setChecked(lifeStoryEnabled);
        familyTreeLineSwitch.setChecked(familyTreeEnabled);
        spouseLineSwitch.setChecked(spouseLineEnabled);
        fatherSwitch.setChecked(fatherEnabled);
        motherSwitch.setChecked(motherEnabled);
        maleSwitch.setChecked(maleEnabled);
        femaleSwitch.setChecked(femaleEnabled);
    }

    /**
     * This method initializes listeners for all the switches
     * This allows the switches to be pressed and their corresponding boolean values to be updated
     * on a status change
     * This also sets up a listener on the logout button and attaches the logout function to it
     */
    private void initializeListeners() {

//        Set a listener for every switch
        lifeStorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lifeStoryEnabled = isChecked;
            }
        });
        familyTreeLineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                familyTreeEnabled = isChecked;
            }
        });
        spouseLineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                spouseLineEnabled = isChecked;
            }
        });
        fatherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fatherEnabled = isChecked;
            }
        });
        motherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                motherEnabled = isChecked;
            }
        });
        maleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                maleEnabled = isChecked;
            }
        });
        femaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                femaleEnabled = isChecked;
            }
        });
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                When the logout button is clicked, send an intent back to the main activity to call the logout function
                Intent logoutIntent = new Intent();
                logoutIntent.putExtra("settingChange", "logout");
                setResult(RESULT_OK, logoutIntent);
//                Exit the activity after denoting that the user should be logged out
                finish();
            }
        });
    }

    /**
     * This method allows for the user to return to the main activity with data attached to the
     * intent transaction.  This data contains the filter status for all the possible filters
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        Put extras on the return intent based on the switch status
        if(item.getItemId() == android.R.id.home) {
            Intent logoutIntent = new Intent();
            logoutIntent.putExtra("settingChange", "regular");
            logoutIntent.putExtra("lifeStoryEnabled", lifeStoryEnabled);
            logoutIntent.putExtra("familyTreeEnabled", familyTreeEnabled);
            logoutIntent.putExtra("spouseLineEnabled", spouseLineEnabled);
            logoutIntent.putExtra("fatherEnabled", fatherEnabled);
            logoutIntent.putExtra("motherEnabled", motherEnabled);
            logoutIntent.putExtra("maleEnabled", maleEnabled);
            logoutIntent.putExtra("femaleEnabled", femaleEnabled);
            setResult(RESULT_OK, logoutIntent);
//            Exit and return to the main activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
