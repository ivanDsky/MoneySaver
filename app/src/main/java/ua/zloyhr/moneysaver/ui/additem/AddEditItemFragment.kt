package ua.zloyhr.moneysaver.ui.additem

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import ua.zloyhr.moneysaver.R
import ua.zloyhr.moneysaver.databinding.FragmentAddItemBinding
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddItemBinding.bind(view)
        binding.apply {
            if(viewModel.task != null) {
                etName.setText(viewModel.task?.name)
                etValue.setText(viewModel.task?.value.toString())
                etDate.setText(
                    SimpleDateFormat.getDateInstance().format(viewModel.task?.timeCreated)
                )
            }
            fabSaveItem.setOnClickListener {_ ->
                if (validateFields()) {
                    viewModel.onSendClick(
                        etName.text.toString(),
                        etValue.text.toString(),
                        etDate.text.toString(),
                    )
                    NavHostFragment.findNavController(this@AddEditItemFragment)
                        .navigate(R.id.action_addEditItemFragment_to_miHome)
                }
            }

            etDate.hint = SimpleDateFormat.getDateInstance().format(calendar.timeInMillis)

            val dialog = DatePickerDialog(requireContext(), { view, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                etDate.setText(SimpleDateFormat.getDateInstance().format(calendar.timeInMillis))
            }, currentYear, currentMonth, currentDayOfMonth)

            etDate.setOnClickListener {
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
            if (etName.text.isBlank()) {
                etName.error = "Name is blank";
                ret = false
            } else {
                etName.error = null
            }

            try {
                etValue.text.toString().toDouble()
                etValue.error = null
            } catch (e: Exception) {
                etValue.error = "Not a double";
                ret = false
            }

            if (etDate.text.isBlank()) {
                etDate.error = "Date is blank";
                ret = false
            } else {
                etDate.error = null
            }
        }
        return ret
    }
}
