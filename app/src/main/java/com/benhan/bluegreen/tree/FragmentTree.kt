package com.benhan.bluegreen.tree

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.benhan.bluegreen.HomeActivity
import com.benhan.bluegreen.R
import com.benhan.bluegreen.adapter.HomeRecyclerAdapter
import com.benhan.bluegreen.comment.Comment
import com.benhan.bluegreen.dataclass.CommentData
import com.benhan.bluegreen.dataclass.PostData
import com.benhan.bluegreen.dataclass.ServerResonse
import com.benhan.bluegreen.listener.ResponseListener
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.benhan.bluegreen.place.PlacePage
import com.benhan.bluegreen.plus.PhotoUploadActivity
import com.benhan.bluegreen.user.OtherUser
import com.benhan.bluegreen.utill.CommentEditText
import com.benhan.bluegreen.utill.MyApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.pnikosis.materialishprogress.ProgressWheel
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class FragmentTree : Fragment() {


    val apiClient = ApiClient()
    val apiInterface: ApiInterface = apiClient.getApiClient().create(
        ApiInterface::class.java
    )
    var postDataList = ArrayList<PostData>()
    val sharedPreference = SharedPreference()
    var adapter: HomeRecyclerAdapter? = null
    private var rootView: View? = null
    var recyclerview: RecyclerView? = null
    var skeletonScreen: SkeletonScreen? = null
    var imm: InputMethodManager? = null
    var ivCommentProfilePhoto: ImageView? = null

    var writeCommentContainer: LinearLayout? = null
    var writeComment: CommentEditText? = null

    var navi: LinearLayout? = null

    var welcome: TextView? = null
    var welcome2: TextView? = null
    var welcome3: TextView? = null

    var email: String? = null
    var name: String? = null

    var profilePhoto: String? = null

    lateinit var tree: ImageView

    var progressWheel : ProgressWheel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (rootView == null)
            rootView = inflater.inflate(R.layout.home_fragment_tree, container, false)

        imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        writeCommentContainer = rootView?.findViewById(R.id.writeCommentContainer)
        writeComment = rootView?.findViewById(R.id.writeComment)
        writeComment?.setKeyImeChangeListener(object : CommentEditText.KeyImeChange{
            override fun onKeyIme(keyCode: Int, event: KeyEvent?) {
                if(KeyEvent.KEYCODE_BACK == event?.keyCode){
                    val handler = Handler()
                    handler.postDelayed({
                        navi?.visibility = View.VISIBLE
                        writeCommentContainer?.visibility = View.GONE
                    }, 100)
                }
            }

        })
        navi = requireActivity().findViewById(R.id.navigation_bar)
        tree = requireActivity().findViewById(R.id.tree)

        progressWheel = rootView?.findViewById(R.id.progress_wheel)
        recyclerview?.visibility = View.INVISIBLE
        progressWheel?.visibility = View.GONE

        email = sharedPreference.getString(requireContext(), "email")!!
        name = sharedPreference.getString(requireContext(), "name")

        profilePhoto = MyApplication.severUrl + sharedPreference.getString(requireContext(), "profilePhoto")
        ivCommentProfilePhoto = rootView?.findViewById<ImageView>(R.id.myProfilePhoto)
        Glide.with(requireActivity()).load(profilePhoto)
            .override(ivCommentProfilePhoto!!.width, ivCommentProfilePhoto!!.height)
            .into(ivCommentProfilePhoto!!)

        Log.d("프로필", profilePhoto)

        recyclerview = rootView?.findViewById(R.id.treeRecyclerView)
        adapter = HomeRecyclerAdapter(
            requireContext(),
            requireActivity(),
            postDataList
        )


        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerview!!.layoutManager = linearLayoutManager
        recyclerview?.itemAnimator = null


//        recyclerview?.findViewHolderForAdapterPosition()


        val swipeLayout = rootView?.findViewById<SwipeRefreshLayout>(R.id.swipeLayout)
        val backgroundColor = ContextCompat.getColor(
            requireContext(),
            R.color.background
        )
        swipeLayout?.setColorSchemeColors(backgroundColor)
        swipeLayout?.setOnRefreshListener {
            postDataList.removeAll(postDataList)
            adapter?.notifyDataChanged()
            adapter?.isMoreDataAvailable = true
            progressWheel?.spin()
            progressWheel?.visibility = View.VISIBLE
            load(0)
            swipeLayout.isRefreshing = false
        }


//////////////////////////////////////////////////////////////////////


        welcome = rootView?.findViewById(R.id.welcome)
        welcome2 = rootView?.findViewById(R.id.welcome2)
        welcome3 = rootView?.findViewById(R.id.welcome3)


        skeletonScreen = Skeleton.bind(recyclerview)
            .adapter(adapter).shimmer(true).angle(20).duration(1000)
            .load(R.layout.layout_default_item_skeleton).show()
        recyclerview?.visibility = View.GONE


        /////////////////////////////////////////////////////////


        val responseListener = object :
            ResponseListener {
            override fun onResponse() {
                load(0)
            }
        }

        val photoUploadActivity = PhotoUploadActivity()
        photoUploadActivity.responseListener = responseListener


        if (adapter?.itemCount == 0) {
            progressWheel?.visibility = View.VISIBLE
            load(0)
        }


        val onPageClickListener = object : HomeRecyclerAdapter.OnPageClickListener {
            override fun onPageClick(position: Int) {
                val intent = Intent(context, PlacePage::class.java)
                intent.putExtra("placeName", postDataList[position].pageName)
                intent.putExtra("placeId", postDataList[position].pageId)
                intent.putExtra("placePhoto", postDataList[position].pageProfilePhoto)
                intent.putExtra("placeType", postDataList[position].pageType)
                intent.putExtra("placeProvince", postDataList[position].pageProvince)
                requireContext().startActivity(intent)

            }

        }
        adapter?.onPageClickListener = onPageClickListener

        val onUserClickListener = object : HomeRecyclerAdapter.OnUserClickListener {
            override fun onUserClick(position: Int) {
                val intent = Intent(context, OtherUser::class.java)
                intent.putExtra("otherUserName", postDataList[position].userName)
                requireActivity().startActivity(intent)
            }


        }

        adapter?.onUserClickListener = onUserClickListener

        val onClickShowAllListener = object : HomeRecyclerAdapter.OnClickShowAllListener {
            override fun onClickShowAll(position: Int) {
                val intent = Intent(context, Comment::class.java)
                intent.putExtra("post_user_name", postDataList[position].userName)
                intent.putExtra("post_description", postDataList[position].postDescription)
                intent.putExtra("post_user_profile", postDataList[position].userProfilePhoto)
                intent.putExtra("post_date", postDataList[position].postDate)
                intent.putExtra("post_id", postDataList[position].postId!!)
                requireActivity().startActivity(intent)
            }
        }

        adapter?.onClickShowAllListener = onClickShowAllListener

        val onLikeClickListener = object : HomeRecyclerAdapter.OnLikeClickListener {

            override fun onLikeClick(position: Int) {
                if (!postDataList[position].isLikingPost!!) {
                    likePost(email!!, postDataList[position].postId!!, name!!)
                    postDataList[position].postLikes = postDataList[position].postLikes?.plus(1)
                    postDataList[position].isLikingPost = true

                } else {
                    unlikePost(email!!, postDataList[position].postId!!)
                    postDataList[position].postLikes = postDataList[position].postLikes?.minus(1)
                    postDataList[position].isLikingPost = false
                }
                upadetBestPost(position)
            }


        }

        adapter?.onLikeClickListener = onLikeClickListener


        adapter!!.addWriteCommentClickListener(object :
            HomeRecyclerAdapter.OnWriteCommentClicked {
            override fun onWriteCommentClicked(position: Int) {

                val handler = Handler()
                handler.postDelayed({navi?.visibility = View.GONE
                    writeCommentContainer?.visibility = View.VISIBLE}, 50)
                writeComment?.requestFocus()
                imm?.showSoftInput(writeComment, 0)
                Log.d("나비", "곤")

                writeComment?.setOnEditorActionListener { v, actionId, event ->
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        val comment = writeComment?.text.toString()
                        writeComment?.text = null
                        writeComment?.clearFocus()
                        navi?.visibility = View.VISIBLE
                        writeCommentContainer?.visibility = View.GONE
                        UIUtil.hideKeyboard(activity)
                        val call: Call<java.util.ArrayList<CommentData>> =
                            apiInterface.writeComment(
                                email!!,
                                postDataList[position].postId!!,
                                name!!,
                                comment
                            )
                        if (comment.isNotEmpty()) {
                            postDataList[position].mainCommentUserName = name!!
                            postDataList[position].mainComment = comment
                            postDataList[position].commentNumber =
                                postDataList[position].commentNumber?.plus(1)
                            call.clone()
                                .enqueue(object : Callback<java.util.ArrayList<CommentData>> {
                                    override fun onFailure(
                                        call: Call<java.util.ArrayList<CommentData>>,
                                        t: Throwable
                                    ) {

                                    }

                                    override fun onResponse(
                                        call: Call<java.util.ArrayList<CommentData>>,
                                        response: Response<java.util.ArrayList<CommentData>>
                                    ) {
                                        adapter?.notifyItemChanged(position)
                                    }


                                })
                        }

                    }
                    false
                }


            }


        })





        return rootView


    }



    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden)
            (activity as HomeActivity).clickHandler(tree)
        else
            tree.setOnClickListener {
                recyclerview!!.smoothScrollToPosition(0)
            }

        if(MyApplication.isProfileUpdated){
            profilePhoto = MyApplication.severUrl + sharedPreference.getString(requireContext(), "profilePhoto")
            Glide.with(requireActivity()).load(profilePhoto)
                .override(ivCommentProfilePhoto!!.width, ivCommentProfilePhoto!!.height)
                .into(ivCommentProfilePhoto!!)
        }
    }


    inner class PostProviderModel : ListPreloader.PreloadModelProvider<PostData> {
        override fun getPreloadItems(position: Int): MutableList<PostData> {
            val postData = postDataList[position]
            if (postDataList[position].postImage?.isEmpty()!!) {
                return Collections.emptyList()
            }
            return Collections.singletonList(postData)
        }

        override fun getPreloadRequestBuilder(item: PostData): RequestBuilder<*>? {
            return Glide.with(requireContext())
                .load(MyApplication.severUrl + item.postImage)
        }


    }


    fun upadetBestPost(position: Int) {
        val call: Call<ServerResonse> = apiInterface.updateBestPost(postDataList[position].pageId)
        call.enqueue(object : Callback<ServerResonse> {
            override fun onFailure(call: Call<ServerResonse>, t: Throwable) {
                adapter?.notifyItemChanged(position)
            }

            override fun onResponse(
                call: Call<ServerResonse>,
                response: Response<ServerResonse>
            ) {

            }
        })
    }


    fun load(index: Int) {


        val call: Call<ArrayList<PostData>> = apiInterface.getPostData(email!!, index)
        call.enqueue(object : Callback<ArrayList<PostData>> {
            override fun onFailure(call: Call<ArrayList<PostData>>, t: Throwable) {

                Log.d("콜에러", t.message)
            }

            override fun onResponse(
                call: Call<ArrayList<PostData>>,
                response: Response<ArrayList<PostData>>
            ) {

                if (response.isSuccessful) {
                    skeletonScreen?.hide()
                    recyclerview?.visibility = View.VISIBLE
                    progressWheel?.visibility = View.INVISIBLE
                    response.body()?.let { postDataList.addAll(it) }
                    recyclerview!!.adapter = adapter
                    adapter?.notifyDataChanged()
                    val postPreloadProvider = ViewPreloadSizeProvider<PostData>()
                    val postProviderModel = PostProviderModel()
                    val preloader: RecyclerViewPreloader<PostData> = RecyclerViewPreloader(
                        Glide.with(requireContext()), postProviderModel, postPreloadProvider, 10
                    )
                    recyclerview?.addOnScrollListener(preloader)


                    if (postDataList.size == 30) {
                        adapter!!.addLoadMoreListener(object :
                            HomeRecyclerAdapter.OnLoadMoreListener {
                            override fun onLoadMore() {
                                recyclerview!!.post {
                                    val index = postDataList.size - 1
                                    loadMore(index)
                                }
                            }

                        })
                    }


                }

                if (adapter?.itemCount == 0) {
                    welcome?.visibility = View.VISIBLE
                    welcome2?.visibility = View.VISIBLE
                    welcome3?.visibility = View.VISIBLE
                    val fadeIn = AlphaAnimation(0.0f, 1.0f)
                    val fadeOut = AlphaAnimation(1.0f, 0.0f)
                    welcome?.startAnimation(fadeIn)
//        txtView.startAnimation(fadeOut)
                    fadeIn.duration = 1500
                    fadeIn.fillAfter = false
                    fadeOut.duration = 1200
                    fadeOut.fillAfter = false
                    fadeOut.startOffset = 4200 + fadeIn.getStartOffset()
                    val fadeIn2 = AlphaAnimation(0.0f, 1.0f)
                    fadeIn2.startOffset = 1500
                    fadeIn2.duration = 1500
                    val fadeIn3 = AlphaAnimation(0.0f, 1.0f)
                    fadeIn3.startOffset = 3000
                    fadeIn3.duration = 1500
                    welcome2?.startAnimation(fadeIn2)
                    welcome3?.startAnimation(fadeIn3)
                } else {
                    welcome?.visibility = View.GONE
                    welcome2?.visibility = View.GONE
                    welcome3?.visibility = View.GONE
                }
            }

        })


    }

    fun loadMore(index: Int) {

        val email = sharedPreference.getString(requireContext(), "email")!!
        postDataList.add(PostData("load"))
        adapter!!.notifyItemInserted(postDataList.size - 1)

        val newIndex = index + 1

        val call: Call<ArrayList<PostData>> = apiInterface.getPostData(email, newIndex)
        call.enqueue(object : Callback<ArrayList<PostData>> {
            override fun onFailure(call: Call<ArrayList<PostData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<PostData>>,
                response: Response<ArrayList<PostData>>
            ) {
                if (response.isSuccessful) {
                    postDataList.removeAt(postDataList.size - 1)
                    val result: ArrayList<PostData>? = response.body()
                    if (result!!.size > 0) {
                        postDataList.addAll(result)
                    } else {
                        adapter!!.isMoreDataAvailable = false
                    }
                    adapter!!.notifyDataChanged()
                }
            }
        })
    }

    fun likePost(email: String, postId: Int, user_name: String) {

        val call: Call<ServerResonse> = apiInterface.likePost(email, postId, user_name)
        call.enqueue(object : Callback<ServerResonse> {
            override fun onFailure(call: Call<ServerResonse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ServerResonse>,
                response: Response<ServerResonse>
            ) {


            }
        })


    }

    private fun unlikePost(email: String, postId: Int) {

        val call: Call<ServerResonse> = apiInterface.unLikePost(email, postId)
        call.enqueue(object : Callback<ServerResonse> {
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

