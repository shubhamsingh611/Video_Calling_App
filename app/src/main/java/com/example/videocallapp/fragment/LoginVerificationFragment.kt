package com.example.videocallapp.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.videocallapp.R
import com.example.videocallapp.activity.HomeActivity
import com.example.videocallapp.databinding.FragmentLoginVerificationBinding
import com.example.videocallapp.utils.AppConstants
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginVerificationFragment : Fragment() {

    private lateinit var binding: FragmentLoginVerificationBinding
    private var getOtpBackend: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login_verification,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = this.arguments

        //Retrieving the phone number entered in Login Page
        val mobileNumber: String? = String.format(
            "+91-%s",
            bundle?.getBundle(AppConstants.MOBILE_TEXT)?.getString(AppConstants.MOBILE_TEXT)
        )
        getOtpBackend = arguments?.getString(AppConstants.OTP_BACKEND_TEXT)

        binding.verifyButton.setOnClickListener {
            if (binding.editTextNumber1.text.toString().trim().isNotEmpty() &&
                binding.editTextNumber2.text.toString().trim().isNotEmpty() &&
                binding.editTextNumber3.text.toString().trim().isNotEmpty() &&
                binding.editTextNumber4.text.toString().trim().isNotEmpty() &&
                binding.editTextNumber5.text.toString().trim().isNotEmpty() &&
                binding.editTextNumber6.text.toString().trim().isNotEmpty()
            ) {
                //Constructing OTP text
                var enteredOtp: String = binding.editTextNumber1.text.toString() +
                        binding.editTextNumber2.text.toString() +
                        binding.editTextNumber3.text.toString() +
                        binding.editTextNumber4.text.toString() +
                        binding.editTextNumber5.text.toString() +
                        binding.editTextNumber6.text.toString()

                if (getOtpBackend != null) {
                    binding.verifyOtpProgressBar.visibility = View.VISIBLE
                    binding.verifyButton.visibility = View.INVISIBLE

                    //Authentication Entered OTP with Backend OTP
                    val phoneAuthCredential = PhoneAuthProvider.getCredential(
                        getOtpBackend!!, enteredOtp
                    )

                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(requireActivity()) { task ->
                            binding.verifyOtpProgressBar.visibility = View.GONE
                            binding.verifyButton.visibility = View.VISIBLE
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    activity,
                                    AppConstants.AUTHENTICATION_SUCCESSFUL,
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(activity, HomeActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    activity,
                                    AppConstants.ENTER_CORRECT_OTP_TEXT,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        activity,
                        AppConstants.INTERNET_CONNECTION_TEXT,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(activity, AppConstants.PLEASE_ENTER_ALL_NUMBER, Toast.LENGTH_SHORT)
                    .show()

            }
        }
        otpNumbersMove()
        binding.tvResendOtp.setOnClickListener {
            //Resend OTP Login Implementation
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91$mobileNumber", 60, TimeUnit.SECONDS, this.requireActivity(),
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    }

                    override fun onVerificationFailed(p0: FirebaseException) {
                        Toast.makeText(activity, p0.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onCodeSent(
                        newBackEndOtp: String,
                        p1: PhoneAuthProvider.ForceResendingToken
                    ) {
                        getOtpBackend = newBackEndOtp
                        Toast.makeText(activity, AppConstants.OTP_SUCCESS_TEXT, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )
        }

    }

    //Moving cursor to next Edit text view on entering one number
    private fun otpNumbersMove() {
        binding.let {
            cursorMove(it.editTextNumber1, it.editTextNumber2)
            cursorMove(it.editTextNumber2, it.editTextNumber3)
            cursorMove(it.editTextNumber3, it.editTextNumber4)
            cursorMove(it.editTextNumber4, it.editTextNumber5)
            cursorMove(it.editTextNumber5, it.editTextNumber6)
        }
    }

    private fun cursorMove(editTextStart: EditText, editTextEnd: EditText) {
        editTextStart.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().trim().isNotEmpty()) {
                    editTextEnd.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

}