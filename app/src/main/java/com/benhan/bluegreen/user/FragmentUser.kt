package com.benhan.bluegreen.user

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.benhan.bluegreen.HomeActivity
import com.benhan.bluegreen.R
import com.benhan.bluegreen.dataclass.User
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.login.LoginActivity2
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.benhan.bluegreen.utill.MyApplication
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentUser: Fragment() {


//    val prefConfig = PrefConfig(requireContext())


    val sharedPreference = SharedPreference()
    val apiClient = ApiClient()
    val apiInterface: ApiInterface = apiClient.getApiClient().create(
        ApiInterface::class.java)
    private var ivProfilePhoto : ImageView? = null

    var likeNumber: Int?  =null
    var postNumber: Int? = null
    var followNumber: Int? = null
    var tvPostNumber :TextView? = null
    var tvFollowerNumber:TextView? = null
    var tvLikeNumber:TextView? = null
    var email: String? = null
    private var tvUsername: TextView? = null
    private var tvActualname: TextView? = null
    private var tvIntroduction: TextView? = null
    var tvUnfinishedInfo: TextView? = null
    var postFragment: UserPostFragment? = null
    var placeFragment: UserPlaceFragment? = null
    lateinit var user: ImageView






    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.home_fragment_user, container, false)


        val ivGrid = rootView.findViewById<ImageView>(R.id.ivGrid)
        val ivLocation = rootView.findViewById<ImageView>(R.id.ivLocation)
        val gridTab = rootView.findViewById<RelativeLayout>(R.id.layoutGrid)
        val locationTab = rootView.findViewById<RelativeLayout>(R.id.layoutLocation)
        postFragment = UserPostFragment()
        placeFragment = UserPlaceFragment()

        user = requireActivity().findViewById(R.id.user)



        gridTab.isClickable = false
        locationTab.isClickable = true



        val btnUpdate = rootView.findViewById<Button>(R.id.btnProfileUpdate)



        btnUpdate.setOnClickListener {
            startActivity(Intent(requireActivity(), ProfileUpdateActivity::class.java))
        }


        tvUsername = rootView.findViewById(R.id.profile_username)
        tvActualname = rootView.findViewById(R.id.actualName)
        tvIntroduction = rootView.findViewById(R.id.userIntroduction)
        val tvLogout: TextView = rootView.findViewById(R.id.logout)
        ivProfilePhoto = rootView.findViewById(R.id.placePhoto)
        email = sharedPreference.getString(requireContext(), "email")!!
        tvPostNumber = rootView.findViewById(R.id.postNumber)
        tvFollowerNumber = rootView.findViewById(R.id.followerNumber)
        tvLikeNumber = rootView.findViewById(R.id.likeNumber)








        tvLogout.setOnClickListener {
            sharedPreference.clear(requireContext())
            startActivity(Intent(requireActivity(), LoginActivity2::class.java))




        }




        replaceFragment(postFragment!!)

        /////////////////////////////////////////////////////////


        gridTab.setOnClickListener {

            gridTab.isClickable = false
            locationTab.isClickable = true

            gridTab.setBackgroundResource(R.drawable.button_shape_stroke)
            ivGrid.setImageResource(R.drawable.grid_green)

            locationTab.setBackgroundResource(R.drawable.button_shape_green)
            ivLocation.setImageResource(R.drawable.location_white)

            user.setOnClickListener {
                postFragment!!.recyclerView?.scrollToPosition(0)
            }

            replaceFragment(postFragment!!)


        }


        /////////////////////////////////////////////


        locationTab.setOnClickListener {

            gridTab.setBackgroundResource(R.drawable.button_shape_green)
            ivGrid.setImageResource(R.drawable.grid_white)

            locationTab.setBackgroundResource(R.drawable.button_shape_stroke)
            ivLocation.setImageResource(R.drawable.location_green)




            gridTab.isClickable = true
            locationTab.isClickable = false


            replaceFragment(placeFragment!!)


        }



        return rootView


    }



    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(hidden)
            (activity as HomeActivity).clickHandler(user)
        else
            user.setOnClickListener {
                placeFragment!!.recyclerView?.smoothScrollToPosition(0)
                postFragment!!.recyclerView?.smoothScrollToPosition(0)
            }

    }


    override fun onResume() {
        super.onResume()
        tvUsername?.text = sharedPreference.getString(requireContext(), "name")
        tvActualname?.text = sharedPreference.getString(requireContext(), "actualName")
        tvIntroduction?.text = sharedPreference.getString(requireContext(), "introduction")
        update(email!!)
        val sharedPreference = SharedPreference()
        val profilePhotoUri = sharedPreference.getString(requireContext(), "profilePhoto")
        val profilePhoto = MyApplication.severUrl + profilePhotoUri
        val tmpProfilePhoto = sharedPreference.getString(requireContext(),"tmpProfilePhoto")
        if(tmpProfilePhoto!!.isNotBlank()){
            Glide.with(requireActivity()).load(tmpProfilePhoto)
                .override(ivProfilePhoto!!.width, ivProfilePhoto!!.height)
                .into(ivProfilePhoto!!)
        } else {
            Glide.with(requireActivity()).load(profilePhoto)
                .override(ivProfilePhoto!!.width, ivProfilePhoto!!.height)
                .into(ivProfilePhoto!!)
        }
        if(tvActualname?.text == "name" || tvIntroduction?.text == "introduce" || profilePhotoUri == "user_profile_images/vector.jpg"){
            tvUnfinishedInfo?.visibility = View.VISIBLE
        }




    }

    override fun onPause() {
        super.onPause()
        (activity as HomeActivity).clickHandler(user)
    }





    fun update(email: String){

        val call: Call<User> = apiInterface.update(email)
        call.enqueue(object: Callback<User>{
            override fun onFailure(call: Call<User>, t: Throwable) {

            }

            override fun onResponse(call: Call<User>, response: Response<User>) {

                likeNumber = response.body()?.likeNumber
                followNumber = response.body()?.followerNumber
                postNumber = response.body()?.postNumber
                tvFollowerNumber?.text = followNumber?.toString()
                tvLikeNumber?.text = likeNumber?.toString()
                tvPostNumber?.text = postNumber?.toString()
                sharedPreference.setInt(requireContext(), "postNumber", postNumber!!)
                sharedPreference.setInt(requireContext(), "followNumber", followNumber!!)

            }

        })

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

}