package com.example.mobileapp.ui.budget.Custom;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.airbnb.lottie.L;
import com.example.mobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BudgetViewModel extends ViewModel {
    private MutableLiveData<List<SalaryItem>> ThuNhapItemsData = new MutableLiveData<>();
    private MutableLiveData<List<SalaryItem>> ChiTieuItemsData = new MutableLiveData<>();
    private FirebaseFirestore firestore;
    private String UserId;
    private MutableLiveData<List<String>> DanhSachPhanLoai = new MutableLiveData<>();
    private MutableLiveData<Double> tongTatCaSoTienThuNhap;
    private MutableLiveData<Double> tongTatCaSoTienChiTieu;
    private String date = getCurrentDate();

    public BudgetViewModel() {
        ThuNhapItemsData = new MutableLiveData<>(new ArrayList<>());
        ChiTieuItemsData = new MutableLiveData<>(new ArrayList<>());
        DanhSachPhanLoai = new MutableLiveData<>(new ArrayList<>());
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        tongTatCaSoTienThuNhap = new MutableLiveData<>(0.0);
        tongTatCaSoTienChiTieu = new MutableLiveData<>(0.0);

        if (currentUser != null) {
            UserId = currentUser.getUid();
        } else {
            // Xử lý trường hợp user chưa đăng nhập
            UserId = "";
        }
    }

    public MutableLiveData<List<SalaryItem>> getThuNhapItemsData() {
        if(ThuNhapItemsData == null) {
            ThuNhapItemsData = new MutableLiveData<>(new ArrayList<>());
        }
        return ThuNhapItemsData;
    }

    public MutableLiveData<List<SalaryItem>> getChiTieuItemsData() {
        if(ChiTieuItemsData == null) {
            ChiTieuItemsData = new MutableLiveData<>(new ArrayList<>());
        }
        return ChiTieuItemsData;
    }

    public MutableLiveData<List<String>> getDanhSachPhanLoai() {
        if(DanhSachPhanLoai == null) {
            DanhSachPhanLoai = new MutableLiveData<>(new ArrayList<>());

        }
        return DanhSachPhanLoai;
    }

    public LiveData<Double> gettongTatCaSoTienThuNhap() {
        return tongTatCaSoTienThuNhap;
    }

    public LiveData<Double> gettongTatCaSoTienChiTieu() {
        return tongTatCaSoTienChiTieu;
    }

    public void loadThuNhap() {
        if (UserId == null || UserId.trim().isEmpty()) {
            Log.e("BudgetViewModel", "loadThuNhap: UserId is empty");
            return;
        }

        firestore.collection("users")
                .document(UserId)
                .collection("NganSach_thu_nhap") // Tên collection chứa SalaryItem (Thu Nhập)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<SalaryItem> load = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        if (doc.exists()) {
                            // Convert DocumentSnapshot -> SalaryItem
                            SalaryItem salaryItem = doc.toObject(SalaryItem.class);
                            if (salaryItem != null) {
                                // Bạn có thể đặt thêm logic nếu SalaryItem cần ID (doc.getId())
                                // nếu salaryItem chưa có ID, bạn có thể setId(doc.getId()).
                                load.add(salaryItem);
                            }
                        }
                    }
                    // Cập nhật LiveData
                    ThuNhapItemsData.setValue(load);
                    calculateTotalSum(load, true);

                    // In log
                    Log.d("BudgetViewModel", "loadThuNhap: " + load.size() + " items loaded from Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e("BudgetViewModel", "Error loading thu nhap from Firestore: ", e);
                });
    }




    public void updateAllowanceItem(boolean isIncome, int parentIndex, int childIndex, AllowanceItem updatedChild) {
        if (updatedChild == null) return;

        List<SalaryItem> oldList = isIncome ? getThuNhapItemsData().getValue() : getChiTieuItemsData().getValue();
        if (oldList == null || parentIndex < 0 || parentIndex >= oldList.size()) {
            return;
        }

        List<SalaryItem> newList = new ArrayList<>(oldList);
        SalaryItem parentItem = newList.get(parentIndex);
        if (parentItem == null || parentItem.getAllowanceItems() == null) {
            return;
        }

        List<AllowanceItem> childList = new ArrayList<>(parentItem.getAllowanceItems());
        if (childIndex < 0 || childIndex >= childList.size()) {
            return;
        }

        // Sửa item con
        childList.set(childIndex, updatedChild);

        // Tạo SalaryItem update
        SalaryItem updatedParentItem = new SalaryItem(
                parentItem.getId(),
                parentItem.getMainTitle(),
                childList,
                parentItem.getColor(),
                date,
                getCurrentDate1()
        );

        newList.set(parentIndex, updatedParentItem);

        // Cập nhật LiveData
        if (isIncome) {
            getThuNhapItemsData().setValue(newList);
            calculateTotalSum(newList, true);
        } else {
            getChiTieuItemsData().setValue(newList);
            calculateTotalSum(newList, false);
        }

        // Lưu Firestore
        if (!UserId.isEmpty() && updatedParentItem.getId() != null) {
            firestore.collection("users")
                    .document(UserId)
                    .collection(isIncome ? "NganSach_thu_nhap" : "NganSach_chi_tieu")
                    .document(updatedParentItem.getId())  // ID cũ
                    .set(updatedParentItem)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("BudgetViewModel", "updateAllowanceItem ok, doc=" + updatedParentItem.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("BudgetViewModel", "updateAllowanceItem fail", e);
                    });
        }
    }


    public void addSalaryItem(boolean isIncome, SalaryItem newSalaryItem) {
        if (newSalaryItem == null) return;

        // Đảm bảo SalaryItem có id (nếu dùng docId cố định)
        if (newSalaryItem.getId() == null || newSalaryItem.getId().trim().isEmpty()) {
            newSalaryItem.setId(UUID.randomUUID().toString());
        }

        if (isIncome) {
            // 1) Cập nhật LiveData cục bộ
            List<SalaryItem> currentList = getThuNhapItemsData().getValue();
            if (currentList == null) currentList = new ArrayList<>();
            else currentList = new ArrayList<>(currentList);

            currentList.add(newSalaryItem);
            getThuNhapItemsData().setValue(currentList);
            calculateTotalSum(currentList, true);

            // 2) Lưu Firestore
            if (!UserId.isEmpty()) {
                firestore.collection("users")
                        .document(UserId)
                        .collection("NganSach_thu_nhap")
                        .document(newSalaryItem.getId())  // docId = newSalaryItem.getId()
                        .set(newSalaryItem)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("BudgetViewModel", "Thêm SalaryItem ThuNhập thành công, ID=" + newSalaryItem.getId());
                        })
                        .addOnFailureListener(e -> {
                            Log.e("BudgetViewModel", "Lỗi thêm SalaryItem ThuNhập", e);
                        });
            }
        } else {
            // 1) Cập nhật LiveData cục bộ
            List<SalaryItem> currentList = getChiTieuItemsData().getValue();
            if (currentList == null) currentList = new ArrayList<>();
            else currentList = new ArrayList<>(currentList);

            currentList.add(newSalaryItem);
            getChiTieuItemsData().setValue(currentList);
            calculateTotalSum(currentList, false);

            // 2) Lưu Firestore
            if (!UserId.isEmpty()) {
                firestore.collection("users")
                        .document(UserId)
                        .collection("NganSach_chi_tieu")
                        .document(newSalaryItem.getId())  // docId
                        .set(newSalaryItem)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("BudgetViewModel", "Thêm SalaryItem ChiTiêu thành công, ID=" + newSalaryItem.getId());
                        })
                        .addOnFailureListener(e -> {
                            Log.e("BudgetViewModel", "Lỗi thêm SalaryItem ChiTiêu", e);
                        });
            }
        }
    }

    public void updateSalaryItem(boolean isIncome, SalaryItem updatedItem) {
        if (updatedItem == null || updatedItem.getId() == null) {
            return;
        }
        // 1) Cập nhật cục bộ
        List<SalaryItem> oldList = isIncome
                ? getThuNhapItemsData().getValue()
                : getChiTieuItemsData().getValue();
        if (oldList == null) return;

        // Tạo một copy list
        List<SalaryItem> newList = new ArrayList<>(oldList);
        for (int i = 0; i < newList.size(); i++) {
            if (newList.get(i).getId().equals(updatedItem.getId())) {
                newList.set(i, updatedItem);
                break;
            }
        }

        // 2) setValue để cập nhật UI
        if (isIncome) {
            getThuNhapItemsData().setValue(newList);
            calculateTotalSum(newList, true);
        } else {
            getChiTieuItemsData().setValue(newList);
            calculateTotalSum(newList, false);
        }

        // 3) Lưu lên Firestore
        String collectionName = isIncome ? "NganSach_thu_nhap" : "NganSach_chi_tieu";
        if (!UserId.isEmpty()) {
            firestore.collection("users")
                    .document(UserId)
                    .collection(collectionName)
                    .document(updatedItem.getId())
                    .set(updatedItem)
                    .addOnSuccessListener(aVoid -> Log.d("BudgetViewModel", "updateSalaryItem: success"))
                    .addOnFailureListener(e -> Log.e("BudgetViewModel", "updateSalaryItem: failed", e));
        }
    }




    public void removeThuNhapItem(int parentIndex, int childIndex) {
        // Lấy danh sách Thu Nhập hiện tại
        List<SalaryItem> oldList = getThuNhapItemsData().getValue();
        if (oldList == null
                || parentIndex < 0
                || parentIndex >= oldList.size()) {
            return;
        }

        // Tạo bản copy list cha
        List<SalaryItem> newList = new ArrayList<>(oldList);

        // Lấy SalaryItem cha
        SalaryItem parentItem = newList.get(parentIndex);
        if (parentItem == null || parentItem.getAllowanceItems() == null) {
            return;
        }

        // Copy danh sách con
        List<AllowanceItem> childList = new ArrayList<>(parentItem.getAllowanceItems());
        if (childIndex < 0 || childIndex >= childList.size()) {
            return;
        }

        // Xóa item con tại vị trí childIndex
        childList.remove(childIndex);

        // Tạo SalaryItem mới với danh sách AllowanceItem đã cập nhật
        SalaryItem updatedParentItem = new SalaryItem(
                parentItem.getId(),
                parentItem.getMainTitle(),
                childList,
                parentItem.getColor(),
                date,
                getCurrentDate1()
        );

        // Cập nhật lại vào newList
        newList.set(parentIndex, updatedParentItem);

        // Gán newList vào LiveData để thông báo thay đổi
        getThuNhapItemsData().setValue(newList);

        // Tính lại tổng tiền
        calculateTotalSum(newList, true);

        String docId = parentItem.getId();
        if (docId != null && !docId.trim().isEmpty() && !UserId.isEmpty()) {
            firestore.collection("users")
                    .document(UserId)
                    .collection("NganSach_thu_nhap")
                    .document(docId)
                    .set(updatedParentItem)    // Ghi đè SalaryItem đã xóa item con
                    .addOnSuccessListener(aVoid -> {
                        Log.d("BudgetViewModel", "Đã xóa 1 AllowanceItem trong Firestore: docId=" + docId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("BudgetViewModel", "Lỗi khi xóa AllowanceItem trong Firestore: ", e);
                    });
        }

    }


    public void updateThuNhapItem(int parentIndex, int childIndex, @NonNull AllowanceItem updatedChild) {
        // Lấy danh sách Thu Nhập hiện tại
        List<SalaryItem> oldList = getThuNhapItemsData().getValue();
        if (oldList == null || parentIndex < 0 || parentIndex >= oldList.size()) {
            return;
        }

        // Copy để tạo list mới (DiffUtil nhận biết thay đổi)
        List<SalaryItem> newList = new ArrayList<>(oldList);

        // Lấy SalaryItem cha
        SalaryItem parentItem = newList.get(parentIndex);
        if (parentItem == null || parentItem.getAllowanceItems() == null) {
            return;
        }

        // Copy danh sách AllowanceItem để chỉnh sửa
        List<AllowanceItem> childList = new ArrayList<>(parentItem.getAllowanceItems());
        if (childIndex < 0 || childIndex >= childList.size()) {
            return;
        }

        // Thay thế phần tử childIndex bằng updatedChild
        childList.set(childIndex, updatedChild);

        // Tạo lại SalaryItem mới (copy fields cũ + list con đã sửa)
        SalaryItem updatedParentItem = new SalaryItem(
                parentItem.getId(),
                parentItem.getMainTitle(),
                childList,
                parentItem.getColor(),
                date,
                getCurrentDate1()
        );

        // Gán lại vào newList
        newList.set(parentIndex, updatedParentItem);

        // Cập nhật LiveData bằng newList
        getThuNhapItemsData().setValue(newList);

        // Tính lại tổng
        calculateTotalSum(newList, true);

    }



    public void removeThuNhapTrenPhanLoai(String TenPhanLoai) {
        if(TenPhanLoai == null || TenPhanLoai.trim().isEmpty()) {
            return;
        }
        
        List<SalaryItem> currentList = getThuNhapItemsData().getValue();
        if(currentList == null || currentList.isEmpty()) {
            return;
        }

        List<SalaryItem> itemsRemove = new ArrayList<>();
        for(SalaryItem salaryItem : currentList) {
            if(salaryItem.getMainTitle().equals(TenPhanLoai)) {
                itemsRemove.add(salaryItem);
            }
        }

        if(itemsRemove.isEmpty()) {
            return;
        }

        currentList.removeAll(itemsRemove);
        ThuNhapItemsData.setValue(currentList);

        calculateTotalSum(currentList,true);

    }


    public void loadChiTieu() {
        if (UserId.isEmpty()) {
            Log.e("BudgetViewModel", "loadChiTieu: UserId is empty");
            return;
        }

        firestore.collection("users").document(UserId)
                .collection("NganSach_chi_tieu")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<SalaryItem> load = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        if (doc.exists()) {
                            SalaryItem resultItem = doc.toObject(SalaryItem.class);
                            if (resultItem != null) {
                                load.add(resultItem);
                            }
                        }
                    }
                    ChiTieuItemsData.setValue(load);
                    calculateTotalSum(load, false);
                })
                .addOnFailureListener(e -> {
                    Log.e("BudgetViewModel", "Error loading chi tieu: ", e);
                });

    }


    public void updateChiTieuItem(int parentIndex, int childIndex, AllowanceItem updatedChild) {
        if (updatedChild == null) {
            return;
        }
        List<SalaryItem> currentList = getChiTieuItemsData().getValue();
        if (currentList == null || parentIndex < 0 || parentIndex >= currentList.size()) {
            return;
        }

        SalaryItem parentItem = currentList.get(parentIndex);
        if (parentItem == null || parentItem.getAllowanceItems() == null) {
            return;
        }

        List<AllowanceItem> childList = new ArrayList<>(parentItem.getAllowanceItems());
        if (childIndex < 0 || childIndex >= childList.size()) {
            return;
        }

        childList.set(childIndex, updatedChild);
        parentItem.setAllowanceItems(childList);
        currentList.set(parentIndex, parentItem);

        getChiTieuItemsData().setValue(currentList);

        // Tính lại tổng chi tiêu
        calculateTotalSum(currentList, false);

        // Nếu cần, update Firestore
    }


    public void removeChiTieuItem(int parentIndex, int childIndex) {
        List<SalaryItem> oldList = getChiTieuItemsData().getValue();
        if (oldList == null
                || parentIndex < 0
                || parentIndex >= oldList.size()) {
            return;
        }

        List<SalaryItem> newList = new ArrayList<>(oldList);

        SalaryItem parentItem = newList.get(parentIndex);
        if (parentItem == null || parentItem.getAllowanceItems() == null) {
            return;
        }

        List<AllowanceItem> childList = new ArrayList<>(parentItem.getAllowanceItems());
        if (childIndex < 0 || childIndex >= childList.size()) {
            return;
        }

        childList.remove(childIndex);

        SalaryItem updatedParentItem = new SalaryItem(
                parentItem.getId(),
                parentItem.getMainTitle(),
                childList,
                parentItem.getColor(),
                date,
                getCurrentDate1()
        );

        newList.set(parentIndex, updatedParentItem);
        getChiTieuItemsData().setValue(newList);

        calculateTotalSum(newList, false);

        String docId = parentItem.getId(); // ID mà bạn gán khi thêm SalaryItem
        if (docId != null && !docId.trim().isEmpty() && !UserId.isEmpty()) {
            firestore.collection("users")
                    .document(UserId)
                    .collection("NganSach_chi_tieu")
                    .document(docId)
                    .set(updatedParentItem)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("BudgetViewModel", "Đã xóa 1 AllowanceItem (Chi Tiêu) trong Firestore, doc = " + docId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("BudgetViewModel", "Lỗi khi xóa AllowanceItem (Chi Tiêu) trong Firestore: ", e);
                    });
        }
    }



    public void calculateTotalSum(List<SalaryItem> salaryItems, boolean isThuNhap) {
        double sum = 0.0;

        if (salaryItems != null) {
            for (SalaryItem salaryItem : salaryItems) {
                double itemSum = 0.0;
                if (salaryItem.getAllowanceItems() != null) {
                    for (AllowanceItem allowanceItem : salaryItem.getAllowanceItems()) {
                        double moneyVal = parseMoneyString(allowanceItem.getMoney());
                        itemSum += moneyVal;

                    }
                }
                    // Gán tổng riêng này vào SalaryItem (nếu bạn có setter phù hợp)

                    salaryItem.setTongSoTien(itemSum);

                    // Đồng thời, cộng dồn vào tổng "toàn bộ"
                    sum += itemSum;
            }
        }
        if (isThuNhap) {
            tongTatCaSoTienThuNhap.setValue(sum);

        } else {
            tongTatCaSoTienChiTieu.setValue(sum);
        }
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

    private String dinhDangLaiSoTien(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    public void removeChiTieuTrenPhanLoai(String TenPhanLoai) {
        if(TenPhanLoai == null || TenPhanLoai.trim().isEmpty()) {
            return;
        }

        List<SalaryItem> currentList = getChiTieuItemsData().getValue();
        if(currentList == null || currentList.isEmpty()) {
            return;
        }

        List<SalaryItem> itemsRemove = new ArrayList<>();
        for(SalaryItem salaryItem : currentList) {
            if(salaryItem.getMainTitle().equals(TenPhanLoai)) {
                itemsRemove.add(salaryItem);
            }
        }

        if(itemsRemove.isEmpty()) {
            return;
        }

        currentList.removeAll(itemsRemove);
        ChiTieuItemsData.setValue(currentList);

        calculateTotalSum(currentList,false);

    }


    // Hàm xử lý chuỗi tiền như trước
    private double parseMoneyString(String moneyStr) {
        if (moneyStr == null) return 0.0;

        moneyStr = moneyStr.replace("đ", "")
                .replace(",", "")
                .replace(".", "")
                .trim();

        if (moneyStr.isEmpty()) return 0.0;

        try {
            return Double.parseDouble(moneyStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public void loadDanhSachPhanLoaiFirebase() {
        if (UserId == null || UserId.trim().isEmpty()) {
            Log.e("BudgetViewModel", "loadDanhSachPhanLoaiFirebase: UserId is empty");
            return;
        }

        firestore.collection("users")
                .document(UserId)
                .collection("ngan_sach")
                .document("myNganSach")
                .collection("phan_loai")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> result = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        if (doc.exists()) {
                            String phanLoai = doc.getString("phan_loai");
                            if (phanLoai != null && !phanLoai.isEmpty()) {
                                result.add(phanLoai);
                            }
                        }
                    }
                    DanhSachPhanLoai.setValue(result);
                    Log.d("BudgetViewModel", "loadDanhSachPhanLoaiFirebase: " + result.size() + " items loaded");
                })
                .addOnFailureListener(e -> {
                    Log.e("BudgetViewModel", "Error loading phan_loai: ", e);
                });
    }



    public void ThemDanhSachPhanLoai(String PhanLoaiMoi) {
        if (PhanLoaiMoi == null || PhanLoaiMoi.trim().isEmpty()) {
            Log.e("BudgetViewModel", "ThemDanhSachPhanLoai: PhanLoaiMoi is null or empty");
            return;
        }

        List<String> currentList = DanhSachPhanLoai.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        if (!currentList.contains(PhanLoaiMoi)) {
            currentList.add(PhanLoaiMoi);
            DanhSachPhanLoai.setValue(currentList);
        } else {
            Log.e("BudgetViewModel", "Phân loại đã tồn tại: " + PhanLoaiMoi);
        }

        firestore.collection("users").document(UserId)
                .collection("ngan_sach").document("myNganSach")
                .collection("phan_loai")
                .add(Collections.singletonMap("phan_loai", PhanLoaiMoi))
                .addOnSuccessListener(documentReference -> {
                    loadDanhSachPhanLoaiFirebase();
                })
                .addOnFailureListener(e -> {
                    Log.e("BudgetViewModel", "Error thêm phân loại: ", e);
                });
    }


    public void XoaDanhSachPhanLoai(String PhanLoaiXoa) {
        if (PhanLoaiXoa == null || PhanLoaiXoa.trim().isEmpty()) {
            return;
        }

        List<String> currentList = DanhSachPhanLoai.getValue();
        if (currentList == null || currentList.isEmpty()) {
            return;
        }

        boolean removed = currentList.remove(PhanLoaiXoa);
        if (removed) {
            DanhSachPhanLoai.setValue(currentList);

            // Nếu bạn muốn xóa từ Firestore:
            firestore.collection("users").document(UserId)
                    .collection("ngan_sach").document("myNganSach")
                .collection("phan_loai")
                .whereEqualTo("phan_loai", PhanLoaiXoa)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().delete()
                            .addOnSuccessListener(aVoid -> Log.d("BudgetViewModel", "Phân loại đã được xóa từ Firestore"))
                            .addOnFailureListener(e -> Log.e("BudgetViewModel", "Error xóa phân loại từ Firestore: ", e));
                    }
                    loadDanhSachPhanLoaiFirebase();
                })
                .addOnFailureListener(e -> {
                    Log.e("BudgetViewModel", "Error tìm phân loại để xóa: ", e);
                });

        } else {
            Log.e("BudgetViewModel", "XoaDanhSachPhanLoai: Phân loại không tồn tại trong danh sách");
        }
    }

    public List<String> LayDanhSachPhanLoai() {

        return DanhSachPhanLoai.getValue();
    }









}
