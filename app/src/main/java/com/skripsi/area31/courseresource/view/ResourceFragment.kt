package com.skripsi.area31.courseresource.view

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
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
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.course.view.CourseActivity
import com.skripsi.area31.courseresource.adapter.ListResourceAdapter
import com.skripsi.area31.courseresource.injection.DaggerResourceComponent
import com.skripsi.area31.courseresource.injection.ResourceComponent
import com.skripsi.area31.courseresource.model.Resource
import com.skripsi.area31.courseresource.presenter.ResourcePresenter
import com.skripsi.area31.databinding.FragmentResourceBinding
import com.skripsi.area31.home.view.HomeFragment
import com.skripsi.area31.utils.Constants
import com.skripsi.area31.utils.Constants.Companion.COURSE_ID
import javax.inject.Inject

class ResourceFragment : BottomSheetDialogFragment(), ResourceContract {
  private var daggerBuild: ResourceComponent = DaggerResourceComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: ResourcePresenter
  @Inject lateinit var gson: Gson
  private lateinit var listResourceAdapter: ListResourceAdapter
  private lateinit var binding: FragmentResourceBinding
  private var currentPage = 0
  private var isLastPage = false
  private var isLoading = false
  private lateinit var accessToken: String
  private lateinit var idCourse: String

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
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_resource, container, false)
    val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    with(binding) {
      shimmerResource.startShimmer()
      listResourceAdapter = ListResourceAdapter()
      rvResource.layoutManager = linearLayoutManager
      rvResource.adapter = listResourceAdapter
      listResourceAdapter.onItemClick = {
        resourceItemClick(it)
      }

      rvResource.isNestedScrollingEnabled = false
      ibBack.setOnClickListener {
        listResourceAdapter.clear()
        listResourceAdapter.notifyDataSetChanged()
        currentPage = 0
        isLastPage = false
        val activity = activity as CourseActivity
        activity.dismissResourceDialog()
      }
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    idCourse = this.arguments?.getString(COURSE_ID).toString()
    accessToken = gson.fromJson(
        context?.getSharedPreferences(Constants.AUTHENTICATION, Context.MODE_PRIVATE)?.getString(
            Constants.TOKEN, null), Token::class.java).accessToken
    presenter.attach(this)
    presenter.apply {
      subscribe()
      getListResource(accessToken, idCourse)
    }
  }

  override fun getListResourceSuccess(response: List<Resource>) {
    with(binding) {
      rvResource.isEnabled = true
      shimmerResource.visibility = View.GONE
      shimmerResource.stopShimmer()
    }
    listResourceAdapter.addAll(response)
    if (response.isEmpty()) {
      binding.ivDontHaveResource.visibility = View.VISIBLE
      binding.tvIvDontHaveResource.visibility = View.VISIBLE
    }
  }

  private fun resourceItemClick(resourceItems: Resource) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resourceItems.link)))
  }

  override fun onCancel(dialog: DialogInterface) {
    listResourceAdapter.clear()
    listResourceAdapter.notifyDataSetChanged()
    currentPage = 0
    isLastPage = false
    super.onCancel(dialog)
  }

  override fun onFailed(message: String) {
    isLoading = true
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }
}