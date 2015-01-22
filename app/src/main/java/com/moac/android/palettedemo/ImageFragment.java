package com.moac.android.palettedemo;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import static com.moac.android.palettedemo.PaletteDemoActivity.PaletteOptionObservable;

public class ImageFragment extends Fragment implements Observer {

    private static final String RESOURCE_ID_ARG = "RESOURCE_ID_ARG";
    private static final String PALETTE_OPTION_ARG = "PALETTE_OPTION_ARG";

    private ImageView imageView;
    private FragmentContainer fragmentContainer;

    public static ImageFragment create(int resourceId, PaletteOption option) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt(RESOURCE_ID_ARG, resourceId);
        args.putSerializable(PALETTE_OPTION_ARG, option);
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
        PaletteOption initialOption = (PaletteOption) getArguments().getSerializable(PALETTE_OPTION_ARG);
        onChangePaletteOption(initialOption);
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

    @Override
    public void update(Observable observable, Object data) {
        onChangePaletteOption((PaletteOption) data);
    }

    private void onChangePaletteOption(PaletteOption palette) {

        if (getView() == null) return;

        Palette.Swatch pi;
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        switch (palette) {
            case VIBRANT:
                pi = Palette.generate(bmp).getVibrantSwatch();
                break;
            case VIBRANT_DARK:
                pi = Palette.generate(bmp).getDarkVibrantSwatch();
                break;
            case VIBRANT_LIGHT:
                pi = Palette.generate(bmp).getLightVibrantSwatch();
                break;
            case MUTED:
                pi = Palette.generate(bmp).getMutedSwatch();
                break;
            case MUTED_DARK:
                pi = Palette.generate(bmp).getDarkMutedSwatch();
                break;
            case MUTED_LIGHT:
                pi = Palette.generate(bmp).getLightVibrantSwatch();
                break;
            default:
                pi = Palette.generate(bmp).getVibrantSwatch();
        }
        if (pi != null) {
            getView().setBackgroundColor(pi.getRgb());
        } else {
            // This could get awkward if multiple images show this simultaneously!
            Toast.makeText(getActivity(), "No palette item for " + palette, Toast.LENGTH_SHORT).show();
        }
    }

    private PaletteOptionObservable getPaletteObservable() {
        return fragmentContainer.getPaletteObservable();
    }

    interface FragmentContainer {
        PaletteOptionObservable getPaletteObservable();
    }

}