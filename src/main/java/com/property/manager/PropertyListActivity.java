package com.property.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.property.manager.fragments.PropertyListFragment;

public class PropertyListActivity extends BaseFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new PropertyListFragment();
    }


}