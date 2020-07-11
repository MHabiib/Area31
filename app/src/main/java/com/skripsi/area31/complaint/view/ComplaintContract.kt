package com.skripsi.area31.complaint.view

import com.skripsi.area31.complaint.model.Complaint
import com.skripsi.area31.core.base.BaseView

interface ComplaintContract : BaseView {
  fun getListComplaintSuccess(listComplaint: List<Complaint>)

}