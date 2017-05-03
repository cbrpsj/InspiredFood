package pc.inspiredfood

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.os.SystemClock
import android.os.Vibrator
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import kotlinx.android.synthetic.main.activity_recipe.*
import org.jetbrains.anko.*
import pc.inspiredfood.App.Companion.categories
import pc.inspiredfood.App.Companion.defaultNoOfPeople
import pc.inspiredfood.App.Companion.updateRecipeList
import java.text.NumberFormat

class RecipeActivity : Activity() {

    val maxNoOfPeople = 20                  // App design is based on 20 as highest number of people
    val maxAmountValue: Double = 999.9999   // App design is based on 999.9999 as highest ingredient amount in edit mode
    var id = 0                              // Holds recipe id selected in previous activity or -1 for new recipe
    var editModeEnabled = false
    var noOfPeople = defaultNoOfPeople
    var ingredientsInRecipe = mutableListOf<Triple<String, Double, String>>()

    // Holds long pressed table row view
    lateinit var tableRowView: View
        private set

    // Holds sound effect for timer alarm
    var alarmSound: Ringtone? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        // Retrieve bundle holding long array from previous activity
        val bundle = intent.getBundleExtra(C.recipeDataBundle)
        val longArray = bundle.getLongArray(C.recipeDataArray)

        // Recipe id is the first array element
        id = longArray[0].toInt()

        // New empty recipe starts in edit mode
        if (id == -1) {

            editModeEnabled = true
            enterEditMode()
        }
        // Existing recipe starts in read-only mode
        else {

            setupInfoLine()
            getRecipeDetails(longArray)
            makeViewsUneditable()
        }

        // Setup sound effect for timer alarm
        try {

            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            alarmSound = RingtoneManager.getRingtone(applicationContext, uri)
        }
        catch (e: Exception){

            // Sound effect could not be loaded
            alarmSound = null
        }

        // Set event listener for edit/save, cancel and SMS button
        button_edit_save.onClick { toggleEditMode() }
        button_cancel.onClick { cancelEditRecipe() }
        button_send_sms.onClick { sendSms() }
    }


    // Create custom context menu for long pressed table row
    override fun onCreateContextMenu(menu: ContextMenu?, view: View?, menuInfo: ContextMenu.ContextMenuInfo?) {

        val tableLayout = view?.parent as TableLayout

        // If not in edit mode, then return
        if (!editModeEnabled) return

        // If long pressed table row is the last ingredient or timer row, then return
        if (tableLayout.id == ingredients_table.id && ingredients_table.getChildAt(ingredients_table.childCount -1) == view)
            return

        if (tableLayout.id == timers_table.id && timers_table.getChildAt(timers_table.childCount -1) == view)
            return

        val inflate = menuInflater
        inflate.inflate(R.menu.context_menu, menu)

        // Save long pressed table row view
        tableRowView = view
    }


    // Remove long pressed table row view
    override fun onContextItemSelected(item: MenuItem?): Boolean {

        val tableLayout = tableRowView.parent as TableLayout

        // If long pressed table row is the last ingredient or timer row, then return
        if (tableLayout.id == ingredients_table.id)
            ingredients_table.removeView(tableRowView)

        if (tableLayout.id == timers_table.id)
            timers_table.removeView(tableRowView)

        return true
    }


    // Display recipe category and number of people
    fun setupInfoLine() {

        setupSpinner()

        // Find number of people for this recipe and display in UI
        noOfPeople = CRUD.getNoOfPeople(id)

        no_of_persons.setText(noOfPeople.toString())

        person_text.text =
                if (noOfPeople < 2) getString(R.string.recipe_info_person)
                else getString(R.string.recipe_info_persons)

        // Listen for click on Done button on soft keyboard
        no_of_persons.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {

                hideKeyboard()
                no_of_persons.clearFocus()
                updatePersonTextAndIngredientAmounts(false)
                true
            }
            else false
        }

        // Listen for change of focus away from EditText
        no_of_persons.setOnFocusChangeListener { v, hasFocus ->

            if (!hasFocus) {

                hideKeyboard()
                updatePersonTextAndIngredientAmounts(false)
            }
        }
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


    // Get details about a specific recipe and map to UI
    fun getRecipeDetails(timerDataArray: LongArray) {

        recipe_name.setText(CRUD.getRecipeName(id))
        recipe_preparation.setText(CRUD.getPreparation(id))

        // Map ingredient lines to UI
        ingredientsInRecipe = CRUD.getIngredientsInRecipe(id)

        for(ingredientLine in ingredientsInRecipe)
            createIngredientTableRow(ingredientLine)

        // Map timer lines to UI
        val timersInRecipe = CRUD.getRecipeTimers(id)

        // Remove timer headline if no timers present
        if (timersInRecipe.isEmpty())
            timer_headline.visibility = View.INVISIBLE
        else {

            // Otherwise make timer headline visible
            timer_headline.visibility = View.VISIBLE

            // Create a table row for each timer
            for (timer in timersInRecipe) {

                // Check if timer is persistent (has survived cancellation of edit mode)
                var persistentTimer: Long? = null

                for (i in 1..timerDataArray.lastIndex step 2)
                    if (timer.first == timerDataArray[i].toInt()) {

                        persistentTimer = timerDataArray[i + 1]
                        break
                    }

                createTimerTableRow(timer, persistentTimer)
            }
        }
    }


    // Create an ingredient table row
    fun createIngredientTableRow(ingredientLine: Triple<String, Double, String>?) {

        val tableRow = TableRowFactory.createIngredientTableRow(this)

        // When there is an ingredient line, add content to the table row's text views
        if (ingredientLine != null) {

            (tableRow.getChildAt(0) as AutoCompleteTextView).setText(ingredientLine.first)
            (tableRow.getChildAt(1) as EditText).setText(formatAmount(ingredientLine.second))
            (tableRow.getChildAt(2) as AutoCompleteTextView).setText(ingredientLine.third)
        }

        // Register table row for the context menu
        registerForContextMenu(tableRow)

        // Set on key event listener for all text views in table row
        for (view in tableRow.childrenSequence())
            view.setOnKeyListener { v, keyCode, event -> removeEmptyIngredientRowWhenNeeded(); createEmptyIngredientRowWhenNeeded() }

        ingredients_table.addView(tableRow)
    }


    // Validate all ingredient table rows
    fun validateIngredientTableRow(ingredientTableRows: Sequence<View>): Boolean {

        for ((index, tableRow) in ingredientTableRows.withIndex()) {

            // If any ingredient fields (except the last row) are empty, display error message
            val emptyFields = countEmptyFieldsInIngredientRow(index)

            if (emptyFields > 0) {

                if (index == ingredients_table.childCount - 1 && emptyFields == 3)
                    continue

                longToast(getString(R.string.ingredient_error))
                return false
            }

            // If amount input is more than max amount, display error message
            val amount = ((tableRow as TableRow).getChildAt(1) as EditText).text.toString().replace(",", ".").toDouble()

            if (amount > maxAmountValue) {

                longToast(getString(R.string.ingredient_amount_error))
                return false
            }
        }

        return true
    }


    // When all fields in last ingredient row are filled out, create new empty row
    fun createEmptyIngredientRowWhenNeeded(): Boolean {

        if (editModeEnabled && countEmptyFieldsInIngredientRow(ingredients_table.childCount - 1) == 0) {

            createIngredientTableRow(null)
            return true
        }

        return false
    }


    // Find and delete empty ingredient rows, except the last row
    fun removeEmptyIngredientRowWhenNeeded() {

        val tableRows = ingredients_table.childrenSequence()
        var indexOfTableRowToRemove: Int? = null

        for ((index, tableRow) in tableRows.withIndex()) {

            if (countEmptyFieldsInIngredientRow(index) == 3 && index != tableRows.count() - 1) {

                indexOfTableRowToRemove = index
                break
            }
        }

        if (indexOfTableRowToRemove != null) {

            ingredients_table.removeViewAt(indexOfTableRowToRemove)
            hideKeyboard()
        }
    }


    fun countEmptyFieldsInIngredientRow(rowNumber: Int): Int {

        val tableRow = ingredients_table.getChildAt(rowNumber) as TableRow
        var counter = 0

        if ((tableRow.getChildAt(0) as EditText).text.toString().isEmpty()) counter++
        if ((tableRow.getChildAt(1) as EditText).text.toString().isEmpty()) counter++
        if ((tableRow.getChildAt(2) as EditText).text.toString().isEmpty()) counter++

        return counter
    }


    // Create a timer table row
    fun createTimerTableRow(timerLine: Triple<Int, String, Int>?, persistentTimer: Long?) {

        val tableRow = TableRowFactory.createTimerTableRow(this)
        tableRow.tag = -1

        // Find all the views in custom table row and cast to correct types
        val timerNameView = tableRow.getChildAt(0) as EditText
        val minutesView = tableRow.getChildAt(1) as EditText
        val timerClockView = tableRow.getChildAt(2) as Chronometer
        val timerButton = tableRow.getChildAt(3) as Button

        timerClockView.stop()
        timerClockView.tag = C.timerStopped
        timerClockView.setOnChronometerTickListener { timerListener(it) }

        // If timer line is not null, map data from timer line to table row
        if (timerLine != null) {

            tableRow.tag = timerLine.first
            timerNameView.setText(timerLine.second)
            minutesView.setText(timerLine.third.toString())

            if (persistentTimer != null) {

                minutesView.visibility = View.GONE
                timerClockView.base = persistentTimer
                timerClockView.visibility = View.VISIBLE
                timerClockView.start()
                timerClockView.tag = C.timerRunning
                timerButton.text = getString(R.string.timer_reset)
            }
        }

        // Register table row for the context menu
        registerForContextMenu(tableRow)

        // Set on key event listener for EditText views in table row
        for (view in tableRow.childrenSequence())
            if (view is EditText)
                view.setOnKeyListener { v, keyCode, event -> removeEmptyTimerRowWhenNeeded(); createEmptyTimerRowWhenNeeded() }

        // Set button event handler
        timerButton.onClick { startTimer(it) }

        timers_table.addView(tableRow)
    }


    // Validate all timer table rows
    fun validateTimerTableRow(timerTableRows: Sequence<View>): Boolean {

        for ((index, tableRow) in timerTableRows.withIndex()) {

            // Remove any leading zeros from minute field
            val minutes = (tableRow as TableRow).getChildAt(1) as EditText

            if (minutes.text.toString().isNotEmpty())
                minutes.setText(minutes.text.toString().toInt().toString())

            // If any timer text fields (except the last row) are empty, display error message and abort save
            val emptyFields = countEmptyFieldsInTimerRow(index)

            if (emptyFields > 0) {

                if (index == timers_table.childCount - 1 && emptyFields == 2)
                    continue

                longToast(getString(R.string.timer_error))
                return false
            }
        }

        return true
    }


    // When all fields in last timer row are filled out, create new empty row
    fun createEmptyTimerRowWhenNeeded(): Boolean {

        if (editModeEnabled && countEmptyFieldsInTimerRow(timers_table.childCount - 1) == 0) {

            createTimerTableRow(null, null)

            // Disable timer button in new row
            val newTimerTableRow = timers_table.getChildAt(timers_table.childCount - 1) as TableRow
            (newTimerTableRow.getChildAt(3) as Button).isEnabled = false

            return true
        }

        return false
    }


    // Find and delete empty timer row, except the last row
    fun removeEmptyTimerRowWhenNeeded() {

        val tableRows = timers_table.childrenSequence()
        var indexOfTableRowToRemove: Int? = null

        for ((index, tableRow) in tableRows.withIndex())
            if (countEmptyFieldsInTimerRow(index) == 2 && index != tableRows.count() - 1) {

                indexOfTableRowToRemove = index
                break
            }

        if (indexOfTableRowToRemove != null) {

            timers_table.removeViewAt(indexOfTableRowToRemove)
            hideKeyboard()
        }
    }


    fun countEmptyFieldsInTimerRow(rowNumber: Int): Int {

        val tableRow = timers_table.getChildAt(rowNumber) as TableRow
        var counter = 0

        if ((tableRow.getChildAt(0) as EditText).text.toString().isEmpty()) counter++

        val minutes = (tableRow.getChildAt(1) as EditText).text.toString()

        if (minutes.isEmpty() || minutes.toInt() == 0) counter++

        return counter
    }


    // Event handler for all timer buttons
    fun startTimer(view: View?) {

        // Use selected Button view to find other views in the table row
        val tableRow = view?.parent as TableRow

        toggleTimerViews(tableRow)
    }


    // Toggle between timer value EditText view and timer clock Chronometer view
    fun toggleTimerViews(timerTableRow: TableRow) {

        val minutes = timerTableRow.getChildAt(1) as EditText
        val timerClock = timerTableRow.getChildAt(2) as Chronometer
        val timerButton = timerTableRow.getChildAt(3) as Button

        if (minutes.visibility == View.VISIBLE) {

            minutes.visibility = View.GONE
            timerClock.base = minutesToMillisPlusSystemTime(minutes.text.toString().toInt())
            timerClock.visibility = View.VISIBLE
            timerClock.start()
            timerClock.tag = C.timerRunning
            timerButton.text = getString(R.string.timer_reset)
        }
        else {

            minutes.visibility = View.VISIBLE
            timerClock.visibility = View.GONE
            timerClock.stop()
            timerClock.tag = C.timerStopped
            timerButton.text = getString(R.string.timer_start)
        }
    }


    // Event listener for all timer clocks
    fun timerListener(view: View?) {

        val tableRow = view?.parent as TableRow
        val timerName = tableRow.getChildAt(0) as EditText
        val timerClock = tableRow.getChildAt(2) as Chronometer

        // Check if timer has expired
        if (timerClock.base < SystemClock.elapsedRealtime()) {

            // Stop timer and set to zero (to prevent negative numbers)
            timerClock.stop()
            timerClock.tag = C.timerStopped
            timerClock.base = SystemClock.elapsedRealtime()

            // Alert message to the user
            val name = timerName.text.toString()

            val msg =
                if(name.isEmpty()) getString(R.string.timer_nameless_expired)
                else name + getString(R.string.timer_expired)

            // Setup timer alert message and OK button
            val alertDialog = alert(msg, getString(R.string.timer_expired_headline)) {

                positiveButton(getString(R.string.ok)) { alarmSound?.stop() }
            }.show()

            // Set text color and text size for alert dialog
            alertDialog.dialog?.setCanceledOnTouchOutside(false)
            alertDialog.dialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.textColor = getColor(R.color.textColorListCellPreparation)
            alertDialog.dialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.textSize = 22f
            (alertDialog.dialog?.findViewById(android.R.id.message) as TextView).textSize = 20f

            // Play custom alarm sound
            alarmSound?.play()

            // Vibrate the phone, if possible
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            // Vibrate to alert the user of expired timer
            if (vibrator.hasVibrator())
                vibrator.vibrate(1000)
        }
    }


    fun minutesToMillisPlusSystemTime(minutes: Int) = SystemClock.elapsedRealtime() + minutes * 60000


    // Event handler for SMS button
    fun sendSms() {

        // Get entered phone number as string and remove all spaces
        val phoneNumber = phone_number.text.toString().replace(" ", "")

        // Check for empty phone number
        if (phoneNumber.isEmpty()) {

            longToast(getString(R.string.phone_number_missing_error))
            return
        }

        // Use regex to check for optional plus sign and min and max number of digits
        val regex = Regex("""^\+?\d{8,16}$""")

        if (!regex.matches(phoneNumber)) {

            longToast(getString(R.string.phone_number_invalid_error))
            return
        }

        phone_number.clearFocus()

        // Check for empty ingredient list in recipe
        if (ingredientsInRecipe.isEmpty()) {

            longToast(getString(R.string.sms_ingredient_list_error))
            return
        }

        // Send recipe ingredient list to recipient phone number as SMS
        sendSMS(phoneNumber, convertIngredientsToSmsString())
    }


    // Convert recipe ingredient list to a single string (for SMS)
    fun convertIngredientsToSmsString(): String {

        // Merge ingredients with same name and unit into a new list
        val tmpIngredientsList = mutableListOf<Triple<String, Double, String>>()

        for (ingredientLine in ingredientsInRecipe) {

            var foundIndex = -1

            // Check for ingredient with same name and unit i new list
            for ((index, tmpIngredientLine) in tmpIngredientsList.withIndex()) {

                if (tmpIngredientLine.first == ingredientLine.first
                        && tmpIngredientLine.third == ingredientLine.third) {

                    foundIndex = index
                    break
                }
            }

            // If ingredient was unique, add it to new list
            if (foundIndex == -1) {

                var (name, amount, unit) = ingredientLine
                amount = calculateAmount(no_of_persons.text.toString().toInt(), ingredientLine.second).toDouble()
                tmpIngredientsList.add(Triple(name, amount, unit))
            }
            // Otherwise, update relevant amount in new list
            else {

                var (name, amount, unit) = tmpIngredientsList[foundIndex]
                amount += calculateAmount(no_of_persons.text.toString().toInt(), ingredientLine.second).toDouble()
                tmpIngredientsList[foundIndex] = Triple(name, amount, unit)
            }
        }


        // Get recipe name and ingredients headlines
        var sms = "*** ${recipe_name.text} ***\n${getString(R.string.ingredients)}:\n"

        // Get name, amount and unit of each ingredient line
        for (tmpIngredientLine in tmpIngredientsList)
            sms += "  ${tmpIngredientLine.first} ${formatAmount(tmpIngredientLine.second)} ${tmpIngredientLine.third}\n"

        // Recipe name followed by 'end' marks the end of SMS string
        sms += "*** ${recipe_name.text} ${getString(R.string.sms_end_of_ingredients)} ***\n"

        return sms
    }


    // Toggle between edit mode and read-only mode
    fun toggleEditMode() {

        if(!editModeEnabled) enterEditMode()
        else exitEditModeAndSave()
    }


    // Enable recipe editing
    fun enterEditMode() {

        // Display default ingredient amounts and number of people
        updatePersonTextAndIngredientAmounts(true)

        editModeEnabled = true
        button_edit_save.text = getString(R.string.save)
        recipe_detail.backgroundColor = resources.getColor(R.color.backgroundEditMode, null)
        recipe_name.background = getDrawable(R.drawable.edit_text_edit_mode_background)
        no_of_persons.clearFocus()

        // Create empty table row at bottom of ingredient table for new ingredient
        createIngredientTableRow(null)

        // Show timer headline
        timer_headline.visibility = View.VISIBLE

        // When timer clocks are visible and stopped, toggle to minutes edit text views
        for (tableRow in timers_table.childrenSequence()) {

            val timerClock = (tableRow as TableRow).getChildAt(2) as Chronometer

            if (timerClock.visibility == View.VISIBLE && timerClock.tag.toString() == C.timerStopped)
                toggleTimerViews(tableRow)
        }

        // Create empty table row at bottom of timer table for new timer
        createTimerTableRow(null, null)

        sms_headline.visibility = View.GONE
        phone_number.visibility = View.GONE
        button_send_sms.visibility = View.GONE

        setupSpinner()
        makeViewsEditable()
    }


    // Exit edit mode, dismiss changes and return to previous activity
    fun cancelEditRecipe() {

        // When not a new recipe, restart current activity
        if (id != -1) {

            // Create a list of timers running
            val recipeIdAndTimerData = mutableListOf<Long>(id.toLong())

            // Add running timer's id and remaining time to list of timers running
            for (tableRow in timers_table.childrenSequence()) {

                val timerClock = (tableRow as TableRow).getChildAt(2) as Chronometer

                if (timerClock.tag.toString() == C.timerRunning) {

                    recipeIdAndTimerData.add(tableRow.tag.toString().toLong())
                    recipeIdAndTimerData.add(timerClock.base)
                }
            }

            // Setup bundle with array of longs containing recipe id, timer ids and remaining millis
            val bundle = Bundle()
            bundle.putLongArray(C.recipeDataArray, recipeIdAndTimerData.toLongArray())

            startActivity(intentFor<RecipeActivity>(C.recipeDataBundle to bundle))
        }

        // End Recipe Activity
        finish()
    }


    // Disable recipe editing and save the changes
    fun exitEditModeAndSave() {

        // Save recipe if no ingredient line has empty fields
        if (!saveRecipe())
            return

        editModeEnabled = false
        button_edit_save.text = getString(R.string.edit)
        recipe_detail.backgroundColor = resources.getColor(R.color.backgroundWhite, null)
        recipe_name.background = getDrawable(R.drawable.edit_text_default_background)
        no_of_persons.clearFocus()

        sms_headline.visibility = View.VISIBLE
        phone_number.visibility = View.VISIBLE
        button_send_sms.visibility = View.VISIBLE

        setupInfoLine()
        makeViewsUneditable()
        hideKeyboard()
    }


    // Enable editing of recipe fields
    fun makeViewsEditable() {

        button_cancel.visibility = View.VISIBLE
        makeViewEditable(recipe_name)
        makeViewEditable(recipe_preparation)
        spinner.isEnabled = true

        // Find all table rows in ingredient table layout
        var tableRows = ingredients_table.childrenSequence()

        for(tableRow in tableRows) {

            makeViewEditable((tableRow as TableRow).getChildAt(0) as EditText)
            makeViewEditable(tableRow.getChildAt(1) as EditText)
            makeViewEditable(tableRow.getChildAt(2) as EditText)
        }

        // Find all table rows in timer table layout
        tableRows = timers_table.childrenSequence()

        for(tableRow in tableRows) {

            makeViewEditable((tableRow as TableRow).getChildAt(0) as EditText)
            makeViewEditable(tableRow.getChildAt(1) as EditText)
            (tableRow.getChildAt(3) as Button).isEnabled = false
        }
    }


    // Disable editing of recipe fields
    fun makeViewsUneditable() {

        button_cancel.visibility = View.GONE
        makeViewUneditable(recipe_name)
        makeViewUneditable(recipe_preparation)
        spinner.isEnabled = false

        // Find all table rows in ingredient table layout
        var tableRows = ingredients_table.childrenSequence()

        for(tableRow in tableRows) {

            makeViewUneditable((tableRow as TableRow).getChildAt(0) as EditText)
            makeViewUneditable(tableRow.getChildAt(1) as EditText)
            makeViewUneditable(tableRow.getChildAt(2) as EditText)
        }

        // Find all table rows in timer table layout
        tableRows = timers_table.childrenSequence()

        for(tableRow in tableRows) {

            makeViewUneditable((tableRow as TableRow).getChildAt(0) as EditText)
            makeViewUneditable(tableRow.getChildAt(1) as EditText)
            (tableRow.getChildAt(3) as Button).isEnabled = true
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

        // If recipe name is empty, display error message and abort save
        if (recipe_name.text.toString().isEmpty()) {

            longToast(getString(R.string.recipe_name_error).toString())
            return false
        }

        // If number of people is out of bounds, display error message and abort save
        val noOfPeopleText = no_of_persons.text.toString()
        val noOfPeopleInt = noOfPeopleText.toInt()

        if (noOfPeopleText.isEmpty() || noOfPeopleInt == 0 || noOfPeopleInt > maxNoOfPeople) {

            longToast(getString(R.string.no_of_people_error))
            return false
        }

        noOfPeople = noOfPeopleInt

        if (id == -1)
            id = CRUD.createEmptyRecipe()


        // Find all table rows in ingredients table layout
        var ingredientTableRows = ingredients_table.childrenSequence()

        // If ingredient validation fails, abort save
        if (!validateIngredientTableRow(ingredientTableRows))
            return false

        // Find all table rows in timer table layout
        var timerTableRows = timers_table.childrenSequence()

        // If timer validation fails, abort save
        if (!validateTimerTableRow(timerTableRows))
            return false


        // Remove last ingredient line if all fields are empty
        if (countEmptyFieldsInIngredientRow(ingredients_table.childCount - 1) == 3) {

            ingredients_table.removeViewAt(ingredients_table.childCount - 1)
            ingredientTableRows = ingredients_table.childrenSequence()
        }

        val tmpIngredientsInRecipe = mutableListOf<Triple<Int, Double, Int>>()

        // Get ingredients in the table
        for (tableRow in ingredientTableRows) {

            // Get values from EditText views in tableRow
            val ingredientName = ((tableRow as TableRow).getChildAt(0) as EditText).text.toString()
            val amount = (tableRow.getChildAt(1) as EditText).text.toString().replace(",", ".").toDouble()
            val unitName = (tableRow.getChildAt(2) as EditText).text.toString()

            // Update format for ingredient amount
            (tableRow.getChildAt(1) as EditText).setText(formatAmount(amount))

            // Create potentially new ingredient and unit in DB
            CRUD.createIngredient(ingredientName)
            CRUD.createUnit(unitName)

            // Get ingredientId and unitId from DB
            val ingredientId = CRUD.getIngredientId(ingredientName)
            val unitId = CRUD.getUnitId(unitName)

            // Add ingredient to list of ingredients in recipe
            tmpIngredientsInRecipe.add(Triple(ingredientId, amount, unitId))
        }


        // Remove last timer line if all fields are empty
        if (countEmptyFieldsInTimerRow(timers_table.childCount - 1) == 2) {

            timers_table.removeViewAt(timers_table.childCount - 1)
            timerTableRows = timers_table.childrenSequence()
        }

        val tmpTimersInRecipe = mutableListOf<Pair<String, Int>>()

        // Get timers in the table
        for (tableRow in timerTableRows) {

            // Get values from EditText views in tableRow
            val timerName = ((tableRow as TableRow).getChildAt(0) as EditText).text.toString()
            val minutes = (tableRow.getChildAt(1) as EditText).text.toString().toInt()

            // Add timer to list of timers in recipe
            tmpTimersInRecipe.add(Pair(timerName, minutes))
        }

        // Update recipe name, category, no of people and preparation in DB
        CRUD.updateRecipeName(id, recipe_name.text.toString())
        CRUD.updateRecipeCategory(id, spinner.selectedItemPosition + 1)
        CRUD.updateNoOfPeople(id, noOfPeople)
        CRUD.updatePreparation(id, recipe_preparation.text.toString())

        // Delete all previous ingredients in recipe, afterwards add all ingredients from UI to DB
        CRUD.deleteIngredientsInRecipe(id)
        CRUD.createIngredientsInRecipe(id, tmpIngredientsInRecipe)

        ingredientsInRecipe = CRUD.getIngredientsInRecipe(id)

        // Delete all previous timers in recipe, afterwards add all timers from UI to DB
        CRUD.deleteTimersInRecipe(id)
        CRUD.createTimersInRecipe(id, tmpTimersInRecipe)

        // If no timers left, then remove timer headline
        timer_headline.visibility =
                if (tmpTimersInRecipe.isEmpty()) View.INVISIBLE
                else View.VISIBLE

        updateRecipeList = true

        return true
    }


    // Update person_text and ingredient amount
    fun updatePersonTextAndIngredientAmounts(reset: Boolean) {

        // Find the updated number of people (string) from user input
        val newNoOfPeopleString = no_of_persons.text.toString()

        // If reset true, reset no of people from DB. If empty or 0, set to default, else set to user input
        val newNoOfPeopleInt =  if (reset) noOfPeople
                                else if (newNoOfPeopleString.isEmpty() || newNoOfPeopleString.toInt() < 1) defaultNoOfPeople
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
            amountView.setText(calculateAmount(newNoOfPeopleInt, ingredientsInRecipe[index++].second))
        }
    }


    fun findRecipeCategory(): Int {

        if (id != -1)
            for (i in categories.indices)
                if (CRUD.getRecipeCategory(id) == categories[i]) return i

        return 1 // Default main course - used for new recipes
    }


    // Calculate ingredient amount based on number of people entered by user
    fun calculateAmount(newNoOfPeople: Int, amount: Double) =
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
}
