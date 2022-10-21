

@FragmentScope
class AddAtbDriveFragmentView
@Inject
constructor(
    callbacks: FragmentCallbacks,
    activity: AppCompatActivity,
    private val formatter: FormatterWrapper,
    private val keyboardController: KeyboardController
) : FragmentCreateView {

    var listener: OnDataChanged? = null

    private val adressTypes = activity.resources.getStringArray(R.array.add_atb_drive_address_types)
    private val driveTypes = activity.resources.getStringArray(R.array.add_atb_drive_vehicle_types)
    val initialVehicleType get() = driveTypes.first()

    private val emptyAddressValue = activity.getString(R.string.atb_drive_empty_address)
    private val kmStartErrorValue = activity.getString(R.string.atb_drive_negative_start_km)
    private val kmFinishErrorValue = activity.getString(R.string.atb_drive_start_equals_km)
    private val kmDistanceErrorValue = activity.getString(R.string.atb_drive_wrong_kms)

    private lateinit var startAddressSpinner: SpinnerLabeled<String>
    private lateinit var finishAddressSpinner: SpinnerLabeled<String>
    private lateinit var startTimeText: TextLabeled
    private lateinit var finishTimeText: TextLabeled
    private lateinit var licensePlateSpinner: SpinnerLabeled<String>
    private lateinit var typeSpinner: SpinnerLabeled<String>
    private lateinit var startAddressText: TextLabeledOutline
    private lateinit var finishAddressText: TextLabeledOutline
    private lateinit var kmStartText: EditTextLabeled
    private lateinit var kmEndText: EditTextLabeled
    private lateinit var distanceText: EditTextLabeled
    private lateinit var withPassenger: SwitchMaterial
    private lateinit var withPassengerLabel: TextView
    private lateinit var saveButton: Button
    private lateinit var errorView: ErrorView
    private lateinit var rootView: View

    private val distanceWatcher = MadTextWatcher {
        val newText = wrapKm(it)
        if (newText != it) {
            distanceText.text = newText
        }
        listener?.distanceChanged(newText)
    }

    private val kmEndeWatcher = MadTextWatcher {
        val newText = wrapKm(it)
        if (newText != it) {
            kmEndText.text = newText
        }
        listener?.kmEndChanged(newText)
    }

    private val kmStartWatcher = MadTextWatcher {
        val newText = wrapKm(it)
        if (newText != it) {
            kmStartText.text = newText
        }
        listener?.kmStartChanged(newText)
    }

    private val onFocusListener = View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus) listener?.onPreCountRequired()
    }

    private val onKeyDownListener = { actionId: Int, event: KeyEvent? ->
        if (actionId == EditorInfo.IME_ACTION_DONE
            || actionId == EditorInfo.IME_ACTION_NEXT
        ) {
            resolveFocus()
            true
        } else {
            false
        }
    }

    private val licensePlateListener = object : SpinnerLabeled.SelectListener<String> {
        override fun onItemSelected(item: String, index: Int) {
            if (item.isEmpty()) {
                keyboardController.showKeyboard(licensePlateSpinner.getEditTextView())
            } else {
                keyboardController.hideKeyboard()
            }
            listener?.carPolicyChoose(item)
        }

        override fun onTextChanged(text: String) {
            listener?.carPolicyChanged(text)
        }
    }

    private val withPassengerListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            listener?.onWithPassengerSwitched(isChecked)
        }
    }

    private var isListenersEnabled = false

    init {
        callbacks.register(this)
    }

    override fun onFragmentCreateView(view: View) {
        startAddressSpinner = view.findViewById(R.id.add_atb_drive_start_address_spinner)
        finishAddressSpinner = view.findViewById(R.id.add_atb_drive_finish_address_spinner)
        startTimeText = view.findViewById(R.id.add_atb_drive_start_time)
        finishTimeText = view.findViewById(R.id.add_atb_drive_finish_time)
        licensePlateSpinner = view.findViewById(R.id.add_atb_drive_license_plate_spinner)
        typeSpinner = view.findViewById(R.id.add_atb_drive_type_autocomplete)
        startAddressText = view.findViewById(R.id.add_atb_drive_start_address_text)
        finishAddressText = view.findViewById(R.id.add_atb_drive_finish_address_text)
        kmStartText = view.findViewById(R.id.add_atb_drive_km_start)
        kmEndText = view.findViewById(R.id.add_atb_drive_km_end)
        distanceText = view.findViewById(R.id.add_atb_drive_distance)
        withPassenger = view.findViewById(R.id.add_atb_drive_with_passenger_switch)
        withPassengerLabel = view.findViewById(R.id.add_atb_drive_with_passenger_label)
        saveButton = view.findViewById(R.id.add_atb_drive_save_button)
        errorView = view.findViewById(R.id.add_atb_drive_error_view)
        rootView = view.findViewById(R.id.add_atb_drive_root_view)

        startAddressSpinner.setItems(adressTypes.toList()) { item -> item }
        finishAddressSpinner.setItems(adressTypes.toList()) { item -> item }
        typeSpinner.setItems(driveTypes.toList()) { item -> item }

        startAddressSpinner.setSelectionIndex(0)
        finishAddressSpinner.setSelectionIndex(0)
        typeSpinner.setSelectionIndex(0)
        saveButton.setOnClickListener { listener?.saveButtonClicked() }

        licensePlateSpinner.setImeOptions(EditorInfo.IME_ACTION_DONE)
    }

    private fun wrapKm(value: String): String {
        if (value.length > 1) {
            return value.removePrefix("0")
        }
        return value
    }

    fun enableListeners() {
        if (isListenersEnabled) {
            return
        }
        isListenersEnabled = true
        startAddressSpinner.onSelected { _, index -> listener?.startAddressPreselected(index) }
        finishAddressSpinner.onSelected { _, index -> listener?.finishAddressPreselected(index) }
        startAddressText.setOnClickListener { listener?.startAddressClicked() }
        finishAddressText.setOnClickListener { listener?.finishAddressClicked() }
        startTimeText.setOnClickListener { listener?.onStartTimeClicked() }
        finishTimeText.setOnClickListener { listener?.onFinishTimeClicked() }
        typeSpinner.onSelected { value, _ -> listener?.onTypeSelected(value) }
        kmStartText.apply {
            addTextChangedListener(kmStartWatcher)
            isFocusable = true
            onFocusChangeListener = onFocusListener
            setKeyCodeListener(onKeyDownListener)
        }

        kmEndText.apply {
            addTextChangedListener(kmEndeWatcher)
            isFocusable = true
            onFocusChangeListener = onFocusListener
            setKeyCodeListener(onKeyDownListener)
        }

        distanceText.apply {
            addTextChangedListener(distanceWatcher)
            isFocusable = true
            onFocusChangeListener = onFocusListener
            setKeyCodeListener(onKeyDownListener)
        }

        licensePlateSpinner.setOnEditorActionListener { actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                keyboardController.hideKeyboard()
                licensePlateSpinner.clearFocus()
                true
            } else {
                false
            }
        }

        licensePlateSpinner.listener = licensePlateListener
    }

    fun setCarLicenses(licenses: List<String>, current: String?) {
        val extendedLicenses = (listOf(EMPTY_STRING) + licenses).toTypedArray()
        licensePlateSpinner.setItems(listOf(EMPTY_STRING) + licenses) { item -> item }
        licensePlateSpinner.listener = null
        var index = extendedLicenses.indexOf(current ?: EMPTY_STRING)
        if (index < 0) {
            index = 0
        }
        licensePlateSpinner.setSelectionIndex(index)
        licensePlateSpinner.listener = licensePlateListener
    }

    fun setStartPreselect(position: Int) {
        startAddressSpinner.setSelectionIndex(position)
    }

    fun setFinishPreselect(position: Int) {
        finishAddressSpinner.setSelectionIndex(position)
    }

    fun hideError() = errorView.hideError()

    fun showError(resId: Int) = errorView.showError(resId)

    private fun resolveFocus() {
        when {
            kmStartText.isFocused -> kmEndText.requestFocus()
            kmEndText.isFocused -> distanceText.requestFocus()
            distanceText.isFocused -> {
                keyboardController.hideKeyboard()
                distanceText.clearFocus()
                listener?.onPreCountRequired()
            }
            else -> keyboardController.hideKeyboard()
        }
    }

    private fun transformAddress(address: Address): String {
        val text = listOf(
            address.street,
            address.homeNumber,
            address.postcode,
            address.city,
            address.district
        ).filter { it.isNotBlank() }
        if (text.isEmpty()) {
            return emptyAddressValue
        }
        return text.joinToString(", ")
    }

    private fun transformDate(date: Long): String {
        if (date < 0) {
            return ""
        }
        return formatter.format(date)
    }

    private fun transformKm(km: Int): String {
        if (km == Int.MIN_VALUE) {
            return ""
        }
        return km.toString()
    }

    fun toggleSaveButton(isEnabled: Boolean) {
        saveButton.isEnabled = isEnabled
    }

    fun disableAll() {
        toggleWithPassenger(false)
        toggleStypeFields(false)
        toggleOther(false)
    }

    fun enableAllFieldsByType(vehicleType: String) {
        toggleWithPassenger(true)
        toggleStypeFields(vehicleType != OTHER)
        toggleOther(true)
    }

    fun toggleStartKmError(isEnabled: Boolean) {
        kmStartText.error = if (isEnabled) kmStartErrorValue else null
    }

    fun toggleFinishKmError(isEnabled: Boolean) {
        kmEndText.error = if (isEnabled) kmFinishErrorValue else null
    }

    fun toggleDistanceKmError(isEnabled: Boolean) {
        distanceText.error = if (isEnabled) kmDistanceErrorValue else null
    }

    private fun toggleOther(isEnabled: Boolean) {
        distanceText.isEnabled = isEnabled
        typeSpinner.isEnabled = isEnabled
        startAddressSpinner.isEnabled = isEnabled
        startAddressText.isEnabled = isEnabled
        startTimeText.isEnabled = isEnabled
        finishAddressSpinner.isEnabled = isEnabled
        finishAddressText.isEnabled = isEnabled
        finishTimeText.isEnabled = isEnabled
        saveButton.isEnabled = isEnabled
    }

    private fun toggleWithPassenger(isEnabled: Boolean) {
        if (withPassenger.isEnabled == isEnabled) {
            return
        }
        withPassenger.isEnabled = isEnabled
        withPassengerLabel.isEnabled = isEnabled
    }

    private fun toggleStypeFields(isEnabled: Boolean) {
        licensePlateSpinner.isEnabled = isEnabled
        if (isEnabled.not()) licensePlateSpinner.text = ""
        kmStartText.isEnabled = isEnabled
        kmEndText.isEnabled = isEnabled
    }

    fun setStartAddress(value: Address) {
        startAddressText.text = transformAddress(value)
    }

    fun setFinishAddress(value: Address) {
        finishAddressText.text = transformAddress(value)
    }

    fun setLicensePlate(value: String) {
        licensePlateSpinner.listener = null
        licensePlateSpinner.text = value
        licensePlateSpinner.listener = licensePlateListener
    }

    fun setKmStart(value: Int) =
        kmStartText.setTextSilently(transformKm(value), kmStartWatcher)

    fun setKmEnd(value: Int) =
        kmEndText.setTextSilently(transformKm(value), kmEndeWatcher)

    fun setDistance(value: Int) =
        distanceText.setTextSilently(transformKm(value), distanceWatcher)

    fun setType(value: String) {
        var index = driveTypes.indexOf(value)
        if (index < 0) {
            index = 0
        }
        typeSpinner.setSelectionIndex(index)
    }

    fun setWithPassenger(isChecked: Boolean) {
        if (withPassenger.isChecked == isChecked) return
        rootView.clearFocus()
        withPassenger.setIsCheckedSilently(isChecked, withPassengerListener)
    }

    fun setStartTime(value: Long) {
        startTimeText.text = transformDate(value)
    }

    fun setFinishTime(value: Long) {
        finishTimeText.text = transformDate(value)
    }


    interface OnDataChanged {
        fun startAddressClicked()
        fun finishAddressClicked()
        fun carPolicyChanged(policy: String)
        fun carPolicyChoose(policy: String)
        fun onStartTimeClicked()
        fun onFinishTimeClicked()
        fun finishAddressPreselected(position: Int)
        fun startAddressPreselected(position: Int)
        fun onWithPassengerSwitched(checked: Boolean)
        fun distanceChanged(it: String)
        fun kmEndChanged(it: String)
        fun kmStartChanged(it: String)
        fun onPreCountRequired()
        fun onTypeSelected(value: String)
        fun saveButtonClicked()
    }
}

fun SpinnerLabeled<String>.onSelected(callback: (String, Int) -> Unit) {
    listener = object : SpinnerLabeled.SelectListener<String> {
        override fun onItemSelected(item: String, index: Int) = callback(item, index)
    }
}
