package com.skripsi.area31.complaint.view

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.complaint.adapter.ComplaintAdapter
import com.skripsi.area31.complaint.injection.ComplaintComponent
import com.skripsi.area31.complaint.injection.DaggerComplaintComponent
import com.skripsi.area31.complaint.model.Complaint
import com.skripsi.area31.complaint.presenter.ComplaintPresenter
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.course.view.CourseActivity
import com.skripsi.area31.databinding.FragmentComplaintBinding
import com.skripsi.area31.home.view.HomeFragment
import com.skripsi.area31.utils.Constants.Companion.ASSIGNN_AT
import com.skripsi.area31.utils.Constants.Companion.AUTHENTICATION
import com.skripsi.area31.utils.Constants.Companion.COURSE_ID
import com.skripsi.area31.utils.Constants.Companion.DESCRIPTION
import com.skripsi.area31.utils.Constants.Companion.QUIZ_DATE
import com.skripsi.area31.utils.Constants.Companion.SCORE_REPORT
import com.skripsi.area31.utils.Constants.Companion.TOKEN
import javax.inject.Inject

class ComplaintFragment : BottomSheetDialogFragment(), ComplaintContract {
  private var daggerBuild: ComplaintComponent = DaggerComplaintComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: ComplaintPresenter
  @Inject lateinit var gson: Gson
  private lateinit var listComplaintAdapter: ComplaintAdapter
  private lateinit var binding: FragmentComplaintBinding
  private var currentPage = 0
  private var isLastPage = false
  private var isLoading = false
  private lateinit var accessToken: String
  private lateinit var idCourse: String
  private var bottomsheerComplaintDetails = ComplainDetailsBottomsheet()

  fun newInstance(): HomeFragment = HomeFragment()

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    disableBottomSheetDraggableBehavior()
  }

  private fun disableBottomSheetDraggableBehavior() {
    this.isCancelable = false
    this.dialog?.setCanceledOnTouchOutside(true)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_complaint, container, false)
    val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    with(binding) {
      shimmerComplaint.startShimmer()
      listComplaintAdapter = ComplaintAdapter()
      rvComplaint.layoutManager = linearLayoutManager
      rvComplaint.adapter = listComplaintAdapter
      listComplaintAdapter.onItemClick = {
        complaintItemClick(it)
      }

      rvComplaint.isNestedScrollingEnabled = false
      ibBack.setOnClickListener {
        listComplaintAdapter.clear()
        listComplaintAdapter.notifyDataSetChanged()
        currentPage = 0
        isLastPage = false
        val activity = activity as CourseActivity
        activity.dismissComplaintDialog()
      }
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    idCourse = this.arguments?.getString(COURSE_ID).toString()
    accessToken = gson.fromJson(
        context?.getSharedPreferences(AUTHENTICATION, Context.MODE_PRIVATE)?.getString(TOKEN, null),
        Token::class.java).accessToken
    presenter.attach(this)
    presenter.apply {
      subscribe()
      getListComplaint(accessToken)
    }
  }

  override fun getListComplaintSuccess(response: List<Complaint>) {
    with(binding) {
      rvComplaint.isEnabled = true
      shimmerComplaint.visibility = View.GONE
      shimmerComplaint.stopShimmer()
    }
    listComplaintAdapter.addAll(response)
    if (response.isEmpty()) {
      binding.ivDontHaveComplaint.visibility = View.VISIBLE
      binding.tvIvDontHaveComplaint.visibility = View.VISIBLE
    }
  }

  private fun complaintItemClick(complaintItems: Complaint) {
    val bundle = Bundle()
    bundle.putString(QUIZ_DATE, complaintItems.quizDate.toString())
    bundle.putString(ASSIGNN_AT, complaintItems.assignedAt.toString())
    bundle.putString(SCORE_REPORT, complaintItems.score.toString())
    bundle.putString(DESCRIPTION, complaintItems.description)
    bottomsheerComplaintDetails.arguments = bundle
    if (!bottomsheerComplaintDetails.isAdded) {
      activity?.supportFragmentManager.let { fragmentManager ->
        fragmentManager?.let {
          bottomsheerComplaintDetails.show(it, bottomsheerComplaintDetails.tag)
        }
      }
    }
  }

  override fun onCancel(dialog: DialogInterface) {
    listComplaintAdapter.clear()
    listComplaintAdapter.notifyDataSetChanged()
    currentPage = 0
    isLastPage = false
    super.onCancel(dialog)
  }

  override fun onFailed(message: String) {
    isLoading = true
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }
}