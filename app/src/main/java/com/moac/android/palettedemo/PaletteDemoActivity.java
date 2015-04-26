package com.moac.android.palettedemo;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Observable;

import static com.moac.android.palettedemo.PaletteOption.VIBRANT;
import static com.moac.android.palettedemo.ScreenUtils.getStatusBarHeight;

public class PaletteDemoActivity extends AppCompatActivity implements ImageFragment.FragmentContainer {

    private final static int[] IMAGE_RESOURCES = {
            R.drawable.sample_pic1,
            R.drawable.sample_pic2,
            R.drawable.sample_pic3,
            R.drawable.sample_pic4,
            R.drawable.sample_pic5
    };
    private static final PaletteOption DEFAULT_PALETTE_OPTION = VIBRANT;
    private static final float WIDTH_FACTOR = 1.2f;

    private PaletteOptionObservable paletteObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette_demo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        adjustForStatusBar(toolbar);

        paletteObservable = new PaletteOptionObservable(DEFAULT_PALETTE_OPTION);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager_image);
        viewPager.setOffscreenPageLimit(viewPager.getOffscreenPageLimit() * (int) WIDTH_FACTOR);
        final FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return ImageFragment.create(IMAGE_RESOURCES[position], paletteObservable.paletteOption);
            }

            @Override
            public float getPageWidth(int position) {
                return (1f / WIDTH_FACTOR);
            }

            @Override
            public int getCount() {
                return IMAGE_RESOURCES.length;
            }
        };
        viewPager.setAdapter(pagerAdapter);

    }

    private void adjustForStatusBar(Toolbar toolbar) {
        // No need to adjust on platforms that don't support transparent system bars
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Set paddingTop of toolbar to height of status bar.
            toolbar.setPadding(0, getStatusBarHeight(this), 0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for (PaletteOption option : PaletteOption.values()) {
            // Setting the id this way is is probably an abuse of the API - the ids are too anonymous
            MenuItem item = menu.add(Menu.NONE, option.ordinal(), option.ordinal(), option.toString());
            item.setCheckable(true);
            item.setChecked(paletteObservable.paletteOption == option);
        }
        // Must be done after adding menu items
        menu.setGroupCheckable(Menu.NONE, true, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isPaletteOptionId(item.getItemId())) {
            selectAndUpdate(item, PaletteOption.values()[item.getItemId()]);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Observable getPaletteObservable() {
        return paletteObservable;
    }

    private void selectAndUpdate(MenuItem menuItem, PaletteOption option) {
        menuItem.setChecked(true);
        paletteObservable.update(option);
    }

    private static boolean isPaletteOptionId(int itemId) {
        return itemId >= 0 && itemId < PaletteOption.values().length;
    }

    private static class PaletteOptionObservable extends Observable {
        PaletteOption paletteOption;

        public PaletteOptionObservable(PaletteOption defaultPalette) {
            paletteOption = defaultPalette;
        }

        public void update(PaletteOption option) {
            paletteOption = option;
            setChanged();
            notifyObservers(paletteOption);
        }
    }
}
