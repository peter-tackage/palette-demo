package com.moac.android.palettedemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class ImageFragment extends Fragment implements Observer {

    private static final String TAG = ImageFragment.class.getSimpleName();

    private static final String RESOURCE_ID_ARG = "RESOURCE_ID_ARG";
    private static final String KEY_PALETTE_OPTION = "PALETTE_OPTION";

    private ImageView imageView;
    private FragmentContainer fragmentContainer;
    private PaletteOption paletteOption;

    public static ImageFragment create(int resourceId, PaletteOption option) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt(RESOURCE_ID_ARG, resourceId);
        args.putSerializable(KEY_PALETTE_OPTION, option);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fragmentContainer = (FragmentContainer) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FragmentContainer");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentContainer = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_image, container, false);

        int imageResourceId = getArguments().getInt(RESOURCE_ID_ARG);
        imageView = (ImageView) root.findViewById(R.id.imageView_pic);
        imageView.setImageResource(imageResourceId);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PaletteOption initialPaletteOption;
        if (savedInstanceState == null) {
            initialPaletteOption = (PaletteOption) getArguments().getSerializable(KEY_PALETTE_OPTION);
        } else {
            initialPaletteOption = (PaletteOption) savedInstanceState.getSerializable(KEY_PALETTE_OPTION);
        }
        onChangePaletteOption(initialPaletteOption);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPaletteObservable().addObserver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPaletteObservable().deleteObserver(this);
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_PALETTE_OPTION, paletteOption);
    }

    @Override
    public void update(Observable observable, Object data) {
        onChangePaletteOption((PaletteOption) data);
    }

    private void onChangePaletteOption(PaletteOption paletteOption) {

        Log.d(TAG, "onChangePaletteOption(): " + paletteOption);

        this.paletteOption = paletteOption;

        if (getView() == null) return;

        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Palette palette = Palette.from(bmp).generate();
        Palette.Swatch swatch;

        switch (paletteOption) {
            case VIBRANT:
                swatch = palette.getVibrantSwatch();
                break;
            case VIBRANT_DARK:
                swatch = palette.getDarkVibrantSwatch();
                break;
            case VIBRANT_LIGHT:
                swatch = palette.getLightVibrantSwatch();
                break;
            case MUTED:
                swatch = palette.getMutedSwatch();
                break;
            case MUTED_DARK:
                swatch = palette.getDarkMutedSwatch();
                break;
            case MUTED_LIGHT:
                swatch = palette.getLightVibrantSwatch();
                break;
            default:
                swatch = palette.getVibrantSwatch();
        }
        if (swatch != null) {
            getView().setBackgroundColor(swatch.getRgb());
        } else {
            // This could get awkward if multiple images show this simultaneously!
            Toast.makeText(getActivity(), "No swatch for " + palette, Toast.LENGTH_SHORT).show();
        }
    }

    private Observable getPaletteObservable() {
        return fragmentContainer.getPaletteObservable();
    }

    interface FragmentContainer {
        Observable getPaletteObservable();
    }

}