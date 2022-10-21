
@Singleton
class AddAtbDriveFragmentModel
@Inject constructor(
        modelDestroyer: ModelDestroyer,
        context: Context,
        private val bidOn: BidOn,
        private val nafigator: Nafigator,
        private val atbDriveModel: AtbDriveModel,
        private val activeTaskModel: ActiveTaskModel,
        private val userModel: UserModel,
        private val userProfileModel: UserProfileModel,
        private val addressEditFragmentModel: AddressEditFragmentModel,
        private val dialogManager: DialogManager,
        private val operationStore: OperationStore
) : BaseModel<
        AddAtbDriveFragmentModel.DataHolder,
        AddAtbDriveFragmentModel.OnDataChanged>
(
        modelDestroyer,
        listOf(R.id.add_atb_drive_fragment)
) {

    val entryId get() = getHolder().entryId

    var startAddressSelect
        get() = getHolder().startAddressSelect
        set(value) {
            if (value == getHolder().startAddressSelect) return
            getHolder().startAddressSelect = value
            startAddress = addressPreselection[value]
        }

    var startAddress
        get() = getHolder().startAddress
        private set(value) {
            if (startAddress != value) {
                getHolder().startAddress = value
                notifyDriveChanged()
            }
        }

    var targetAddressSelect
        get() = getHolder().targetAddressSelect
        set(value) {
            if (value == getHolder().targetAddressSelect) return
            getHolder().targetAddressSelect = value
            targetAddress = addressPreselection[value]
        }

    var targetAddress
        get() = getHolder().targetAddress
        private set(value) {
            if (targetAddress != value) {
                getHolder().targetAddress = value
                notifyDriveChanged()
            }
        }

    var start
        get() = getHolder().start
        private set(value) {
            if (start != value) {
                getHolder().start = value
                notifyDriveChanged()
            }
        }

    var finish
        get() = getHolder().finish
        private set(value) {
            if (finish != value) {
                getHolder().finish = value
                notifyDriveChanged()
            }
        }

    var withPassenger
        get() = getHolder().withPassenger
        set(value) {
            if (withPassenger != value) {
                getHolder().withPassenger = value
                notifyDriveChanged()
            }
        }

    var startKm
        get() = getHolder().startKm
        private set(value) {
            if (startKm != value) {
                getHolder().startKm = value
                notifyDriveChanged()
            }
        }

    var finishKm
        get() = getHolder().finishKm
        private set(value) {
            if (finishKm != value) {
                getHolder().finishKm = value
                notifyDriveChanged()
            }
        }

    var deltaKm
        get() = getHolder().deltaKm
        private set(value) {
            if (deltaKm != value) {
                getHolder().deltaKm = value
                notifyDriveChanged()
            }
        }

    val licensePlate
        get() = getHolder().licensePlate

    var completed
        get() = getHolder().completed
        set(value) {
            if (completed != value) {
                getHolder().completed = value
                notifyDriveChanged()
            }
        }

    var vehicleType
        get() = getHolder().vehicleType
        set(value) {
            if (vehicleType != value) {
                if (getHolder().vehicleType == OTHER) {
                    getHolder().licensePlate =
                            userProfileModel.getUserProfile()?.licensePlate
                                    ?: EMPTY_STRING
                }
                getHolder().vehicleType = value
                if (value == OTHER) {
                    getHolder().licensePlate = EMPTY_STRING
                    getHolder().startKm = Int.MIN_VALUE
                    getHolder().finishKm = Int.MIN_VALUE
                }
                getHolder().withPassenger = false
                notifyDriveChanged()
            }
        }


    var isEditLocked
        get() = getHolder().editLock
        private set(value) {
            if (getHolder().editLock != value) {
                getHolder().editLock = value
                listeners.forEach { it.onDriveChanged() }
            }
        }
    private val addressPreselection get() = getHolder().addressPreselection

    var carLicenses
        get() = getHolder().carLicenses
        set(value) {
            if (getHolder().carLicenses != value) {
                getHolder().carLicenses = value
                listeners.forEach { it.onCarLicensesChanged() }
            }
        }

    private val deviceId: String
    private val atbDriveListener = AtbDriveModel.OnDataChanged {
        setDriveById(entryId)
        getHolder().carLicenses = getCarLicensesLicenses()
    }

    private val operationListener = object : OperationStore.OnDataChangeListener {
        override fun onDataChanged() {
            updateEditLock()
        }
    }

    private val startAddressEditListener = object : AddressEditFragmentModel.OnDataChanged {
        override fun onNewAddressSelected(address: Address) {
            addressEditFragmentModel.removeListener(this)
            startAddress = address
        }
    }

    private val targetAddressEditListener = object : AddressEditFragmentModel.OnDataChanged {
        override fun onNewAddressSelected(address: Address) {
            addressEditFragmentModel.removeListener(this)
            targetAddress = address
        }
    }

    init {
        deviceId = Settings.Secure.getString(context.contentResolver,
                Settings.Secure.ANDROID_ID).slice(0..5)
    }

    companion object {
        private const val EMPTY_ADDRESS = 0
        private const val TASK_ADDRESS = 2
        private const val LAST_ADDRESS = 3
    }

    fun goBack() = nafigator.goBack()

    fun save() {
        recountDistance()
        if (kmErrors().isNotEmpty()) return
        if (getHolder().inProgress) return
        getHolder().inProgress = true
        getConnectedOperation { list ->
            if (list.isNotEmpty()) {
                val oldOperation = list.first()
                createOperationWithOldOperation(oldOperation)
                return@getConnectedOperation
            }
            val oldDrive = atbDriveModel.getDriveById(getHolder().entryId)
            createOperationWithOldDrive(oldDrive)
        }
    }

    private fun createOperationWithOldDrive(drive: AtbDrive?) {
        val data = SaveAtbDrivesOperation.Data(Operation.State(drive, composeDrive()))
        bidOn.forward(SaveAtbDrivesOperation(data)) {
            userModel.getLocalUserData().lastAddress = targetAddress
            nafigator.goBack()
        }
    }

    private fun createOperationWithOldOperation(oldOperation: SaveAtbDrivesOperation) {
        val data = SaveAtbDrivesOperation.Data(Operation.State(
                oldOperation.request.drive.old,
                composeDrive()
        ))
        bidOn.forward(SaveAtbDrivesOperation(data, oldOperation.storeId)) {
            userModel.getLocalUserData().lastAddress = targetAddress
            nafigator.goBack()
        }
    }

    private fun composeDrive() = AtbDrive(
            getHolder().drive?.id ?: System.currentTimeMillis(),
            getHolder().entryId,
            getHolder().taskId,
            start,
            finish,
            vehicleType,
            licensePlate,
            if (startKm != Int.MIN_VALUE) startKm else null,
            if (finishKm != Int.MIN_VALUE) finishKm else null,
            deltaKm,
            Address(startAddress, startAddressSelect),
            Address(targetAddress, targetAddressSelect),
            withPassenger,
            getHolder().drive?.timeSent,
            getHolder().drive?.timeChanged ?: System.currentTimeMillis(),
            getHolder().drive?.delete ?: false,
            completed
    )

    fun editStartAddress() {
        val bundle = Bundle()
        bundle.putSerializable("current", startAddress)
        nafigator.navigate(R.id.addressEditFragment, bundle)
        addressEditFragmentModel.addListener(startAddressEditListener)
    }

    fun editFinishAddress() {
        val bundle = Bundle()
        bundle.putSerializable("current", targetAddress)
        nafigator.navigate(R.id.addressEditFragment, bundle)
        addressEditFragmentModel.addListener(targetAddressEditListener)
    }

    fun editStartTime() = dialogManager.selectDateTime(
            if (start == -1L) System.currentTimeMillis() else start,
            null, null) {
        start = it
    }

    fun editFinishTime() = dialogManager.selectDateTime(
            if (finish == -1L) System.currentTimeMillis() else finish,
            null, null) {
        finish = it
    }

    fun setKmStart(km: String) {
        startKm = km.toIntOrNull() ?: Int.MIN_VALUE
    }

    fun setKmFinish(km: String) {
        finishKm = km.toIntOrNull() ?: Int.MIN_VALUE
    }

    fun setDistance(km: String) {
        deltaKm = km.toIntOrNull() ?: 0
    }

    private fun precalculateKmStart() {
        if (isKmStartEntered().not() && isKmEndEntered() && isDistanceEntered()) {
            val diff = finishKm - deltaKm
            startKm = if (diff >= 0) diff else 0
        }
    }

    private fun precalculateKmFinish() {
        if (isKmStartEntered() && isKmEndEntered().not() && isDistanceEntered()) {
            finishKm = startKm + deltaKm
        }
    }

    private fun precalculateDistance() {
        if (isKmStartEntered() && isKmEndEntered() && isDistanceEntered().not()) {
            val diff = finishKm - startKm
            deltaKm = if (diff >= 0) diff else 0
        }
    }

    private fun isKmStartEntered() = startKm != Int.MIN_VALUE
    private fun isKmEndEntered() = finishKm != Int.MIN_VALUE
    private fun isDistanceEntered() = deltaKm != 0

    fun setLicensePlate(plate: String) {
        if (licensePlate != plate) {
            getHolder().licensePlate = plate
        }
    }

    fun chooseLicensePlate(plate: String) {
        if (licensePlate != plate) {
            getHolder().licensePlate = plate
            notifyDriveChanged()
        }
    }

    fun setDriveById(newEntryId: String) {
        if (newEntryId.isBlank()) {
            setUpNewDrive()
            return
        }
        val existingDrive = atbDriveModel.getDriveById(newEntryId)
        if (newEntryId.isNotEmpty() && existingDrive != null) {
            setUpDrive(existingDrive)
        }
        getHolder().entryId = newEntryId
        updateEditLock()
        notifyDriveChanged()
    }

    private fun setUpDrive(drive: AtbDrive?) {
        if (drive == null) {
            setUpNewDrive()
            return
        }
        getHolder().drive = drive
        getHolder().taskId = drive.taskId
        getHolder().startAddress = Address(drive.startAddress)
        getHolder().targetAddress = Address(drive.targetAddress)
        getHolder().start = drive.start ?: -1
        getHolder().finish = drive.finish ?: -1
        getHolder().withPassenger = drive.withPassenger
        getHolder().startKm = drive.startKm ?: Int.MIN_VALUE
        getHolder().finishKm = drive.finishKm ?: Int.MIN_VALUE
        getHolder().deltaKm = drive.deltaKm
        getHolder().vehicleType = drive.vehicleType ?: EMPTY_STRING
        getHolder().licensePlate = drive.licensePlate ?: EMPTY_STRING
        getHolder().completed = drive.completed

        getHolder().startAddressSelect = drive.startAddress.selected
        getHolder().targetAddressSelect = drive.targetAddress.selected
        getHolder().completed = drive.completed
    }

    private fun updateEditLock() {
        if (!completed) {
            isEditLocked = false
            return
        }
        getConnectedOperation { operations ->
            isEditLocked = completed && operations.isEmpty()
        }
    }

    private fun getConnectedOperation(
            callback: (List<SaveAtbDrivesOperation>) -> Unit
    ) {
        if (getHolder().entryId.isEmpty()) {
            callback(emptyList())
            return
        }
        operationStore.getByType(
                Operation.OperationType.SAVE_ATB_DRIVE.name)
        { operations ->
            val list = operations.filterIsInstance(SaveAtbDrivesOperation::class.java)
                    .filter {
                        it.isUnreachable.not()
                                && it.request.drive.new.entryId == getHolder().entryId
                    }
            callback(list)
        }
    }

    fun recountDistance() {
        if (vehicleType == OTHER) return
        if (startKm >= 0 && finishKm > 0 && deltaKm > 0) return
        precalculateKmStart()
        precalculateKmFinish()
        precalculateDistance()
    }

    fun kmErrors(): List<KmError> {
        val errors = mutableListOf<KmError>()
        if (isKmStartEntered() && startKm < 0) {
            errors.add(KmError.NEGATIVE_START)
        }
        if (startKm >= 0 && finishKm >= 0 && startKm >= finishKm) {
            errors.add(KmError.FINISH_LESS_START)
        }
        if (deltaKm > 0
                && startKm != Int.MIN_VALUE
                && finishKm != Int.MIN_VALUE
                && startKm + deltaKm != finishKm
        ) {
            errors.add(KmError.UNKONSISTENT_DATA)
        }
        return errors.toList()
    }

    private fun notifyDriveChanged() = listeners.forEach { it.onDriveChanged() }

    private fun getCarLicensesLicenses() =
            userProfileModel.getUserProfile()?.histories?.mapNotNull {
                it.licensePlate
            } ?: emptyList()

    private fun setUpNewDrive() {
        getHolder().drive = null
        val finishAddress = Address(
                street = activeTaskModel.task.pdStreet ?: EMPTY_STRING,
                homeNumber = activeTaskModel.task.pdHomeNumber ?: EMPTY_STRING,
                postcode = activeTaskModel.task.pdPostcode ?: EMPTY_STRING,
                city = activeTaskModel.task.pdCity ?: EMPTY_STRING,
                district = activeTaskModel.task.pdDistrict ?: EMPTY_STRING
        )

        getHolder().entryId = "MAD_${System.currentTimeMillis()}_$deviceId"
        getHolder().taskId = activeTaskModel.task.taskId
        getHolder().startAddressSelect = LAST_ADDRESS
        getHolder().targetAddressSelect = TASK_ADDRESS
        startAddress = Address(userModel.getLocalUserData().lastAddress)
        targetAddress = finishAddress
        start = System.currentTimeMillis()
        finish = System.currentTimeMillis() + MINUTE
        withPassenger = false
        vehicleType = userProfileModel.getUserProfile()?.vehicleType
                ?: EMPTY_STRING
        getHolder().licensePlate =
            if(vehicleType == OTHER) EMPTY_STRING
            else userProfileModel.getUserProfile()?.licensePlate
                ?: EMPTY_STRING

    }

    override fun createHolder() = DataHolder()

    override fun onInitData() {
        atbDriveModel.addListener(atbDriveListener)
        operationStore.addListener(operationListener)
    }

    override fun onClearData() {
        atbDriveModel.removeListener(atbDriveListener)
        operationStore.removeListener(operationListener)
    }

    inner class DataHolder : ModelData {
        internal var drive: AtbDrive? = null

        var inProgress = false

        var entryId: String = EMPTY_STRING
        var taskId = activeTaskModel.task.taskId
        var carLicenses = getCarLicensesLicenses()
        var editLock = false

        var startAddressSelect = EMPTY_ADDRESS
        var targetAddressSelect = EMPTY_ADDRESS
        var startAddress = Address()
        var targetAddress = Address()
        var start = System.currentTimeMillis()
        var finish = System.currentTimeMillis() + MINUTE
        var withPassenger = false
        var startKm = Int.MIN_VALUE
        var finishKm = Int.MIN_VALUE
        var deltaKm = 0
        var vehicleType = EMPTY_STRING
        var licensePlate = carLicenses.firstOrNull() ?: EMPTY_STRING
        var completed = false

        val addressPreselection = listOf(
                Address(),
                Address(userProfileModel.getUserProfile()?.address),
                Address(
                        street = activeTaskModel.task.pdStreet ?: EMPTY_STRING,
                        homeNumber = activeTaskModel.task.pdHomeNumber ?: EMPTY_STRING,
                        postcode = activeTaskModel.task.pdPostcode ?: EMPTY_STRING,
                        city = activeTaskModel.task.pdCity ?: EMPTY_STRING,
                        district = activeTaskModel.task.pdDistrict ?: EMPTY_STRING
                ),
                Address(userModel.getLocalUserData().lastAddress),
                Address(userProfileModel.getUserProfile()?.teamAddress)
        )
    }

    interface OnDataChanged : ModelListener {
        fun onDriveChanged()
        fun onCarLicensesChanged()
    }
}

enum class KmError {
    NEGATIVE_START,
    FINISH_LESS_START,
    UNKONSISTENT_DATA
}
