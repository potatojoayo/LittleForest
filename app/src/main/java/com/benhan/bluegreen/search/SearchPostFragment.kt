package com.benhan.bluegreen.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.benhan.bluegreen.utill.GpsTracker
import com.benhan.bluegreen.utill.GridDividerDecoration
import com.benhan.bluegreen.R
import com.benhan.bluegreen.fullpost.FullPost
import com.benhan.bluegreen.adapter.HomeRecyclerAdapter
import com.benhan.bluegreen.adapter.PostImageSearchAdapter
import com.benhan.bluegreen.dataclass.PostImageData
import com.benhan.bluegreen.listener.OnItemClickListener
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.benhan.bluegreen.utill.MyApplication
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchPostFragment: Fragment() {


    val postImageDataList = ArrayList<PostImageData>()
    val apiClient = ApiClient()
    val apiInterface = apiClient.getApiClient().create(ApiInterface::class.java)
    var adapter: PostImageSearchAdapter? = null
    var recyclerView: RecyclerView? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.search_post_fragment, container, false)


        TedPermission.with(requireContext())
            .setPermissionListener(permissionListener)
            .setRationaleMessage("회원님과 가까운 곳을 보기 위해서는 위치 정보 접근 권한이 필요해요")
            .setDeniedMessage("언제든 [설정] > [권한] 에서 권한을 허용 하시면 가까운 곳을 보실 수 있어요")
            .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .check()





        recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview)
        val dividerDecoration = GridDividerDecoration(
            resources,
            R.drawable.divider_recyler_gallery
        )
        recyclerView?.hasFixedSize()
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.addItemDecoration(dividerDecoration)

        adapter = PostImageSearchAdapter(
            requireContext(),
            postImageDataList
        )
        recyclerView?.adapter = adapter

        val mOnItemClickListener = object:
            OnItemClickListener {
            override fun OnItemClick(viewHolder: RecyclerView.ViewHolder, position: Int) {
                val name = postImageDataList[position].name
                val post_id = postImageDataList[position].postId
                val intent = Intent(requireContext(), FullPost::class.java)
                intent.putExtra("place_or_user_name", name)
                intent.putExtra("post_id", post_id)
                startActivity(intent)
            }
        }











        ////////////swipe /////////////


        swipeRefreshLayout = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeLayout)
        val backgroundColor = ContextCompat.getColor(requireContext(),
            R.color.background
        )
        swipeRefreshLayout?.setColorSchemeColors(backgroundColor)




        recyclerView?.layoutManager = GridLayoutManager(requireContext(), 3)

        adapter!!.onItemClickListener = mOnItemClickListener


        return rootView

    }



    private val permissionListener = object : PermissionListener {
        override fun onPermissionGranted() {

            val gpsTracker =
                GpsTracker(requireContext())
            val x = gpsTracker.fetchLatitude()
            val y = gpsTracker.fetchLongtitude()

            swipeRefreshLayout?.setOnRefreshListener {
                postImageDataList.removeAll(postImageDataList)
                adapter?.notifyDataChanged()
                loadClose(0, x, y)
                adapter?.isMoreDataAvailable = true
                swipeRefreshLayout?.isRefreshing = false
            }

            if(adapter?.itemCount == 0)
                loadClose(0, x, y)


        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            Toast.makeText(requireContext(), "권한 거부", Toast.LENGTH_SHORT).show()


            swipeRefreshLayout?.setOnRefreshListener {
                postImageDataList.removeAll(postImageDataList)
                adapter?.notifyDataChanged()
                load(0)
                adapter?.isMoreDataAvailable = true
                swipeRefreshLayout?.isRefreshing = false
            }
            if(adapter?.itemCount == 0)
                load(0)

        }



    }








    fun load(index: Int){

        val call: Call<ArrayList<PostImageData>> = apiInterface.getRandomPostImage(index)
        call.enqueue(object: Callback<ArrayList<PostImageData>> {
            override fun onFailure(call: Call<ArrayList<PostImageData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<PostImageData>>,
                response: Response<ArrayList<PostImageData>>
            ) {

                if(response.isSuccessful) {
                    MyApplication.isChanged = false
                    response.body()?.let { postImageDataList.addAll(it) }
                    adapter?.notifyDataChanged()
                    if(response.body()?.size == 30 && index == 0){
                        setOnLoadMoreListener()
                    }
                }
            }


        })


    }

    fun loadClose(index: Int, x: Double, y:Double){

        val call: Call<ArrayList<PostImageData>> = apiInterface.getCloseRandomPostImage(index, x, y)
        call.enqueue(object: Callback<ArrayList<PostImageData>> {
            override fun onFailure(call: Call<ArrayList<PostImageData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<PostImageData>>,
                response: Response<ArrayList<PostImageData>>
            ) {

                if(response.isSuccessful) {
                    response.body()?.let { postImageDataList.addAll(it) }
                    adapter?.notifyDataChanged()
                    if(response.body()?.size == 30 && index == 0){
                        setOnLoadCloseMoreListener(x, y)
                    }
                }
            }


        })


    }

    fun loadMore(index: Int){


        postImageDataList.add(PostImageData("load"))
        adapter?.notifyItemInserted(postImageDataList.size - 1)

        val newIndex = index + 1
        val call: Call<ArrayList<PostImageData>> = apiInterface.getRandomPostImage(newIndex)
        call.enqueue(object : Callback<ArrayList<PostImageData>> {
            override fun onFailure(call: Call<ArrayList<PostImageData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<PostImageData>>,
                response: Response<ArrayList<PostImageData>>
            ) {
                if(response.isSuccessful){
                    postImageDataList.removeAt(postImageDataList.size - 1)
                    val result: ArrayList<PostImageData>? = response.body()
                    if(result!!.size > 0) {
                        postImageDataList.addAll(result)
                    }else {
                        adapter!!.isMoreDataAvailable = false
                    }
                    adapter!!.notifyDataChanged()
                }

            }

        })




    }

    fun loadCloseMore(index: Int, x: Double, y:Double){


        postImageDataList.add(PostImageData("load"))
        adapter?.notifyItemInserted(postImageDataList.size - 1)

        val newIndex = index + 1
        val call: Call<ArrayList<PostImageData>> = apiInterface.getCloseRandomPostImage(newIndex, x, y)
        call.enqueue(object : Callback<ArrayList<PostImageData>> {
            override fun onFailure(call: Call<ArrayList<PostImageData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<PostImageData>>,
                response: Response<ArrayList<PostImageData>>
            ) {
                if(response.isSuccessful){
                    postImageDataList.removeAt(postImageDataList.size - 1)
                    val result: ArrayList<PostImageData>? = response.body()
                    if(result!!.size > 0) {
                        postImageDataList.addAll(result)
                        if(result.size ==30)
                            setOnLoadCloseMoreListener(x, y)
                    }else {
                        adapter!!.isMoreDataAvailable = false
                    }
                    adapter!!.notifyDataChanged()
                }

            }

        })




    }

    fun setOnLoadMoreListener(){

        val onLoadMoreListener = object: HomeRecyclerAdapter.OnLoadMoreListener{
            override fun onLoadMore() {

                recyclerView!!.post(object : Runnable{
                    override fun run() {
                        val index = postImageDataList.size-1
                        loadMore(index)
                    }

                })
            }

        }

        adapter!!.loadMoreListener = onLoadMoreListener

    }

    fun setOnLoadCloseMoreListener(x: Double, y: Double){

        val onLoadMoreListener = object: HomeRecyclerAdapter.OnLoadMoreListener{
            override fun onLoadMore() {

                recyclerView!!.post(object : Runnable{
                    override fun run() {
                        val index = postImageDataList.size-1
                        loadCloseMore(index, x, y)
                    }

                })
            }

        }

        adapter!!.loadMoreListener = onLoadMoreListener

    }

}