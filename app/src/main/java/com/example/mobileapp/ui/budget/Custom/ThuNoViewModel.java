package com.example.mobileapp.ui.budget.Custom;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class ThuNoViewModel extends ViewModel {
    private MutableLiveData<List<ThuNo>> debtListNo;
    private MutableLiveData<List<ThuNo>> debtListKhoanThu;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();



    public MutableLiveData<List<ThuNo>> getDebtListNo() {
        if (debtListNo == null) {
            debtListNo = new MutableLiveData<>(new ArrayList<>());
            loadDebtNo();
        }
        return  debtListNo;
    }

    public MutableLiveData<List<ThuNo>> getDebtListKhoanThu() {
        if (debtListKhoanThu == null) {
            debtListKhoanThu = new MutableLiveData<>(new ArrayList<>());
            loadDebtsKhoanThu();
        }
        return debtListKhoanThu;
    }

    public void loadDebtNo() {
        firestore.collection("users")
                .document(UserId)
                .collection("debt_no")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ThuNo> thuNos = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ThuNo thuNo = document.toObject(ThuNo.class);
                            thuNos.add(thuNo);
                        }
                        debtListNo.setValue(thuNos);
                    }else {
                        debtListNo.setValue(new ArrayList<>());
                    }
                });

    }

    public void loadDebtsKhoanThu() {
       firestore.collection("users")
               .document(UserId)
               .collection("debt_khoan_thu")
               .get()
               .addOnCompleteListener(task -> {
                  if(task.isSuccessful()) {
                      List<ThuNo> thuNos = new ArrayList<>();
                      for (QueryDocumentSnapshot document : task.getResult()) {
                          ThuNo thuNo = document.toObject(ThuNo.class);
                          thuNos.add(thuNo);
                      }
                      debtListKhoanThu.setValue(thuNos);
                  } else {
                      debtListKhoanThu.setValue(new ArrayList<>());
                  }
               });
    }

    public void updateDebtSelectedStatus(int position, boolean isSelected, boolean isDebtNo) {
        List<ThuNo> currentList = isDebtNo ? debtListNo.getValue() : debtListKhoanThu.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.get(position).setSelected(isSelected);

            // Tạo danh sách mới để kích hoạt LiveData
            List<ThuNo> updatedList = new ArrayList<>(currentList);
            if (isDebtNo) {
                debtListNo.setValue(updatedList);
            } else {
                debtListKhoanThu.setValue(updatedList);
            }
        }
    }


    public void addDebtNo(ThuNo thuNo) {

        firestore.collection("users")
               .document(UserId)
               .collection("debt_no")
               .add(thuNo)
               .addOnSuccessListener(documentReference -> {
                   Log.d("Success", "Debt added successfully: " + documentReference.getId());
                  List<ThuNo> DanhSachHientai = debtListNo.getValue();
                  if(DanhSachHientai != null) {
                      String docID = documentReference.getId();
                      thuNo.setDDocId(docID);

                      DanhSachHientai.add(thuNo);
                      debtListNo.setValue(DanhSachHientai);
                  }
               }).addOnFailureListener(e -> {
                   Log.e("Error", "Failed to add debt", e);
               });
    }

    public void addDebtKhoanThu(ThuNo thuNo) {
        firestore.collection("users")
                .document(UserId)
                .collection("debt_khoan_thu")
                .add(thuNo)
                .addOnSuccessListener(documentReference -> {
                    List<ThuNo> Danhsachhientai = debtListKhoanThu.getValue();
                    if( Danhsachhientai != null) {
                        String docID = documentReference.getId();
                        thuNo.setDDocId(docID);


                        Danhsachhientai.add(thuNo);
                        debtListKhoanThu.setValue(Danhsachhientai);
                    }
                }).addOnFailureListener(e -> {

                });
    }

//    public void CapNhatDebtNoLenFireBase(String debtId, Debt debt) {
//        firestore.collection("users")
//                .document(UserId)
//                .collection("debt_no")
//                .document(debtId)
//                .set(debt)
//                .addOnFailureListener(aVoid -> {
//                    loadDebtNo();
//                })
//                .addOnFailureListener(e -> {
//
//                });
//    }

    public void CapNhatDebtKhoanThuLenFireBase(String debtId, ThuNo thuNo) {
        if (debtId == null || thuNo == null) return;
        firestore.collection("users")
                .document(UserId)
                .collection("debt_khoan_thu")
                .document(debtId)
                .set(thuNo)
                .addOnSuccessListener(aVoid -> loadDebtsKhoanThu())
                .addOnFailureListener(e -> {
                    e.printStackTrace();

                });
    }

    public void CapNhatDebtNoLenFireBase(String debtId, ThuNo thuNo) {
        if (debtId == null || thuNo == null) return;
        firestore.collection("users")
                .document(UserId)
                .collection("debt_no")
                .document(debtId)
                .set(thuNo)
                .addOnSuccessListener(aVoid -> loadDebtNo())
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    public void updateDebtNo(int position, ThuNo thuNo) {
        List<ThuNo> currentList = debtListNo.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.set(position, thuNo);
            debtListNo.setValue(currentList);
        }
    }

//    public void CapNhatDebtKhoanThuLenFireBase(String debtId, Debt debt) {
//        firestore.collection("users")
//                .document(UserId)
//                .collection("debt_khoan_thu")
//                .document(debtId)
//                .set(debt)
//                .addOnFailureListener(aVoid -> {
//                    loadDebtsKhoanThu();
//                })
//                .addOnFailureListener(e ->{
//
//                });
//    }

    public void updateDebtKhoanThu(int position, ThuNo thuNo) {
        List<ThuNo> currentList = debtListKhoanThu.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.set(position, thuNo);
            debtListKhoanThu.setValue(currentList);
        }
    }



    public void removeDebtNo(int position) {
        List<ThuNo> currentList = debtListNo.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {

            ThuNo thuNo = currentList.get(position);

            String debtId = thuNo.getDDocId(); // Lưu lại DDocId để xử lý rollback nếu cần.
            currentList.remove(position);
            debtListNo.setValue(currentList); // Cập nhật danh sách trong ViewModel.

            if (debtId != null && !debtId.isEmpty()) {
                firestore.collection("users")
                        .document(UserId)
                        .collection("debt_no")
                        .document(debtId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // Xóa thành công, không cần thêm logic.
                        })
                        .addOnFailureListener(e -> {
                            // Nếu xóa thất bại, thêm lại vào danh sách.
                            currentList.add(position, thuNo);
                            debtListNo.setValue(currentList);

                        });
            }
        } else {

        }
    }


    // Similarly, for khoan thu:
    public void removeDebtKhoanThu(int position) {
        List<ThuNo> currentList = debtListKhoanThu.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {

            ThuNo thuNo = currentList.get(position);

            String debtId = thuNo.getDDocId(); // Lưu lại DDocId để xử lý rollback nếu cần.
            currentList.remove(position);
            debtListKhoanThu.setValue(currentList); // Cập nhật danh sách trong ViewModel.

            if (debtId != null && !debtId.isEmpty()) {
                firestore.collection("users")
                        .document(UserId)
                        .collection("debt_khoan_thu")
                        .document(debtId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // Xóa thành công, không cần thêm logic.
                        })
                        .addOnFailureListener(e -> {
                            // Nếu xóa thất bại, thêm lại vào danh sách.
                            currentList.add(position, thuNo);
                            debtListKhoanThu.setValue(currentList);

                        });
            }
        } else {
        }
    }



}
