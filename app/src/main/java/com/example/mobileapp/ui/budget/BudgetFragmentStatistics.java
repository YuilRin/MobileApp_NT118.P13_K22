package com.example.mobileapp.ui.budget;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.databinding.FragmentBudgetStatisticsBinding;
import com.example.mobileapp.ui.budget.Custom.Debt;
import com.example.mobileapp.ui.budget.Custom.DebtAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BudgetFragmentStatistics extends Fragment {

    private RecyclerView recyclerView;
    private DebtAdapter debtAdapter;
    private List<Debt> debtList;
    private TextView tabNo;
    private TextView tabKhoanThu, tvNgayHienTai;
    private AppCompatButton btnDuyetAll, btnBoDuyetAll, btnThem;

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        FragmentBudgetStatisticsBinding binding = FragmentBudgetStatisticsBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_budget_statistics, container, false);
        // Khởi tạo RecyclerView

        //recyclerView = binding.recyclerView;
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tabNo = view.findViewById(R.id.tab_no);
        tabKhoanThu = view.findViewById(R.id.tab_lich_su);

        // hien thi ngay

        tvNgayHienTai = view.findViewById(R.id.tv_NgayHienTai);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm      dd/MM/yyyy", Locale.getDefault());
        String NgayHienTai = sdf.format(new Date());
        tvNgayHienTai.setText( "Thời gian: " + NgayHienTai);


        // Đặt mặc định tab "Nợ" là tab được chọn
        selectTab(tabNo);

        // Xử lý khi nhấn vào tab "Nợ"
        tabNo.setOnClickListener(v -> selectTab(tabNo));

        // Xử lý khi nhấn vào tab "Lịch Sử"
        tabKhoanThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(tabKhoanThu);
            }
        });

        btnDuyetAll = view.findViewById(R.id.btn_Duyet);
        btnBoDuyetAll = view.findViewById(R.id.btnBoDuyet);
        btnThem = view.findViewById(R.id.btn_Them);

        debtList = new ArrayList<>();
        debtList.add(new Debt("Cafe cùng bạn bè", "100.000 đ", "Nguyen Van A", "07/10/2024", "08/10/2024", false, false, null));
        debtList.add(new Debt("Cafe cùng bạn bè", "100.000 đ", "Nguyen Van A", "07/10/2024", "08/10/2024", false, false, null));
        debtList.add(new Debt("Cafe cùng bạn bè", "100.000 đ", "Nguyen Van A", "07/10/2024", "08/10/2024", false, false, null));
        debtList.add(new Debt("Cafe cùng bạn bè", "100.000 đ", "Nguyen Van A", "07/10/2024", "23/12/2024", false, false, null));


        // Khởi tạo Adapter và set cho RecyclerView
        debtAdapter = new DebtAdapter(getContext(), debtList, (position, debt) ->
            showEditDebtDialog(position, debt) );
        recyclerView.setAdapter(debtAdapter);

        // Su kien bam nut duyet
        btnDuyetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //duyet
                for (Debt debt : debtList) {
                    if (debt.isSlected)
                    {
                        // chuyen doi tuong sang da tra
                        debt.daTra = true;
                        // cap nhat ngay
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                        debt.NgayTra = sdf.format(new Date());

                        debt.setSelected(false);
                    }
                }
                debtAdapter.notifyDataSetChanged();
            }

        });


        // su kien nut bo duyet
        btnBoDuyetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Debt debt : debtList) {
                    if (debt.isSlected)
                    {
                        debt.daTra = false;
                        debt.setSelected(false);
                    }
                    debtAdapter.notifyDataSetChanged();
                }

            }
        });

        btnThem.setOnClickListener(v -> showThemDiaLog());


        return view;
    }

    private void showThemDiaLog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_debt_personal, null);
        builder.setView(dialogView);


        TextView tvNhanDan = dialogView.findViewById(R.id.tv_NhanDan);
        tvNhanDan.setText("Thêm Nợ");
        EditText etNoiDungNo = dialogView.findViewById(R.id.etNoiDungNo);
        EditText etSoTienNo = dialogView.findViewById(R.id.etSoTienNo);
        EditText etNguoNo = dialogView.findViewById(R.id.etNguonNo);
        EditText etNgayNo = dialogView.findViewById(R.id.etNgayNo);
        EditText etNgayDenHan = dialogView.findViewById(R.id.etNgayDenHan);
        Button btnThem = dialogView.findViewById(R.id.btnUpdate);
        btnThem.setText("Thêm");
        Button btnHuy = dialogView.findViewById(R.id.btnDelete);
        btnHuy.setText("Hủy");

        AlertDialog dialog = builder.create();

        btnThem.setOnClickListener(v -> {
            String noidung = etNoiDungNo.getText().toString();
            String SoTien = etSoTienNo.getText().toString();
            String NguonNo = etNguoNo.getText().toString();
            String NgayNo = etNgayNo.getText().toString();
            String NgayDenHan = etNgayDenHan.getText().toString();

            if (!noidung.isEmpty() && !SoTien.isEmpty() && !NgayNo.isEmpty() && !NgayDenHan.isEmpty() && !NguonNo.isEmpty()) {
                // Thêm khoản nợ mới vào danh sách
                Debt newDebt = new Debt(noidung, SoTien, NguonNo, NgayNo, NgayDenHan, false, false, null);
                debtList.add(newDebt);
                debtAdapter.notifyItemInserted(debtList.size() - 1);

                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        btnHuy.setOnClickListener(view -> {
            dialog.dismiss();
        });

        etNgayNo.setOnClickListener(v ->{
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(getContext(), (View, year, month, dayofmonth) -> {
                String ChonNgay = dayofmonth + "/" + (month + 1) + "/" + year;
                etNgayNo.setText(ChonNgay);
            }, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
        });

        etNgayDenHan.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(getContext() , (View, year, month, dayofmonth) -> {
                String ChonNgay = dayofmonth + "/" + (month + 1) + "/" + year;
                etNgayDenHan.setText(ChonNgay);
            }, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();

        });


        dialog.show();

    }


    private void showEditDebtDialog(int position, Debt debt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_debt_personal, null);
        builder.setView(dialogView);

        EditText etNoiDungNo = dialogView.findViewById(R.id.etNoiDungNo);
        EditText etSoTienNo = dialogView.findViewById(R.id.etSoTienNo);
        EditText etNguonNo = dialogView.findViewById(R.id.etNguonNo);
        EditText etNgayNo = dialogView.findViewById(R.id.etNgayNo);
        EditText etNgayDenHan = dialogView.findViewById(R.id.etNgayDenHan);
        Button btnSua = dialogView.findViewById(R.id.btnUpdate);
        Button btnXoa = dialogView.findViewById(R.id.btnDelete);


        // Gán giá trị ban đầu
        etNoiDungNo.setText(debt.getTitle());
        etSoTienNo.setText(debt.getSoTien());
        etNgayNo.setText(debt.getNgayNo());
        etNgayDenHan.setText(debt.getNgayDenHan());
        etNguonNo.setText(debt.getNguonNo());

        AlertDialog dialog = builder.create();

        btnSua.setOnClickListener(v -> {
            // Cập nhật đối tượng Debt
            debt.title = etNoiDungNo.getText().toString();
            debt.SoTien = etSoTienNo.getText().toString();
            debt.NguonNo = etNguonNo.getText().toString();
            debt.NgayNo = etNgayNo.getText().toString();
            debt.NgayDenHan = etNgayDenHan.getText().toString();

            debtAdapter.notifyItemChanged(position); // Cập nhật lại RecyclerView
            dialog.dismiss();
        });

        btnXoa.setOnClickListener(v -> {
          new AlertDialog.Builder(getContext())
                  .setTitle("Xóa khoản nợ")
                  .setMessage("Bạn có chắc muốn xóa khoản nợ này không?")
                  .setPositiveButton("Xóa", (dialogInterface, i) -> {
                      //xoas
                      debtList.remove(position);
                      debtAdapter.notifyItemRemoved(position);
                      dialog.dismiss();
                  })
                  .setNegativeButton("Hủy", (dialogInterface, i) ->
                      dialogInterface.dismiss()).show();
        });

        etNgayNo.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view, year, month, dayofmonth) -> {
                String ChonNgay = dayofmonth + "/" + (month + 1) + "/" + year;
                etNgayNo.setText(ChonNgay);
            }, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
        });

        etNgayDenHan.setOnClickListener(v ->
        {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view, year, month, dayofmonth) -> {
                String ChonNgay = dayofmonth + "/" + (month + 1) + "/" + year;
                etNgayDenHan.setText(ChonNgay);
            }, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
        });


        dialog.show();
    }

    private void selectTab(TextView selectedTab) {
        // Thay đổi giao diện cho tab được chọn
        Drawable selectedBackground = ContextCompat.getDrawable(getContext(), R.drawable.tab_selected_background);
        int currentPaddingLeft = selectedTab.getPaddingLeft();
        int currentPaddingTop = selectedTab.getPaddingTop();
        int currentPaddingRight = selectedTab.getPaddingRight();
        int currentPaddingBottom = selectedTab.getPaddingBottom();
        if (selectedTab == tabNo) {
            tabNo.setBackground(selectedBackground);
            tabKhoanThu.setBackground(null); // Không có nền để tab này không nổi bật

            tabNo.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            tabKhoanThu.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
        } else if (selectedTab == tabKhoanThu) {
            tabKhoanThu.setBackground(selectedBackground);
            tabNo.setBackground(null); // Không có nền để tab này không nổi bật

            tabKhoanThu.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            tabNo.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
        }
        selectedTab.setPadding(currentPaddingLeft, currentPaddingTop, currentPaddingRight, currentPaddingBottom);
    }


}
