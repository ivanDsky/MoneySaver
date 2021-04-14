package ua.zloyhr.moneysaver.ui.additem

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ua.zloyhr.moneysaver.R
import ua.zloyhr.moneysaver.databinding.FragmentAddItemBinding
import ua.zloyhr.moneysaver.ui.MainActivity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddEditItemFragment : Fragment(R.layout.fragment_add_item) {
    private val viewModel: AddEditItemViewModel by viewModels()
    private lateinit var binding: FragmentAddItemBinding
    private val calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentMonth = calendar.get(Calendar.MONTH)
    private val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    private val locale = Locale.ENGLISH

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navArgs: AddEditItemFragmentArgs by navArgs()
        (activity as MainActivity).supportActionBar?.title = navArgs.title

        val isPositive: Boolean = if (viewModel.task != null) {
            viewModel.task?.value!! >= 0
        } else {
            navArgs.title == "Add positive item"
        }

        binding = FragmentAddItemBinding.bind(view)
        binding.apply {
            if (viewModel.task != null) {
                etName.editText?.setText(viewModel.task?.name)
                etValue.editText?.setText(viewModel.task?.value.toString())
                etDate.editText?.setText(
                    SimpleDateFormat.getDateInstance(DateFormat.LONG,locale).format(viewModel.task?.timeCreated)
                )
            }
            fabSaveItem.setOnClickListener { _ ->
                if (validateFields()) {
                    viewModel.onSendClick(
                        etName.editText?.text.toString(),
                        etValue.prefixText.toString() + etValue.editText?.text.toString(),
                        etDate.editText?.text.toString(),
                    )
                    NavHostFragment.findNavController(this@AddEditItemFragment)
                        .navigate(R.id.action_addEditItemFragment_to_miHome)
                }
            }

            etName.hint = if(isPositive) "Enter profit title" else "Enter expense title"
            etValue.prefixText = if (isPositive) "+" else "-"
            etValue.hint = if (isPositive) "+500.00$" else "-500.00$"
            etDate.hint = SimpleDateFormat.getDateInstance(DateFormat.LONG,locale).format(calendar.timeInMillis)


            val dialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                etDate.editText?.setText(
                    SimpleDateFormat.getDateInstance(DateFormat.LONG, locale).format(calendar.timeInMillis)
                )
            }, currentYear, currentMonth, currentDayOfMonth)

            etDateField.setOnClickListener {
                dialog.show()
                val manager =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

    }

    private fun validateFields(): Boolean {
        var ret = true
        binding.apply {
            if (etName.editText?.text?.isBlank() != false) {
                etName.error = "Name is blank";
                ret = false
            } else {
                etName.error = null
            }

            try {
                etValue.editText?.text.toString().toDouble()
                etValue.error = null
            } catch (e: Exception) {
                etValue.error = "Not a double";
                ret = false
            }

            if (etDate.editText?.text?.isBlank() != false) {
                etDate.error = "Date is blank";
                ret = false
            } else {
                etDate.error = null
            }
        }
        return ret
    }
}
