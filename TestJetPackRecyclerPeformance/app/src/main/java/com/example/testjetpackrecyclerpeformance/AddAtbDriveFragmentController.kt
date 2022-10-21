

@FragmentScope
class AddAtbDriveFragmentController
@Inject constructor(
        callbacks: FragmentCallbacks,
        toolbarController: ToolbarController,
        private val model: AddAtbDriveFragmentModel,
        private val fragmentView: AddAtbDriveFragmentView,
        private val fragment: AddAtbDriveFragment
) : BaseFragmentController(callbacks, toolbarController), FragmentDestroyView {

    private val viewListener = object : AddAtbDriveFragmentView.OnDataChanged {
        override fun finishAddressClicked() = model.editFinishAddress()

        override fun carPolicyChanged(policy: String) = model.setLicensePlate(policy)

        override fun carPolicyChoose(policy: String) = model.chooseLicensePlate(policy)

        override fun onStartTimeClicked() = model.editStartTime()

        override fun onFinishTimeClicked() = model.editFinishTime()

        override fun finishAddressPreselected(position: Int) {
            model.targetAddressSelect = position
        }

        override fun startAddressPreselected(position: Int) {
            model.startAddressSelect = position
        }

        override fun onWithPassengerSwitched(checked: Boolean) {
            model.withPassenger = checked
        }

        override fun distanceChanged(it: String) = model.setDistance(it)
        override fun kmEndChanged(it: String) = model.setKmFinish(it)
        override fun kmStartChanged(it: String) = model.setKmStart(it)

        override fun onPreCountRequired() = model.recountDistance()

        override fun onTypeSelected(value: String) {
            model.vehicleType = value
        }

        override fun startAddressClicked() = model.editStartAddress()

        override fun saveButtonClicked() {
            model.save()
        }
    }

    private val modelListener = object : AddAtbDriveFragmentModel.OnDataChanged {
        override fun onCarLicensesChanged() = setCarLicenses()

        override fun onDriveChanged() {
            validateDrive()
            setDrive()
        }
    }

    private fun validateDrive() {
        if (model.isEditLocked) {
            fragmentView.disableAll()
            fragmentView.hideError()
            return
        }
        fragmentView.enableAllFieldsByType(model.vehicleType)
        model.completed = resolveComplete()

        val isTimestampsFilled = model.start > 0 && model.finish > 0
        val isStartFinishTimeValid = model.start < model.finish
        fragmentView.toggleSaveButton(isStartFinishTimeValid && isTimestampsFilled && kmValid())
        when {
            isTimestampsFilled.not() -> fragmentView.showError(R.string.add_atb_drive_no_timestamps_error_message)
            isStartFinishTimeValid.not() -> fragmentView.showError(R.string.add_atb_drive_finish_error_message)
            else -> fragmentView.hideError()
        }
    }

    private fun resolveComplete(): Boolean {
        val isAddressValid = addressValid(model.startAddress) && addressValid(model.targetAddress)
        if (model.withPassenger) {
            return isAddressValid
        }
        return isAddressValid && when (model.vehicleType) {
            PRIVATE_CAR -> model.deltaKm >= 0
            OTHER -> true
            else -> false
        }
    }

    private fun kmValid(): Boolean {
        val errors = model.kmErrors()
        fragmentView.toggleStartKmError(errors.contains(KmError.NEGATIVE_START))
        fragmentView.toggleFinishKmError(errors.contains(KmError.FINISH_LESS_START))
        fragmentView.toggleDistanceKmError(errors.contains(KmError.UNKONSISTENT_DATA))
        return errors.isEmpty()
    }

    private fun addressValid(address: Address) = address.city.isNotBlank()

    private fun setCarLicenses() = fragmentView.setCarLicenses(model.carLicenses,
            model.licensePlate)

    override fun getTitle() = R.string.add_atb_drive_fargment_title

    override fun onFragmentCreateView(view: View) {
        super.onFragmentCreateView(view)
        if (model.entryId.isBlank()) {
            model.setDriveById(getId())
        }
        validateDrive()
        setCarLicenses()
        setDrive()
        trySetInitialVehicleType()
        fragmentView.listener = viewListener
        model.addListener(modelListener)
    }

    private fun setDrive() {
        fragmentView.setStartPreselect(model.startAddressSelect)
        fragmentView.setFinishPreselect(model.targetAddressSelect)
        fragmentView.setStartTime(model.start)
        fragmentView.setFinishTime(model.finish)
        fragmentView.setStartAddress(model.startAddress)
        fragmentView.setFinishAddress(model.targetAddress)
        fragmentView.setLicensePlate(model.licensePlate)
        fragmentView.setWithPassenger(model.withPassenger)
        fragmentView.setKmStart(model.startKm)
        fragmentView.setKmEnd(model.finishKm)
        fragmentView.setDistance(model.deltaKm)
        fragmentView.setType(model.vehicleType)
        fragmentView.enableListeners()
    }

    override fun onFragmentDestroyView() {
        fragmentView.listener = null
        model.removeListener(modelListener)
    }

    private fun getId(): String {
        val args = fragment.arguments

        if (args == null) {
            Assert.fail("Arguments is null!")
            model.goBack()
            return EMPTY_STRING
        }
        val arguments = AddAtbDriveFragmentArgs.fromBundle(args)
        return arguments.id
    }

    private fun trySetInitialVehicleType() {
        if (model.vehicleType.isEmpty()) {
            model.vehicleType = fragmentView.initialVehicleType
        }
    }
}
