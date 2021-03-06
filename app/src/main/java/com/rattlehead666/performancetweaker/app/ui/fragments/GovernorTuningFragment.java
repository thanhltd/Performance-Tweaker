package com.rattlehead666.performancetweaker.app.ui.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.rattlehead666.performancetweaker.app.R;
import com.rattlehead666.performancetweaker.app.utils.CpuFrequencyUtils;
import com.rattlehead666.performancetweaker.app.utils.GovernorProperty;

public class GovernorTuningFragment extends PreferenceFragment
    implements Preference.OnPreferenceChangeListener {

  public static String TAG = "GOVERNOR_TUNING";
  PreferenceCategory preferenceCategory;
  EditTextPreference editTextPreferences[];
  GovernorProperty[] governorProperties;
  FrameLayout governorPropertiesContainer;
  Context context;
  ProgressBar progressBar;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_pref_container, container, false);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.governor_tuning_preferences);

    preferenceCategory = (PreferenceCategory) findPreference("governor_tune_pref");
    governorPropertiesContainer =
        (FrameLayout) getActivity().findViewById(R.id.frame_layout_preference);
    progressBar = (ProgressBar) getActivity().findViewById(R.id.loading);
    context = getActivity();

    new GetGovernorPropertiesTask().execute();
  }

  @Override public boolean onPreferenceChange(Preference preference, Object o) {
    CpuFrequencyUtils.setGovernorProperty(new GovernorProperty(preference.getKey(), o.toString()),
        getActivity());
    preference.setSummary(o.toString());
    return true;
  }

  private class GetGovernorPropertiesTask extends AsyncTask<Void, Void, GovernorProperty[]> {

    @Override protected void onPreExecute() {
      super.onPreExecute();

      //      progressBar.setVisibility(View.VISIBLE);
      //   governorPropertiesContainer.setVisibility(View.GONE);
    }

    @Override protected GovernorProperty[] doInBackground(Void... params) {
      governorProperties = CpuFrequencyUtils.getGovernorProperties();
      return governorProperties;
    }

    @Override protected void onPostExecute(GovernorProperty[] governorProperties) {
      super.onPostExecute(governorProperties);

      //      progressBar.setVisibility(View.GONE);
      //      governorPropertiesContainer.setVisibility(View.VISIBLE);

      if (governorProperties != null && governorProperties.length != 0) {
        editTextPreferences = new EditTextPreference[governorProperties.length];
        for (int i = 0; i < editTextPreferences.length; i++) {
          editTextPreferences[i] = new EditTextPreference(context);
          editTextPreferences[i].setKey(governorProperties[i].getGovernorProperty());
          editTextPreferences[i].setTitle(governorProperties[i].getGovernorProperty());
          editTextPreferences[i].setSummary(governorProperties[i].getGovernorPropertyValue());
          editTextPreferences[i].setDialogTitle(governorProperties[i].getGovernorProperty());
          editTextPreferences[i].setDefaultValue(governorProperties[i].getGovernorPropertyValue());
          editTextPreferences[i].setOnPreferenceChangeListener(GovernorTuningFragment.this);

          preferenceCategory.addPreference(editTextPreferences[i]);
        }
      }
    }
  }
}
