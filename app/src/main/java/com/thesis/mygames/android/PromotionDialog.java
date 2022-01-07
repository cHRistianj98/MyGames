package com.thesis.mygames.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
        String choosePiece = "Wybierz figurę";
        String ok = "Ok";
        String cancel = "Cofnij";

        builder.setTitle(choosePiece)
                .setSingleChoiceItems(list, 0, (dialog, i) -> position = i)
                .setPositiveButton(ok, (dialog, i) -> mListener.onPositiveButtonClicked(list, position))
                .setNegativeButton(cancel, (dialog, i) -> mListener.onNegativeButtonClicked());

        return builder.create();
    }
}
