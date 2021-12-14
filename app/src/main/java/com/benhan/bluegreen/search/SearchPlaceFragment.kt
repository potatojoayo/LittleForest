package com.benhan.bluegreen.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.benhan.bluegreen.utill.GpsTracker
import com.benhan.bluegreen.place.PlacePage
import com.benhan.bluegreen.R
import com.benhan.bluegreen.adapter.HomeRecyclerAdapter
import com.benhan.bluegreen.adapter.SearchRecyclerAdapter
import com.benhan.bluegreen.dataclass.PlaceSearchData
import com.benhan.bluegreen.listener.OnItemClickListener
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.pnikosis.materialishprogress.ProgressWheel
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchPlaceFragment: Fragment() {


    val apiClient = ApiClient()
    val apiInterface = apiClient.getApiClient().create(ApiInterface::class.java)
    var adapter: SearchRecyclerAdapter? = null
    val places = ArrayList<PlaceSearchData>()
    var keyword = ""
    var searchBar: EditText? = null
    var recyclerView: RecyclerView? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var progressWheel: ProgressWheel? = null




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootview = layoutInflater.inflate(R.layout.search_place_fragment, container, false)

        recyclerView = rootview.findViewById(R.id.recyclerview)
        adapter = SearchRecyclerAdapter(places)

        progressWheel = rootview.findViewById(R.id.progress_wheel)



        TedPermission.with(requireContext())
            .setPermissionListener(permissionListener)
            .setRationaleMessage("회원님과 가까운 곳을 보기 위해서는 위치 정보 접근 권한이 필요해요")
            .setDeniedMessage("언제든 [설정] > [권한] 에서 권한을 허용 하시면 가까운 곳을 보실 수 있어요")
            .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .check()

        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.setHasFixedSize(true)
        recyclerView?.setItemViewCacheSize(20)
        adapter?.setHasStableIds(true)



        val placeOnItemClickListener = object:
            OnItemClickListener {

            override fun OnItemClick(viewHolder: RecyclerView.ViewHolder, position: Int) {

                val intent = Intent(requireContext(), PlacePage::class.java)
                val selectedPlace = places[position]
                val placeName = selectedPlace.name
                val placeId =selectedPlace.id
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
        adapter?.onItemClickListener = placeOnItemClickListener


        recyclerView?.adapter = adapter






        //////////////swipe ///////////////////

//        swipeRefreshLayout = rootview.findViewById(R.id.swipeLayout)
//        val backgroundColor = ContextCompat.getColor(requireContext(),
//            R.color.background
//        )
//        swipeRefreshLayout?.setColorSchemeColors(backgroundColor)





        searchBar = rootview.findViewById(R.id.searchBar)



        val navi =  requireActivity().findViewById<LinearLayout>(R.id.navigation_bar)
        KeyboardVisibilityEvent.setEventListener(
            activity,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {

                    val handler = Handler()

                    handler.postDelayed(object : Runnable{
                        override fun run() {

                            if(!isOpen)
                                navi.visibility = View.VISIBLE
                        }
                    }, 10)
                    if(isOpen)
                    { navi.visibility = View.GONE}
                }
            })





        return rootview
    }


    override fun onResume() {
        super.onResume()
        searchBar?.text = null
    }




    private val permissionListener = object : PermissionListener {
        override fun onPermissionGranted() {

            val gpsTracker =
                GpsTracker(requireContext())
            val x = gpsTracker.fetchLatitude()
            val y = gpsTracker.fetchLongtitude()

//            swipeRefreshLayout?.setOnRefreshListener {
//                places.removeAll(places)
//                adapter?.notifyDataChanged()
//                loadClose("",0, x, y)
//                searchBar?.text = null
//                adapter?.isMoreDataAvailable = true
//                swipeRefreshLayout?.isRefreshing = false
//            }
            searchBar?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(
                    v: TextView?,
                    actionId: Int,
                    event: KeyEvent?
                ): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_DONE){
                        keyword = searchBar?.text.toString()

                        if (!keyword.isNullOrEmpty()){
                            places.removeAll(places)
                            recyclerView?.removeAllViews()
                            loadClose(keyword, 0,x , y)
                        }
                        hideKeyboard(requireActivity())
                        searchBar?.clearFocus()

                    }
                    return false
                }

            })
            if(adapter?.itemCount == 0) {
                progressWheel?.visibility = View.VISIBLE
                recyclerView?.visibility = View.GONE
                loadClose("", 0, x, y)
            }
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            Toast.makeText(requireContext(), "권한 거부", Toast.LENGTH_SHORT).show()


//            swipeRefreshLayout?.setOnRefreshListener {
//                searchBar?.text = null
//                places.removeAll(places)
//                adapter?.notifyDataChanged()
//                load("", 0)
//                adapter?.isMoreDataAvailable = true
//                swipeRefreshLayout?.isRefreshing = false
//            }
            searchBar?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(
                    v: TextView?,
                    actionId: Int,
                    event: KeyEvent?
                ): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_DONE){
                        keyword = searchBar?.text.toString()


                        if (!keyword.isNullOrEmpty()){
                            places.removeAll(places)
                            recyclerView?.removeAllViews()
                            load(keyword, 0)
                        }
                        hideKeyboard(requireActivity())
                        searchBar?.clearFocus()

                    }
                    return false
                }

            })
            if(adapter?.itemCount == 0){
                progressWheel?.visibility = View.VISIBLE
                recyclerView?.visibility = View.GONE
                load("", 0)
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
                    progressWheel?.visibility = View.GONE
                    recyclerView?.visibility = View.VISIBLE
                    response.body()?.let { places.addAll(it) }
                    adapter?.notifyDataChanged()
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
                    progressWheel?.visibility = View.GONE
                    recyclerView?.visibility = View.VISIBLE
                    response.body()?.let { places.addAll(it) }
                    adapter?.notifyDataChanged()
                    if(response.body()?.size == 30 && index == 0)
                        setOnLoadCloseMoreListener()
                }
            }


        })

    }



    fun loadMore(keyword: String, index: Int){


        places.add(PlaceSearchData("load"))
        adapter?.notifyItemInserted(places.size-1)

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
                        adapter!!.isMoreDataAvailable = false
                    }
                    adapter!!.notifyDataChanged()
                }

            }


        })




    }

    fun loadCloseMore(keyword: String, index: Int, x: Double, y: Double){


        places.add(PlaceSearchData("load"))
        adapter?.notifyItemInserted(places.size-1)

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
                        adapter!!.isMoreDataAvailable = false
                    }
                    adapter!!.notifyDataChanged()
                }

            }


        })




    }

    fun setOnLoadMoreListener(){

        val onLoadMoreListener = object : HomeRecyclerAdapter.OnLoadMoreListener{
            override fun onLoadMore() {
                recyclerView?.post(object : Runnable{
                    override fun run() {
                        val index = places.size - 1
                        loadMore(keyword , index)
                    }

                })
            }
        }

        adapter?.onLoadMoreListener = onLoadMoreListener
    }

    fun setOnLoadCloseMoreListener(){

        val onLoadMoreListener = object : HomeRecyclerAdapter.OnLoadMoreListener{
            override fun onLoadMore() {
                recyclerView?.post(object : Runnable{
                    override fun run() {
                        val index = places.size - 1
                        val gpsTracker =
                            GpsTracker(requireContext())
                        val x = gpsTracker.fetchLatitude()
                        val y = gpsTracker.fetchLongtitude()
                        loadCloseMore(keyword , index, x, y)
                    }

                })
            }
        }

        adapter?.onLoadMoreListener = onLoadMoreListener
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

}