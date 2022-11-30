package com.app.fuse.ui.mainScreen.profile.editProfile

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.fuse.R
import com.app.fuse.databinding.FragmentEditProfileBinding
import com.app.fuse.db.BinjaDatabase
import com.app.fuse.network.BinjaRepository
import com.app.fuse.ui.login.model.RegisterData
import com.app.fuse.utils.Constants
import com.app.fuse.utils.Resources
import com.app.fuse.utils.SessionManager
import com.app.fuse.utils.common.hide
import com.app.fuse.utils.common.hideKeyboard
import com.app.fuse.utils.common.performCrop
import com.app.fuse.utils.common.show
import com.app.fuse.utils.common.showToast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.app.fuse.ui.mainScreen.profile.model.UpdateProfileResponse
import com.google.gson.Gson
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.progress_bar.*
import java.io.File
import java.util.*


class EditProfile : Fragment(R.layout.fragment_edit_profile), View.OnClickListener {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var editProfileViewModel: EditProfileViewModel
    lateinit var session: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)
        init()
        setViewModel()
        setOnClickListners()
    }

    private fun init() {
        session = SessionManager(requireContext())
    }

    private fun setOnClickListners() {
        binding.updateCV.setOnClickListener(this)
        binding.profilePic.setOnClickListener(this)
        binding.backArrowEditProfile.setOnClickListener(this)
    }

    private fun setViewModel() {
        val binjaRepository = BinjaRepository(BinjaDatabase(requireContext()))
        val editPorfileViewModelFactory =
            EditProfileViewModelFactory(requireActivity().application, binjaRepository)
        editProfileViewModel = ViewModelProvider(
            this,
            editPorfileViewModelFactory
        ).get(EditProfileViewModel::class.java)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.updateCV -> onClickUpdateProfile()
            R.id.backArrowEditProfile -> findNavController().navigateUp()
            R.id.profilePic -> galleryIntent()
        }
    }

    private fun onClickUpdateProfile() {
        hideKeyboard(requireActivity())

        val userName = binding.edtUserName.text.toString()
        val userAge = binding.edtAge.text.toString()
        val designation = binding.edtDesignation.text.toString()


        editProfileViewModel.updateUserProfile(requireActivity(), userName, userAge, designation)

        editProfileViewModel.updateProfile.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resources.Success -> {
                    pgrBarLayout.hide(requireActivity())
                    val updateProfileResponse: UpdateProfileResponse? = response.data
                    requireContext().showToast("${updateProfileResponse?.msg}")

                    session.token = response.data?.access_token

                    val loginUserData = session.userLoginData
                    val gson = Gson()
                    val loginData: RegisterData =
                        gson.fromJson(loginUserData, RegisterData::class.java)

                    loginData.profile = response.data?.data!!.profile

                    loginData.username = response.data.data.username
                    loginData.designation = response.data.data.designation
                    loginData.age = response.data.data.age.toString()
                    val json = gson.toJson(loginData)
                    session.userLoginData = json

                    findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)

                }
                is Resources.Loading -> {
                    pgrBarLayout.show(requireActivity())
                }
                is Resources.Error -> {
                    pgrBarLayout.hide(requireActivity())
                    requireContext().showToast("Error${response.message}")
                }
            }
        })
    }

    //Gallery Option
    private fun galleryIntent() {
        val packageName: String =
            requireActivity().applicationContext.packageName
        val authority = "$packageName.fileprovider"
        Matisse.from(this@EditProfile)
            .choose(setOf(MimeType.PNG, MimeType.JPEG, MimeType.BMP))
            .countable(true)
            .theme(R.style.Matisse_Dracula)
            .maxSelectable(1)
            .capture(true)
            .captureStrategy(CaptureStrategy(true, authority))
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .showSingleMediaType(true)
            .showPreview(false) // Default is `true`
            .forResult(Constants.REQUEST_CODE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            Constants.REQUEST_CODE_IMAGE -> {
                for (u in Matisse.obtainPathResult(data)) {
                    editProfileViewModel.filePath = u
                }
                for (u in Matisse.obtainResult(data)) {
                    editProfileViewModel.uri = u
                }
                performCrop(File(editProfileViewModel.filePath), this@EditProfile)
            }
            Constants.REQUEST_CODE_CROP -> {
                if (editProfileViewModel.filePath != "") {
                    Glide.with(requireContext()).load(editProfileViewModel.filePath)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.profilePic)
                }
            }
        }
    }
}