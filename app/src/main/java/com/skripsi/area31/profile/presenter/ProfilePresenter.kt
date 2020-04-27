package com.skripsi.area31.profile.presenter

import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.profile.network.ProfileApi
import com.skripsi.area31.profile.view.ProfileContract
import javax.inject.Inject

class ProfilePresenter @Inject constructor(private val profileApi: ProfileApi) :
    BasePresenter<ProfileContract>()