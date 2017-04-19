package pc.inspiredfood

import android.app.Activity
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TableRow
import kotlinx.android.synthetic.main.activity_recipe.*
import org.jetbrains.anko.*
import pc.inspiredfood.App.Companion.categories
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
import pc.inspiredfood.CRUD.updatePreparation
import pc.inspiredfood.CRUD.updateRecipeName
import java.text.NumberFormat

class RecipeActivity : Activity() {

    var id = 0
    var editModeEnabled = false
    var ingredientsInRecipe: List<Triple<String, Double, String>> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        // Set id to Int sent in intent from previous activity
        id = intent.getIntExtra(C.recipeId, -1)

        setupInfoLine()

        getRecipeDetails(id)
        makeViewsUneditable()       // Default setting

        // Set event listener
        button_edit_save.onClick { toggleEditMode() }
    }


    override fun onResume() {

        super.onResume()
    }


    // Get details about a specific recipe
    fun getRecipeDetails(id: Int) {

        ingredientsInRecipe = getIngredientsInRecipe(id)

        recipe_name.setText(getRecipeName(id))
        recipe_preparation.setText(getPreparation(id))
        createTableRows()
    }


    // Create table rows and insert data in rows
    fun createTableRows() {

        // Create a table row for each element in the list and display ingredient info
        for(ingredientLine in ingredientsInRecipe) {

            // Create table row
            val tableRow = TableRow(this)
            val layoutParams = TableRow.LayoutParams()

            // Set padding and border for table row
            tableRow.setPadding(dpToPixel(5f), dpToPixel(5f), dpToPixel(7f), dpToPixel(5f))
            tableRow.background = getDrawable(R.drawable.cell)

            // Set weight (used in ingredient text view). Ensure space between ingredient and amount
            layoutParams.weight = 1f

            // Create EditText view for table row
            val editTextViewIngredient = EditText(this)
            val editTextViewAmount = EditText(this)
            val editTextViewUnit = EditText(this)

            // Apply weight to ingredient EditText view
            editTextViewIngredient.layoutParams = layoutParams

            // Align text in amount EditText view to the right
            editTextViewAmount.gravity = Gravity.END

            // Set attributes for all three EditText views in the table row
            setEditTextViewAttributes(editTextViewIngredient, ingredientLine.first, 0, 0, 0, 0)
            setEditTextViewAttributes(editTextViewAmount, formatAmount(ingredientLine.second), dpToPixel(15f), 0, dpToPixel(10f), 0)
            setEditTextViewAttributes(editTextViewUnit, ingredientLine.third, 0, 0, 0, 0)

            // Add EditText views to table row, and add table row to table layout
            tableRow.addView(editTextViewIngredient)
            tableRow.addView(editTextViewAmount)
            tableRow.addView(editTextViewUnit)
            ingredients_table.addView(tableRow)
        }
    }


    // Set attributes to an EditText view
    fun setEditTextViewAttributes(editTextView: EditText, text: String, left: Int, top: Int, right: Int, bottom: Int) {
        // Remove underlining in EditText
        editTextView.background = null

        editTextView.setPadding(left, top, right, bottom)
        editTextView.textColor = R.color.cellTextColor
        editTextView.setText(text)
        editTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
    }


    // Toggle between edit mode and read-only mode
    fun toggleEditMode() {

        if(!editModeEnabled) enterEditMode()
        else exitEditModeAndSave()
    }


    // Enables recipe editing
    fun enterEditMode() {

        editModeEnabled = true
        button_edit_save.setText("${getString(R.string.save)}")
        recipe_detail.backgroundColor = resources.getColor(R.color.backgroundEditMode, null)
        makeViewsEditable()
    }


    // Disable recipe editing and save the changes
    fun exitEditModeAndSave() {

        editModeEnabled = false
        button_edit_save.setText("${getString(R.string.edit)}")
        recipe_detail.backgroundColor = resources.getColor(R.color.backgroundWhite, null)
        makeViewsUneditable()
        hideKeyboard()
        saveRecipe()
    }


    fun makeViewsEditable() {

        makeViewEditable(recipe_name)
        spinner.isEnabled = true
        makeViewEditable(recipe_preparation)

        val tableRows = ingredients_table.childrenSequence()

        for(tableRow in tableRows) {
            makeViewEditable((tableRow as TableRow).getChildAt(0) as EditText)
            makeViewEditable(tableRow.getChildAt(1) as EditText)
            makeViewEditable(tableRow.getChildAt(2) as EditText)
        }
    }


    fun makeViewsUneditable() {

        makeViewUneditable(recipe_name)
        spinner.isEnabled = false
        makeViewUneditable(recipe_preparation)

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


    // Save all fields in the recipe in the DB
    fun saveRecipe() {

        val ingredientsInRecipe = mutableListOf<Triple<Int, Double, Int>>()
        val tableRows = ingredients_table.childrenSequence()

        // Get ingredients in the table
        for(tableRow in tableRows) {

            // Cast to TableRow
            tableRow as TableRow

            // Get values from EditText views in tableRow
            val ingredientName = (tableRow.getChildAt(0) as EditText).text.toString()
            val amount = (tableRow.getChildAt(1) as EditText).text.toString().toDouble()
            val unitName = (tableRow.getChildAt(2) as EditText).text.toString()

            // Create potentially new ingredient and unit in DB
            createIngredient(ingredientName)
            createUnit(unitName)

            // Get ingredientId and unitId from DB
            val ingredientId = getIngredientId(ingredientName)
            val unitId = getUnitId(unitName)

            // Add ingredient to list of ingredients in recipe
            ingredientsInRecipe.add(Triple(ingredientId, amount, unitId))
        }

        // Update recipe name and preparation in DB
        updateRecipeName(id, recipe_name.text.toString())
        updatePreparation(id, recipe_preparation.text.toString())

        // Delete all previous ingredients in recipe, afterwards add all ingredients from UI to DB
        deleteIngredientsInRecipe(id)
        createIngredientsInRecipe(id, ingredientsInRecipe)

        updateRecipeList = true
    }


    fun setupInfoLine() {

        // Spinner (Dropdown)

        val localised_categories = listOf<String>(getString(R.string.starter), getString(R.string.main), getString(R.string.dessert))

        // Create arrayAdapter with our own item_spinner.xml
        val spinnerAdapter = ArrayAdapter(this, R.layout.item_spinner, localised_categories)

        // Set spinner adapter and set default selection to index 1 (Main course)
        spinner.adapter = spinnerAdapter
        spinner.setSelection(findRecipeCategory())

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        // Number of persons EditText (Numbers only):

        // Find number of people for this recipe and display in UI
        val noOfPeople = getNoOfPeople(id)
        no_of_persons.setText(noOfPeople.toString())

        // Listen for click on Done button on soft keyboard
        no_of_persons.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                no_of_persons.clearFocus()
                updatePersonTextAndIngredientAmounts(noOfPeople)
                true
            }
            else false
        }

        // Listen for change of focus away from EditText
        no_of_persons.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard()
                updatePersonTextAndIngredientAmounts(noOfPeople)
            }
        }


        // Person EditText:

        person_text.text =  if (noOfPeople < 2) getString(R.string.recipe_info_person)
                            else getString(R.string.recipe_info_persons)
    }


    // Update ingredient
    fun updatePersonTextAndIngredientAmounts(noOfPeople: Int) {

        // Find the updated number of people (string) from user input
        val newNoOfPeopleString = no_of_persons.text.toString()

        // When updated number of people is empty or < 1, number of people is set to 2
        val newNoOfPeopleInt =  if (newNoOfPeopleString.isEmpty() || newNoOfPeopleString.toInt() < 1) 2
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

            val ingredientView = (tableRow as TableRow).getChildAt(1) as EditText
            ingredientView.setText(calculateAmount(noOfPeople, newNoOfPeopleInt, ingredientsInRecipe[index++].second))
        }
    }


    fun findRecipeCategory(): Int {

        for (i in categories.indices)
            if (getRecipeCategory(id) == categories[i]) return i

        return 1 // default main course
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


    fun hideKeyboard() { inputMethodManager.hideSoftInputFromWindow(recipe_detail.windowToken, 0)}


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
