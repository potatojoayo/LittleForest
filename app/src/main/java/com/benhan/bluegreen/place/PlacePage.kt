package com.benhan.bluegreen.place

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.benhan.bluegreen.R
import com.benhan.bluegreen.adapter.HomeRecyclerAdapter
import com.benhan.bluegreen.adapter.PostImageSearchAdapter
import com.benhan.bluegreen.dataclass.PlacePageData
import com.benhan.bluegreen.dataclass.PostImageData
import com.benhan.bluegreen.dataclass.ServerResonse
import com.benhan.bluegreen.fullpost.FullPost
import com.benhan.bluegreen.listener.OnItemClickListener
import com.benhan.bluegreen.listener.ResponseListener
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.benhan.bluegreen.plus.PlusGalleryActivity
import com.benhan.bluegreen.utill.GridDividerDecoration
import com.benhan.bluegreen.utill.MyApplication
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.pnikosis.materialishprogress.ProgressWheel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PlacePage : AppCompatActivity() {

    val apiClient = ApiClient()
    val apiInterface = apiClient.getApiClient().create(ApiInterface::class.java)
    val postDataList = ArrayList<PostImageData>()
    val adapter =
        PostImageSearchAdapter(this, postDataList)
    var placeId  = 1
    var pageRecyclerView : RecyclerView? = null
    var welcome: TextView? = null

    var isFollowing: Boolean? = null
    var isLiking = false
    var name : String? = null
    var tvWhenEmptyPost : TextView? = null

    var progressWheel: ProgressWheel? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_page)

        val sharedPreference = SharedPreference()
        val email = sharedPreference.getString(this, "email")!!
        val backgroundColor = ContextCompat.getColor(this,
            R.color.background
        )
        val whiteColor = ContextCompat.getColor(this,
            R.color.white
        )


        progressWheel = findViewById(R.id.progress_wheel)
        val tvTopPageName = findViewById<TextView>(R.id.topPlaceName)
        val ivPlacePhoto = findViewById<ImageView>(R.id.placePhoto)
        val tvPostNumber = findViewById<TextView>(R.id.postNumber)
        val tvFollowerNumber = findViewById<TextView>(R.id.followerNumber)
        val tvLikeNumber = findViewById<TextView>(R.id.likeNumber)
        val tvPlaceName = findViewById<TextView>(R.id.placeName)
        val tvProvince = findViewById<TextView>(R.id.placeProvince)
        val tvType = findViewById<TextView>(R.id.placeType)
        val btnPost = findViewById<ImageView>(R.id.btnPost)
        val btnFollow = findViewById<Button>(R.id.btnFollow)
        pageRecyclerView = findViewById(R.id.pageRecycler)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val ivLike = findViewById<ImageView>(R.id.ivLike)
        welcome = findViewById(R.id.welcome)
        name = sharedPreference.getString(this, "name")


        pageRecyclerView?.visibility = View.GONE

        tvWhenEmptyPost = findViewById(R.id.tvWhenEmptyPost)


        val permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        val permissionListener = object : PermissionListener {

            override fun onPermissionGranted() {
                startActivity(Intent(this@PlacePage, PlusGalleryActivity::class.java))
            }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@PlacePage, "권한 거부\n" + deniedPermissions.toString(),
                    Toast.LENGTH_SHORT).show()
            }
        }




        val placeName = intent.getStringExtra("placeName")
        placeId = intent.getIntExtra("placeId", 1)
        val placePhoto = intent.getStringExtra("placePhoto")
        val placeType = intent.getStringExtra("placeType")
        val placeProvince = intent.getStringExtra("placeProvince")

        Log.d("아이디", placeId.toString())

        btnPost.setOnClickListener {

            if(permissionCheck == PackageManager.PERMISSION_DENIED) {


                TedPermission.with(this)
                    .setPermissionListener(permissionListener)
                    .setRationaleMessage("사진첩을 열기 위해서는 갤러리 접근 권한이 필요해요")
                    .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있어요")
                    .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check()
            }


            else if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this, PlusGalleryActivity::class.java)
                intent.putExtra("place_id", placeId)
                startActivity(intent)
                finish()

            }

        }

        val checkFollowing: Call<ServerResonse> = apiInterface.checkFollowing(placeId, email)
        checkFollowing.enqueue(object : Callback<ServerResonse>{
            override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

                Log.d("팔로잉", t.message)

            }

            override fun onResponse(call: Call<ServerResonse>, response: Response<ServerResonse>) {

                    isFollowing = response.body()?.isFollowing!!

                    Log.d("팔로잉", isFollowing.toString())
                    if (isFollowing as Boolean) {
                        btnFollow.setBackgroundResource(R.drawable.button_shape_stroke)
                        btnFollow.setTextColor(backgroundColor)
                        btnFollow.text = "팔로잉"
                        return

                    } else{

                        btnFollow.setBackgroundResource(R.drawable.button_shape_green)
                        btnFollow.setTextColor(whiteColor)
                        btnFollow.text = "팔로우"

                        return

                    }

            }


        })
        val checkLiking: Call<ServerResonse> = apiInterface.checkLike(placeId, email)
        checkLiking.enqueue(object: Callback<ServerResonse>{
            override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

            }

            override fun onResponse(call: Call<ServerResonse>, response: Response<ServerResonse>) {

                isLiking = response.body()?.isLiking!!
                if (isLiking){
                    ivLike.setImageResource(R.drawable.tree_selected)
                } else{
                    ivLike.setImageResource(R.drawable.tree)



                }
            }
        })









        ivBack.setOnClickListener {

            finish()
        }




        tvTopPageName.text = placeName
        tvPlaceName.text = placeName
        tvProvince.text = placeProvince
        tvType.text = placeType

        if(!placePhoto.isNullOrEmpty()) {
            val placePhotoUri = MyApplication.severUrl + placePhoto
            Glide.with(this).load(placePhotoUri).override(ivPlacePhoto.width, ivPlacePhoto.height)
                .into(ivPlacePhoto)
        }
        val callGetPageInfo: Call<PlacePageData> = apiInterface.getPageInfo(placeId)
        callGetPageInfo.enqueue(object : Callback<PlacePageData>{
            override fun onFailure(call: Call<PlacePageData>, t: Throwable) {

            }

            override fun onResponse(call: Call<PlacePageData>, response: Response<PlacePageData>) {
                tvPostNumber.text = response.body()!!.postNumber.toString()
                tvFollowerNumber.text = response.body()!!.followerNumber.toString()
                tvLikeNumber.text = response.body()!!.likeNumber.toString()
            }


        })


        ivLike.setOnClickListener {
            if(!isLiking){
                ivLike.setImageResource(R.drawable.tree_selected)
                tvLikeNumber.text = (Integer.parseInt(tvLikeNumber.text.toString()) + 1).toString()

                val toLike: Call<ServerResonse> = apiInterface.likePlace(placeId, email, name!!)
                toLike.enqueue(object : Callback<ServerResonse>{
                    override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

                    }

                    override fun onResponse(
                        call: Call<ServerResonse>,
                        response: Response<ServerResonse>
                    ) {
                        isLiking = true
                    }
                })


            } else {
                ivLike.setImageResource(R.drawable.tree)
                tvLikeNumber.text = (Integer.parseInt(tvLikeNumber.text.toString()) -1 ).toString()
                val toUnLike: Call<ServerResonse> = apiInterface.unlikePlace(placeId, email, isLiking)
                toUnLike.enqueue(object : Callback<ServerResonse>{
                    override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

                    }

                    override fun onResponse(
                        call: Call<ServerResonse>,
                        response: Response<ServerResonse>
                    ) {
                        isLiking = false
                    }
                })
            }


        }

        btnFollow.setOnClickListener {




            if(!isFollowing!!) {
                isFollowing = true
                btnFollow.setBackgroundResource(R.drawable.button_shape_stroke)
                btnFollow.setTextColor(backgroundColor)
                btnFollow.text = "팔로잉"
                tvFollowerNumber.text = ( Integer.parseInt(tvFollowerNumber.text.toString()) + 1 ).toString()
                val toFollow: Call<ServerResonse> =
                    apiInterface.followPlace(placeId, email, isFollowing!!)
                toFollow.enqueue(object : Callback<ServerResonse> {
                    override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

                    }

                    override fun onResponse(
                        call: Call<ServerResonse>,
                        response: Response<ServerResonse>
                    ) {

                    }


                })


            }else {
                isFollowing = false
                btnFollow.setBackgroundResource(R.drawable.button_shape_green)
                btnFollow.setTextColor(whiteColor)
                btnFollow.text = "팔로우"
                tvFollowerNumber.text = ( Integer.parseInt(tvFollowerNumber.text.toString()) - 1 ).toString()
                val toUnFollow: Call<ServerResonse> =
                    apiInterface.unfollowPlace(placeId, email, isFollowing!!)
                toUnFollow.enqueue(object : Callback<ServerResonse> {
                    override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

                    }

                    override fun onResponse(
                        call: Call<ServerResonse>,
                        response: Response<ServerResonse>
                    ) {

                    }


                })



            }
        }





        val responseListener= object :
            ResponseListener {
            override fun onResponse() {
                getPostData(placeId, 0)
            }


        }
        val photoUploadToPageActivity =
            PhotoUploadToPageActivity()
        photoUploadToPageActivity.responseListener = responseListener



        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





        val dividerDecoration = GridDividerDecoration(
            resources,
            R.drawable.divider_recyler_gallery
        )
        val mOnItemClickListener = object:
            OnItemClickListener {
            override fun OnItemClick(viewHolder: RecyclerView.ViewHolder, position: Int) {


                val name = sharedPreference.getString(this@PlacePage, "name")
                val post_id = postDataList[position].postId

                val intent = Intent(this@PlacePage, FullPost::class.java)
                intent.putExtra("place_or_user_name", name)
                intent.putExtra("post_id", post_id)
                startActivity(intent)
            }



        }











        adapter.onItemClickListener = mOnItemClickListener





        pageRecyclerView?.addItemDecoration(dividerDecoration)
            pageRecyclerView?.itemAnimator = DefaultItemAnimator()
            pageRecyclerView?.adapter = adapter
            pageRecyclerView?.layoutManager = GridLayoutManager(this@PlacePage, 3)



        getPostData(placeId, 0)



        val swipeLayout = findViewById<SwipeRefreshLayout>(R.id.swipeLayout)
        swipeLayout.setColorSchemeColors(backgroundColor)
        swipeLayout?.setOnRefreshListener {
            postDataList.removeAll(postDataList)
            adapter.notifyDataChanged()
            getPostData(placeId, 0)
            swipeLayout.isRefreshing = false
        }


    }


    override fun onResume() {
        super.onResume()
        if(MyApplication.isChanged){
            postDataList.removeAll(postDataList)
            getPostData(placeId, 0)
            MyApplication.isChanged = false
        }
    }



    fun getPostData(place_id: Int, index: Int){
        val callGetPagePosts: Call<ArrayList<PostImageData>> = apiInterface.getPagePosts(place_id, index)
        callGetPagePosts.enqueue(object : Callback<ArrayList<PostImageData>>{
            override fun onFailure(call: Call<ArrayList<PostImageData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<PostImageData>>,
                response: Response<ArrayList<PostImageData>>
            ) {
                progressWheel?.visibility = View.GONE
                response.body()?.let { postDataList.addAll(it) }
                adapter.notifyDataChanged()
                if(postDataList.size == 0){
                    tvWhenEmptyPost?.visibility = View.VISIBLE
                } else {
                    pageRecyclerView?.visibility = View.VISIBLE
                }



            }


        })
    }

    fun getMorePostData(place_id: Int, index: Int){

        postDataList.add(PostImageData("load"))
        adapter.notifyItemInserted(postDataList.size-1)

        val newIndex = index + 1
        val call: Call<ArrayList<PostImageData>> = apiInterface.getPagePosts(place_id, newIndex)
        call.enqueue(object : Callback<ArrayList<PostImageData>> {
            override fun onFailure(call: Call<ArrayList<PostImageData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<PostImageData>>,
                response: Response<ArrayList<PostImageData>>
            ) {
                if(response.isSuccessful){
                    postDataList.removeAt(postDataList.size - 1)
                    val result: ArrayList<PostImageData>? = response.body()
                    if(result!!.size > 0) {
                        postDataList.addAll(result)
                        if(result.size == 20){
                            setOnLoadMoreListener()
                        }
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
                pageRecyclerView?.post(object : Runnable{
                    override fun run() {
                        val index = postDataList.size - 1
                        getMorePostData(placeId , index)
                    }

                })
            }
        }

        adapter.loadMoreListener = onLoadMoreListener
    }


}
