package com.benhan.bluegreen.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.benhan.bluegreen.R
import com.benhan.bluegreen.adapter.HomeRecyclerAdapter
import com.benhan.bluegreen.adapter.OtherUserPostAdapter
import com.benhan.bluegreen.adapter.SearchRecyclerAdapter
import com.benhan.bluegreen.dataclass.PlaceSearchData
import com.benhan.bluegreen.dataclass.PostData
import com.benhan.bluegreen.dataclass.User
import com.benhan.bluegreen.fullpost.FullPost
import com.benhan.bluegreen.listener.OnItemClickListener
import com.benhan.bluegreen.localdata.LFVIewModel
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.benhan.bluegreen.place.PlacePage
import com.benhan.bluegreen.utill.GridDividerDecoration
import com.benhan.bluegreen.utill.MyApplication
import com.bumptech.glide.Glide
import com.pnikosis.materialishprogress.ProgressWheel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtherUser : AppCompatActivity() {


    val apiClient = ApiClient()
    val apiInterface = apiClient.getApiClient().create(ApiInterface::class.java)

    var actual_name: String? = null
    var introduction: String? = null
    var postNumber: String? = null
    var followingNumber: String? = null
    var likeNumber: String? = null
    var profilePhoto: String? = null
    var email: String? = null
    var name: String? = null
    var postDataList = ArrayList<PostData>()
    var adapter: OtherUserPostAdapter? = null
    var viewModel: LFVIewModel? = null
    var dividerDecoration: GridDividerDecoration? = null

    val places = ArrayList<PlaceSearchData>()
    var locationAdapter: SearchRecyclerAdapter? = null
    var progressWheel: ProgressWheel? = null
    var recyclerView: RecyclerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user)


        val tvUsername = findViewById<TextView>(R.id.profile_username)
        val tvActualname = findViewById<TextView>(R.id.actualName)
        val tvIntroduction = findViewById<TextView>(R.id.userIntroduction)
        val ivProfilePhoto = findViewById<ImageView>(R.id.placePhoto)
        recyclerView = findViewById<RecyclerView>(R.id.userPostRecycler)
        val tvPostNumber = findViewById<TextView>(R.id.postNumber)
        val tvFollowNumber = findViewById<TextView>(R.id.followNumber)
        val tvLikeNumber = findViewById<TextView>(R.id.likeNumber)
        val ivGrid = findViewById<ImageView>(R.id.ivGrid)
        val ivLocation = findViewById<ImageView>(R.id.ivLocation)
        val gridTab = findViewById<RelativeLayout>(R.id.layoutGrid)
        val locationTab = findViewById<RelativeLayout>(R.id.layoutLocation)
        name = intent.getStringExtra("otherUserName")
        val ivX: ImageView = findViewById(R.id.ivBack)
        progressWheel = findViewById(R.id.progress_wheel)

        recyclerView?.visibility = View.GONE

        ivX.setOnClickListener {
            finish()
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        viewModel = ViewModelProvider(this).get(LFVIewModel::class.java)

        postDataList = viewModel!!.postDataList

        progressWheel?.visibility = View.VISIBLE




        adapter =
            OtherUserPostAdapter(this, postDataList)


        val mOnItemClickListener = object :
            OnItemClickListener {
            override fun OnItemClick(viewHolder: RecyclerView.ViewHolder, position: Int) {

                val name = postDataList[position].userName
                val post_id = postDataList[position].postId


                val intent = Intent(this@OtherUser, FullPost::class.java)
                intent.putExtra("place_or_user_name", name)
                intent.putExtra("post_id", post_id)
                startActivity(intent)

            }


        }


        adapter?.onItemClickListener = mOnItemClickListener


        ///////////////////---------=====recyclerView?======-----/////////////////////////////////////////////////////////////////////////////////

        dividerDecoration = GridDividerDecoration(
            resources,
            R.drawable.divider_recyler_gallery
        )
        recyclerView?.layoutManager = GridLayoutManager(this, 3)
        recyclerView?.removeItemDecoration(dividerDecoration!!)
        recyclerView?.addItemDecoration(dividerDecoration!!)
        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.adapter = adapter


//////////////////////////////////////////////////////////////////////////////////////////////

        gridTab.isClickable = false
        locationTab.isClickable = true

        val call: Call<User> = apiInterface.getOtherUserData(name!!)
        call.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {

                Log.d("아더유저", t.message)


            }

            override fun onResponse(call: Call<User>, response: Response<User>) {

                Log.d("아더유저", response.message())

                val res = response.body()!!

                actual_name = res.actualname
                introduction = res.introduction
                postNumber = res.postNumber.toString()
                followingNumber = res.followerNumber.toString()
                likeNumber = res.likeNumber.toString()
                profilePhoto = res.profilephoto
                email = res.email
                tvUsername.text = name
                tvActualname.text = actual_name
                tvIntroduction.text = introduction
                tvPostNumber.text = postNumber
                tvFollowNumber.text = followingNumber
                tvLikeNumber.text = likeNumber
                if (!profilePhoto.isNullOrEmpty()) {
                    val profilePhotoUri = MyApplication.severUrl + profilePhoto
                    Glide.with(this@OtherUser)
                        .load(profilePhotoUri)
                        .override(ivProfilePhoto.width, ivProfilePhoto.height)
                        .into(ivProfilePhoto)
                }


                if (postDataList.size == 0 && email != null)
                    load(email!!, 0)
            }

        })


///////////////////////////////////////////////////////////////////////////////////////////


        gridTab.setOnClickListener {

            gridTab.isClickable = false
            locationTab.isClickable = true



            gridTab.setBackgroundResource(R.drawable.button_shape_stroke)
            ivGrid.setImageResource(R.drawable.grid_green)

            locationTab.setBackgroundResource(R.drawable.button_shape_green)
            ivLocation.setImageResource(R.drawable.location_white)




            adapter = OtherUserPostAdapter(
                this,
                postDataList
            )


            val mOnItemClickListener = object :
                OnItemClickListener {
                override fun OnItemClick(viewHolder: RecyclerView.ViewHolder, position: Int) {

                    val name = postDataList[position].userName
                    val post_id = postDataList[position].postId


                    val intent = Intent(this@OtherUser, FullPost::class.java)
                    intent.putExtra("place_or_user_name", name)
                    intent.putExtra("post_id", post_id)
                    startActivity(intent)

                }


            }


            adapter?.onItemClickListener = mOnItemClickListener



            recyclerView?.removeAllViews()
            dividerDecoration = GridDividerDecoration(
                resources,
                R.drawable.divider_recyler_gallery
            )
            recyclerView?.layoutManager = GridLayoutManager(this, 3)

            recyclerView?.removeItemDecoration(dividerDecoration!!)

            recyclerView?.addItemDecoration(dividerDecoration!!)
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = adapter


            if (adapter?.itemCount == 0) {
                progressWheel?.visibility = View.VISIBLE
                recyclerView?.visibility = View.GONE
                load(email!!, 0)
            }


        }


///////////////////////////////////////////////////////////////////////////////////////////

        locationTab.setOnClickListener {

            locationAdapter =
                SearchRecyclerAdapter(places)
            recyclerView?.removeItemDecoration(dividerDecoration!!)



            gridTab.isClickable = true
            locationTab.isClickable = false

            gridTab.setBackgroundResource(R.drawable.button_shape_green)
            ivGrid.setImageResource(R.drawable.grid_white)

            locationTab.setBackgroundResource(R.drawable.button_shape_stroke)
            ivLocation.setImageResource(R.drawable.location_green)


            recyclerView?.removeAllViews()

            recyclerView?.adapter = locationAdapter
            recyclerView?.layoutManager = LinearLayoutManager(this)

            val mOnItemClickListener = object :
                OnItemClickListener {

                override fun OnItemClick(viewHolder: RecyclerView.ViewHolder, position: Int) {

                    val intent = Intent(this@OtherUser, PlacePage::class.java)
                    val selectedPlace = places[position]
                    val placeName = selectedPlace.name
                    val placeId = selectedPlace.id
                    val placePhoto = selectedPlace.photo
                    val placeType = selectedPlace.type
                    val placeProvince = selectedPlace.province
                    intent.putExtra("placeName", placeName)
                    intent.putExtra("placeId", placeId)
                    intent.putExtra("placePhoto", placePhoto)
                    intent.putExtra("placeType", placeType)
                    intent.putExtra("placeProvince", placeProvince)
                    startActivity(intent)


                }

            }

            locationAdapter!!.onItemClickListener = mOnItemClickListener
            if (places.size == 0){
                progressWheel?.visibility = View.VISIBLE
                recyclerView?.visibility = View.GONE
                loadPlace(email!!, 0)
            }


        }


/////////////////////////function/////////////////////////////////////////


    }



    fun load(email: String, index: Int) {
        val call: Call<ArrayList<PostData>> = apiInterface.getOtherUserPost(email, index)
        call.enqueue(object : Callback<ArrayList<PostData>> {
            override fun onFailure(call: Call<ArrayList<PostData>>, t: Throwable) {

                Log.d("콜에러", t.message)
            }

            override fun onResponse(
                call: Call<ArrayList<PostData>>,
                response: Response<ArrayList<PostData>>
            ) {
                if(response.body()!!.size == 30){
                    adapter!!.addLoadMoreListener(object : OtherUserPostAdapter.OnLoadMoreListener{
                        override fun onLoadMore() {
                            recyclerView?.post{
                                val index = postDataList.size - 1
                                loadMore(email, index)
                            }
                        }

                    })
                }
                progressWheel?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
                Log.d("콜에러", response.message())
                response.body()?.let { postDataList.addAll(it) }
                adapter!!.notifyDataChanged()
            }

        })


    }

    fun loadMore(email: String, index: Int) {


        postDataList.add(PostData("load"))
        adapter!!.notifyItemInserted(postDataList.size - 1)

        val newindex = index + 1

        val call: Call<ArrayList<PostData>> = apiInterface.getOtherUserPost(email, newindex)
        call.enqueue(object : Callback<ArrayList<PostData>> {
            override fun onFailure(call: Call<ArrayList<PostData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<PostData>>,
                response: Response<ArrayList<PostData>>
            ) {

                if (response.isSuccessful) {
                    if (postDataList.size > 0)
                        postDataList.removeAt(postDataList.size - 1)

                    val result: ArrayList<PostData>? = response.body()


                    if (!result.isNullOrEmpty() && result.size > 0) {
                        postDataList.addAll(result)
                    } else {

                        adapter!!.isMoreDataAvailable = false

                    }
                    adapter!!.notifyDataChanged()

                }
            }


        })


    }

    fun loadPlace(email: String, index: Int) {


        val call: Call<ArrayList<PlaceSearchData>> = apiInterface.getOtherUserPlace(email, index)

        call.enqueue(object : Callback<ArrayList<PlaceSearchData>> {
            override fun onFailure(call: Call<ArrayList<PlaceSearchData>>, t: Throwable) {

                Log.d("실패", t.message)
            }

            override fun onResponse(
                call: Call<ArrayList<PlaceSearchData>>,
                response: Response<ArrayList<PlaceSearchData>>
            ) {



                if (response.isSuccessful) {
                    progressWheel?.visibility = View.GONE
                    recyclerView?.visibility = View.VISIBLE
                    response.body()?.let { places.addAll(it) }
                    locationAdapter?.notifyDataChanged()
                    Log.d("플레이스", places.isEmpty().toString())
                    if (response.body()?.size!! == 20 && index == 0) {
                        val onLoadMoreListener = object : HomeRecyclerAdapter.OnLoadMoreListener {

                            override fun onLoadMore() {
                                recyclerView?.post(object : Runnable {
                                    override fun run() {

                                        val index = places.size - 1

                                        loadMorePlace(email!!, index)

                                    }

                                })

                            }
                        }

                        locationAdapter!!.onLoadMoreListener = onLoadMoreListener
                    }
                } else {
                    Log.d("메세지", response.isSuccessful.toString())
                }
            }


        })


    }

    fun loadMorePlace(email: String, startRow: Int) {


        places.add(PlaceSearchData("load"))
        locationAdapter?.notifyItemInserted(places.size - 1)


        val newindex = startRow + 1
        val call: Call<ArrayList<PlaceSearchData>> = apiInterface.getOtherUserPlace(email, newindex)
        call.enqueue(object : Callback<ArrayList<PlaceSearchData>> {
            override fun onFailure(call: Call<ArrayList<PlaceSearchData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<PlaceSearchData>>,
                response: Response<ArrayList<PlaceSearchData>>
            ) {

                if (response.isSuccessful) {
                    if (places.size > 0)
                        places.removeAt(places.size - 1)
                    val result: ArrayList<PlaceSearchData>? = response.body()

                    if (!result.isNullOrEmpty() && result.size > 0) {

                        places.addAll(result)
                    } else {
                        locationAdapter?.isMoreDataAvailable = false

                    }

                    locationAdapter?.notifyDataChanged()


                } else {

                    Log.e("콜", response.message())

                }

            }


        })


    }
}
