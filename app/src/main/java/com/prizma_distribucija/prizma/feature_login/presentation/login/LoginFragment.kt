package com.prizma_distribucija.prizma.feature_login.presentation.login

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.prizma_distribucija.prizma.R
import com.prizma_distribucija.prizma.core.util.Resource
import com.prizma_distribucija.prizma.databinding.FragmentLoginBinding
import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.core.util.collectLatestLifecycleFlow
import com.prizma_distribucija.prizma.core.util.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    lateinit var loadingDialog: AlertDialog

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentLoginBinding.bind(view)

        setUpTextColorForTitle()

        createLoadingDialog()

        setUpKeyboardListeners()

        requireActivity().collectLatestLifecycleFlow(viewModel.signInStatus) {
            when (it) {
                is Resource.Success -> {
                    handleLogInSuccess(it)
                }

                is Resource.Error -> {
                    handleLogInError(it)
                }

                is Resource.Loading -> {
                    handleLogInLoading()
                }
            }
        }

        val codeObserver = Observer<String> { code ->
            setCode(code)
        }

        viewModel.code.observe(viewLifecycleOwner, codeObserver)
    }

    private fun setUpTextColorForTitle() {
        val halfBlackHalfGreenText =
            "<font color=#FF000000>Unesi svoj</font> <b><font color=#2AEC57>KOD</font></b>"
        binding.tvTitle.text =
            Html.fromHtml(halfBlackHalfGreenText, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun setCode(code: String) {
        binding.apply {
            when (code.length) {
                0 -> {
                    et1.setText("")
                    et2.setText("")
                    et3.setText("")
                    et4.setText("")
                }

                1 -> {
                    et1.setText(code[0].toString())
                    et2.setText("")
                    et3.setText("")
                    et4.setText("")
                }

                2 -> {
                    et1.setText(code[0].toString())
                    et2.setText(code[1].toString())
                    et3.setText("")
                    et4.setText("")
                }

                3 -> {
                    et1.setText(code[0].toString())
                    et2.setText(code[1].toString())
                    et3.setText(code[2].toString())
                    et4.setText("")
                }

                4 -> {
                    et1.setText(code[0].toString())
                    et2.setText(code[1].toString())
                    et3.setText(code[2].toString())
                    et4.setText(code[3].toString())
                }
            }
        }
    }

    private fun handleLogInSuccess(resource: Resource<User>) {
        loadingDialog.dismiss()
        navigateToTrackLocationFragment(resource)
    }

    private fun navigateToTrackLocationFragment(resource: Resource<User>) {
        val action =
            LoginFragmentDirections.actionLoginFragmentToTrackLocationFragment(
                resource.data!!
            )
        findNavController().safeNavigate(action)
    }

    private fun handleLogInError(resource: Resource<User>) {
        loadingDialog.dismiss()
        Snackbar.make(binding.root, resource.message.toString(), Snackbar.LENGTH_LONG).show()
    }

    private fun handleLogInLoading() {
        loadingDialog.show()
    }

    private fun setUpKeyboardListeners() {
        binding.apply {
            tvNum1.setOnClickListener {
                viewModel.onDigitAdded(1)
            }

            tvNum2.setOnClickListener {
                viewModel.onDigitAdded(2)
            }

            tvNum3.setOnClickListener {
                viewModel.onDigitAdded(3)
            }

            tvNum4.setOnClickListener {
                viewModel.onDigitAdded(4)
            }

            tvNum5.setOnClickListener {
                viewModel.onDigitAdded(5)
            }

            tvNum6.setOnClickListener {
                viewModel.onDigitAdded(6)
            }

            tvNum7.setOnClickListener {
                viewModel.onDigitAdded(7)
            }

            tvNum8.setOnClickListener {
                viewModel.onDigitAdded(8)
            }

            tvNum9.setOnClickListener {
                viewModel.onDigitAdded(9)
            }

            tvNum0.setOnClickListener {
                viewModel.onDigitAdded(0)
            }

            tvBackspace.setOnClickListener {
                viewModel.onDigitDeleted()
            }
        }
    }

    private fun createLoadingDialog() {
        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(R.layout.loading_dialog)
            .setCancelable(false)
            .create()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}