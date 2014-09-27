package com.moac.android.palettedemo;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Observable;

import static com.moac.android.palettedemo.PaletteOption.VIBRANT;

public class PaletteDemoActivity extends Activity implements ImageFragment.FragmentContainer {

    private final static int[] IMAGE_RESOURCES = {
            R.drawable.sample_pic1,
            R.drawable.sample_pic2,
            R.drawable.sample_pic3,
            R.drawable.sample_pic4,
            R.drawable.sample_pic5
    };
    private static final PaletteOption DEFAULT_PALETTE_OPTION = VIBRANT;
    private static final float WIDTH_FACTOR = 3;

    private PaletteOptionObservable paletteObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette_demo);

        paletteObservable = new PaletteOptionObservable(DEFAULT_PALETTE_OPTION);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager_image);
        viewPager.setOffscreenPageLimit(viewPager.getOffscreenPageLimit() * (int)WIDTH_FACTOR);
        final FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getFragmentManager()) {
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
