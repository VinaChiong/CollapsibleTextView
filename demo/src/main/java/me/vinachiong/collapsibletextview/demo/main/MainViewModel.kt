package me.vinachiong.collapsibletextview.demo.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val currentName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}
