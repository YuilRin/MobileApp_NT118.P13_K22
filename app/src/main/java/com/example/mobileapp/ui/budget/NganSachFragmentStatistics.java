package com.example.mobileapp.ui.budget;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.databinding.FragmentBudgetStatisticsBinding;
import com.example.mobileapp.ui.budget.Custom.ThuNo;
import com.example.mobileapp.ui.budget.Custom.ThuNoAdapter;
import com.example.mobileapp.ui.budget.Custom.ThuNoViewModel;
import com.example.mobileapp.ui.budget.Custom.ImageSelectDebtAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NganSachFragmentStatistics extends Fragment {

    private FragmentBudgetStatisticsBinding binding;
    private RecyclerView recyclerViewKhoanNo;
    private RecyclerView recylerKhoanThu;
    private ThuNoAdapter thuNoAdapterKhoanNo;
    private ThuNoAdapter thuNoAdapterKhoanThu;
    private TextView tabNo;
    private TextView tabKhoanThu, tvNgayHienTai, tvTieuDe;
    private AppCompatButton btnDuyetAll, btnBoDuyetAll, btnThem;
    private ThuNoViewModel thuNoViewModel;
    private EditText ThanhTimKiem;

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
        binding = FragmentBudgetStatisticsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Khởi tạo ViewModel
        thuNoViewModel = new ViewModelProvider(this).get(ThuNoViewModel.class);
        ThanhTimKiem = view.findViewById(R.id.search_edit_text);


        recyclerViewKhoanNo = view.findViewById(R.id.recycler_view);
        recyclerViewKhoanNo.setLayoutManager(new LinearLayoutManager(getContext()));

        recylerKhoanThu = view.findViewById(R.id.recycler_KhoanThu);
        recylerKhoanThu.setLayoutManager(new LinearLayoutManager(getContext()));

        tabNo = view.findViewById(R.id.tab_no);
        tabKhoanThu = view.findViewById(R.id.tab_lich_su);

        // hien thi ngay

        tvNgayHienTai = view.findViewById(R.id.tv_NgayHienTai);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm      dd/MM/yyyy", Locale.getDefault());
        String NgayHienTai = sdf.format(new Date());
        tvNgayHienTai.setText( "Thời gian: " + NgayHienTai);

        tvTieuDe= view.findViewById(R.id.DanhSachNo);

        btnDuyetAll = view.findViewById(R.id.btn_Duyet);
        btnBoDuyetAll = view.findViewById(R.id.btnBoDuyet);
        btnThem = view.findViewById(R.id.btn_Them);


        // Khởi tạo Adapter và set cho RecyclerView
        thuNoAdapterKhoanNo = new ThuNoAdapter(getContext(), new ArrayList<>(), (position, thuNo) ->
            showEditDebtDialog(thuNo.getDDocId(), position, thuNo, "no") , true);
        recyclerViewKhoanNo.setAdapter(thuNoAdapterKhoanNo);


        thuNoAdapterKhoanThu = new ThuNoAdapter(getContext(), new ArrayList<>(), ((position, thuNo) ->
                showEditDebtDialog(thuNo.getDDocId(), position, thuNo, "khoan_thu")), false);
        recylerKhoanThu.setAdapter(thuNoAdapterKhoanThu);

        // Đặt mặc định tab "Nợ" là tab được chọn
        ChonTab(tabNo);

        // Xử lý khi nhấn vào tab "Nợ"
        tabNo.setOnClickListener(v -> ChonTab(tabNo));

        // Xử lý khi nhấn vào tab "Khoản Thu"
        tabKhoanThu.setOnClickListener(v -> ChonTab(tabKhoanThu));

        // Su kien bam nut duyet
        btnDuyetAll.setOnClickListener(v -> ChapNhanDuyet());

        // su kien nut bo duyet
        btnBoDuyetAll.setOnClickListener(v -> HuyBoChapNhan());


        thuNoViewModel.getDebtListNo().observe(getViewLifecycleOwner(), debts -> {
            thuNoAdapterKhoanNo.updateDebtList(debts); // Cập nhật RecyclerView khi dữ liệu thay đổi

        });


        thuNoViewModel.getDebtListKhoanThu().observe(getViewLifecycleOwner(), debts -> {
            thuNoAdapterKhoanThu.updateDebtList(debts);

        });

        List<ThuNo> capnhaThuNoNo = thuNoViewModel.getDebtListNo().getValue();
        List<ThuNo> CapNhatDetThu = thuNoViewModel.getDebtListKhoanThu().getValue();

        CapNhatTrangThai(true, capnhaThuNoNo);
        CapNhatTrangThai(false, CapNhatDetThu);

        btnThem.setOnClickListener(v -> showThemDiaLog());

        ThanhTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String truyvan = editable.toString().trim().toLowerCase(Locale.getDefault());

                //kiem tra tab dang hien thi
                boolean isDebtab = (recyclerViewKhoanNo.getVisibility() == TextView.VISIBLE);
                List<ThuNo> DanhsachHienTai = isDebtab ? thuNoViewModel.getDebtListNo().getValue() : thuNoViewModel.getDebtListKhoanThu().getValue();
                ThuNoAdapter AdapterHienTai = isDebtab ? thuNoAdapterKhoanNo : thuNoAdapterKhoanThu;
                List<ThuNo> DanhSachLoc = new ArrayList<>();


                List<ThuNo> LocKhoanNo = new ArrayList<>();
                for(ThuNo thuNo : DanhsachHienTai) {
                    if (thuNo.getTitle().toLowerCase(Locale.getDefault()).contains(truyvan)) {
                        DanhSachLoc.add(thuNo);
                    }
                }
                AdapterHienTai.updateDebtList(DanhSachLoc);

            }
        });



        return view;
    }

    private void CapNhatTrangThai(boolean debtno,List<ThuNo> thuNos) {
        if(debtno)
        {
            for (ThuNo thuNo : thuNos) {
                thuNoViewModel.CapNhatDebtNoLenFireBase(thuNo.getDDocId(), thuNo);
            }
        }else {
            for (ThuNo thuNo : thuNos) {
                thuNoViewModel.CapNhatDebtNoLenFireBase(thuNo.getDDocId(), thuNo);
            }
        }
    }

    private void ChapNhanDuyet() {
        boolean isDebtTab = recyclerViewKhoanNo.getVisibility() == View.VISIBLE;
        List<ThuNo> currentList = isDebtTab ? thuNoViewModel.getDebtListNo().getValue() : thuNoViewModel.getDebtListKhoanThu().getValue();
        ThuNoAdapter AdapterHienTai = isDebtTab ? thuNoAdapterKhoanNo : thuNoAdapterKhoanThu;

        if (currentList == null || AdapterHienTai == null) {
            Toast.makeText(getContext(), "Dữ liệu không khả dụng", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean anySelected = false;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        String ngayTra;

        for (ThuNo thuNo : currentList) {
            if (thuNo != null && thuNo.isSelected()) {
                try {
                    ngayTra = sdf.format(new Date());
                    thuNo.setNgayTra(ngayTra); // Cập nhật ngày trả
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Không thể định dạng ngày cho khoản nợ: " + thuNo.getTitle(), Toast.LENGTH_SHORT).show();
                    continue; // Tiếp tục xử lý các mục khác
                }

                thuNo.setDaTra(true); // Đánh dấu là đã trả
                thuNo.setSelected(false); // Bỏ chọn

                // Cập nhật Firestore
                if (thuNo.getDDocId() != null) {
                    if (isDebtTab) {
                        thuNoViewModel.CapNhatDebtNoLenFireBase(thuNo.getDDocId(), thuNo);
                    } else {
                        thuNoViewModel.CapNhatDebtKhoanThuLenFireBase(thuNo.getDDocId(), thuNo);
                    }
                }

                anySelected = true;
            }
        }

        AdapterHienTai.notifyDataSetChanged();

        if (anySelected) {
            Toast.makeText(getContext(), "Đã duyệt các khoản nợ đã chọn", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Không có khoản nợ nào được chọn để duyệt", Toast.LENGTH_SHORT).show();
        }
    }


    private void HuyBoChapNhan() {
        boolean isDebtTab = recyclerViewKhoanNo.getVisibility() == View.VISIBLE;
        List<ThuNo> currentList = isDebtTab ? thuNoViewModel.getDebtListNo().getValue() : thuNoViewModel.getDebtListKhoanThu().getValue();
        ThuNoAdapter currentAdapter = isDebtTab ? thuNoAdapterKhoanNo : thuNoAdapterKhoanThu;

        if (currentList == null) return;

        boolean anySelected = false;
        for (ThuNo thuNo : currentList) {
            if (thuNo.isSelected()) {
                thuNo.setDaTra(false);
                thuNo.setSelected(false);
                if( isDebtTab ) thuNoViewModel.CapNhatDebtNoLenFireBase(thuNo.getDDocId(), thuNo);
                else thuNoViewModel.CapNhatDebtKhoanThuLenFireBase(thuNo.getDDocId(), thuNo);
                anySelected = true;
            }
        }

        currentAdapter.notifyDataSetChanged();

        if (anySelected) {
            Toast.makeText(getContext(), "Đã bỏ duyệt các khoản nợ đã chọn", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Không có khoản nợ nào được chọn để bỏ duyệt", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void ChonTab(TextView chontab) {
        Drawable selectedBackground = ContextCompat.getDrawable(getContext(), R.drawable.tab_selected_background);
        Drawable unselectedBackground = ContextCompat.getDrawable(getContext(), R.drawable.tab_unselected_background);

        if (chontab == tabNo) {
            // sua Noi dung tie de
            tvTieuDe.setText("Danh sách nợ");

            tabNo.setBackground(selectedBackground);
            tabKhoanThu.setBackground(unselectedBackground);

            tabNo.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            tabKhoanThu.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));

            // Hiển thị RecyclerView "Nợ" và ẩn "Khoản Thu"
            recyclerViewKhoanNo.setVisibility(View.VISIBLE);
            recylerKhoanThu.setVisibility(View.GONE);


        } else if (chontab == tabKhoanThu) {
            // sua Noi dung tie de
            tvTieuDe.setText("Danh sách khoản thu");

            tabKhoanThu.setBackground(selectedBackground);
            tabNo.setBackground(unselectedBackground);

            tabKhoanThu.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            tabNo.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));

            recylerKhoanThu.setVisibility(View.VISIBLE);
            recyclerViewKhoanNo.setVisibility(View.GONE);
        }

    }

    private Integer ChonAnhDeLuu = null;

    private void showThemDiaLog() {
        boolean isDebtTab  = recyclerViewKhoanNo.getVisibility() == View.VISIBLE;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_debt_personal, null);
        builder.setView(dialogView);


        TextView tvNhanDan = dialogView.findViewById(R.id.tv_NhanDan);
        tvNhanDan.setText("Thêm " + (isDebtTab ? "Nợ" : "Khoản Thu"));

        AppCompatButton btnChonAnh = dialogView.findViewById(R.id.btn_ChonAnh);
        ImageView imv_ShowAnh = dialogView.findViewById(R.id.imv_show_image);

        EditText etNoiDungNo = dialogView.findViewById(R.id.etNoiDungNo);
        EditText etSoTienNo = dialogView.findViewById(R.id.etSoTienNo);
        EditText etNguonNo = dialogView.findViewById(R.id.etNguonNo);
        EditText etNgayNo = dialogView.findViewById(R.id.etNgayNo);
        EditText etNgayDenHan = dialogView.findViewById(R.id.etNgayDenHan);
        Button btnThem = dialogView.findViewById(R.id.btnUpdate);
        btnThem.setText("Thêm");
        Button btnHuy = dialogView.findViewById(R.id.btnDelete);
        btnHuy.setText("Hủy");

        // Điều chỉnh giao diện theo tab
        if (!isDebtTab) {
            etNoiDungNo.setHint("Nội dung khoản thu");
            etSoTienNo.setHint("Số tiền khoản thu");
            etNguonNo.setHint("Nguồn thu");
            etNgayNo.setHint("Ngày bắt đầu");
            etNgayDenHan.setHint("Ngày hết hạn");
        }


        AlertDialog dialog = builder.create();


        btnChonAnh.setOnClickListener(v -> {
            ShowChonAnhDialog(imageResId -> {
                imv_ShowAnh.setBackgroundResource(imageResId);
                ChonAnhDeLuu = imageResId;
            });
        });


        btnThem.setOnClickListener(view -> {
            String noidung = etNoiDungNo.getText().toString().trim();
            String SoTien = etSoTienNo.getText().toString().trim();
            String NguonNo = etNguonNo.getText().toString().trim();
            String NgayNo = etNgayNo.getText().toString().trim();
            String NgayDenHan = etNgayDenHan.getText().toString().trim();

            if(!noidung.isEmpty() && !SoTien.isEmpty() && !NguonNo.isEmpty() && !NgayNo.isEmpty() && !NgayDenHan.isEmpty() && ChonAnhDeLuu != null)
            {
                //
                ThuNo newThuNo = new ThuNo(ChonAnhDeLuu, noidung, SoTien, NguonNo, NgayNo, NgayDenHan, false, false, null);
                if (isDebtTab) {
                    thuNoViewModel.addDebtNo(newThuNo);
                } else {
                    thuNoViewModel.addDebtKhoanThu(newThuNo);
                }
                Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            } else {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            }

        });


        btnHuy.setOnClickListener(view -> {
            dialog.dismiss();
        });

        etNgayNo.setOnClickListener(v -> showDatePickerDialog(etNgayNo) );
        etNgayDenHan.setOnClickListener(v -> showDatePickerDialog(etNgayDenHan));



        dialog.show();

    }

    public interface GoiLaiLuaChonAnh {
        void DaLuaChonAnh(int imageResId);
    }


    private void ShowChonAnhDialog(GoiLaiLuaChonAnh callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogVew = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_image_debt, null);
        builder.setView(dialogVew);

        AlertDialog dialog = builder.create();

        RecyclerView recyclerView = dialogVew.findViewById(R.id.recycler_image_debt_select);
        Button btnQuayLai = dialogVew.findViewById(R.id.btnQuayLai);

        btnQuayLai.setOnClickListener(v -> dialog.dismiss());

        List<Integer> DanhSachHinhAnh = Arrays.asList(
                R.drawable.ic_badge,
                R.drawable.ic_cancelation,
                R.drawable.ic_online_learning,
                R.drawable.ic_coffee,
                R.drawable.ic_giftbox,
                R.drawable.ic_traffic_jam,
                R.drawable.ic_event,
                R.drawable.ic_eat,
                R.drawable.ic_working_time,
                R.drawable.ic_health_insurance,
                R.drawable.ic_performance,
                R.drawable.ic_pay, R.drawable.ic_taxes, R.drawable.ic_social_security, R.drawable.ic_tax_paper,
                R.drawable.ic_regulation

        );



        ImageSelectDebtAdapter imgdapter = new ImageSelectDebtAdapter(getContext(), DanhSachHinhAnh);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(imgdapter);

        imgdapter.setOnImageClickListener(imageResid -> {
            callback.DaLuaChonAnh(imageResid);
            dialog.dismiss();
        });

        dialog.show();

    }



    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
            editText.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    private void showEditDebtDialog(String docID, int position, ThuNo thuNo, String Type) {
        boolean isDebtTab = recyclerViewKhoanNo.getVisibility() == View.VISIBLE;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_debt_personal, null);
        builder.setView(dialogView);

        // Khởi tạo các View
        ImageView imageView = dialogView.findViewById(R.id.imv_show_image);
        EditText etNoiDungNo = dialogView.findViewById(R.id.etNoiDungNo);
        EditText etSoTienNo = dialogView.findViewById(R.id.etSoTienNo);
        EditText etNguonNo = dialogView.findViewById(R.id.etNguonNo);
        EditText etNgayNo = dialogView.findViewById(R.id.etNgayNo);
        EditText etNgayDenHan = dialogView.findViewById(R.id.etNgayDenHan);
        Button btnSua = dialogView.findViewById(R.id.btnUpdate);
        Button btnXoa = dialogView.findViewById(R.id.btnDelete);
        AppCompatButton chonAnhDaiDien = dialogView.findViewById(R.id.btn_ChonAnh);

        // Điều chỉnh giao diện theo tab
        if (!isDebtTab) {
            etNoiDungNo.setHint("Nội dung khoản thu");
            etSoTienNo.setHint("Số tiền khoản thu");
            etNguonNo.setHint("Nguồn thu");
            etNgayNo.setHint("Ngày bắt đầu");
            etNgayDenHan.setHint("Ngày hết hạn");
        }

        // Gán giá trị ban đầu từ Debt
        if (thuNo != null) {
            imageView.setBackgroundResource(thuNo.getImgeResId());
            etNoiDungNo.setText(thuNo.getTitle());
            etSoTienNo.setText(thuNo.getSoTien());
            etNguonNo.setText(thuNo.getNguonNo());
            etNgayNo.setText(thuNo.getNgayNo());
            etNgayDenHan.setText(thuNo.getNgayDenHan());
        }

        AlertDialog dialog = builder.create();

        // Xử lý chọn ảnh đại diện
        chonAnhDaiDien.setOnClickListener(v -> {
            ShowChonAnhDialog(imageResId -> {
                imageView.setImageResource(imageResId);
                thuNo.setImgeResId(imageResId);
            });
        });

        // Xử lý nút Sửa
        btnSua.setOnClickListener(v -> {
            String noidung = etNoiDungNo.getText().toString().trim();
            String soTien = etSoTienNo.getText().toString().trim();
            String nguonNo = etNguonNo.getText().toString().trim();
            String ngayNo = etNgayNo.getText().toString().trim();
            String ngayDenHan = etNgayDenHan.getText().toString().trim();

            if (!noidung.isEmpty() && !soTien.isEmpty() && !nguonNo.isEmpty() && !ngayNo.isEmpty() && !ngayDenHan.isEmpty()) {
                thuNo.setTitle(noidung);
                thuNo.setSoTien(soTien);
                thuNo.setNguonNo(nguonNo);
                thuNo.setNgayNo(ngayNo);
                thuNo.setNgayDenHan(ngayDenHan);

                if (Type.equals("no")) {
                    thuNoViewModel.updateDebtNo(position, thuNo);
                    thuNoAdapterKhoanNo.notifyItemChanged(position);
                    thuNoViewModel.CapNhatDebtNoLenFireBase(docID, thuNo);
                } else {
                    thuNoViewModel.updateDebtKhoanThu(position, thuNo);
                    thuNoAdapterKhoanThu.notifyItemChanged(position);
                    thuNoViewModel.CapNhatDebtKhoanThuLenFireBase(docID, thuNo);
                }

                Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút Xóa
        btnXoa.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Xóa khoản nợ")
                    .setMessage("Bạn có chắc muốn xóa khoản nợ này không?")
                    .setPositiveButton("Xóa", (dialogInterface, i) -> {
                        if (Type.equals("no")) {
                            thuNoViewModel.removeDebtNo(position);
                            thuNoAdapterKhoanNo.notifyItemRemoved(position);
                        } else {
                            thuNoViewModel.removeDebtKhoanThu(position);
                            thuNoAdapterKhoanThu.notifyItemRemoved(position);
                        }
                        Toast.makeText(getContext(), "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Hủy", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        });

        // Xử lý chọn ngày
        etNgayNo.setOnClickListener(v -> showDatePickerDialog(etNgayNo));
        etNgayDenHan.setOnClickListener(v -> showDatePickerDialog(etNgayDenHan));

        dialog.show();
    }

}
