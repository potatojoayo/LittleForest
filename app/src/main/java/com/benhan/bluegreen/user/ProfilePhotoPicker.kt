package com.benhan.bluegreen.user

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.benhan.bluegreen.R
import com.benhan.bluegreen.adapter.GalleryAdapter
import com.benhan.bluegreen.dataclass.PhotoVO
import com.benhan.bluegreen.dataclass.ServerResonse
import com.benhan.bluegreen.listener.OnItemClickListener
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.benhan.bluegreen.utill.GetImageUri
import com.benhan.bluegreen.utill.GridDividerDecoration
import com.benhan.bluegreen.utill.MyApplication
import com.bumptech.glide.Glide
import com.nanchen.compresshelper.CompressHelper
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfilePhotoPicker: AppCompatActivity() {




    val apiClient = ApiClient()
    val apiInterface = apiClient.getApiClient().create(ApiInterface::class.java)
    val sharedPreference = SharedPreference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_photo_picker)



        val recyclerView = findViewById<RecyclerView>(R.id.galleryRecyclerView)
        val selectedView = findViewById<ImageView>(R.id.selectedImage)





        val getImageUri = GetImageUri(this)
        val list = getImageUri.getImageUri()
        val galleryAdapter =
            GalleryAdapter(this, list)
        val slidingLayout = findViewById<SlidingUpPanelLayout>(R.id.sliding_layout)

        slidingLayout.coveredFadeColor = 0





        val ivX = findViewById<ImageView>(R.id.ivX)
        val ivNext = findViewById<TextView>(R.id.plusNext)

        ivX.setOnClickListener {

            finish()



        }




        val mOnItemClickListener = object :
            OnItemClickListener {


            override fun OnItemClick(viewHolder: RecyclerView.ViewHolder, position: Int) {
                val photoVO: PhotoVO = galleryAdapter.list[position]
                val photoVOSelectedBefore: PhotoVO? = galleryAdapter.list.find {
                    it.selected
                }


                if (photoVO.selected){


                    if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.HIDDEN ||
                        slidingLayout.panelState == SlidingUpPanelLayout.PanelState.ANCHORED ||
                        slidingLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED){

                        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED

                    }


                    if(slidingLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED){


                        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
                    }






                }else{


                    if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.HIDDEN ||
                        slidingLayout.panelState == SlidingUpPanelLayout.PanelState.ANCHORED ||
                        slidingLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED){

                        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED

                    }

                    photoVOSelectedBefore?.selected = false
                    photoVO.selected = true

                    Glide.with(this@ProfilePhotoPicker).load(photoVO.imgPath)
                        .centerCrop()
                        .into(selectedView)







                }
                galleryAdapter.list[position] = photoVO
                galleryAdapter.notifyDataSetChanged()

                ivNext.setOnClickListener {

                    onNextClicked(galleryAdapter.list[position])

                }

            }


        }


        galleryAdapter.list[0].selected
        Glide.with(this).load(galleryAdapter.list[0].imgPath)
            .centerCrop().into(selectedView)

        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        galleryAdapter.onItemClickListener = mOnItemClickListener
        val dividerDecoration = GridDividerDecoration(
            resources,
            R.drawable.divider_recyler_gallery
        )


        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = galleryAdapter
        recyclerView.addItemDecoration(dividerDecoration)
        recyclerView.itemAnimator = DefaultItemAnimator()


    }




    fun onNextClicked(selectedPhoto: PhotoVO) {


        val MIN_IMAGE_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics)
            .toInt()
//        val MAX_IMAGE_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1000f, resources.displayMetrics)
//            .toInt()



        sharedPreference.setString(this, "tmpProfilePhoto", selectedPhoto.imgPath.toString())





        CropImage.activity(selectedPhoto.imgPath)
            .setCropShape(com.theartofdev.edmodo.cropper.CropImageView.CropShape.OVAL)
            .setGuidelines(com.theartofdev.edmodo.cropper.CropImageView.Guidelines.OFF)
            .setScaleType(com.theartofdev.edmodo.cropper.CropImageView.ScaleType.CENTER_INSIDE)
            .setFixAspectRatio(true)
            .setInitialCropWindowPaddingRatio(0F)
            .setMinCropWindowSize(MIN_IMAGE_SIZE, MIN_IMAGE_SIZE)
            .setAllowRotation(false)
            .setBorderLineThickness(0F)
            .start(this)






    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {



        val sharePreference = SharedPreference()
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result :CropImage.ActivityResult = CropImage.getActivityResult(data)
            if(resultCode == Activity.RESULT_OK){
                val resultUri = result.uri.path
                val file = File(resultUri!!)
                val newFile = CompressHelper.Builder(this)
                    .setQuality(80)    // 默认压缩质量为80
                    .setFileName(file.name) // 设置你需要修改的文件名
                    .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                    .build()
                    .compressToFile(file)
                val email = sharePreference.getString(this, "email")?.toRequestBody("text/plain".toMediaTypeOrNull())


                val requestBody = newFile.asRequestBody("image/*".toMediaTypeOrNull())
                val fileToUpload = MultipartBody.Part.createFormData("file", file.name, requestBody)


                val callback:Call<ServerResonse> = this.apiInterface.uploadProfilePhoto(fileToUpload, email!!)
                callback.enqueue(object: Callback<ServerResonse>{
                    override fun onFailure(call: Call<ServerResonse>, t: Throwable) {
                        Log.d("에러 ", t.message)
                    }

                    override fun onResponse(
                        call: Call<ServerResonse>,
                        response: Response<ServerResonse>
                    ) {
                        Log.d("코드", response.message())
                        val profilePhoto = response.body()?.profile_photo
                        sharePreference.setString(this@ProfilePhotoPicker, "profilePhoto", profilePhoto!!)

                    }


                })


            } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

            }

        }
        super.onActivityResult(requestCode, resultCode, data)

        MyApplication.isProfileUpdated = true

        finish()


    }




}