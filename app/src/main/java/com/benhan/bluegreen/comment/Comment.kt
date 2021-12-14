package com.benhan.bluegreen.comment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.benhan.bluegreen.R
import com.benhan.bluegreen.adapter.CommentRecyclerAdapter
import com.benhan.bluegreen.dataclass.CommentData
import com.benhan.bluegreen.localdata.SharedPreference
import com.benhan.bluegreen.network.ApiClient
import com.benhan.bluegreen.network.ApiInterface
import com.benhan.bluegreen.utill.Functions
import com.benhan.bluegreen.utill.MyApplication
import com.bumptech.glide.Glide
import com.pnikosis.materialishprogress.ProgressWheel
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class Comment : AppCompatActivity() {

    var tvName : TextView? = null
    var tvContents : TextView? = null
    var tvDate : TextView? = null
    var ivUserProfile : CircleImageView? = null
    var userName: String? = null
    var postContents: String? = null
    
    var profilePhoto: String? = null
    var postId: Int? = null
    var description : RelativeLayout? =null
    var ivBack : ImageView? = null
    var ivMyProfile : CircleImageView? = null
    var etWriteComment : EditText? = null
    var tvUploadComment : TextView? = null
    var myProfile : String? = null
    var myEmail: String? = null
    var myNmae: String? = null
    var adapter : CommentRecyclerAdapter? = null
    var itemPosition: Int? =null
    var userToReply: String? = null
    var recyclerView: RecyclerView? = null
    var progressWheel : ProgressWheel? = null




    val sharedPreference = SharedPreference()
    val apiClient = ApiClient()
    val apiInterface = apiClient.getApiClient().create(ApiInterface::class.java)

    val commentList = ArrayList<CommentData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        /*     view     */

        tvName = findViewById(R.id.posterName)
        tvContents = findViewById(R.id.descriptionContents)
        tvDate = findViewById(R.id.postedDate)
        ivUserProfile = findViewById(R.id.profilePhoto)
        description = findViewById(R.id.description)
        ivBack = findViewById(R.id.ivBack)
        ivMyProfile = findViewById(R.id.myProfilePhoto)
        etWriteComment = findViewById(R.id.writeComment)
        tvUploadComment = findViewById(R.id.uploadComment)
        recyclerView = findViewById(R.id.commentsRecycler)
        progressWheel = findViewById(R.id.progress_wheel)

        recyclerView?.visibility = View.GONE

        val functions = Functions(this)

        val backgroundColor = ContextCompat.getColor(this,
            R.color.background
        )
        val naviColor = ContextCompat.getColor(this,
            R.color.navi
        )

        /*     data from former activity     */

        userName = intent.getStringExtra("post_user_name")
        postContents = intent.getStringExtra("post_description")
        profilePhoto = intent.getStringExtra("post_user_profile")
        postId = intent.getIntExtra("post_id", 0)
        myProfile = sharedPreference.getString(this, "profilePhoto")
        myEmail = sharedPreference.getString(this, "email")
        myNmae = sharedPreference.getString(this, "name")

        /*               get comment data from server                  */



        val call: Call<ArrayList<CommentData>> =
            apiInterface.getPostComments(postId!!, myEmail!!)

        call.enqueue(object : Callback<ArrayList<CommentData>>{
            override fun onFailure(call: Call<ArrayList<CommentData>>, t: Throwable) {

                Log.d("실패?",t.message)
            }

            override fun onResponse(
                call: Call<ArrayList<CommentData>>,
                response: Response<ArrayList<CommentData>>
            ) {
                progressWheel?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
                response.body()?.let { commentList.addAll(it) }
                adapter?.notifyDataSetChanged()
            }


        })







        /*               adapter  setting                   */

        adapter = CommentRecyclerAdapter(
            this,
            commentList,
            progressWheel!!
        )
        val onItemDeleted = object : CommentRecyclerAdapter.OnItemDeleted{
            override fun onItemDeleted(position: Int) {

                call.clone().enqueue(object : Callback<ArrayList<CommentData>>{
                    override fun onFailure(call: Call<ArrayList<CommentData>>, t: Throwable) {

                        Log.d("실패?",t.message)
                    }

                    override fun onResponse(
                        call: Call<ArrayList<CommentData>>,
                        response: Response<ArrayList<CommentData>>
                    ) {
                        commentList.removeAll(commentList)
                        response.body()?.let { commentList.addAll(it) }
                        adapter?.notifyDataSetChanged()
                    }


                })
            }
        }
        adapter?.onItemDeleted = onItemDeleted
        adapter!!.addOnClickReply(object : CommentRecyclerAdapter.OnClickReply{
            override fun onClickReply(userName: String, position: Int) {

                etWriteComment?.requestFocus()
                etWriteComment?.setText("@$userName ", TextView.BufferType.EDITABLE)
                if(!userName.isNullOrEmpty()) {
                    etWriteComment?.setSelection("@$userName ".length)
                }
                userToReply = userName
                itemPosition = position

                functions.openKeyboard()



            }


        })






        /*            recyclerView setting              */


        val animator = object: DefaultItemAnimator(){

            override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                return true
            }
        }

        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter
        recyclerView?.itemAnimator = animator
        recyclerView!!.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if(event!!.action == MotionEvent.ACTION_DOWN){
                    etWriteComment?.clearFocus()
                    return true
                }
                return false
            }

        })







        /*            view funciton               */


        if (postContents.isNullOrEmpty()){
            description?.visibility = View.GONE
        } else{
            description?.visibility=View.VISIBLE
        }







        /*     set data into view     */

        tvName?.text = userName
        tvContents?.text = postContents

        val profilePhotoUri = MyApplication.severUrl+profilePhoto
            Glide.with(this).load(profilePhotoUri)
                .override(ivUserProfile!!.width, ivUserProfile!!.height)
                .into(ivUserProfile!!)

        val myProfielUri = MyApplication.severUrl + myProfile
        Glide.with(this).load(myProfielUri)
            .override(ivMyProfile!!.width, ivMyProfile!!.height)
            .into(ivMyProfile!!)
        ivBack?.setOnClickListener {
            finish()
        }


        /*          post comment                 */



        etWriteComment!!.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(etWriteComment?.text!!.trim().isNotEmpty()){
                    tvUploadComment?.setTextColor(backgroundColor)

                }else if (etWriteComment?.text!!.trim().isNullOrEmpty()){
                    tvUploadComment?.setTextColor(naviColor)
                    }
            }


            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }


        })

        fun uploadComment() {

            if (etWriteComment?.text!!.startsWith("@$userToReply ")) {


                val call: Call<ArrayList<CommentData>> = this.apiInterface
                    .writeReply(
                        myEmail!!, postId!!, myNmae!!, etWriteComment!!.text.toString()
                        , commentList[itemPosition!!].comment_no!!
                    )

                call.enqueue(object : Callback<ArrayList<CommentData>> {
                    override fun onFailure(call: Call<ArrayList<CommentData>>, t: Throwable) {

                    }

                    override fun onResponse(
                        call: Call<ArrayList<CommentData>>,
                        response: Response<ArrayList<CommentData>>
                    ) {

                        val ref: Call<ArrayList<CommentData>> =
                            apiInterface.getPostComments(postId!!, myEmail!!)

                        ref.enqueue(object : Callback<ArrayList<CommentData>>{
                            override fun onFailure(call: Call<ArrayList<CommentData>>, t: Throwable) {

                                Log.d("실패?",t.message)
                            }

                            override fun onResponse(
                                call: Call<ArrayList<CommentData>>,
                                response: Response<ArrayList<CommentData>>
                            ) {
                                commentList.removeAll(commentList)
                                response.body()?.let { commentList.addAll(it) }
                                adapter?.notifyDataSetChanged()
                            }


                        })

                    }

                })
            } else {
                val call: Call<ArrayList<CommentData>> = this.apiInterface
                    .writeComment(
                        myEmail!!, postId!!, myNmae!!, etWriteComment!!.text.toString()
                    )
                call.enqueue(object : Callback<ArrayList<CommentData>> {
                    override fun onFailure(call: Call<ArrayList<CommentData>>, t: Throwable) {



                    }

                    override fun onResponse(
                        call: Call<ArrayList<CommentData>>,
                        response: Response<ArrayList<CommentData>>
                    ) {

                        response.body()?.let { commentList.addAll(it) }
                        adapter?.notifyDataSetChanged()

                    }

                })
            }


        }

        tvUploadComment?.setOnClickListener {

            if(etWriteComment!!.text.trim().isNotEmpty()) {
                uploadComment()
            }
            functions.hideKeyboard(etWriteComment!!)
            etWriteComment?.text = null
            etWriteComment?.clearFocus()
        }



        etWriteComment!!.setOnEditorActionListener(object: TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    if(etWriteComment!!.text.trim().isNotEmpty()) {
                        uploadComment()
                    }
                    functions.hideKeyboard(etWriteComment!!)
                    etWriteComment?.text = null
                    etWriteComment?.clearFocus()
                }
                return false
            }
        })






    }
}
