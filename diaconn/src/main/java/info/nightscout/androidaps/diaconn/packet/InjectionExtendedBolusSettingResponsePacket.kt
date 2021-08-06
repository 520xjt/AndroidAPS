package info.nightscout.androidaps.diaconn.packet

import dagger.android.HasAndroidInjector
import info.nightscout.androidaps.diaconn.DiaconnG8Pump
import info.nightscout.androidaps.logging.LTag
import javax.inject.Inject

/**
 * InjectionExtendedBolusSettingResponsePacket
 */
class InjectionExtendedBolusSettingResponsePacket(
    injector: HasAndroidInjector
) : DiaconnG8Packet(injector ) {

    @Inject lateinit var diaconnG8Pump: DiaconnG8Pump
    var result = 0
    init {
        msgType = 0x88.toByte()
        aapsLogger.debug(LTag.PUMPCOMM, "InjectionExtendedBolusSettingResponsePacket init ")
    }

    override fun handleMessage(data: ByteArray?) {
        val defectCheck = defect(data)
        if (defectCheck != 0) {
            aapsLogger.debug(LTag.PUMPCOMM, "InjectionExtendedBolusSettingResponsePacket Got some Error")
            failed = true
            return
        } else failed = false

        val bufferData = prefixDecode(data)
        result = getByteToInt(bufferData)

        if(!isSuccSettingResponseResult(result)) {
            diaconnG8Pump.resultErrorCode = result
            failed = true
            return
        }
        diaconnG8Pump.otpNumber =  getIntToInt(bufferData)
        aapsLogger.debug(LTag.PUMPCOMM, "Result --> $result")
        aapsLogger.debug(LTag.PUMPCOMM, "otpNumber --> ${diaconnG8Pump.otpNumber}")
    }

    override fun getFriendlyName(): String {
        return "PUMP_INJECTION_EXTENDED_BOLUS_SETTING_RESPONSE"
    }
}