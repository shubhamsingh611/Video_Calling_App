package com.example.videocallapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.videocallapp.R
import com.example.videocallapp.databinding.FragmentLoginBinding
import com.example.videocallapp.utils.AppConstants
import com.example.videocallapp.utils.AppConstants.ENTER_MOBILE_TEXT
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonGetOtp.setOnClickListener {
            if (binding.editTextPhone.text.toString().trim().isNotEmpty()) {
                if (binding.editTextPhone.text.toString().trim().length == 10) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonGetOtp.visibility = View.INVISIBLE

                    //OTP Authentication Process
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        AppConstants.INDIA_STD_CODE + binding.editTextPhone.text.toString(),
                        60,
                        TimeUnit.SECONDS, this.requireActivity(),
                        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                                binding.progressBar.visibility = View.GONE
                                binding.buttonGetOtp.visibility = View.VISIBLE
                            }

                            override fun onVerificationFailed(p0: FirebaseException) {
                                binding.progressBar.visibility = View.GONE
                                binding.buttonGetOtp.visibility = View.VISIBLE
                                Toast.makeText(activity, p0.message, Toast.LENGTH_SHORT).show()
                            }

                            override fun onCodeSent(
                                backEndOtp: String,
                                p1: PhoneAuthProvider.ForceResendingToken
                            ) {
                                binding.progressBar.visibility = View.GONE
                                binding.buttonGetOtp.visibility = View.VISIBLE

                                val loginVerificationFragment = LoginVerificationFragment()
                                val bundle = Bundle()

                                //Storing Mobile Number in Shared Preferences
                                val preferences = requireActivity().getSharedPreferences(
                                    AppConstants.SHARED_PREF_TAG,
                                    Context.MODE_PRIVATE
                                )
                                val editor = preferences.edit()
                                editor.putString(
                                    AppConstants.USER_MOBILE_TEXT,
                                    "+91-" + binding.editTextPhone.text.toString()
                                ).apply()
                                bundle.putString(
                                    AppConstants.MOBILE_TEXT,
                                    binding.editTextPhone.text.toString()
                                )
                                bundle.putString(AppConstants.OTP_BACKEND_TEXT, backEndOtp)

                                loginVerificationFragment.arguments = bundle
                                requireActivity()!!.supportFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.container_main_activity,
                                        loginVerificationFragment,
                                        AppConstants.FRAGMENT_TAG
                                    )
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }
                    )
                } else {
                    Toast.makeText(
                        activity,
                        AppConstants.ENTER_CORRECT_NUMBER_TEXT,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                Toast.makeText(activity, ENTER_MOBILE_TEXT, Toast.LENGTH_SHORT).show()
            }
        }
    }
}