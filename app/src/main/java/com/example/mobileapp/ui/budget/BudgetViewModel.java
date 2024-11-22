package com.example.mobileapp.ui.budget;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BudgetViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public BudgetViewModel () {
        mText = new MutableLiveData<>();
        mText.setValue("This is budget fragment");
    }
    public LiveData<String> getText () {return  mText;}
}
