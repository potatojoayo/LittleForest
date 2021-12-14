package com.benhan.bluegreen.plus

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.benhan.bluegreen.utill.GpsTracker
import com.benhan.bluegreen.HomeActivity
import com.benhan.bluegreen.network.ProgressRequestBody
import com.benhan.bluegreen.R
import com.benhan.bluegreen.adapter.HomeRecyclerAdapter
import com.benhan.bluegreen.adapter.SearchRecyclerAdapter
import com.benhan.bluegreen.dataclass.PlaceSearchData
import com.benhan.bluegreen.dataclass.ServerResonse
import com.benhan.bluegreen.listener.OnItemClickListener
import com.benhan.bluegreen.listener.ResponseListener
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.bumptech.glide.Glide
import com.daimajia.numberprogressbar.NumberProgressBar
import com.daimajia.numberprogressbar.OnProgressBarListener
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.nanchen.compresshelper.CompressHelper
import kotlinx.android.synthetic.main.plus_fragment_gallery_upload.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class PhotoUploadActivity: AppCompatActivity(),
    ProgressRequestBody.UploadCallbacks, OnProgressBarListener{



    val apiClient = ApiClient()
    val apiInterface: ApiInterface = apiClient.getApiClient().create(
        ApiInterface::class.java)
    private var etDescription: EditText? = null
    val places = ArrayList<PlaceSearchData>()
    val adapter =
        SearchRecyclerAdapter(places)
    var keyword: String = ""
    var id: Int? = null
    private var file: File? = null
    var responseListener : ResponseListener? =null
    var recyclerView: RecyclerView? = null
    var email : String? = null
    var progressBar: NumberProgressBar? = null
    var newFile: File? = null






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plus_fragment_gallery_upload)


        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setRationaleMessage("회원님과 가까운 곳을 보기 위해서는 위치 정보 접근 권한이 필요해요")
            .setDeniedMessage("언제든 [설정] > [권한] 에서 권한을 허용 하시면 가까운 곳을 보실 수 있어요")
            .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .check()

        val backgroundColor = ContextCompat.getColor(this,
            R.color.background
        )



        val selectedPhotoString: String? = intent.getStringExtra("photo")
        val selectedPhoto: String = Uri.parse(selectedPhotoString).path!!
        val actualImageFile = File(selectedPhoto)
        file = saveBitmapToFile(actualImageFile)
        newFile = CompressHelper.Builder(this)
            .setQuality(80)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .build()
            .compressToFile(file);

        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        progressBar = findViewById(R.id.number_progress_bar)


        recyclerView = findViewById(R.id.searchRecycler)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = adapter
        recyclerView?.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                hideKeyboard(this@PhotoUploadActivity)
                return false
            }


        })



        val naviColor = ContextCompat.getColor(this,
            R.color.navi
        )

        val sharedPreference = SharedPreference()
        email = sharedPreference.getString(this, "email")

        val tvPost = findViewById<TextView>(R.id.post)

        etDescription = findViewById(R.id.descriptionUpload)
        etDescription?.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE)
                etDescription?.clearFocus()
                hideKeyboard(this)
             false
        }

        tvPost.setTextColor(naviColor)
        tvPost.isClickable = false

        val ivSelectedPhoto = findViewById<ImageView>(R.id.selectedImageUpload)

        val mOnItemClickListener = object:
            OnItemClickListener {

            override fun OnItemClick(viewHolder: RecyclerView.ViewHolder, position: Int) {

                tvPost.setOnClickListener {
                    uploadToServer(etDescription?.text.toString())
                }

                val place: PlaceSearchData = adapter.placeList[position]
                val placeSelectedBefore: PlaceSearchData? = adapter.placeList.find{
                    it.isSelected
                }
                tvPost.setTextColor(backgroundColor)
                tvPost.isClickable = true
                if (!place.isSelected) {
                    placeSelectedBefore?.isSelected = false
                    place.isSelected = true
                    id = place.id
                    }
                adapter.placeList[position] = place
                adapter.notifyDataChanged()


            }

        }

        adapter.onItemClickListener = mOnItemClickListener

        Glide.with(this).load(selectedPhoto).centerCrop()
            .into(ivSelectedPhoto)





        val ivBack = findViewById<ImageView>(R.id.ivBack)
        ivBack.setOnClickListener {
           finish()
            hideKeyboard(this)
        }


    }








    private val permissionListener = object : PermissionListener {
        override fun onPermissionGranted() {

            val gpsTracker =
                GpsTracker(this@PhotoUploadActivity)
            val x = gpsTracker.fetchLatitude()
            val y = gpsTracker.fetchLongtitude()


            if(adapter.itemCount == 0)
                loadClose("",0, x, y)
            searchBar.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchBar.clearFocus()
                    hideKeyboard(this@PhotoUploadActivity)


                    keyword = searchBar.text.toString()


                    if (keyword.isEmpty()){
                        keyword = ""}
                    places.removeAll(places)
                    recyclerView?.removeAllViews()
                    adapter.notifyDataChanged()
                    loadClose(keyword, 0, x, y)
                }
                false
            }


        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            Toast.makeText(this@PhotoUploadActivity, "권한 거부", Toast.LENGTH_SHORT).show()


            if(adapter?.itemCount == 0)
                load("", 0)
            searchBar.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchBar.clearFocus()
                    hideKeyboard(this@PhotoUploadActivity)

                    keyword = searchBar.text.toString()


                    if (keyword.isEmpty()){
                        keyword = ""}
                    places.removeAll(places)
                    recyclerView?.removeAllViews()
                    adapter.notifyDataChanged()
                    load(keyword, 0)
                }
                false
            }

        }



    }

    fun load(keyword: String ,index: Int) {


        val call: Call<ArrayList<PlaceSearchData>> = apiInterface.searchPlace(keyword, index)
        call.enqueue(object: Callback<ArrayList<PlaceSearchData>> {
            override fun onFailure(call: Call<ArrayList<PlaceSearchData>>, t: Throwable) {

                Log.d("사이즈", t.message)

            }

            override fun onResponse(
                call: Call<ArrayList<PlaceSearchData>>,
                response: Response<ArrayList<PlaceSearchData>>
            ) {
                if(response.isSuccessful){
                    response.body()?.let { places.addAll(it) }
                    adapter.notifyDataChanged()
                    Log.d("사이즈", response.body()?.size.toString())
                    if (response.body()?.size == 30 && index == 0)
                        setOnLoadMoreListener()
                }
            }


        })

    }

    fun loadClose(keyword: String ,index: Int, x: Double, y: Double) {


        val call: Call<ArrayList<PlaceSearchData>> = apiInterface.searchClosePlace(keyword, index, x, y)
        call.enqueue(object: Callback<ArrayList<PlaceSearchData>> {
            override fun onFailure(call: Call<ArrayList<PlaceSearchData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<PlaceSearchData>>,
                response: Response<ArrayList<PlaceSearchData>>
            ) {
                if(response.isSuccessful){
                    response.body()?.let { places.addAll(it) }
                    adapter.notifyDataChanged()
                    if(response.body()?.size == 30 && index == 0)
                        setOnLoadCloseMoreListener()
                }
            }


        })

    }

    fun loadMore(keyword: String, index: Int){


        places.add(PlaceSearchData("load"))
        adapter.notifyItemInserted(places.size-1)

        val newIndex = index + 1
        val call: Call<ArrayList<PlaceSearchData>> = apiInterface.searchPlace(keyword, newIndex)
        call.enqueue(object : Callback<ArrayList<PlaceSearchData>> {
            override fun onFailure(call: Call<ArrayList<PlaceSearchData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<PlaceSearchData>>,
                response: Response<ArrayList<PlaceSearchData>>
            ) {
                if(response.isSuccessful){
                    places.removeAt(places.size - 1)
                    val result: ArrayList<PlaceSearchData>? = response.body()
                    if(result!!.size > 0) {

                        places.addAll(result)
                    }else {
                        adapter.isMoreDataAvailable = false
                    }
                    adapter.notifyDataChanged()
                }

            }


        })




    }

    fun loadCloseMore(keyword: String, index: Int, x: Double, y: Double){


        places.add(PlaceSearchData("load"))
        adapter.notifyItemInserted(places.size-1)

        val newIndex = index + 1
        val call: Call<ArrayList<PlaceSearchData>> = apiInterface.searchClosePlace(keyword, newIndex, x, y)
        call.enqueue(object : Callback<ArrayList<PlaceSearchData>> {
            override fun onFailure(call: Call<ArrayList<PlaceSearchData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<PlaceSearchData>>,
                response: Response<ArrayList<PlaceSearchData>>
            ) {
                if(response.isSuccessful){
                    places.removeAt(places.size - 1)
                    val result: ArrayList<PlaceSearchData>? = response.body()
                    if(result!!.size > 0) {
                        places.addAll(result)
                    }else {
                        adapter.isMoreDataAvailable = false
                    }
                    adapter.notifyDataChanged()
                }

            }


        })




    }

    fun setOnLoadMoreListener(){

        val onLoadMoreListener = object : HomeRecyclerAdapter.OnLoadMoreListener{
            override fun onLoadMore() {
                recyclerView?.post {
                    val index = places.size - 1
                    loadMore(keyword , index)
                }
            }
        }

        adapter.onLoadMoreListener = onLoadMoreListener
    }

    fun setOnLoadCloseMoreListener(){

        val onLoadMoreListener = object : HomeRecyclerAdapter.OnLoadMoreListener{
            override fun onLoadMore() {
                recyclerView?.post {
                    val index = places.size - 1
                    val gpsTracker =
                        GpsTracker(this@PhotoUploadActivity)
                    val x = gpsTracker.fetchLatitude()
                    val y = gpsTracker.fetchLongtitude()
                    loadCloseMore(keyword , index, x, y)
                }
            }
        }

        adapter.onLoadMoreListener = onLoadMoreListener
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun uploadToServer(desc: String) {

            progressBar?.visibility = View.VISIBLE


            val fileBody =
                ProgressRequestBody(
                    newFile!!,
                    "image",
                    this
                )
            val filePart = MultipartBody.Part.createFormData("file", file?.name, fileBody)
//            val requestBody = file!!.asRequestBody("image/*".toMediaTypeOrNull())
//            val fileToUpload = MultipartBody.Part.createFormData("file", file!!.name, requestBody)
            val filename = newFile!!.name.toRequestBody("text/plain".toMediaTypeOrNull())
            val mEmail = email!!.toRequestBody("text/plain".toMediaTypeOrNull())
            val mdesc = desc.toRequestBody("text/plain".toMediaTypeOrNull())
            val callUpload: Call<ServerResonse> = this@PhotoUploadActivity.apiInterface.uploadImage(
                filePart, id!!, filename, mEmail,
                mdesc
            )
            callUpload.enqueue(object : Callback<ServerResonse> {
                override fun onFailure(call: Call<ServerResonse>, t: Throwable) {
                    Log.d("에러 ", t.message)
                }
                override fun onResponse(
                    call: Call<ServerResonse>,
                    response: Response<ServerResonse>
                ) {
                    startActivity(Intent(this@PhotoUploadActivity, HomeActivity::class.java))
                    finish()
                    responseListener?.onResponse()
                    Log.d("코드", response.message())
                }
            })
    }





    override fun onProgressUpdate(percentage: Int) {
        progressBar?.progress = percentage
    }


    override fun onError() {

    }

    override fun onFinish() {


    }

    private fun saveBitmapToFile(file:File): File{
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        o.inSampleSize = 6

        var inputStream = FileInputStream(file)
        BitmapFactory.decodeStream(inputStream, null, o)
        inputStream.close()

        val REQUIRED_SIZE=75
        var scale = 1
        while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE){
            scale *= 2
        }

        val o2 = BitmapFactory.Options()
        o2.inSampleSize = scale
        inputStream = FileInputStream(file)

        val selectedBitmap = BitmapFactory.decodeStream(inputStream, null ,o2)
        inputStream.close()

        file.createNewFile()
        val outputStream = FileOutputStream(file)
        selectedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        return file
    }

    override fun onProgressChange(current: Int, max: Int) {

    }


}