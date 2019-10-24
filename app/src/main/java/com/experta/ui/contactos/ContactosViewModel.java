package com.experta.ui.contactos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ContactosViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ContactosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is contactos fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}