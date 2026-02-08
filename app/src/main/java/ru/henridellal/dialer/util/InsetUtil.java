package ru.henridellal.dialer.util;

import android.app.Activity;
import android.graphics.Insets;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.RelativeLayout;

import ru.henridellal.dialer.R;

public class InsetUtil {
    public static void applyVanillaWrapper(final Activity activity) {
        activity.getWindow().getDecorView().setOnApplyWindowInsetsListener(
            new OnApplyWindowInsetsListener(activity)
        );
    }

    public static void setContentView(Activity activity, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            activity.setContentView(R.layout.vanilla_wrapper);
            ViewGroup container = activity.findViewById(R.id.wrappedContent);
            View v = LayoutInflater.from(activity).inflate(resId, container);
        } else {
            activity.setContentView(resId);
        }
    }

    public static class OnApplyWindowInsetsListener implements View.OnApplyWindowInsetsListener {
        private final Activity activity;

        public OnApplyWindowInsetsListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            Insets statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars());

            View statusBarInsetView = activity.findViewById(R.id.statusBarInsetView);
            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) statusBarInsetView.getLayoutParams();

            lp.height = statusBarInsets.top;
            statusBarInsetView.setLayoutParams(lp);

            Insets navBarInsets = insets.getInsets(WindowInsets.Type.systemGestures());
            if (rotation == Surface.ROTATION_0) {
                View bottomInsetView = activity.findViewById(R.id.bottomInsetView);
                lp = (RelativeLayout.LayoutParams) bottomInsetView.getLayoutParams();
                lp.height = navBarInsets.bottom;
                bottomInsetView.setLayoutParams(lp);
            }

            Insets cutoutInsets = insets.getInsets(WindowInsets.Type.displayCutout());
            View leftInsetView = activity.findViewById(R.id.leftInsetView);
            lp = (RelativeLayout.LayoutParams) leftInsetView.getLayoutParams();
            if (rotation == Surface.ROTATION_90) {
                lp.width = cutoutInsets.left;
            } else if (rotation == Surface.ROTATION_270) {
                lp.width = navBarInsets.left;
            }

            leftInsetView.setLayoutParams(lp);

            View rightInsetView = activity.findViewById(R.id.rightInsetView);
            lp = (RelativeLayout.LayoutParams) rightInsetView.getLayoutParams();
            if (rotation == Surface.ROTATION_90) {
                lp.width = navBarInsets.right;
            } else if (rotation == Surface.ROTATION_270) {
                lp.width = cutoutInsets.right;
            }

            rightInsetView.setLayoutParams(lp);

            return insets;
        }
    }
}