package com.benhan.bluegreen.search

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.benhan.bluegreen.HomeActivity
import com.benhan.bluegreen.R
import com.benhan.bluegreen.utill.GpsTracker
import com.benhan.bluegreen.utill.MyApplication
import java.util.*


class FragmentSearch : Fragment() {


    lateinit var search: ImageView

    var postFragment: SearchPostFragment? = null
    var placeFragment: SearchPlaceFragment? = null
    var permissionCheck : Int? = null
    var x: Double? = null
    var y: Double? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val rootView = inflater.inflate(R.layout.home_fragment_search, container, false)


        val gpsTracker =
            GpsTracker(requireContext())
        x = gpsTracker.fetchLatitude()
        y = gpsTracker.fetchLongtitude()


        permissionCheck = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )


        search = requireActivity().findViewById(R.id.search)
        postFragment = SearchPostFragment()
        placeFragment = SearchPlaceFragment()
        val gridTab = rootView.findViewById<RelativeLayout>(R.id.layoutGrid)
        val locationTab = rootView.findViewById<RelativeLayout>(R.id.layoutLocation)
        val ivGrid = rootView.findViewById<ImageView>(R.id.ivGrid)
        val ivLocation = rootView.findViewById<ImageView>(R.id.ivLocation)

        replaceFragment(postFragment!!)



        gridTab.setOnClickListener {


            gridTab.isClickable = false
            locationTab.isClickable = true
            hideKeyboard(requireActivity())

            gridTab.setBackgroundResource(R.drawable.button_shape_stroke)
            ivGrid.setImageResource(R.drawable.grid_green)

            locationTab.setBackgroundResource(R.drawable.button_shape_green)
            ivLocation.setImageResource(R.drawable.location_white)

            search.setOnClickListener {
                postFragment!!.recyclerView?.scrollToPosition(0)
            }

            replaceFragment(postFragment!!)

        }


        locationTab.setOnClickListener {


            gridTab.isClickable = true
            locationTab.isClickable = false

            gridTab.setBackgroundResource(R.drawable.button_shape_green)
            ivGrid.setImageResource(R.drawable.grid_white)

            locationTab.setBackgroundResource(R.drawable.button_shape_stroke)
            ivLocation.setImageResource(R.drawable.location_green)

            search.setOnClickListener {
                placeFragment!!.recyclerView?.scrollToPosition(0)
            }


            replaceFragment(placeFragment!!)
        }





        return rootView
    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity).clickHandler(search)
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


    private fun replaceFragment(fragment: Fragment) {
        val backStateName = fragment.javaClass.name
        val childFragmentManager = childFragmentManager
        val fragmentPopped: Boolean = childFragmentManager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped) { //fragment not in back stack, create it.
            val ft = childFragmentManager.beginTransaction()
            ft.replace(R.id.frame, fragment)
            ft.addToBackStack(backStateName)
            ft.commit()
        }
    }

    fun getCurrentAddress(
        latitude: Double,
        longitude: Double
    ): String? { /*지오코더... GPS를 주소로 변환*/
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>?
        addresses = geocoder.getFromLocation(latitude, longitude, 100)
        val address: Address = addresses[0]
        return address.getAddressLine(0).toString().toString() + "\n"
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden)
            (activity as HomeActivity).clickHandler(search)
        else
            search.setOnClickListener {
                postFragment!!. recyclerView ?. smoothScrollToPosition (0)
                placeFragment!!. recyclerView ?. smoothScrollToPosition (0)
            }

        if(MyApplication.isChanged){
            postFragment?.postImageDataList?.removeAll(postFragment?.postImageDataList!!)
            if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
                postFragment?.loadClose(0, x!!, y!!)
            }else{
                postFragment?.load(0)
            }
            MyApplication.isChanged = false
        }
    }


}