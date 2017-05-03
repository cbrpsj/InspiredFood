package pc.inspiredfood

import android.content.Context
import android.view.LayoutInflater
import android.widget.*


object TableRowFactory {

    fun createIngredientTableRow(context: Context): TableRow {

        // Setup inflater, and inflate custom ingredient table row
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val tableRow = inflater.inflate(R.layout.table_row_ingredient, null) as TableRow

        // Find autocomplete views in custom table row and cast to correct types
        val ingredientView = tableRow.getChildAt(0) as AutoCompleteTextView
        val unitView = tableRow.getChildAt(2) as AutoCompleteTextView

        // Create an array adapter and set autocomplete's adapter to the new adapter
        val ingredientAutoCompleteAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, App.ingredients.toList())
        val unitAutoCompleteAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, App.units.toList())

        ingredientView.setAdapter(ingredientAutoCompleteAdapter)
        unitView.setAdapter(unitAutoCompleteAdapter)

        return tableRow
    }


    fun createTimerTableRow(context: Context): TableRow {

        // Setup inflater, and inflate custom timer table row
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val tableRow = inflater.inflate(R.layout.table_row_timer, null) as TableRow

        return tableRow
    }
}