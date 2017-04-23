package pc.inspiredfood

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.activity_recipe_list.*
import org.jetbrains.anko.*
import pc.inspiredfood.App.Companion.categories
import pc.inspiredfood.App.Companion.ingredients
import pc.inspiredfood.App.Companion.units
import pc.inspiredfood.App.Companion.updateRecipeList
import pc.inspiredfood.CRUD.createIngredient
import pc.inspiredfood.CRUD.createIngredientsInRecipe
import pc.inspiredfood.CRUD.createUnit
import pc.inspiredfood.CRUD.deleteIngredientsInRecipe
import pc.inspiredfood.CRUD.getIngredientId
import pc.inspiredfood.CRUD.getPreparation
import pc.inspiredfood.CRUD.getIngredientsInRecipe
import pc.inspiredfood.CRUD.getNoOfPeople
import pc.inspiredfood.CRUD.getRecipeCategory
import pc.inspiredfood.CRUD.getRecipeName
import pc.inspiredfood.CRUD.getUnitId
import pc.inspiredfood.CRUD.updateNoOfPeople
import pc.inspiredfood.CRUD.updatePreparation
import pc.inspiredfood.CRUD.updateRecipeCategory
import pc.inspiredfood.CRUD.updateRecipeName
import java.text.NumberFormat

class RecipeActivity : Activity() {

    var id = 0
    var editModeEnabled = false
    var ingredientsInRecipe = mutableListOf<Triple<String, Double, String>>()

    // Holds long pressed table row view
    lateinit var tableRowView: View
        private set

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        // Set id to Int sent in intent from previous activity
        id = intent.getIntExtra(C.recipeId, -1)

        setupInfoLine()
        getRecipeDetails()
        makeViewsUneditable()       // Default setting

        // Set event listener for edit/save button
        button_edit_save.onClick { toggleEditMode() }
    }


    // Create custom context menu for long pressed table row
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {

        // When not in edit mode or long pressed table row is the last row, then return
        if (!editModeEnabled || ingredients_table.getChildAt(ingredients_table.childCount -1) == v)
            return

        val inflate = menuInflater
        inflate.inflate(R.menu.context_menu, menu)

        // Save long pressed table row view
        if (v != null) tableRowView = v
    }


    // Remove long pressed table row view
    override fun onContextItemSelected(item: MenuItem?): Boolean {

        ingredients_table.removeView(tableRowView)
        return true
    }


    // Get details about a specific recipe and map to UI
    fun getRecipeDetails() {

        ingredientsInRecipe = getIngredientsInRecipe(id)
        recipe_name.setText(getRecipeName(id))
        recipe_preparation.setText(getPreparation(id))

        for(ingredientLine in ingredientsInRecipe)
            createTableRow(ingredientLine)
    }


    // Create an ingredient table row
    fun createTableRow(ingredientLine: Triple<String, Double, String>?) {

        // Create table row
        val tableRow = TableRow(this)
        val layoutParams = TableRow.LayoutParams()

        // Set padding and border for table row
        tableRow.setPadding(dpToPixel(5f), dpToPixel(5f), dpToPixel(7f), dpToPixel(5f))
        tableRow.background = getDrawable(R.drawable.cell_border_and_background)
        registerForContextMenu(tableRow)

        // Set weight (used in ingredient text view). Ensure space between ingredient and amount
        layoutParams.weight = 1f

        // Create EditText view for table row
        val editTextViewIngredient = AutoCompleteTextView(this)
        val editTextViewAmount = EditText(this)
        val editTextViewUnit = AutoCompleteTextView(this)

        // Create an arrayAdapter and set autocomplete's adapter to the new adapter
        val autoCompleteAdapterIngredient = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ingredients.toList())
        editTextViewIngredient.setAdapter(autoCompleteAdapterIngredient)

        val autoCompleteAdapterUnit = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, units.toList())
        editTextViewUnit.setAdapter(autoCompleteAdapterUnit)

        // Apply weight to ingredient EditText view and minimum width for unit
        editTextViewIngredient.layoutParams = layoutParams
        editTextViewUnit.minimumWidth = dpToPixel(55f)

        // Set attributes, hints and hint text style for all three EditText views in the table row
        setEditTextViewAttributes(editTextViewIngredient, 0, 0, 0, 0, getString(R.string.ingredient))
        setEditTextViewAttributes(editTextViewAmount, dpToPixel(15f), 0, dpToPixel(10f), 0, getString(R.string.amount))
        setEditTextViewAttributes(editTextViewUnit, 0, 0, 0, 0, getString(R.string.unit))

        // When ingredient line is not null, map data from ingredient line to table row
        if (ingredientLine != null) {

            editTextViewIngredient.setText(ingredientLine.first)
            editTextViewAmount.setText(formatAmount(ingredientLine.second))
            editTextViewUnit.setText(ingredientLine.third)
        }

        // Add EditText views to table row, and add table row to table layout
        tableRow.addView(editTextViewIngredient)
        tableRow.addView(editTextViewAmount)
        tableRow.addView(editTextViewUnit)
        ingredients_table.addView(tableRow)
    }


    // Set attributes and event listener to an EditText view
    fun setEditTextViewAttributes(editTextView: EditText, left: Int, top: Int, right: Int, bottom: Int, hintText: String) {

        // Remove underlining in EditText
        editTextView.background = null

        editTextView.setPadding(left, top, right, bottom)
        editTextView.textColor = getColor(R.color.textColorListCellPreparation)
        editTextView.hintTextColor = getColor(R.color.hintTextColor)
        editTextView.hint = Html.fromHtml("<small><i>$hintText</i></small>", Html.FROM_HTML_MODE_COMPACT)
        editTextView.maxLines = 1
        editTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
        editTextView.setOnKeyListener { v, keyCode, event -> removeEmptyIngredientRowWhenNeeded(); createEmptyIngredientRowWhenNeeded() }

        if (editTextView is AutoCompleteTextView) {

            editTextView.inputType = InputType.TYPE_TEXT_VARIATION_PHONETIC // To avoid spelling check
            editTextView.gravity = Gravity.CENTER_VERTICAL
            editTextView.threshold = 1 // Number of characters to enter before autocomplete activates
        }
        else {
            // Edit text view is then for amount
            editTextView.inputType = InputType.TYPE_CLASS_NUMBER.or(InputType.TYPE_NUMBER_FLAG_DECIMAL)
            editTextView.gravity = Gravity.CENTER.or(Gravity.END)
        }
    }


    // Find and delete empty ingredient rows, except the last row
    fun removeEmptyIngredientRowWhenNeeded() {

        val tableRows = ingredients_table.childrenSequence()
        var indexOfTableRowToRemove: Int? = null

        for ((index, tableRow) in tableRows.withIndex()) {

            if (countEmptyFieldsInIngredientRow(index) == 3 && index != tableRows.count()-1) {
                indexOfTableRowToRemove = index
                break
            }
        }

        if (indexOfTableRowToRemove != null) {
            ingredients_table.removeViewAt(indexOfTableRowToRemove)
            hideKeyboard()
        }
    }


    // Toggle between edit mode and read-only mode
    fun toggleEditMode() {

        if(!editModeEnabled) enterEditMode()
        else exitEditModeAndSave()
    }


    // Enable recipe editing
    fun enterEditMode() {

        // Display default ingredient amounts and number of people
        val noOfPeople = getNoOfPeople(id)
        updatePersonTextAndIngredientAmounts(noOfPeople, true)

        editModeEnabled = true
        button_edit_save.text = getString(R.string.save)
        recipe_detail.backgroundColor = resources.getColor(R.color.backgroundEditMode, null)

        // Create empty table row at bottom of ingredient table for new ingredient
        createTableRow(null)

        setupSpinner()
        makeViewsEditable()
    }


    // Disable recipe editing and save the changes
    fun exitEditModeAndSave() {

        // Save recipe if no ingredient line has empty fields
        if (!saveRecipe())
            return

        editModeEnabled = false
        button_edit_save.text = getString(R.string.edit)
        recipe_detail.backgroundColor = resources.getColor(R.color.backgroundWhite, null)

        setupInfoLine()
        makeViewsUneditable()
        hideKeyboard()
    }


    // Enable editing of recipe fields
    fun makeViewsEditable() {

        makeViewEditable(recipe_name)
        makeViewEditable(recipe_preparation)
        spinner.isEnabled = true

        // Find all table rows in table layout
        val tableRows = ingredients_table.childrenSequence()

        for(tableRow in tableRows) {
            makeViewEditable((tableRow as TableRow).getChildAt(0) as EditText)
            makeViewEditable(tableRow.getChildAt(1) as EditText)
            makeViewEditable(tableRow.getChildAt(2) as EditText)
        }
    }


    // Disable editing of recipe fields
    fun makeViewsUneditable() {

        makeViewUneditable(recipe_name)
        makeViewUneditable(recipe_preparation)
        spinner.isEnabled = false

        // Find all table rows in table layout
        val tableRows = ingredients_table.childrenSequence()

        for(tableRow in tableRows) {
            makeViewUneditable((tableRow as TableRow).getChildAt(0) as EditText)
            makeViewUneditable(tableRow.getChildAt(1) as EditText)
            makeViewUneditable(tableRow.getChildAt(2) as EditText)
        }
    }


    // Set attributes to enable editing in an EditText view
    fun makeViewEditable(view: EditText) {

        view.isCursorVisible = true
        view.isClickable = true
        view.isFocusable = true
        view.isFocusableInTouchMode = true
    }


    // Set attributes to disable editing in an EditText view
    fun makeViewUneditable(view: EditText) {

        view.isCursorVisible = false
        view.isClickable = false
        view.isFocusable = false
        view.isFocusableInTouchMode = false
    }


    // Save all fields in the recipe to the DB
    fun saveRecipe(): Boolean {

        // Find all table rows in table layout
        var tableRows = ingredients_table.childrenSequence()

        for ((index, tableRow) in tableRows.withIndex()) {

            tableRow as TableRow
            val emptyFields = countEmptyFieldsInIngredientRow(index)

            // If any ingredient fields (except the last row) are empty, display error message and abort save
            if (emptyFields > 0) {

                if (index == ingredients_table.childCount-1 && emptyFields == 3)
                    continue

                longToast(getString(R.string.ingredientError).toString())
                return false
            }
        }

        // Remove last ingredient line when all fields are empty
        if (countEmptyFieldsInIngredientRow(ingredients_table.childCount-1) == 3) {

            ingredients_table.removeViewAt(ingredients_table.childCount - 1)
            tableRows = ingredients_table.childrenSequence()
        }

        val tmpIngredientsInRecipe = mutableListOf<Triple<Int, Double, Int>>()

        // Get ingredients in the table
        for(tableRow in tableRows) {

            // Cast to TableRow
            tableRow as TableRow

            // Get values from EditText views in tableRow
            val ingredientName = (tableRow.getChildAt(0) as EditText).text.toString()
            val amount = (tableRow.getChildAt(1) as EditText).text.toString().replace(",", ".").toDouble()
            val unitName = (tableRow.getChildAt(2) as EditText).text.toString()

            // Create potentially new ingredient and unit in DB
            createIngredient(ingredientName)
            createUnit(unitName)

            // Get ingredientId and unitId from DB
            val ingredientId = getIngredientId(ingredientName)
            val unitId = getUnitId(unitName)

            // Add ingredient to list of ingredients in recipe
            tmpIngredientsInRecipe.add(Triple(ingredientId, amount, unitId))
        }

        // Update recipe name, category, no of people and preparation in DB
        updateRecipeName(id, recipe_name.text.toString())
        updateRecipeCategory(id, spinner.selectedItemPosition +1)
        updateNoOfPeople(id, no_of_persons.text.toString().toInt())
        updatePreparation(id, recipe_preparation.text.toString())

        // Delete all previous ingredients in recipe, afterwards add all ingredients from UI to DB
        deleteIngredientsInRecipe(id)
        createIngredientsInRecipe(id, tmpIngredientsInRecipe)

        ingredientsInRecipe = getIngredientsInRecipe(id)

        updateRecipeList = true
        return true
    }


    fun countEmptyFieldsInIngredientRow(rowNumber: Int): Int {

        val lastTableRow = ingredients_table.getChildAt(rowNumber) as TableRow
        var counter = 0

        if ((lastTableRow.getChildAt(0) as EditText).text.toString().isEmpty()) counter++
        if ((lastTableRow.getChildAt(1) as EditText).text.toString().isEmpty()) counter++
        if ((lastTableRow.getChildAt(2) as EditText).text.toString().isEmpty()) counter++

        return counter
    }


    // When all fields in last table row are filled out, create new empty row
    fun createEmptyIngredientRowWhenNeeded(): Boolean {

        if (editModeEnabled && countEmptyFieldsInIngredientRow(ingredients_table.childCount-1) == 0) {

            createTableRow(null)
            return true
        }

        return false
    }


    // Setup spinner (dropdown) based on state of edit mode
    fun setupSpinner() {

        // Find localised category names
        val localised_categories = listOf<String>(getString(R.string.starter), getString(R.string.main), getString(R.string.dessert))

        // Create arrayAdapter with custom default spinner
        var spinnerAdapter = ArrayAdapter(this, R.layout.spinner_default, localised_categories)
        spinner.background = getDrawable(R.drawable.spinner_default_background)

        // When edit mode enabled, set spinner to custom edit layout
        if (editModeEnabled) {
            spinnerAdapter = ArrayAdapter(this, R.layout.spinner_edit, localised_categories)
            spinner.background = getDrawable(R.drawable.spinner_edit_background)
        }

        // Set custom spinner popup
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_popup)

        spinner.adapter = spinnerAdapter
        spinner.setSelection(findRecipeCategory())
    }


    // Display recipe category and number of people
    fun setupInfoLine() {

        setupSpinner()

        // Find number of people for this recipe and display in UI
        val noOfPeople = getNoOfPeople(id)

        no_of_persons.setText(noOfPeople.toString())

        // Listen for click on Done button on soft keyboard
        no_of_persons.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {

                hideKeyboard()
                no_of_persons.clearFocus()
                updatePersonTextAndIngredientAmounts(noOfPeople, false)
                true
            }

            else false
        }

        // Listen for change of focus away from EditText
        no_of_persons.setOnFocusChangeListener { v, hasFocus ->

            if (!hasFocus) {

                hideKeyboard()
                updatePersonTextAndIngredientAmounts(noOfPeople, false)
            }
        }

        person_text.text =  if (noOfPeople < 2) getString(R.string.recipe_info_person)
                            else getString(R.string.recipe_info_persons)
    }


    // Update person_text and ingredient amount
    fun updatePersonTextAndIngredientAmounts(noOfPeople: Int, reset: Boolean) {

        // Find the updated number of people (string) from user input
        val newNoOfPeopleString = no_of_persons.text.toString()

        // If reset true, reset no of people from db. If empty or 0, set to 2, else set to user input
        val newNoOfPeopleInt =  if (reset) noOfPeople
                                else if (newNoOfPeopleString.isEmpty() || newNoOfPeopleString.toInt() < 1) 2
                                else newNoOfPeopleString.toInt()

        // Display new number of people in UI
        no_of_persons.setText(newNoOfPeopleInt.toString())

        // Update person text according to number of people
        person_text.text =  if (newNoOfPeopleInt < 2) getString(R.string.recipe_info_person)
                            else getString(R.string.recipe_info_persons)

        // When edit mode is enabled, ingredient amount is not updated
        if (editModeEnabled)
            return

        // Get all table rows from table layout
        val tableRows = ingredients_table.childrenSequence()
        var index = 0

        // Get the EditText field for amount and display the updated ingredient amount for each tableRow
        for(tableRow in tableRows) {

            val amountView = (tableRow as TableRow).getChildAt(1) as EditText
            amountView.setText(calculateAmount(noOfPeople, newNoOfPeopleInt, ingredientsInRecipe[index++].second))
        }
    }


    fun findRecipeCategory(): Int {

        for (i in categories.indices)
            if (getRecipeCategory(id) == categories[i]) return i

        return 1 // default main course - can be used for new recipes
    }


    // Calculate ingredient amount based on number of people entered by user
    fun calculateAmount(noOfPeople: Int, newNoOfPeople: Int, amount: Double) =
            if (newNoOfPeople == noOfPeople) formatAmount(amount)
            else formatAmount((amount / noOfPeople) * newNoOfPeople)


    // Convert amount (Double) to a string with between 0 - 2 decimals
    fun formatAmount(amount: Double): String {

        val formatter = NumberFormat.getInstance()
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 2

        return formatter.format(amount)
    }


    fun hideKeyboard() { inputMethodManager.hideSoftInputFromWindow(recipe_detail.windowToken, 0) }


    // Convert DP to Pixel
    fun dpToPixel(dp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()


    // Get category in the correct language
    fun translateCategory(categoryName: String): String =
            when(categoryName){
                "Starter" -> getString(R.string.starter)
                "Main"    -> getString(R.string.main)
                else      -> getString(R.string.dessert)
            }
}
