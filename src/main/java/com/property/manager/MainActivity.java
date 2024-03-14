package com.property.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.property.manager.fragments.PropertyDetailsFragment;
import com.property.manager.model.Property;
import com.property.manager.recycler_view.PropertyViewHolder;

import java.util.List;
import java.util.UUID;

public class MainActivity extends BaseFragmentActivity {

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(PropertyViewHolder.EXTRA_PROPERTY_ID);

        List<Property> properties= getIntent().getParcelableArrayListExtra("properties");

        System.out.println("PRoperties in our system ___________"+properties);
        return new PropertyDetailsFragment().newInstance(crimeId,properties);
    }

}