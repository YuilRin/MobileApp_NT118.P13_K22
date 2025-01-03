package com.example.mobileapp.ui.budget;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapp.R;
import com.example.mobileapp.databinding.FragmentBudgetOverviewBinding;
import com.example.mobileapp.ui.budget.Custom.DanhMucItem;
import com.example.mobileapp.ui.budget.Custom.NganSachViewModel;
import com.example.mobileapp.ui.budget.Custom.ImageSelectDebtAdapter;
import com.example.mobileapp.ui.budget.Custom.SalaryAdapter;
import com.example.mobileapp.ui.budget.Custom.SalaryItem;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class NganSachFragmentOverView extends Fragment {
    private FragmentBudgetOverviewBinding binding;
    private RecyclerView recyclerViewThuNhap, recyclerViewChiTieu;
    private SalaryAdapter adapterThuNhap;
    private SalaryAdapter adapterChiTieu;
    private TextView tvTongThuNhap, tvTongChiPhi;
    private NganSachViewModel nganSachViewModel;
    private Button ThemNganSach;
    private Spinner spinnerPhanLoai, spinnerThuChi;
    private RadioButton radioChonPhanLoai, radioTaoPhanLoai;
    private EditText edt_NhapPhanLoai, edt_NoiDung, edt_SoTien;
    private TextView tv_TongThuNhap, tv_TongHaoPhi, tv_SoDuHienTai, Tv_DenCuoiThang, tv_SoTienCoThe, tv_TrangThai;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {

        binding = FragmentBudgetOverviewBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        nganSachViewModel = new ViewModelProvider(this).get(NganSachViewModel.class);

        tvTongThuNhap = root.findViewById(R.id.TvTongThuNhap);
        tvTongChiPhi = root.findViewById(R.id.TvTongChiPhi);
        ThemNganSach = root.findViewById(R.id.add_budget_button);
        recyclerViewThuNhap = root.findViewById(R.id.recyclerView);
        recyclerViewChiTieu = root.findViewById(R.id.recyclerViewExpenditure);
        tv_TongThuNhap = root.findViewById(R.id.total_income);
        tv_TongHaoPhi = root.findViewById(R.id.total_expenses);
        tv_SoDuHienTai = root.findViewById(R.id.current_balance);
        Tv_DenCuoiThang = root.findViewById(R.id.days_left);
        tv_SoTienCoThe = root.findViewById(R.id.SoTienCoThe);
        tv_TrangThai = root.findViewById(R.id.TrangThai);


        recyclerViewThuNhap.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChiTieu.setLayoutManager(new LinearLayoutManager(getContext()));

        adapterThuNhap = new SalaryAdapter();
        adapterChiTieu = new SalaryAdapter();

        HienThiTongQuan();
        recyclerViewThuNhap.setAdapter(adapterThuNhap);
        recyclerViewChiTieu.setAdapter(adapterChiTieu);

        tvTongThuNhap.setTextColor(0xFF85C88A);

        adapterThuNhap.setOnChildLongClickListener(((item, VitriCha, VitriCon) -> {
            showChinhSuaThuNhapDialog(item, VitriCha, VitriCon);
        }));

        adapterChiTieu.setOnChildLongClickListener(((item, VitriCha, VitriCon) -> {
            showChinhSuaChiTieuDialog(item, VitriCha, VitriCon);
        }));

        adapterChiTieu.setLangNgheClickCha(((item, vitriCha) -> {
            new AlertDialog.Builder(requireContext())   // Nếu đang ở Fragment
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc muốn thực hiện thao tác này?")
                    .setPositiveButton("OK", (dialog, which) -> {
                        nganSachViewModel.removeChiTieuTrenPhanLoai(item.getMainTitle());
                        nganSachViewModel.updateSalaryItem(false, item);
                        nganSachViewModel.XoaDanhSachPhanLoai(item.getMainTitle());
                        dialog.dismiss(); // đóng dialog
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        }));

        adapterThuNhap.setLangNgheClickCha(((item, vitriCha) -> {
            new AlertDialog.Builder(requireContext())   // Nếu đang ở Fragment
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc muốn thực hiện thao tác này?")
                    .setPositiveButton("OK", (dialog, which) -> {
                        nganSachViewModel.removeThuNhapTrenPhanLoai(item.getMainTitle());
                        nganSachViewModel.removeSalaryItem(true, item);
                        nganSachViewModel.XoaDanhSachPhanLoai(item.getMainTitle());
                        dialog.dismiss(); // đóng dialog
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        }));

        nganSachViewModel.getThuNhapItemsData().observe(getViewLifecycleOwner(), ThuNhapItems ->{
            adapterThuNhap.submitList(new ArrayList<>(ThuNhapItems));
        });

        nganSachViewModel.getChiTieuItemsData().observe(getViewLifecycleOwner(), ChiTieuItems ->{
            adapterChiTieu.submitList(new ArrayList<>(ChiTieuItems));
        });

        nganSachViewModel.gettongTatCaSoTienThuNhap().observe(getViewLifecycleOwner(), Tong -> {
            tvTongThuNhap.setText("Tổng thu nhập: " + dinhDangLaiSoTien(Tong) + " đ");
            HienThiTongQuan();
        });

        nganSachViewModel.gettongTatCaSoTienChiTieu().observe(getViewLifecycleOwner(), Tong -> {
            tvTongChiPhi.setText("Tổng chi phí: " + dinhDangLaiSoTien(Tong) + " đ");
            HienThiTongQuan();
        });

        nganSachViewModel.getDanhSachPhanLoai().observe(getViewLifecycleOwner(), DanhSach->{

        });


        ThemNganSach.setOnClickListener(v-> {
            HienHopNhapThem();
        });


        nganSachViewModel.loadThuNhap();
        nganSachViewModel.loadChiTieu();



        return binding.getRoot();
    }


    private void HienThiTongQuan() {
        tv_TongThuNhap.setText(tvTongThuNhap.getText());
        tv_TongHaoPhi.setText(tvTongChiPhi.getText());

        String SoTienThuNhap = tv_TongThuNhap.getText().toString();
        String SoTienHaoPhi = tv_TongHaoPhi.getText().toString();
        double thuNhapDouble = ChuyenTienSangDouble(SoTienThuNhap);
        double chiPhiDouble = ChuyenTienSangDouble(SoTienHaoPhi);

        // Tính số dư
        double soDu = thuNhapDouble - chiPhiDouble;

        tv_SoDuHienTai.setText("Số dư hiện tại: " + dinhDangLaiSoTien(soDu) + " đ");

        // Tính số ngày còn lại (theo Calendar)
        Calendar c = Calendar.getInstance();
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int daysLeft = daysInMonth - dayOfMonth; // hoặc +1 nếu muốn

        Tv_DenCuoiThang.setText("Số ngày còn lại trong tháng: " + daysLeft);

        String statusKhongThe = "Số dư đã hết, không thể chi tiêu thêm!";
        String statusCoThe = "Bạn vẫn còn đủ ngân sách để chi!";

        if (soDu <= 0)
        {
            tv_SoTienCoThe.setText(dinhDangLaiSoTien(soDu) + " đ");
            tv_SoTienCoThe.setTextColor(0xFFD9534F);
            tv_TrangThai.setText(statusKhongThe);
        }
        else
        {
            tv_SoTienCoThe.setText(dinhDangLaiSoTien(soDu) + " đ");
            tv_SoTienCoThe.setTextColor(0xFF85C88A);
            tv_TrangThai.setText(statusCoThe);
        }
    }

    private double ChuyenTienSangDouble(String Tien) {
        if (Tien == null || Tien.trim().isEmpty()) {
            return 0.0;
        }
        Tien = Tien.replace("Tổng thu nhập:", "")
                .replace("Tổng chi phí:", "")
                .replace("Tổng:", "")            // nếu có thêm các từ khoá khác
                .replace("đ", "")
                .replace(",", "")
                .replace(".", "")
                .trim();

        if (Tien.isEmpty()) {
            return 0.0;
        }

        try {
            return Double.parseDouble(Tien);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    //chinh sua cho thu nhap
    private void showChinhSuaThuNhapDialog(DanhMucItem item, int VitriCha, int VitrCon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_budget_personal, null);
        builder.setView(dialogView);

        TextView tvNhanDan = dialogView.findViewById(R.id.tv_NhanDan);
        Spinner spinnerThuChi = dialogView.findViewById(R.id.Spinner_ThuHayChi);
        Spinner spinnerPhanLoai = dialogView.findViewById(R.id.Spinner_PhanLoai);
        LinearLayout llRadiogroup = dialogView.findViewById(R.id.llRadioGroup);
        Button btn_chonanh = dialogView.findViewById(R.id.btn_ChonAnh);
        ImageView imvshow = dialogView.findViewById(R.id.imv_show_image);
        EditText edt_TenNganSach = dialogView.findViewById(R.id.etTenNganSach);
        EditText edt_Sotien = dialogView.findViewById(R.id.etSoTien);
        Button btnSua = dialogView.findViewById(R.id.btnAdd);
        Button btnXoaPhanLoai = dialogView.findViewById(R.id.btn_XoaPhanLoai);

        AlertDialog dialog = builder.create();
        tvNhanDan.setText("Sửa ngân sách thu nhập");
        llRadiogroup.setVisibility(View.GONE);

        List<String> Thamchieu = new ArrayList<>();
        Thamchieu.add("Đây là thu nhập");
        ArrayAdapter<String> adapterThamChieu = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Thamchieu
        );
        adapterThamChieu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerThuChi.setAdapter(adapterThamChieu);

        SalaryItem thamchieuphanloai = new SalaryItem();
        thamchieuphanloai = nganSachViewModel.getThuNhapItemsData().getValue().get(VitriCha);
        List<String> ThamChieuPhanLoai = new ArrayList<>();
        ThamChieuPhanLoai.add(thamchieuphanloai.getMainTitle());
        ArrayAdapter<String> adapterThamChieuPhanLoai = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                ThamChieuPhanLoai
        );
        adapterThamChieuPhanLoai.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhanLoai.setAdapter(adapterThamChieuPhanLoai);


//        budgetViewModel.loadDanhSachPhanLoaiFirebase();
//
//        budgetViewModel.getDanhSachPhanLoai().observe(getViewLifecycleOwner(), Danhsach ->{
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                    requireContext(),
//                    android.R.layout.simple_spinner_item,
//                    Danhsach
//            );
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinnerPhanLoai.setAdapter(adapter);
//
//        });
//
//        spinnerPhanLoai.setSelection();
//        budgetViewModel.loadDanhSachPhanLoaiFirebase();


        btnXoaPhanLoai.setText("Xóa thu nhập này");
        btnXoaPhanLoai.setOnClickListener(v-> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Xóa thu nhập này?")
                    .setMessage("Bạn có chắc muốn xóa mục này?")
                    .setPositiveButton("Xóa", (dialogInterface, i) -> {
                        nganSachViewModel.removeThuNhapItem(VitriCha, VitrCon);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();;
        });

        imvshow.setBackgroundResource(item.getAvatarResId());
        ChonAnhDeLuu = item.getAvatarResId();
        btn_chonanh.setOnClickListener(v ->{
            ShowChonAnhDialog(imageResId -> {
                imvshow.setBackgroundResource(imageResId);
                ChonAnhDeLuu = imageResId;
            });
        });

        edt_TenNganSach.setText(item.getContent());
        String soTien = item.getMoney();
        soTien = soTien.replace("đ", "")
                .replace(",", "")
                .replace(".", "")
                .trim();
        edt_Sotien.setText(soTien);

        btnSua.setText("Sửa");

        btnSua.setOnClickListener(v->{
            String newName = edt_TenNganSach.getText().toString().trim();
            String newMoney = edt_Sotien.getText().toString().trim();
            newMoney = newMoney.replace("đ", "")
                    .replace(",", "")
                    .replace(".", "")
                    .trim();
            double sotien = Double.parseDouble(newMoney);
            newMoney = dinhDangLaiSoTien(sotien) + " đ";



            item.setContent(newName);
            item.setMoney(newMoney);
            item.setAvatarResId(ChonAnhDeLuu);
            //budgetViewModel.updateThuNhapItem(VitriCha, VitrCon, item);
            nganSachViewModel.updateAllowanceItem(true, VitriCha, VitrCon, item);

            ChonAnhDeLuu = null;
            dialog.dismiss();
        });

        dialog.show();
    }

    //chinh suaw cho chi tieu
    private void showChinhSuaChiTieuDialog(DanhMucItem item, int VitriCha, int VitrCon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_budget_personal, null);
        builder.setView(dialogView);


        TextView tvNhanDan = dialogView.findViewById(R.id.tv_NhanDan);
        Spinner spinnerThuChi = dialogView.findViewById(R.id.Spinner_ThuHayChi);
        Spinner spinnerPhanLoai = dialogView.findViewById(R.id.Spinner_PhanLoai);
        LinearLayout llRadiogroup = dialogView.findViewById(R.id.llRadioGroup);
        Button btn_chonanh = dialogView.findViewById(R.id.btn_ChonAnh);
        ImageView imvshow = dialogView.findViewById(R.id.imv_show_image);
        EditText edt_TenNganSach = dialogView.findViewById(R.id.etTenNganSach);
        EditText edt_Sotien = dialogView.findViewById(R.id.etSoTien);
        Button btnSua = dialogView.findViewById(R.id.btnAdd);
        Button btnXoaPhanLoai = dialogView.findViewById(R.id.btn_XoaPhanLoai);

        AlertDialog dialog = builder.create();
        tvNhanDan.setText("Sửa ngân sách thu nhập");
        llRadiogroup.setVisibility(View.GONE);

        List<String> Thamchieu = new ArrayList<>();
        Thamchieu.add("Đây là chi tiêu");
        ArrayAdapter<String> adapterThamChieu = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Thamchieu
        );
        adapterThamChieu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerThuChi.setAdapter(adapterThamChieu);

        //thiet lap spinner cho phan loai
        SalaryItem thamchieuphanloai = new SalaryItem();
        thamchieuphanloai = nganSachViewModel.getThuNhapItemsData().getValue().get(VitriCha);
        List<String> ThamChieuPhanLoai = new ArrayList<>();
        ThamChieuPhanLoai.add(thamchieuphanloai.getMainTitle());
        ArrayAdapter<String> adapterThamChieuPhanLoai = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                ThamChieuPhanLoai
        );
        adapterThamChieuPhanLoai.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhanLoai.setAdapter(adapterThamChieuPhanLoai);

        btnXoaPhanLoai.setText("Xóa chi tiêu này");
        btnXoaPhanLoai.setOnClickListener(v-> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Xóa chi tiêu này?")
                    .setMessage("Bạn có chắc muốn xóa mục này?")
                    .setPositiveButton("Xóa", (dialogInterface, i) -> {
                       nganSachViewModel.removeChiTieuItem(VitriCha, VitrCon);
                       dialog.dismiss();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();;
        });

        imvshow.setBackgroundResource(item.getAvatarResId());
        ChonAnhDeLuu = item.getAvatarResId();
        btn_chonanh.setOnClickListener(v ->{
            ShowChonAnhDialog(imageResId -> {
                imvshow.setBackgroundResource(imageResId);
                ChonAnhDeLuu = imageResId;
            });
        });

        edt_TenNganSach.setText(item.getContent());
        String soTien = item.getMoney();
        soTien = soTien.replace("đ", "")
                .replace(",", "")
                .replace(".", "")
                .trim();
        edt_Sotien.setText(soTien);


        btnSua.setText("Sửa");

        btnSua.setOnClickListener(v->{
            String newName = edt_TenNganSach.getText().toString().trim();
            String newMoney = edt_Sotien.getText().toString().trim();
            newMoney = newMoney.replace("đ", "")
                    .replace(",", "")
                    .replace(".", "")
                    .trim();
            double sotien = Double.parseDouble(newMoney);
            newMoney = dinhDangLaiSoTien(sotien) + " đ";


            item.setContent(newName);
            item.setMoney(newMoney);
            item.setAvatarResId(ChonAnhDeLuu);
            //budgetViewModel.updateChiTieuItem(VitriCha, VitrCon, item);
            nganSachViewModel.updateAllowanceItem(false, VitriCha, VitrCon, item);
            ChonAnhDeLuu = null;
            dialog.dismiss();
        });


        dialog.show();
    }

    private void ShowChonAnhDialog(NganSachFragmentStatistics.GoiLaiLuaChonAnh callback) {
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

    private Integer ChonAnhDeLuu = null;

    // Hàm định dạng lại số tiền
    private String dinhDangLaiSoTien(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    private void HienHopNhapThem() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_budget_personal, null);
        builder.setView(dialogView);

        ImageView imv_ShowAnh = dialogView.findViewById(R.id.imv_show_image);
        AppCompatButton btnChonAnh = dialogView.findViewById(R.id.btn_ChonAnh);

        spinnerPhanLoai = dialogView.findViewById(R.id.Spinner_PhanLoai);
        spinnerThuChi = dialogView.findViewById(R.id.Spinner_ThuHayChi);
        radioChonPhanLoai = dialogView.findViewById(R.id.radio_ChonPhanLoai);
        radioTaoPhanLoai = dialogView.findViewById(R.id.radio_TaoPhanLoai);
        edt_NhapPhanLoai = dialogView.findViewById(R.id.edt_NhapPhanLoai);
        edt_NoiDung = dialogView.findViewById(R.id.etTenNganSach);
        edt_SoTien = dialogView.findViewById(R.id.etSoTien);


        Button btnXoaPhanLoai = dialogView.findViewById(R.id.btn_XoaPhanLoai);
        Button btnThemNganSach = dialogView.findViewById(R.id.btnAdd);

        AlertDialog dialog = builder.create();


        btnChonAnh.setOnClickListener(v -> {
            ShowChonAnhDialog(imageResId -> {
                imv_ShowAnh.setBackgroundResource(imageResId);
                ChonAnhDeLuu = imageResId;
            });
        });

        radioChonPhanLoai.setOnClickListener(v -> {
            if (radioChonPhanLoai.isChecked()) {
                edt_NhapPhanLoai.setVisibility(View.GONE);
                spinnerPhanLoai.setVisibility(View.VISIBLE);
                radioTaoPhanLoai.setChecked(false);
                btnXoaPhanLoai.setVisibility(View.VISIBLE);
            }
        });

        radioTaoPhanLoai.setOnClickListener(v ->{
           if (radioTaoPhanLoai.isChecked()) {
               edt_NhapPhanLoai.setVisibility(View.VISIBLE);
               spinnerPhanLoai.setVisibility(View.GONE);
               radioChonPhanLoai.setChecked(false);
               btnXoaPhanLoai.setVisibility(View.GONE);
           }
       });

        List<String> ThuChi = new ArrayList<>();
        ThuChi.add("Đây là thu nhập");
        ThuChi.add("Đây là chi tiêu");

        ArrayAdapter<String> adapterHinhThuc = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                ThuChi
        );

        adapterHinhThuc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerThuChi.setAdapter(adapterHinhThuc);

        nganSachViewModel.loadDanhSachPhanLoaiFirebase();

        nganSachViewModel.getDanhSachPhanLoai().observe(getViewLifecycleOwner(), Danhsach ->{
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    Danhsach
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPhanLoai.setAdapter(adapter);
        });

        nganSachViewModel.loadDanhSachPhanLoaiFirebase();

        btnXoaPhanLoai.setOnClickListener(v ->{
            dialogXoaPhanLoai(dialog, spinnerPhanLoai);

        });

        btnThemNganSach.setOnClickListener(v ->{
            String thuChi = spinnerThuChi.getSelectedItem() != null ? spinnerThuChi.getSelectedItem().toString() : "";
            boolean isIncome = "Đây là thu nhập".equals(thuChi);
            String TenPhanLoai;
            if(spinnerPhanLoai.getVisibility() == View.GONE)
            {
                 TenPhanLoai = edt_NhapPhanLoai.getText().toString().trim();
                 if(TenPhanLoai.isEmpty()) {
                     Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 nganSachViewModel.ThemDanhSachPhanLoai(TenPhanLoai);
            }else {
                Object selectedItem = spinnerPhanLoai.getSelectedItem();
                TenPhanLoai = selectedItem != null ? selectedItem.toString() : "";

            }

            String TenNganSach = edt_NoiDung.getText().toString().trim();

            String Sotien = edt_SoTien.getText().toString().trim();

            if(TenNganSach.isEmpty() && Sotien.isEmpty() && TenPhanLoai.isEmpty()) {
                Toast.makeText(getContext(),"Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            Sotien = Sotien.replace("đ", "")
                    .replace(",", "")
                    .replace(".", "")
                    .trim();
            double soTien = Double.parseDouble(Sotien);
            Sotien = dinhDangLaiSoTien(soTien) + " đ";

            String Ngay = getCurrentDate();

            // Tạo AllowanceItem
            DanhMucItem newItem = new DanhMucItem(
                    ChonAnhDeLuu,
                    TenNganSach,
                    Sotien,
                    isIncome ? "Thu nhập: " : "Chi tiêu: ",
                    Ngay,
                    getCurrentDate1()
            );

            // Kiểm tra phân loại có tồn tại hay không
            List<SalaryItem> currentList = isIncome
                    ? new ArrayList<>(nganSachViewModel.getThuNhapItemsData().getValue())
                    : new ArrayList<>(nganSachViewModel.getChiTieuItemsData().getValue());
            SalaryItem addsalary = new SalaryItem();
            boolean phanLoaiTonTai = false;

            if (currentList != null) {
                for (int i = 0; i < currentList.size(); i++) {
                    SalaryItem salaryItem = currentList.get(i);
                    if (salaryItem.getMainTitle().equals(TenPhanLoai)) {
                        // Sao chép danh sách AllowanceItems hiện tại
                        List<DanhMucItem> updatedDanhMucItems = new ArrayList<>(salaryItem.getAllowanceItems());

                        // Thêm mục mới
                        updatedDanhMucItems.add(newItem);
                        SalaryItem updatedSalaryItem = new SalaryItem(
                                salaryItem.getId(),
                                salaryItem.getMainTitle(),
                                updatedDanhMucItems,
                                salaryItem.getColor(),
                                getCurrentDate(),
                                getCurrentDate1()
                        );
                        // Cập nhật danh sách AllowanceItems mới
                        currentList.set(i, updatedSalaryItem);
                        phanLoaiTonTai = true;
                        nganSachViewModel.updateSalaryItem(isIncome, updatedSalaryItem);
                        break;
                    }
                }
            }
            if (!phanLoaiTonTai) {
                // Nếu phân loại chưa tồn tại, tạo mới SalaryItem
                String uniqueId = UUID.randomUUID().toString();
                SalaryItem newSalaryItem = new SalaryItem(
                        uniqueId,
                        TenPhanLoai,
                        Collections.singletonList(newItem),
                        isIncome ? 0xFF85C88A : 0xFFD9534F, // Xanh lá hoặc đỏ
                        getCurrentDate(),
                        getCurrentDate1()
                );
                nganSachViewModel.addSalaryItem(isIncome, newSalaryItem);
            }


            // Cập nhật LiveData
            dialog.dismiss();

        });


        spinnerPhanLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = nganSachViewModel.LayDanhSachPhanLoai().get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when nothing is selected
            }
        });

        dialog.show();
    }

    public String getCurrentDate() {
        // Lấy đối tượng Calendar hiện tại
        Calendar calendar = Calendar.getInstance();

        // Định dạng ngày theo mẫu mong muốn, ví dụ: "dd/MM/yyyy"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Chuyển đổi Calendar thành Date và định dạng thành String
        String currentDate = sdf.format(calendar.getTime());

        return currentDate;
    }


    public String getCurrentDate1() {
        // Lấy đối tượng Calendar hiện tại
        Calendar calendar = Calendar.getInstance();

        // Định dạng ngày theo mẫu mong muốn, ví dụ: "dd/MM/yyyy"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());

        // Chuyển đổi Calendar thành Date và định dạng thành String
        String currentDate = sdf.format(calendar.getTime());

        return currentDate;
    }


    private void dialogXoaPhanLoai(AlertDialog dialog, Spinner spinnerPhanLoai){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Object selectedItem = spinnerPhanLoai.getSelectedItem();

        if (selectedItem == null) {
            Toast.makeText(getContext(), "Vui lòng chọn một phân loại để xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        builder.setTitle("Bạn chắc chắn xóa phân loại này");
        builder.setMessage("Bạn có chắc " +
                "chắn sẽ xóa nó đi?");
        String TenPhanLoai = selectedItem != null ? selectedItem.toString() : "";

        String phanloai = selectedItem.toString();

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //thuc hien xoa di moi noi dung trong phan loai do
                nganSachViewModel.XoaDanhSachPhanLoai(phanloai);
            }
        });


        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}


