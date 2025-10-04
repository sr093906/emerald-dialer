package ru.henridellal.dialer;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import java.util.HashMap;
import java.util.Map;

public class DialerApp extends Application {
	
	public static final String LOG_TAG = "ru.henridellal.dialer";

	private static Map<String, Integer> themes = new HashMap<String, Integer>();
	static {
		themes.put("light", R.style.AppTheme_Light);
		themes.put("dark", R.style.AppTheme_Dark);
		themes.put("night", R.style.AppTheme_Night);
		themes.put("amoled", R.style.AppTheme_Amoled);
	}

	public static void setTheme(Activity activity) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		String theme = preferences.getString("theme", "light");
		activity.setTheme(themes.get(theme));
	}

	public static Map<Integer, String> numberTypeLabels;

	public static final int[] numberTypes = new int[] {
			Phone.TYPE_ASSISTANT,
			Phone.TYPE_CALLBACK,
			Phone.TYPE_CAR,
			Phone.TYPE_COMPANY_MAIN,
			Phone.TYPE_CUSTOM,
			Phone.TYPE_FAX_HOME,
			Phone.TYPE_FAX_WORK,
			Phone.TYPE_HOME,
			Phone.TYPE_ISDN,
			Phone.TYPE_MAIN,
			Phone.TYPE_MMS,
			Phone.TYPE_MOBILE,
			Phone.TYPE_OTHER,
			Phone.TYPE_OTHER_FAX,
			Phone.TYPE_PAGER,
			Phone.TYPE_RADIO,
			Phone.TYPE_TELEX,
			Phone.TYPE_TTY_TDD,
			Phone.TYPE_WORK,
			Phone.TYPE_WORK_MOBILE,
			Phone.TYPE_WORK_PAGER
	};

	public static String primaryNumberTypeLabel;

	public static void setNumberTypeLabels(Resources res) {
		numberTypeLabels = new HashMap<>();
        for (int numberType : numberTypes) {
            numberTypeLabels.put(
                    numberType,
                    (String) Phone.getTypeLabel(res, numberType, null)
            );
        }

		primaryNumberTypeLabel = res.getString(R.string.number_type_primary);
	}
}
