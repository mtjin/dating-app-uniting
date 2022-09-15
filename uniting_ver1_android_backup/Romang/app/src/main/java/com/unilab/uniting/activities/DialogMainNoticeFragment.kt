package com.unilab.uniting.activities

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.storage.FirebaseStorage
import com.unilab.uniting.R
import com.unilab.uniting.utils.DateUtil
import com.unilab.uniting.utils.FirebaseHelper
import com.unilab.uniting.utils.Strings


class DialogMainNoticeFragment : DialogFragment(), View.OnClickListener {
    
    private lateinit var rootView: View

    private lateinit var noticeImgView : ImageView
    private lateinit var closeLongBtn: Button
    private lateinit var doneBtn: Button

    private var isMainNotice = true
    public var photoUrl = ""

    private val storage = FirebaseStorage.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawableResource(R.color.colorClear)
        rootView = inflater.inflate(R.layout.dialog_main_notice, container, false)
        
        init()
        setOnClickListener()

        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setNoticeImgView(isMainNotice)
    }

    private fun init() {
        //바인딩
        noticeImgView = rootView.findViewById(R.id.noticeImgView)
        closeLongBtn = rootView.findViewById(R.id.closeLongBtn)
        doneBtn = rootView.findViewById(R.id.doneBtn)


    }


    private fun setNoticeImgView(isMainNotice : Boolean) {
        if(photoUrl.isNotEmpty()){
            setGlide(photoUrl)
            return
        }

        FirebaseHelper.db.collection("Data").document("MainNotice")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        val url = document.data?.get("notice") as? String ?: ""
                        setGlide(url)
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
    }

    private fun setGlide(url: String){
        activity?.let{
            if(!it.isDestroyed){
                Glide.with(it)
                        .asBitmap()
                        .load(url)
                        .error(R.drawable.ic_uniting_app)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(object : CustomTarget<Bitmap?>() {
                            override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                                val w = resource.width
                                val h = resource.height
                                noticeImgView.layoutParams.width = dialog?.window?.decorView?.width ?: rootView.layoutParams.width
                                noticeImgView.layoutParams.height = noticeImgView.layoutParams.width * resource.height / resource.width
                                noticeImgView.requestLayout()
                                noticeImgView.setImageBitmap(resource)
                            }
                        })
            }
        }
    }




    private fun setOnClickListener(){
        closeLongBtn.setOnClickListener {
            activity?.let {
                val pref = it.getSharedPreferences(FirebaseHelper.mUid, Context.MODE_PRIVATE)
                val editor = pref.edit()
                editor.putLong(Strings.mainNoticeDismissLong, DateUtil.getUnixTimeLong())

                editor.apply()
            }
            if (dialog != null && dialog!!.isShowing) {
                dismiss()
            }
        }

        doneBtn.setOnClickListener {
            val activity = activity as? MainNoticeDismissListener
            activity?.mainNoticeDismissed()
            if (dialog != null && dialog!!.isShowing) {
                dismiss()
            }
        }
    }

    override fun onClick(v: View) {
        if (dialog != null && dialog!!.isShowing) {
            dismiss()
        }
    }

    interface MainNoticeDismissListener {
        fun mainNoticeDismissed()
    }

    companion object {
        //상수
        const val TAG_LIKE_DIALOG = "dialog_like"
        const val LIKE_CHECK = "like_check"
        const val TAG = "DialogTAG"

        val instance: DialogMainNoticeFragment
            get() = DialogMainNoticeFragment()
    }
}