package com.benhan.bluegreen.localdata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.benhan.bluegreen.dataclass.PhotoVO

class PassSelectedPhoto: ViewModel() {


     val selectedPhotoData =  MutableLiveData<PhotoVO>()

    fun passSelectedPhoto(selectedPhoto: PhotoVO){

        selectedPhotoData.value = selectedPhoto


    }

}