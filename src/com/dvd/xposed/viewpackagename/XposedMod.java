package com.dvd.xposed.viewpackagename;

import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedMod implements IXposedHookLoadPackage {

	public static String TAG = "com.android.settings";

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals(TAG))
			return;

		XposedHelpers.findAndHookMethod(TAG
				+ ".applications.InstalledAppDetails", lpparam.classLoader,
				"setAppLabelAndIcon", PackageInfo.class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						PackageInfo info = (PackageInfo) param.args[0];

						View mRootView = (View) XposedHelpers.getObjectField(
								param.thisObject, "mRootView");

						Resources res = mRootView.getResources();

						int appSnippetId = res.getIdentifier("app_snippet",
								"id", TAG);

						int appVersionId = res.getIdentifier("app_size", "id",
								TAG);

						View appSnippet = mRootView.findViewById(appSnippetId);

						TextView label = (TextView) appSnippet
								.findViewById(appVersionId);

						String name = label.getText().toString();

						XSharedPreferences pref = new XSharedPreferences(
								"com.dvd.xposed.viewpackagename", "pref");

						if (pref.getBoolean("on", true)) {
							label.setText(info.packageName + "\n" + name);
						}

					}
				});
	}

}