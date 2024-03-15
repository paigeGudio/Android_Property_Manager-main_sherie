package com.property.manager.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import com.property.manager.R;
import com.property.manager.model.Property;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PropertyDetailsFragment extends Fragment {

    private Property property;

    private TextView propertyIdText;

    private TextView propertyNameText;

    private TextView propertyDescriptionText;

    private TextView propertyOwnerText;

    private TextView propertyOwnerEmailText;
    private Button availabilityDateBtn;
    private Button editPropertyBtn;
    private Button mOwnerButton;

    private ActivityResultLauncher<Intent> launcher;
    public static final int REQUEST_CONTACT = 1;
    public static final String ARG_PROPERTY_ID = "arg_property_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_EDIT = "DialogEdit";
    public static final String REQUEST_DATE = "0";
    public static final String REQUEST_EDIT = "1";
    //List<Property> properties;

    public  PropertyDetailsFragment newInstance(UUID propertyId, List<Property> properties){
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROPERTY_ID, propertyId);
        args.putParcelableArrayList("properties", (ArrayList<? extends Parcelable>) properties);
        //this.properties=properties;
        //System.out.println("properties in constructor loaded"+this.properties.get(0).getPropertyName());
        PropertyDetailsFragment fragment = new PropertyDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID propertyId = (UUID)getArguments().getSerializable(ARG_PROPERTY_ID);
        List<Property> properties= getArguments().getParcelableArrayList("properties");
        //System.out.println("propertyid from Main ++++++++"+propertyId);
        System.out.println("properties inside oncreate ***********"+properties.get(0).getPropertyName());
        //TODO:Update the Property Lab with initial dummy data
        for(Property p:properties)
        {
            if(p.getPropertyId().equals(propertyId))
            {
                property = p;
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_property, container, false);
        propertyIdText = (TextView) v.findViewById(R.id.property_id);
        propertyIdText.setText(property.getPropertyId().toString());
        propertyNameText = (TextView) v.findViewById(R.id.property_name);
        propertyNameText.setText(property.getPropertyName());
        propertyDescriptionText = (TextView) v.findViewById(R.id.property_description);
        propertyDescriptionText.setText(property.getPropertyDescription());
        propertyOwnerText = (TextView) v.findViewById(R.id.property_owner_name);
        propertyOwnerText.setText(property.getOwner());
        propertyOwnerEmailText = (TextView) v.findViewById(R.id.property_owner_email);
        propertyOwnerEmailText.setText(property.getOwnerEMail());
        availabilityDateBtn = (Button)v.findViewById(R.id.availability_date);
        availabilityDateBtn.setText(property.getAvailabilityDate().toString());
        availabilityDateBtn.setOnClickListener(buttonClick);
        editPropertyBtn = (Button)v.findViewById(R.id.edit_property);
        editPropertyBtn.setOnClickListener(buttonClick);

        mOwnerButton = (Button)v.findViewById(R.id.property_owner);
        mOwnerButton.setOnClickListener(ownerButtonClick);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == REQUEST_CONTACT) {
                        Intent data = result.getData();
                        Uri contactUri = data.getData();
                        String[] queryFields = new String[] {
                                ContactsContract.Contacts.DISPLAY_NAME
                        };
                        assert contactUri != null;
                        try (Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null)) {
                            if (c.getCount() == 0){
                                return;
                            }

                            c.moveToFirst();
                            String owner = c.getString(0);
                            property.setOwner(owner);
                            mOwnerButton.setText(owner);
                        }
                    }
                }
        );
        updateDate();
        return v;
    }

    private void updateDate() {
        availabilityDateBtn.setText(property.getAvailabilityDate().toString());
    }

    public void updatePropertyData(){
        propertyNameText.setText(property.getPropertyName());
        propertyDescriptionText.setText(property.getPropertyDescription());
        propertyOwnerText.setText(property.getOwner());
        propertyOwnerEmailText.setText(property.getOwnerEMail());
    }

    private final View.OnClickListener ownerButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            launcher.launch(pickContact);
        }
    };


    private final View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager manager = requireActivity()
                    .getSupportFragmentManager();
            if (v.getId() == R.id.availability_date) {
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(property.getAvailabilityDate());
                manager.setFragmentResultListener(REQUEST_DATE, PropertyDetailsFragment.this, fragmentResultListener);
                dialog.show(manager, DIALOG_DATE);
            }else if (v.getId() == R.id.edit_property){
                List<Property> properties= getArguments().getParcelableArrayList("properties");
                PropertyEditFragment dialog = PropertyEditFragment.newInstance(property.getPropertyId(), properties);
                manager.setFragmentResultListener(REQUEST_EDIT, PropertyDetailsFragment.this, fragmentResultListener);
                dialog.show(manager, DIALOG_EDIT);
            }
        }
    };

    private final FragmentResultListener fragmentResultListener = new FragmentResultListener() {
        @Override
        public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
            if (REQUEST_DATE.equalsIgnoreCase(requestKey)){
                Date date = (Date) result
                        .getSerializable(DatePickerFragment.EXTRA_DATE);
                property.setAvailabilityDate(date);
                updateDate();
            }else if (REQUEST_EDIT.equalsIgnoreCase(requestKey)){
                String propertyName = result.getString(PropertyEditFragment.ARG_PROPERTY_NAME);
                String propertyDescription = result.getString(PropertyEditFragment.ARG_PROPERTY_DESCRIPTION);
                String propertyOwnerName = result.getString(PropertyEditFragment.ARG_PROPERTY_OWNER);
                String propertyOwnerEMail = result.getString(PropertyEditFragment.ARG_PROPERTY_OWNER_EMAIL);
                property.setPropertyName(propertyName);
                property.setPropertyDescription(propertyDescription);
                property.setOwner(propertyOwnerName);
                property.setOwnerEMail(propertyOwnerEMail);
                updatePropertyData();
            }
        }
    };
}
