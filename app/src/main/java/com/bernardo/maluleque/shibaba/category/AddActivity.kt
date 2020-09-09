package com.bernardo.maluleque.shibaba.category

import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bernardo.maluleque.shibaba.R
import com.bernardo.maluleque.shibaba.model.Category
import com.bernardo.maluleque.shibaba.model.CategoryType
import com.bernardo.maluleque.shibaba.viewmodel.CategoryViewModel
import kotlinx.android.synthetic.main.activity_add.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddActivity : AppCompatActivity() {


    private lateinit var selectedCategoryType: CategoryType
    val categoryViewModel: CategoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        textInputLayoutCategoryType.setOnClickListener { openCategoryTypeDialog() }

        buttonSaveCategory.setOnClickListener {
            val categoryName = categoryNameInputText.text.toString()
            categoryViewModel.saveCategory(Category(type = selectedCategoryType, name = categoryName))
        }
    }

    private fun openCategoryTypeDialog() {
        val arrayAdapter = ArrayAdapter<CategoryType>(this, android.R.layout.select_dialog_item, CategoryType.values())
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.select_category)
                .setAdapter(arrayAdapter) { _: DialogInterface?, which: Int ->
                    categoryTypeInputText.setText(arrayAdapter.getItem(which)?.type)
                    selectedCategoryType = arrayAdapter.getItem(which)!!
                }
        builder.create()
        builder.show()
    }
}