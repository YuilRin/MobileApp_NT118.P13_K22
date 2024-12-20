package com.example.mobileapp.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mobileapp.Class.BusinessProvider;
import com.example.mobileapp.R;

import java.util.List;

public class BusinessProviderAdapter extends ArrayAdapter<BusinessProvider> {

    public BusinessProviderAdapter(Context context, List<BusinessProvider> providers) {
        super(context, 0, providers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.business_provider_item, parent, false);
        }

        BusinessProvider provider = getItem(position);

        TextView providerId = convertView.findViewById(R.id.provider_id);
        TextView providerName = convertView.findViewById(R.id.provider_name);
        TextView providerProductId = convertView.findViewById(R.id.provider_product_number);
        TextView providerPhone = convertView.findViewById(R.id.provider_phone);
        TextView providerHead = convertView.findViewById(R.id.provider_head);
        TextView providerEmail = convertView.findViewById(R.id.provider_email);
        TextView providerDate = convertView.findViewById(R.id.provider_date);
        TextView providerStatus = convertView.findViewById(R.id.provider_status);
        TextView providerNote = convertView.findViewById(R.id.provider_note);

        providerId.setText(provider.getProviderId());
        providerName.setText(provider.getProviderName());
        providerProductId.setText("Số sp cc: " + provider.getProviderProductId());
        providerPhone.setText("Số điện thoại: " + provider.getProviderPhone());
        providerHead.setText("Đại diện: " + provider.getProviderHead());
        providerEmail.setText("Email: " + provider.getProviderEmail());
        providerDate.setText("Ngày hợp tác: " + provider.getProviderDate());
        providerStatus.setText("Tình trạng: " + provider.getProviderStatus());
        providerNote.setText("Ghi chú: " + provider.getProviderNote());

        return convertView;
    }
}
