package com.innim.stoka.doodlz;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private DoodleView mDoodleView;
    private float mAcceleration;
    private float mCurrentAcceleration;
    private float mLastAcceleration;
    private boolean mDialogOnScreen = false;

    // Для обноаружения встряхивания устройства
    private static final int ACCELERATION_THRESHOLD = 100000;

    //Для идентификации запросов на использование внешнего хранилища - для работы сохранения
    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true); // У фрагмента имеются команды меню

        // Получение ссылки на DoodleView
        mDoodleView = (DoodleView) view.findViewById(R.id.doodleView);

        mAcceleration = 0.00f;
        mCurrentAcceleration = SensorManager.GRAVITY_EARTH;
        mLastAcceleration = SensorManager.GRAVITY_EARTH;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        enableAccelerometerListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        disableAccelerometerListening();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.doodle_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.color:
                ColorDialogFragment colorDialog = new ColorDialogFragment();
                colorDialog.show(getFragmentManager(), "color dialog");
                return true;
            case R.id.line_width:
                LineWidthDialogFragment widthDialog =
                        new LineWidthDialogFragment();
                widthDialog.show(getFragmentManager(), "line width dialog");
                return true;
            case R.id.delete_drawing:
                confirmErase();
                return true;
            case R.id.save:
                saveImage();
                return true;
            case R.id.print:
                mDoodleView.printImage();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Обрабатывает результат зарпоса разрешений (saveImage)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {
        switch (requestCode) {
            case SAVE_IMAGE_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    mDoodleView.saveImage();
                return;
        }
    }

    public DoodleView getDoodleView() {
        return mDoodleView;
    }

    public void setDialogOnScreen(boolean visible) {
        mDialogOnScreen = visible;
    }

    private void saveImage() {
        if (getContext().checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setMessage(R.string.permission_explanation);

                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermissions(new String[] {
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        SAVE_IMAGE_PERMISSION_REQUEST_CODE);
                            }
                        });

                builder.create().show();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        SAVE_IMAGE_PERMISSION_REQUEST_CODE);
            }
        } else {
            mDoodleView.saveImage();
        }
    }

    private void enableAccelerometerListening() {
        SensorManager sensorManager = (SensorManager) getActivity()
                .getSystemService(Context.SENSOR_SERVICE);

        sensorManager.registerListener(mSensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void disableAccelerometerListening() {
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(
                        Context.SENSOR_SERVICE);

        sensorManager.unregisterListener(mSensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    private void confirmErase() {
        EraseImageDialogFragment fragment = new EraseImageDialogFragment();
        fragment.show(getFragmentManager(), "erase dialog");
    }

    private final SensorEventListener mSensorEventListener =
            new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    if (!mDialogOnScreen) {
                        float x = sensorEvent.values[0];
                        float y = sensorEvent.values[1];
                        float z = sensorEvent.values[2];

                        mLastAcceleration = mCurrentAcceleration;

                        // Вычеслить текущее ускорение
                        mCurrentAcceleration = x * x  + y * y + z * z;

                        // Вычеслить изменение ускорения
                        mAcceleration = mCurrentAcceleration *
                                (mCurrentAcceleration - mLastAcceleration);

                        if (mAcceleration > ACCELERATION_THRESHOLD)
                            confirmErase();
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };
}
