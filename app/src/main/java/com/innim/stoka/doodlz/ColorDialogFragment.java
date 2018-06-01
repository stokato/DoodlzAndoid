package com.innim.stoka.doodlz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.SeekBar;

/**
 * Created by s.t.o.k.a.t.o on 01.06.2018.
 */
public class ColorDialogFragment extends DialogFragment {
    private SeekBar mAlphaSeekBar;
    private SeekBar mRedSeekBar;
    private SeekBar mGreenSeekBar;
    private SeekBar mBlueSeekBar;
    private View mColorView;
    private int mColor;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View colorDialogView = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_color, null);
        builder.setView(colorDialogView); // Добавление GUI в диалоговое окно

        builder.setTitle(R.string.title_color_dialog);

        mAlphaSeekBar = (SeekBar) colorDialogView.findViewById(R.id.alphaSeekBar);
        mRedSeekBar = (SeekBar) colorDialogView.findViewById(R.id.redSeekBar);
        mGreenSeekBar = (SeekBar) colorDialogView.findViewById(R.id.greenSeekBar);
        mBlueSeekBar = (SeekBar) colorDialogView.findViewById(R.id.blueSeekBar);

        mColorView = colorDialogView.findViewById(R.id.colorView);

        mAlphaSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        mRedSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        mGreenSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        mBlueSeekBar.setOnSeekBarChangeListener(colorChangedListener);

        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        mColor = doodleView.getDrawingColor();
        mAlphaSeekBar.setProgress(Color.alpha((mColor)));
        mRedSeekBar.setProgress(Color.red(mColor));
        mGreenSeekBar.setProgress(Color.green(mColor));
        mBlueSeekBar.setProgress(Color.blue(mColor));

        builder.setPositiveButton(R.string.button_set_color,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doodleView.setDrawingColor(mColor);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        MainActivityFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        MainActivityFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(false);
    }

    private final SeekBar.OnSeekBarChangeListener colorChangedListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                    if (fromUser) {
                        mColor = Color.argb(mAlphaSeekBar.getProgress(),
                                mRedSeekBar.getProgress(), mGreenSeekBar.getProgress(),
                                mBlueSeekBar.getProgress());
                        mColorView.setBackgroundColor(mColor);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            };

    private MainActivityFragment getDoodleFragment() {
        return (MainActivityFragment) getFragmentManager().findFragmentById(R.id.doodleFragment);
    }
}
