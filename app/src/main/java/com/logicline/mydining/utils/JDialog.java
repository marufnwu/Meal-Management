package com.logicline.mydining.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.logicline.mydining.R;
import com.logicline.mydining.utils.Ext.MyExtensions;

public class JDialog {

    public enum IconType {
        SUCCESS,
        WARNING,
        ERROR,
        INFO
    }

    private String TAG = "JDialog";
    private String negativeButtonText, positiveButtonText, bodyText, titleText;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private android.app.Dialog dialog;
    public boolean showPositiveButton = false;
    public boolean showNegativeButton = false;
    private TextView txtBody;
    private boolean cancelable = false;
    private JDialogImage image;
    private IconType iconType = IconType.SUCCESS; // Default to success
    private boolean animateIcon = true;

    public OnGenericDialogListener onGenericDialogListener;
    public OnPositiveButtonClickListener onPositiveButtonClickListener;
    public OnNegativeButtonClickListener onNegativeButtonClickListener;

    public interface OnGenericDialogListener {
        void onPositiveButtonClick(JDialog dialog);
        void onNegativeButtonClick(JDialog dialog);
        void onToast(String message);
    }

    public interface OnPositiveButtonClickListener {
        void onPositiveButtonClick(JDialog dialog);
    }

    public interface OnNegativeButtonClickListener {
        void onNegativeButtonClick(JDialog dialog);
    }

    @SuppressLint("StaticFieldLeak")
    private static JDialog genericDialog;

    private JDialog(Context context) {
        JDialog.context = context;
        setupDialog();
    }

    private JDialog instance() {
        return this;
    }

    public static JDialog make(Context context) {
        Log.d("JDialog", "make called");
        genericDialog = new JDialog(context);
        setLifeCycle(context);
        return genericDialog;
    }

    private void setupDialog() {
        if (dialog == null) {
            dialog = new android.app.Dialog(context);
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(cancelable);
        dialog.setContentView(R.layout.dialog_generic);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private static void setLifeCycle(Context context) {
        LifecycleOwner lifeCycle = MyExtensions.INSTANCE.lifecycleOwner(context);
        if (lifeCycle != null) {
            lifeCycle.getLifecycle()
                    .addObserver(new LifecycleEventObserver() {
                        @Override
                        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                            if (Lifecycle.Event.ON_DESTROY == event) {
                                destroyDialogObject();
                            }
                        }
                    });
        }
    }

    private static void destroyDialogObject() {
        if (genericDialog != null) {
            genericDialog.hideDialog();
            genericDialog = null;
        }
    }

    public JDialog imageUrl(JDialogImage image) {
        this.image = image;
        return this;
    }

    public JDialog setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
        this.showNegativeButton = true;
        return this;
    }

    public JDialog setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
        this.showPositiveButton = true;
        return this;
    }

    public JDialog setPositiveButton(String text, OnPositiveButtonClickListener onPositiveButtonClickListener) {
        this.positiveButtonText = text;
        this.showPositiveButton = true;
        this.onPositiveButtonClickListener = onPositiveButtonClickListener;
        return this;
    }

    public JDialog setNegativeButton(String text, OnNegativeButtonClickListener onNegativeButtonClickListener) {
        this.negativeButtonText = text;
        this.showNegativeButton = true;
        this.onNegativeButtonClickListener = onNegativeButtonClickListener;
        return this;
    }

    public JDialog setBodyText(String bodyText) {
        this.bodyText = bodyText;
        return this;
    }

    public JDialog setTitleText(String titleText) {
        this.titleText = titleText;
        return this;
    }

    public JDialog setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public JDialog setIconType(IconType iconType) {
        this.iconType = iconType;
        return this;
    }

    public JDialog setAnimateIcon(boolean animateIcon) {
        this.animateIcon = animateIcon;
        return this;
    }

    public boolean isShowNegativeButton() {
        return showNegativeButton;
    }

    public JDialog setShowNegativeButton(boolean showNegativeButton) {
        this.showNegativeButton = showNegativeButton;
        return this;
    }

    public boolean isShowPositiveButton() {
        return showPositiveButton;
    }

    public JDialog setShowPositiveButton(boolean showPositiveButton) {
        this.showPositiveButton = showPositiveButton;
        return this;
    }

    public OnGenericDialogListener getOnGenericDialogListener() {
        return onGenericDialogListener;
    }

    public JDialog setOnGenericDialogListener(OnGenericDialogListener onGenericDialogListener) {
        this.onGenericDialogListener = onGenericDialogListener;
        return this;
    }

    public TextView getTxtBody() {
        return txtBody;
    }

    public JDialog build() {
        MaterialButton btnPositive = dialog.findViewById(R.id.btnPositive);
        MaterialButton btnNegative = dialog.findViewById(R.id.btnNegative);
        ImageView imageView = dialog.findViewById(R.id.img);
        ImageView icon = dialog.findViewById(R.id.imgIcon);
        CardView iconContainer = dialog.findViewById(R.id.iconContainer);
        FrameLayout imgContainer = dialog.findViewById(R.id.imgContainer);
        txtBody = dialog.findViewById(R.id.txtBody);

        // Configure icon based on type
        if (iconType != null) {
            // Set appropriate icon and background color
            int iconResId = R.drawable.success;
            int backgroundColor = context.getResources().getColor(R.color.md_theme_primary);
            int iconTint = context.getResources().getColor(R.color.md_theme_primary);

            switch (iconType) {
                case SUCCESS:
                    iconResId = R.drawable.success;
                    backgroundColor = context.getResources().getColor(R.color.success_light);
                    iconTint = context.getResources().getColor(R.color.success);
                    break;
                case WARNING:
                    iconResId = R.drawable.warning;
                    backgroundColor = context.getResources().getColor(R.color.warning_light);
                    iconTint = context.getResources().getColor(R.color.warning);
                    break;
                case ERROR:
                    iconResId = R.drawable.error;
                    backgroundColor = context.getResources().getColor(R.color.error_light);
                    iconTint = context.getResources().getColor(R.color.error);
                    break;
                case INFO:
                    iconResId = R.drawable.informing;
                    backgroundColor = context.getResources().getColor(R.color.info_light);
                    iconTint = context.getResources().getColor(R.color.info);
                    break;
            }

            icon.setImageResource(iconResId);
            icon.setImageTintList(ColorStateList.valueOf(iconTint));
            iconContainer.setCardBackgroundColor(backgroundColor);

            // Animate the icon if enabled
            if (animateIcon) {
                Animation pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.pulse);
                iconContainer.startAnimation(pulseAnimation);
            }
        } else {
            iconContainer.setVisibility(View.GONE);
        }

        // Setup image if provided
        if (image != null && image.imagePath != null) {
            imageView.setVisibility(View.VISIBLE);
            imgContainer.setVisibility(View.VISIBLE);

            // Apply rounded corners to image using Glide
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(context.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp)));

            Glide.with(context)
                    .load(image.imagePath)
                    .apply(requestOptions)
                    .into(imageView);

            // Setup click listener if link is provided
            String link = image.link;
            if (link != null) {
                imageView.setOnClickListener(view -> {
                    // Handle link click here
                });
            }
        } else {
            imageView.setVisibility(View.GONE);
            imgContainer.setVisibility(View.GONE);
        }

        // Setup body text
        if (bodyText != null) {
            txtBody.setText(bodyText);
            txtBody.setVisibility(View.VISIBLE);
        } else {
            txtBody.setVisibility(View.GONE);
        }

        // Setup button text and visibility
        if (negativeButtonText != null) {
            btnNegative.setText(negativeButtonText);
        }

        if (positiveButtonText != null) {
            btnPositive.setText(positiveButtonText);
        }

        // Setup button click listeners
        btnPositive.setOnClickListener(v -> {
            if (onGenericDialogListener != null) {
                onGenericDialogListener.onPositiveButtonClick(instance());
            }

            if (onPositiveButtonClickListener != null) {
                onPositiveButtonClickListener.onPositiveButtonClick(instance());
            }
        });

        btnNegative.setOnClickListener(v -> {
            if (onGenericDialogListener != null) {
                onGenericDialogListener.onNegativeButtonClick(instance());
            }
            if (onNegativeButtonClickListener != null) {
                onNegativeButtonClickListener.onNegativeButtonClick(instance());
            }
        });

        // Set button visibility
        btnNegative.setVisibility(showNegativeButton ? View.VISIBLE : View.GONE);
        btnPositive.setVisibility(showPositiveButton ? View.VISIBLE : View.GONE);

        // Reset image
        imageUrl(null);

        return this;
    }

    public void showDialog() {
        Activity activity = (Activity) context;

        if (dialog != null && !activity.isFinishing() && !activity.isDestroyed()) {
            if (dialog.isShowing()) {
                hideDialog();
            }
            dialog.show();

            // Add entrance animation
            if (dialog.getWindow() != null) {
                dialog.getWindow().getDecorView().startAnimation(
                        AnimationUtils.loadAnimation(context, R.anim.dialog_enter)
                );
            }
        }
    }

    public void hideDialog() {
        Activity activity = (Activity) context;

        if (dialog != null && !activity.isFinishing() && !activity.isDestroyed()) {
            if (dialog.isShowing()) {
                // Add exit animation
                if (dialog.getWindow() != null) {
                    Animation exitAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_exit);
                    exitAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                    dialog.getWindow().getDecorView().startAnimation(exitAnim);
                } else {
                    dialog.dismiss();
                }
            }
        }
    }
}