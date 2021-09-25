package com.example.mysimpleledger.view.auth.registration.fragment

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.mysimpleledger.R
import com.example.mysimpleledger.databinding.FragmentOtpCodeBinding
import com.example.mysimpleledger.utils.navigateUpOrFinish


class OtpCodeFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentOtpCodeBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtpCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNavController()
        initView()
    }

    private fun initView(){
        setEmailInView("rifat@gmail.com")
        binding.apply {
            btConfirm.setOnClickListener(this@OtpCodeFragment)
            tvEditMail.setOnClickListener(this@OtpCodeFragment)
        }
    }

    private fun setEmailInView(email: String){
        binding.tvCodeSendMail.text = fromHtml(resources.getString(R.string.code_send_email) + "<font color='#0349FA'> <u>$email</u></font>")
        //binding.tvEmail.text = resources.getString(R.string.code_sent_email, "<font color='#4073BF'>$email</font>")
    }
    private fun fromHtml(source: String?): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(source)
        }
    }

    private fun initNavController(){
        navController = findNavController()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.btConfirm.id->{

            }
            binding.tvEditMail.id->{
                navController.navigateUpOrFinish(requireActivity())
            }
        }
    }

}