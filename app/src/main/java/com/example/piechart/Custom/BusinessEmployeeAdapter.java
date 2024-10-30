package com.example.piechart.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.piechart.Class.BusinessEmployee;
import com.example.piechart.R;

import java.util.List;

public class BusinessEmployeeAdapter extends ArrayAdapter<BusinessEmployee> {

    public BusinessEmployeeAdapter(Context context, List<BusinessEmployee> employees) {
        super(context, 0, employees);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.business_employee_item, parent, false);
        }

        BusinessEmployee emp = getItem(position);

        TextView employeeId = convertView.findViewById(R.id.employee_id);
        TextView employeeName = convertView.findViewById(R.id.employee_name);
        TextView employeeRoom = convertView.findViewById(R.id.employee_room);
        TextView employeeRank = convertView.findViewById(R.id.employee_rank);
        TextView employeePhone = convertView.findViewById(R.id.employee_phone);
        TextView employeeEmail = convertView.findViewById(R.id.employee_email);
        TextView employeeType = convertView.findViewById(R.id.employee_type);
        TextView employeeStatus = convertView.findViewById(R.id.employee_status);
        TextView employeeEvaluation = convertView.findViewById(R.id.employee_date);
        TextView employeeNote = convertView.findViewById(R.id.employee_note);

        employeeId.setText(emp.getEmployeeId());
        employeeName.setText(emp.getEmployeeName());
        employeeRoom.setText("Bộ phận: " + emp.getEmployeeRoom());
        employeeRank.setText("Cấp: " + emp.getEmployeeRank());
        employeePhone.setText("SĐT: " + emp.getEmployeePhone());
        employeeEmail.setText("Email: " + emp.getEmployeeEmail());
        employeeType.setText("Loại: " + emp.getEmployeeType());
        employeeStatus.setText("Tình trạng: " + emp.getEmployeeStatus());
        employeeEvaluation.setText("Đánh giá: " + emp.getEmployeeEvaluation());
        employeeNote.setText("Ghi chú: " + emp.getEmployeeNote());

        return convertView;
    }
}
