package ru.henridellal.dialer;

import static android.os.Build.VERSION_CODES.M;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.henridellal.dialer.preference.ContactSourcesPreference;
import ru.henridellal.dialer.util.ThemingUtil;

@SuppressLint("CustomSplashScreen")
public class FirstLaunchActivity extends Activity implements View.OnClickListener {
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.getBoolean("privacy_policy", false)) {
            PrivacyPolicyDialog.show(this, preferences.edit());
        }
        setContentView(R.layout.first_launch);
        findViewById(R.id.btn_provide_permissions).setOnClickListener(this);
        findViewById(R.id.btn_finish_init).setOnClickListener(this);
        if (PermissionManager.hasRequiredPermissions(this)) {
            findViewById(R.id.btn_finish_init).setEnabled(true);
        }

        TextView header = new TextView(this);
        header.setText(R.string.permission_request);
        ListView permissionNote = findViewById(R.id.permission_note);
        permissionNote.addHeaderView(header);
        PermissionAdapter adapter = new PermissionAdapter(this, R.layout.permission_desc);
        permissionNote.setAdapter(adapter);
    }

    @TargetApi(M)
    private void checkPermissions() {
        if (!PermissionManager.hasAllPermissions(this)) {
            requestPermissions(PermissionManager.PERMISSIONS, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.CALL_PHONE.equals(permissions[i])) {
                    findViewById(R.id.btn_finish_init).setEnabled(
                            grantResults[i] == PackageManager.PERMISSION_GRANTED
                    );
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_provide_permissions) {
            checkPermissions();
        } else if (id == R.id.btn_finish_init) {
            if (PermissionManager.isPermissionGranted(this, Manifest.permission.READ_CONTACTS)) {
                ContactSourcesPreference.initDefaults(this, preferences);
            }
            Intent intent = new Intent(this, DialerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    protected static class PermissionAdapter extends ArrayAdapter<PermissionEntry> {
        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SECTION_TITLE = 1;

        public PermissionAdapter(Context context, int resource) {
            super(context, resource);

            ArrayList<PermissionEntry> permissions = new ArrayList<>();
            permissions.add(new PermissionEntry(-1, R.string.permission_title_required));
            permissions.add(new PermissionEntry(R.attr.drawableIconPhone, R.string.permission_make_calls));

            permissions.add(new PermissionEntry(-1, R.string.permission_title_optional));
            permissions.add(new PermissionEntry(R.attr.drawableContacts, R.string.permission_call_log));
            permissions.add(new PermissionEntry(R.attr.drawableContacts, R.string.permission_contacts));
            addAll(permissions);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            int viewType = getItemViewType(position);
            PermissionEntry entry = ((PermissionEntry) getItem(position));

            if (null == convertView) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (viewType == TYPE_SECTION_TITLE) {
                    view = inflater.inflate(R.layout.glass_section_title, parent, false);
                } else {
                    view = inflater.inflate(R.layout.permission_desc, parent, false);
                }
            } else {
                view = convertView;
            }

            if (viewType == TYPE_SECTION_TITLE) {
                ((TextView) view.findViewById(R.id.glass_section_title)).setText(entry.descriptionId);
            } else {
                ((ImageView)view.findViewById(R.id.permission_image)).setImageDrawable(
                        ThemingUtil.getThemedDrawable(getContext(), entry.resId)
                );
                ((TextView)view.findViewById(R.id.permission_desc)).setText(entry.descriptionId);
            }
            return view;
        }

        @Override
        public int getItemViewType(int position) {
            return ((PermissionEntry)getItem(position)).resId == -1 ? TYPE_SECTION_TITLE : TYPE_ITEM;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences = null;
    }
}
