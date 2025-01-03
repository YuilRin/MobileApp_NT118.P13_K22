package com.example.mobileapp.ui.budget.Custom;

import static java.security.AccessController.getContext;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mobileapp.R;

import java.io.Flushable;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class DebtViewModel extends ViewModel {
    private MutableLiveData<List<Debt>> debtListNo;
    private MutableLiveData<List<Debt>> debtListKhoanThu;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();



    public MutableLiveData<List<Debt>> getDebtListNo() {
        if (debtListNo == null) {
            debtListNo = new MutableLiveData<>(new ArrayList<>());
            loadDebtNo();
        }
        return  debtListNo;
    }

    public MutableLiveData<List<Debt>> getDebtListKhoanThu() {
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
                        List<Debt> debts = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Debt debt = document.toObject(Debt.class);
                            debts.add(debt);
                        }
                        debtListNo.setValue(debts);
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
                      List<Debt> debts = new ArrayList<>();
                      for (QueryDocumentSnapshot document : task.getResult()) {
                          Debt debt = document.toObject(Debt.class);
                          debts.add(debt);
                      }
                      debtListKhoanThu.setValue(debts);
                  } else {
                      debtListKhoanThu.setValue(new ArrayList<>());
                  }
               });
    }

    public void updateDebtSelectedStatus(int position, boolean isSelected, boolean isDebtNo) {
        List<Debt> currentList = isDebtNo ? debtListNo.getValue() : debtListKhoanThu.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.get(position).setSelected(isSelected);

            // Tạo danh sách mới để kích hoạt LiveData
            List<Debt> updatedList = new ArrayList<>(currentList);
            if (isDebtNo) {
                debtListNo.setValue(updatedList);
            } else {
                debtListKhoanThu.setValue(updatedList);
            }
        }
    }


    public void addDebtNo(Debt debt) {

        firestore.collection("users")
               .document(UserId)
               .collection("debt_no")
               .add(debt)
               .addOnSuccessListener(documentReference -> {
                   Log.d("Success", "Debt added successfully: " + documentReference.getId());
                  List<Debt> DanhSachHientai = debtListNo.getValue();
                  if(DanhSachHientai != null) {
                      String docID = documentReference.getId();
                      debt.setDDocId(docID);

                      DanhSachHientai.add(debt);
                      debtListNo.setValue(DanhSachHientai);
                  }
               }).addOnFailureListener(e -> {
                   Log.e("Error", "Failed to add debt", e);
               });
    }

    public void addDebtKhoanThu(Debt debt) {
        firestore.collection("users")
                .document(UserId)
                .collection("debt_khoan_thu")
                .add(debt)
                .addOnSuccessListener(documentReference -> {
                    List<Debt> Danhsachhientai = debtListKhoanThu.getValue();
                    if( Danhsachhientai != null) {
                        String docID = documentReference.getId();
                        debt.setDDocId(docID);


                        Danhsachhientai.add(debt);
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

    public void CapNhatDebtKhoanThuLenFireBase(String debtId, Debt debt) {
        if (debtId == null || debt == null) return;
        firestore.collection("users")
                .document(UserId)
                .collection("debt_khoan_thu")
                .document(debtId)
                .set(debt)
                .addOnSuccessListener(aVoid -> loadDebtsKhoanThu())
                .addOnFailureListener(e -> {
                    e.printStackTrace();

                });
    }

    public void CapNhatDebtNoLenFireBase(String debtId, Debt debt) {
        if (debtId == null || debt == null) return;
        firestore.collection("users")
                .document(UserId)
                .collection("debt_no")
                .document(debtId)
                .set(debt)
                .addOnSuccessListener(aVoid -> loadDebtNo())
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    public void updateDebtNo(int position, Debt debt) {
        List<Debt> currentList = debtListNo.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.set(position, debt);
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

    public void updateDebtKhoanThu(int position, Debt debt) {
        List<Debt> currentList = debtListKhoanThu.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.set(position, debt);
            debtListKhoanThu.setValue(currentList);
        }
    }



    public void removeDebtNo(int position) {
        List<Debt> currentList = debtListNo.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {

            Debt debt = currentList.get(position);

            String debtId = debt.getDDocId(); // Lưu lại DDocId để xử lý rollback nếu cần.
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
                            currentList.add(position, debt);
                            debtListNo.setValue(currentList);

                        });
            }
        } else {

        }
    }


    // Similarly, for khoan thu:
    public void removeDebtKhoanThu(int position) {
        List<Debt> currentList = debtListKhoanThu.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {

            Debt debt = currentList.get(position);

            String debtId = debt.getDDocId(); // Lưu lại DDocId để xử lý rollback nếu cần.
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
                            currentList.add(position, debt);
                            debtListKhoanThu.setValue(currentList);

                        });
            }
        } else {
        }
    }



}
