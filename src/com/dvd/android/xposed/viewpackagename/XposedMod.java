package com.dvd.android.xposed.viewpackagename;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
						final PackageInfo info = (PackageInfo) param.args[0];

						View mRootView = (View) XposedHelpers.getObjectField(
								param.thisObject, "mRootView");
						final Context c = ((View) mRootView.getParent())
								.getContext();

						Resources res = mRootView.getResources();

						int appSnippetId = res.getIdentifier("app_snippet",
								"id", TAG);

						int appVersionId = res.getIdentifier("app_size", "id",
								TAG);

						View appSnippet = mRootView.findViewById(appSnippetId);

						TextView label = (TextView) appSnippet
								.findViewById(appVersionId);
						label.setOnLongClickListener(new View.OnLongClickListener() {
							@SuppressWarnings("deprecation")
							@Override
							public boolean onLongClick(View v) {

								ClipboardManager clipboard = (ClipboardManager) c
										.getSystemService(Context.CLIPBOARD_SERVICE);
								clipboard.setText(info.packageName);
								Toast.makeText(c,
										"Package name copied in clipboard",
										Toast.LENGTH_SHORT).show();
								return false;

							}
						});

						label.setText(info.packageName + "\n"
								+ label.getText().toString());
					}

				});
	}

}