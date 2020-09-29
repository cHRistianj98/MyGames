package com.thesis.mygames.androidutils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.thesis.mygames.R;

import org.jetbrains.annotations.NotNull;

public class PromotionDialog extends DialogFragment {

    private int position = 0;

    public interface SingleChoiceListener{
        void onPositiveButtonClicked(String[] list, int position);
        void onNegativeButtonClicked();
    }

    SingleChoiceListener mListener;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            mListener = (SingleChoiceListener) context;
        } catch (Exception e) {
            throw new ClassCastException(getActivity().toString() + "SingleChoiceListener Must Implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String[] list = getActivity().getResources().getStringArray(R.array.pieces);

        builder.setTitle("Wybierz figurę")
                .setSingleChoiceItems(list, 0, (dialog, i) -> position = i)
                .setPositiveButton("Ok", (dialog, i) -> mListener.onPositiveButtonClicked(list, position))
                .setNegativeButton("Cofnij", (dialog, i) -> mListener.onNegativeButtonClicked());

        return builder.create();
    }
}
