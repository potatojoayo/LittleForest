package com.benhan.bluegreen.bell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.benhan.bluegreen.HomeActivity
import com.benhan.bluegreen.R
import com.benhan.bluegreen.adapter.BellAdapter
import com.benhan.bluegreen.adapter.HomeRecyclerAdapter
import com.benhan.bluegreen.dataclass.BellData
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.pnikosis.materialishprogress.ProgressWheel
import kotlinx.android.synthetic.main.home_fragment_bell.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentBell: Fragment() {

    val apiClient = ApiClient()
    val apiInterface = apiClient.getApiClient().create(ApiInterface::class.java)
    val bellDataList = ArrayList<BellData>()
    val sharedPreference = SharedPreference()
    var adapter: BellAdapter? = null
    var myEmail: String? = null
    var bell: ImageView? = null
    var recyclerView: RecyclerView? = null
    var progressWheel: ProgressWheel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.home_fragment_bell, container, false)

        myEmail = sharedPreference.getString(requireContext(), "email")

        recyclerView = rootView.findViewById(R.id.recyclerview)
        progressWheel = rootView.findViewById(R.id.progress_wheel)

        recyclerView?.visibility = View.GONE
        progressWheel?.visibility = View.VISIBLE


        bell = requireActivity().findViewById(R.id.bell)


        /*=========================================================================================================*/


        adapter = BellAdapter(
            requireContext(),
            bellDataList
        )
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.adapter = adapter


        val swipeRefreshLayout = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeLayout)
        val backgroundColor = ContextCompat.getColor(requireContext(),
            R.color.background
        )
        swipeRefreshLayout?.setColorSchemeColors(backgroundColor)


        swipeRefreshLayout.setOnRefreshListener {

            bellDataList.removeAll(bellDataList)
            getNotificationData(0)
            adapter?.isMoreDataAvailable = true
            swipeRefreshLayout.isRefreshing = false

        }



        if(adapter?.itemCount == 0)
        getNotificationData(0)



        return rootView

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(hidden)
            (activity as HomeActivity).clickHandler(bell!!)
        else
            bell?.setOnClickListener {
                recyclerView?.smoothScrollToPosition(0)
            }

    }


    fun getNotificationData(index: Int) {

        val call: Call<ArrayList<BellData>> = apiInterface.getNotification(myEmail!!, index)
        call.enqueue(object : Callback<ArrayList<BellData>>{
            override fun onFailure(call: Call<ArrayList<BellData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<BellData>>,
                response: Response<ArrayList<BellData>>
            ) {
                progressWheel?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
                response.body()?.let { bellDataList.addAll(it) }
                adapter?.notifyDataSetChanged()
                if(response.body()?.size == 20 && index == 0){
                    val onLoadMoreListener = object : HomeRecyclerAdapter.OnLoadMoreListener {
                        override fun onLoadMore() {

                            recyclerview!!.post(object : Runnable {
                                override fun run() {
                                    val index = bellDataList.size - 1
                                    getMoreNotificationData(index)
                                }

                            })
                        }

                    }


                    adapter?.onLoadMoreListener = onLoadMoreListener
                }
            }


        })


    }

    fun getMoreNotificationData(index: Int) {

        bellDataList.add(BellData("load"))
        adapter?.notifyItemInserted(bellDataList.size - 1)

        val newIndex = index + 1
        val call: Call<ArrayList<BellData>> = apiInterface.getNotification(myEmail!!, newIndex)
        call.enqueue(object : Callback<ArrayList<BellData>>{
            override fun onFailure(call: Call<ArrayList<BellData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<BellData>>,
                response: Response<ArrayList<BellData>>
            ) {
                if(response.isSuccessful){
                    bellDataList.removeAt(bellDataList.size - 1)
                    val result: ArrayList<BellData>? = response.body()
                    if(result!!.size > 0 ){
                        bellDataList.addAll(result)
                    }else{
                        adapter!!.isMoreDataAvailable = false
                    }
                }
                adapter?.notifyDataChanged()
            }


        })


    }

}
