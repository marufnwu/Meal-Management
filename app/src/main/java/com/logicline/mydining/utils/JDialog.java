package com.logicline.mydining.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.logicline.mydining.R;

public class JDialog {

    public enum IconType{
        SUCCESS,
        WARNING,
        ERROR
    }



    private String TAG = "GenericDialog";
    private String negativeButtonText, positiveButtonText, bodyText;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private android.app.Dialog dialog;
    public boolean showPositiveButton = false;
    public boolean showNegativeButton = false;
    private TextView txtBody;
    private boolean cancelable = false;
    private JDialogImage image;
    private IconType iconType;

    public OnGenericDialogListener onGenericDialogListener;
    public OnPositiveButtonClickListener onPositiveButtonClickListener;
    public OnNegativeButtonClickListener onNegativeButtonClickListener;

    public  interface OnGenericDialogListener {
        void onPositiveButtonClick(JDialog dialog);
        void onNegativeButtonClick(JDialog dialog);
        void onToast(String message);
    }

    public   interface OnPositiveButtonClickListener {
        void onPositiveButtonClick(JDialog dialog);
    }

    public  interface OnNegativeButtonClickListener {
        void onNegativeButtonClick(JDialog dialog);
    }



    @SuppressLint("StaticFieldLeak")
    private static JDialog genericDialog;

    private JDialog(Context context) {
        JDialog.context = context;
        setupDialog();
    }

    private JDialog instance(){
        return this;
    }



    public static  JDialog make(Context context){
        Log.d("GenericDialog", "make called");


        genericDialog = new JDialog(context);

        return genericDialog;
    }

    private  void setupDialog() {
        if(dialog==null){
            dialog = new android.app.Dialog(context);
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(cancelable);
        dialog.setContentView(R.layout.dialog_generic);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private  static void setLifeCycle(Context context) {
        LifecycleOwner lifeCycle = MyExtensions.INSTANCE.lifecycleOwner(context);
        if(lifeCycle!=null){

            lifeCycle.getLifecycle()
                    .addObserver(new LifecycleEventObserver() {

                        @Override
                        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {

                            if(Lifecycle.Event.ON_DESTROY == event){
                               destroyDialogObject();
                           }
                        }
                    });
        }else{
        }
    }

    private static void destroyDialogObject() {
        genericDialog.hideDialog();
        genericDialog = null;
    }

    public JDialog imageUrl(JDialogImage image) {
        this.image = image;
        return this;
    }

    public JDialog setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
        return this;
    }

    public JDialog setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
        return this;
    }

    public JDialog setPositiveButton(String text, OnPositiveButtonClickListener onPositiveButtonClickListener){
        this.positiveButtonText = text;
        this.showPositiveButton = true;
        this.onPositiveButtonClickListener = onPositiveButtonClickListener;

        return this;
    }
    public JDialog setNegativeButton(String text, OnNegativeButtonClickListener onNegativeButtonClickListener){
        this.negativeButtonText = text;
        this.showNegativeButton = true;
        this.onNegativeButtonClickListener = onNegativeButtonClickListener;

        return this;
    }


    public JDialog setBodyText(String bodyText) {
        this.bodyText = bodyText;
        return this;
    }

    public JDialog setCancelable(boolean cancelable){
        this.cancelable = cancelable;
        return this;
    }

    public JDialog setIconType(IconType iconType){
        this.iconType = iconType;
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

    public JDialog setOnGenericDialogListener(OnGenericDialogListener onGenericDialogListener){
        this.onGenericDialogListener = onGenericDialogListener;
        return this;
    }

    public TextView getTxtBody() {
        return txtBody;
    }


    public JDialog build(){
        Button btnPositive = dialog.findViewById(R.id.btnPositive);
        Button btnNegative = dialog.findViewById(R.id.btnNegative);
        ImageView imageView = dialog.findViewById(R.id.img);
        ImageView icon = dialog.findViewById(R.id.imgIcon);

        FrameLayout imgContainer = dialog.findViewById(R.id.imgContainer);

        txtBody = dialog.findViewById(R.id.txtBody);

        if(iconType==null){
            icon.setVisibility(View.GONE);
        }else{
            switch (iconType){
                case SUCCESS:
                    icon.setImageResource(R.drawable.success);
                    break;
                case WARNING:
                    icon.setImageResource(R.drawable.warning);
                    break;
                case ERROR:
                    icon.setImageResource(R.drawable.error);
                    break;
            }
        }

        if(image!=null){
            if(image.imagePath!=null){
                imageView.setVisibility(View.VISIBLE);
                imgContainer.setVisibility(View.VISIBLE);

                Glide.with(context)
                        .load(image.imagePath)
                        .into(imageView);


                String link = image.link;
                if(link!=null){
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                }
            }
        }else{
            imageView.setVisibility(View.GONE);
            imgContainer.setVisibility(View.GONE);
        }

        if (bodyText!=null){
            txtBody.setText(bodyText);
        }else {
            txtBody.setVisibility(View.GONE);
        }

        if (negativeButtonText!=null){
            btnNegative.setText(negativeButtonText);
        }else {
            btnNegative.setVisibility(View.GONE);
        }

        if (positiveButtonText!=null){
            btnPositive.setText(positiveButtonText);
        }else {
            btnNegative.setVisibility(View.GONE);
        }

        btnPositive.setOnClickListener(v -> {
            if(onGenericDialogListener!=null){
                onGenericDialogListener.onPositiveButtonClick(instance());
            }

            if(onPositiveButtonClickListener!=null){
                onPositiveButtonClickListener.onPositiveButtonClick(instance());
            }
        });

        btnNegative.setOnClickListener(v -> {
            if(onGenericDialogListener!=null){
                onGenericDialogListener.onNegativeButtonClick(instance());
            }
            if(onNegativeButtonClickListener!=null){
                onNegativeButtonClickListener.onNegativeButtonClick(instance());
            }
        });

        if (showNegativeButton){
            btnNegative.setVisibility(View.VISIBLE);
        }else{
            btnNegative.setVisibility(View.GONE);
        }

        if (showPositiveButton){
            btnPositive.setVisibility(View.VISIBLE);
        }else{
            btnPositive.setVisibility(View.GONE);
        }


        imageUrl(null);


        return this;


    }

    public void showDialog(){

        Activity acc = (Activity) context;

        if (dialog!=null && !acc.isFinishing() && !acc.isDestroyed()){
            if(dialog.isShowing()){
                hideDialog();
            }
            dialog.show();
        }
    }

    public void hideDialog(){
        Activity acc = (Activity) context;

        if (dialog!=null && !acc.isFinishing() && !acc.isDestroyed()){
            dialog.dismiss();
        }
    }



}




