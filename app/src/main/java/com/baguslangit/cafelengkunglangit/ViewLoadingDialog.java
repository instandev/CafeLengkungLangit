package com.baguslangit.cafelengkunglangit;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.baguslangit.cafelengkunglangit.databinding.ViewLoadingDialogBinding;

import java.util.Objects;

public class ViewLoadingDialog extends Dialog {
    private String pesan = "";
    private ViewLoadingDialogBinding binding;
    public ViewLoadingDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ViewLoadingDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DialogOption();
    }

    private void DialogOption(){
        setCanceledOnTouchOutside(false);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding.dialogLoadingPesan.setText(pesan);
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }
}