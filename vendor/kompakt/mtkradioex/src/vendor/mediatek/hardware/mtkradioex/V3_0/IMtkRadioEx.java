package vendor.mediatek.hardware.mtkradioex.V3_0;

import android.hardware.radio.V1_0.CdmaSmsAck;
import android.hardware.radio.V1_0.Dial;
import android.hardware.radio.V1_0.ImsSmsMessage;
import android.hardware.radio.V1_0.LastCallFailCause;
import android.hardware.radio.V1_0.RadioCdmaSmsConst;
import android.hardware.radio.V1_0.SmsAcknowledgeFailCause;
import android.hardware.radio.V1_4.DataCallFailCause;
import android.hardware.radio.V1_5.EutranBands;
import android.hardware.radio.V1_5.NgranBands;
import android.hardware.radio.V1_5.UtranBands;
import android.hidl.base.V1_0.DebugInfo;
import android.hidl.base.V1_0.IBase;
import android.os.HidlSupport;
import android.os.HwBinder;
import android.os.HwBlob;
import android.os.HwParcel;
import android.os.IHwBinder;
import android.os.IHwInterface;
import android.os.NativeHandle;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public interface IMtkRadioEx extends IBase {
    public static final String kInterfaceName = "vendor.mediatek.hardware.mtkradioex@3.0::IMtkRadioEx";

    void abortCertificate(int i, int i2) throws RemoteException;

    void abortFemtocellList(int i) throws RemoteException;

    void acknowledgeLastIncomingCdmaSmsEx(int i, CdmaSmsAck cdmaSmsAck) throws RemoteException;

    void acknowledgeLastIncomingGsmSmsEx(int i, boolean z, int i2) throws RemoteException;

    void activateUiccCard(int i) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    IHwBinder asBinder();

    void cancelAvailableNetworks(int i) throws RemoteException;

    void cancelUssi(int i) throws RemoteException;

    void cfgA2offset(int i, int i2, int i3) throws RemoteException;

    void cfgB1offset(int i, int i2, int i3) throws RemoteException;

    void clearLteAvailableFile(int i) throws RemoteException;

    void conferenceDial(int i, ConferenceDial conferenceDial) throws RemoteException;

    void controlCall(int i, int i2, int i3) throws RemoteException;

    void controlImsConferenceCallMember(int i, int i2, int i3, String str, int i4) throws RemoteException;

    void dataConnectionAttach(int i, int i2) throws RemoteException;

    void dataConnectionDetach(int i, int i2) throws RemoteException;

    void deactivateNrScgCommunication(int i, boolean z, boolean z2) throws RemoteException;

    void deactivateUiccCard(int i) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    void deleteUPBEntry(int i, int i2, int i3, int i4) throws RemoteException;

    void dialWithSipUri(int i, String str) throws RemoteException;

    void disableAllCALinks(int i, int i2) throws RemoteException;

    void doGeneralSimAuthentication(int i, SimAuthStructure simAuthStructure) throws RemoteException;

    void eccRedialApprove(int i, int i2, int i3) throws RemoteException;

    void editUPBEntry(int i, ArrayList<String> arrayList) throws RemoteException;

    void enableCAPlusBandWidthFilter(int i, boolean z) throws RemoteException;

    void enableCapabaility(int i, String str, int i2, int i3) throws RemoteException;

    void enableDsdaIndication(int i, boolean z) throws RemoteException;

    void enableSCGfailure(int i, boolean z, int i2, int i3, int i4) throws RemoteException;

    void forceReleaseCall(int i, int i2) throws RemoteException;

    void get4x4MimoEnabled(int i) throws RemoteException;

    void getATR(int i) throws RemoteException;

    void getAllBandMode(int i) throws RemoteException;

    void getApcInfo(int i) throws RemoteException;

    void getAvailableNetworksWithAct(int i) throws RemoteException;

    void getBandMode(int i) throws RemoteException;

    void getBandPriorityList(int i) throws RemoteException;

    void getCALinkCapabilityList(int i, int i2) throws RemoteException;

    void getCALinkEnableStatus(int i, String str, int i2) throws RemoteException;

    void getCaBandMode(int i, int i2) throws RemoteException;

    void getCallSubAddress(int i) throws RemoteException;

    void getCampedFemtoCellInfo(int i) throws RemoteException;

    void getColp(int i) throws RemoteException;

    void getColr(int i) throws RemoteException;

    void getCurrentPOLList(int i) throws RemoteException;

    void getCurrentUiccCardProvisioningStatus(int i) throws RemoteException;

    void getDeactivateNrScgCommunication(int i) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    void getDisable2G(int i) throws RemoteException;

    void getDsdaStatus(int i) throws RemoteException;

    void getEccNum(int i) throws RemoteException;

    void getEngineeringModeInfo(int i, int i2) throws RemoteException;

    void getFemtocellList(int i) throws RemoteException;

    void getGsmBroadcastActivation(int i) throws RemoteException;

    void getGsmBroadcastLangs(int i) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    void getIWlanRegistrationState(int i) throws RemoteException;

    void getIccid(int i) throws RemoteException;

    void getImsCfgFeatureValue(int i, int i2, int i3) throws RemoteException;

    void getImsCfgProvisionValue(int i, int i2) throws RemoteException;

    void getImsCfgResourceCapValue(int i, int i2) throws RemoteException;

    void getLte1xRttCellList(int i, boolean z) throws RemoteException;

    void getLteBsrTimer(int i) throws RemoteException;

    void getLteData(int i) throws RemoteException;

    void getLteRRCState(int i) throws RemoteException;

    void getLteReleaseVersion(int i) throws RemoteException;

    void getLteScanDuration(int i) throws RemoteException;

    void getPOLCapability(int i) throws RemoteException;

    void getPhoneBookMemStorage(int i) throws RemoteException;

    void getPhoneBookStringsLength(int i) throws RemoteException;

    void getPlmnNameFromSE13Table(int i, int i2, int i3) throws RemoteException;

    void getProvisionValue(int i, String str) throws RemoteException;

    void getQamEnabled(int i, boolean z) throws RemoteException;

    void getRoamingEnable(int i, int i2) throws RemoteException;

    void getRxTestResult(int i, int i2) throws RemoteException;

    void getSignalStrengthWithWcdmaEcio(int i) throws RemoteException;

    void getSmartRatSwitch(int i, int i2) throws RemoteException;

    void getSmsMemStatus(int i) throws RemoteException;

    void getSmsParameters(int i) throws RemoteException;

    void getSmsRuimMemoryStatus(int i) throws RemoteException;

    void getSuggestedPlmnList(int i, int i2, int i3, int i4) throws RemoteException;

    void getTOEInfo(int i) throws RemoteException;

    void getTm9Enabled(int i, boolean z) throws RemoteException;

    void getVoiceDomainPreference(int i) throws RemoteException;

    void getWfcConfig(int i, int i2) throws RemoteException;

    void getXcapStatus(int i) throws RemoteException;

    void handleStkCallSetupRequestFromSimWithResCode(int i, int i2) throws RemoteException;

    void hangupAll(int i) throws RemoteException;

    void hangupWithReason(int i, int i2, int i3) throws RemoteException;

    void imsBearerStateConfirm(int i, int i2, int i3, int i4) throws RemoteException;

    void imsDeregNotification(int i, int i2) throws RemoteException;

    void imsEctCommand(int i, String str, int i2) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    void modifyModemType(int i, int i2, int i3) throws RemoteException;

    void notifyEPDGScreenState(int i, int i2) throws RemoteException;

    void notifyImsServiceReady() throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    void pullCall(int i, String str, boolean z) throws RemoteException;

    void queryCallForwardInTimeSlotStatus(int i, CallForwardInfoEx callForwardInfoEx) throws RemoteException;

    void queryFemtoCellSystemSelectionMode(int i) throws RemoteException;

    void queryNetworkLock(int i, int i2) throws RemoteException;

    void queryPhbStorageInfo(int i, int i2) throws RemoteException;

    void querySsacStatus(int i) throws RemoteException;

    void queryUPBAvailable(int i, int i2, int i3) throws RemoteException;

    void queryUPBCapability(int i) throws RemoteException;

    void queryVopsStatus(int i) throws RemoteException;

    void readPhbEntry(int i, int i2, int i3, int i4) throws RemoteException;

    void readPhoneBookEntryExt(int i, int i2, int i3) throws RemoteException;

    void readUPBAasList(int i, int i2, int i3) throws RemoteException;

    void readUPBAnrEntry(int i, int i2, int i3) throws RemoteException;

    void readUPBEmailEntry(int i, int i2, int i3) throws RemoteException;

    void readUPBGasList(int i, int i2, int i3) throws RemoteException;

    void readUPBGrpEntry(int i, int i2) throws RemoteException;

    void readUPBSneEntry(int i, int i2, int i3) throws RemoteException;

    void registerCellQltyReport(int i, String str, String str2, String str3, String str4) throws RemoteException;

    void removeCbMsg(int i, int i2, int i3) throws RemoteException;

    void resetAllConnections(int i) throws RemoteException;

    void resetMdDataRetryCount(int i, String str) throws RemoteException;

    void resetSuppServ(int i) throws RemoteException;

    void responseAcknowledgementMtk() throws RemoteException;

    void restartRILD(int i) throws RemoteException;

    void routeAuthMessage(int i, int i2, ArrayList<Byte> arrayList) throws RemoteException;

    void routeCertificate(int i, int i2, ArrayList<Byte> arrayList, ArrayList<Byte> arrayList2) throws RemoteException;

    void rttModifyRequestResponse(int i, int i2, int i3) throws RemoteException;

    void runGbaAuthentication(int i, String str, String str2, boolean z, int i2) throws RemoteException;

    void selectFemtocell(int i, String str, String str2, String str3) throws RemoteException;

    void sendAtciRequest(int i, ArrayList<Byte> arrayList) throws RemoteException;

    void sendCnap(int i, String str) throws RemoteException;

    void sendEmbmsAtCommand(int i, String str) throws RemoteException;

    void sendImsSmsEx(int i, ImsSmsMessage imsSmsMessage) throws RemoteException;

    void sendRequestRaw(int i, ArrayList<Byte> arrayList) throws RemoteException;

    void sendRequestStrings(int i, ArrayList<String> arrayList) throws RemoteException;

    void sendRsuRequest(int i, RsuRequestInfo rsuRequestInfo) throws RemoteException;

    void sendRttModifyRequest(int i, int i2, int i3) throws RemoteException;

    void sendRttText(int i, int i2, int i3, String str) throws RemoteException;

    void sendSarIndicator(int i, int i2, String str) throws RemoteException;

    void sendSubsidyLockRequest(int i, int i2, ArrayList<Byte> arrayList) throws RemoteException;

    void sendUssi(int i, String str) throws RemoteException;

    void sendVsimNotification(int i, int i2, int i3, int i4) throws RemoteException;

    void sendVsimOperation(int i, int i2, int i3, int i4, int i5, ArrayList<Byte> arrayList) throws RemoteException;

    void sendWifiAssociated(int i, String str, int i2, String str2, String str3, int i3) throws RemoteException;

    void sendWifiEnabled(int i, String str, int i2) throws RemoteException;

    void sendWifiIpAddress(int i, String str, String str2, String str3, int i2, int i3, String str4, String str5, int i4, String str6) throws RemoteException;

    void set4x4MimoEnabled(int i, int i2) throws RemoteException;

    void setApcMode(int i, int i2, int i3, int i4) throws RemoteException;

    void setBandPriorityList(int i, ArrayList<Integer> arrayList) throws RemoteException;

    void setBarringPasswordCheckedByNW(int i, String str, String str2, String str3, String str4) throws RemoteException;

    void setBgsrchDeltaSleepTimer(int i, int i2) throws RemoteException;

    void setCALinkEnableStatus(int i, boolean z, String str, int i2) throws RemoteException;

    void setCallAdditionalInfo(int i, ArrayList<String> arrayList) throws RemoteException;

    void setCallForwardInTimeSlot(int i, CallForwardInfoEx callForwardInfoEx) throws RemoteException;

    void setCallIndication(int i, int i2, int i3, int i4, int i5) throws RemoteException;

    void setCallSubAddress(int i, boolean z) throws RemoteException;

    void setCallValidTimer(int i, int i2) throws RemoteException;

    void setClip(int i, int i2) throws RemoteException;

    void setColp(int i, int i2) throws RemoteException;

    void setColr(int i, int i2) throws RemoteException;

    void setDisable2G(int i, boolean z) throws RemoteException;

    void setEccMode(int i, String str, int i2, int i3, int i4) throws RemoteException;

    void setEccNum(int i, String str, String str2) throws RemoteException;

    void setEmergencyAddressId(int i, String str) throws RemoteException;

    void setEtws(int i, int i2) throws RemoteException;

    void setFdMode(int i, int i2, int i3, int i4) throws RemoteException;

    void setFemtoCellSystemSelectionMode(int i, int i2) throws RemoteException;

    void setGsmBroadcastLangs(int i, String str) throws RemoteException;

    void setGwsdMode(int i, ArrayList<String> arrayList) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    void setIgnoreSameNumberInterval(int i, int i2) throws RemoteException;

    void setImsBearerNotification(int i, int i2) throws RemoteException;

    void setImsCallMode(int i, int i2) throws RemoteException;

    void setImsCfgFeatureValue(int i, int i2, int i3, int i4, int i5) throws RemoteException;

    void setImsCfgProvisionValue(int i, int i2, String str) throws RemoteException;

    void setImsEnable(int i, boolean z) throws RemoteException;

    void setImsRegistrationReport(int i) throws RemoteException;

    void setImsRtpReport(int i, int i2, int i3, int i4) throws RemoteException;

    void setImscfg(int i, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) throws RemoteException;

    void setKeepAliveByIpData(int i, String str) throws RemoteException;

    void setKeepAliveByPDCPCtrlPDU(int i, String str) throws RemoteException;

    void setLocationInfo(int i, ArrayList<String> arrayList) throws RemoteException;

    void setLteBandEnableStatus(int i, int i2, boolean z) throws RemoteException;

    void setLteBsrTimer(int i, int i2) throws RemoteException;

    void setLteReleaseVersion(int i, int i2) throws RemoteException;

    void setLteScanDuration(int i, int i2) throws RemoteException;

    void setMaxUlSpeed(int i, int i2) throws RemoteException;

    void setModemImsCfg(int i, String str, String str2, int i2) throws RemoteException;

    void setModemPower(int i, boolean z) throws RemoteException;

    void setNROption(int i, int i2) throws RemoteException;

    void setNattKeepAliveStatus(int i, String str, boolean z, String str2, int i2, String str3, int i3) throws RemoteException;

    void setNetworkLock(int i, int i2, int i3, String str, String str2, String str3, String str4) throws RemoteException;

    void setNetworkSelectionModeManualWithAct(int i, String str, String str2, String str3) throws RemoteException;

    void setNrBandMode(int i, ArrayList<Integer> arrayList, ArrayList<Integer> arrayList2, ArrayList<Integer> arrayList3, ArrayList<Integer> arrayList4) throws RemoteException;

    void setPOLEntry(int i, int i2, String str, int i3) throws RemoteException;

    void setPhoneBookMemStorage(int i, String str, String str2) throws RemoteException;

    void setPhonebookReady(int i, int i2) throws RemoteException;

    void setProvisionValue(int i, String str, String str2) throws RemoteException;

    void setQamEnabled(int i, boolean z, boolean z2) throws RemoteException;

    void setRemoveRestrictEutranMode(int i, int i2) throws RemoteException;

    void setResponseFunctionsAssist(IAssistRadioResponse iAssistRadioResponse) throws RemoteException;

    void setResponseFunctionsCap(ICapRadioResponse iCapRadioResponse) throws RemoteException;

    void setResponseFunctionsEm(IEmRadioResponse iEmRadioResponse, IEmRadioIndication iEmRadioIndication) throws RemoteException;

    void setResponseFunctionsForAtci(IAtciResponse iAtciResponse, IAtciIndication iAtciIndication) throws RemoteException;

    void setResponseFunctionsIms(IImsRadioResponse iImsRadioResponse, IImsRadioIndication iImsRadioIndication) throws RemoteException;

    void setResponseFunctionsMtk(IMtkRadioExResponse iMtkRadioExResponse, IMtkRadioExIndication iMtkRadioExIndication) throws RemoteException;

    void setResponseFunctionsMwi(IMwiRadioResponse iMwiRadioResponse, IMwiRadioIndication iMwiRadioIndication) throws RemoteException;

    void setResponseFunctionsRcs(IRcsRadioResponse iRcsRadioResponse, IRcsRadioIndication iRcsRadioIndication) throws RemoteException;

    void setResponseFunctionsRsu(IRsuRadioResponse iRsuRadioResponse, IRsuRadioIndication iRsuRadioIndication) throws RemoteException;

    void setResponseFunctionsSE(ISERadioResponse iSERadioResponse, ISERadioIndication iSERadioIndication) throws RemoteException;

    void setResponseFunctionsSmartRatSwitch(ISmartRatSwitchRadioResponse iSmartRatSwitchRadioResponse, ISmartRatSwitchRadioIndication iSmartRatSwitchRadioIndication) throws RemoteException;

    void setResponseFunctionsSubsidyLock(ISubsidyLockResponse iSubsidyLockResponse, ISubsidyLockIndication iSubsidyLockIndication) throws RemoteException;

    void setResumeRegistration(int i, int i2) throws RemoteException;

    void setRoamingEnable(int i, ArrayList<Integer> arrayList) throws RemoteException;

    void setRttMode(int i, int i2) throws RemoteException;

    void setRxTestConfig(int i, int i2) throws RemoteException;

    void setSearchRat(int i, ArrayList<Integer> arrayList) throws RemoteException;

    void setSearchStoredFreqInfo(int i, int i2, int i3, int i4, ArrayList<Integer> arrayList) throws RemoteException;

    void setServiceStateToModem(int i, int i2, int i3, int i4, int i5, int i6, int i7) throws RemoteException;

    void setSimPower(int i, int i2) throws RemoteException;

    void setSipHeader(int i, ArrayList<String> arrayList) throws RemoteException;

    void setSipHeaderReport(int i, ArrayList<String> arrayList) throws RemoteException;

    void setSmartSceneSwitch(int i, int i2, int i3, int i4) throws RemoteException;

    void setSmsParameters(int i, SmsParams smsParams) throws RemoteException;

    void setSuppServProperty(int i, String str, String str2) throws RemoteException;

    void setTm9Enabled(int i, boolean z, boolean z2) throws RemoteException;

    void setTrm(int i, int i2) throws RemoteException;

    void setTxPower(int i, int i2) throws RemoteException;

    void setTxPowerStatus(int i, int i2) throws RemoteException;

    void setVendorSetting(int i, int i2, String str) throws RemoteException;

    void setVoiceDomainPreference(int i, int i2) throws RemoteException;

    void setWfcConfig(int i, int i2, String str, String str2) throws RemoteException;

    void setWfcProfile(int i, int i2) throws RemoteException;

    void setWifiAssociated(int i, ArrayList<String> arrayList) throws RemoteException;

    void setWifiEnabled(int i, String str, int i2, int i3) throws RemoteException;

    void setWifiIpAddress(int i, ArrayList<String> arrayList) throws RemoteException;

    void setWifiPingResult(int i, int i2, int i3, int i4) throws RemoteException;

    void setWifiSignalLevel(int i, int i2, int i3) throws RemoteException;

    void setupXcapUserAgentString(int i, String str) throws RemoteException;

    void smartRatSwitch(int i, int i2, int i3) throws RemoteException;

    void supplyDepersonalization(int i, String str, int i2) throws RemoteException;

    void supplyDeviceNetworkDepersonalization(int i, String str) throws RemoteException;

    void syncDataSettingsToMd(int i, ArrayList<Integer> arrayList) throws RemoteException;

    void toggleRttAudioIndication(int i, int i2, int i3) throws RemoteException;

    void triggerModeSwitchByEcc(int i, int i2) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    void videoCallAccept(int i, int i2, int i3) throws RemoteException;

    void videoRingtoneEventRequest(int i, ArrayList<String> arrayList) throws RemoteException;

    void vtDial(int i, Dial dial) throws RemoteException;

    void vtDialWithSipUri(int i, String str) throws RemoteException;

    void writePhbEntry(int i, PhbEntryStructure phbEntryStructure) throws RemoteException;

    void writePhoneBookEntryExt(int i, PhbEntryExt phbEntryExt) throws RemoteException;

    void writeUPBGrpEntry(int i, int i2, ArrayList<Integer> arrayList) throws RemoteException;

    static IMtkRadioEx asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IMtkRadioEx)) {
            return (IMtkRadioEx) iface;
        }
        IMtkRadioEx proxy = new Proxy(binder);
        try {
            for (String descriptor : proxy.interfaceChain()) {
                if (descriptor.equals(kInterfaceName)) {
                    return proxy;
                }
            }
        } catch (RemoteException e) {
        }
        return null;
    }

    static IMtkRadioEx castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IMtkRadioEx getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IMtkRadioEx getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IMtkRadioEx getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IMtkRadioEx getService() throws RemoteException {
        return getService("default");
    }

    public static final class Proxy implements IMtkRadioEx {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            Objects.requireNonNull(remote);
            this.mRemote = remote;
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of vendor.mediatek.hardware.mtkradioex@3.0::IMtkRadioEx]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void responseAcknowledgementMtk() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setResponseFunctionsMtk(IMtkRadioExResponse radioResponse, IMtkRadioExIndication radioIndication) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeStrongBinder(radioResponse == null ? null : radioResponse.asBinder());
            _hidl_request.writeStrongBinder(radioIndication != null ? radioIndication.asBinder() : null);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(2, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setResponseFunctionsIms(IImsRadioResponse radioResponse, IImsRadioIndication radioIndication) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeStrongBinder(radioResponse == null ? null : radioResponse.asBinder());
            _hidl_request.writeStrongBinder(radioIndication != null ? radioIndication.asBinder() : null);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(3, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setResponseFunctionsMwi(IMwiRadioResponse radioResponse, IMwiRadioIndication radioIndication) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeStrongBinder(radioResponse == null ? null : radioResponse.asBinder());
            _hidl_request.writeStrongBinder(radioIndication != null ? radioIndication.asBinder() : null);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(4, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setResponseFunctionsSE(ISERadioResponse radioResponse, ISERadioIndication radioIndication) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeStrongBinder(radioResponse == null ? null : radioResponse.asBinder());
            _hidl_request.writeStrongBinder(radioIndication != null ? radioIndication.asBinder() : null);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(5, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setResponseFunctionsEm(IEmRadioResponse radioResponse, IEmRadioIndication radioIndication) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeStrongBinder(radioResponse == null ? null : radioResponse.asBinder());
            _hidl_request.writeStrongBinder(radioIndication != null ? radioIndication.asBinder() : null);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(6, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setResponseFunctionsAssist(IAssistRadioResponse radioResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeStrongBinder(radioResponse == null ? null : radioResponse.asBinder());
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(7, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setResponseFunctionsCap(ICapRadioResponse radioResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeStrongBinder(radioResponse == null ? null : radioResponse.asBinder());
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(8, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setResponseFunctionsRsu(IRsuRadioResponse radioResponse, IRsuRadioIndication radioIndication) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeStrongBinder(radioResponse == null ? null : radioResponse.asBinder());
            _hidl_request.writeStrongBinder(radioIndication != null ? radioIndication.asBinder() : null);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(9, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void videoCallAccept(int serial, int videoMode, int callId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(videoMode);
            _hidl_request.writeInt32(callId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(10, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void imsEctCommand(int serial, String number, int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(number);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(11, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void controlCall(int serial, int controlType, int callId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(controlType);
            _hidl_request.writeInt32(callId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(12, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void imsDeregNotification(int serial, int cause) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(cause);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(13, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setImsEnable(int serial, boolean isOn) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(isOn);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(14, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setImscfg(int serial, boolean volteEnable, boolean vilteEnable, boolean vowifiEnable, boolean viwifiEnable, boolean smsEnable, boolean eimsEnable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(volteEnable);
            _hidl_request.writeBool(vilteEnable);
            _hidl_request.writeBool(vowifiEnable);
            _hidl_request.writeBool(viwifiEnable);
            _hidl_request.writeBool(smsEnable);
            _hidl_request.writeBool(eimsEnable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(15, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getProvisionValue(int serial, String provisionstring) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(provisionstring);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(16, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setProvisionValue(int serial, String provisionstring, String provisionValue) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(provisionstring);
            _hidl_request.writeString(provisionValue);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(17, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void controlImsConferenceCallMember(int serial, int controlType, int confCallId, String address, int callId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(controlType);
            _hidl_request.writeInt32(confCallId);
            _hidl_request.writeString(address);
            _hidl_request.writeInt32(callId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(18, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setWfcProfile(int serial, int wfcPreference) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(wfcPreference);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(19, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void conferenceDial(int serial, ConferenceDial dailInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            dailInfo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(20, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setModemImsCfg(int serial, String keys, String values, int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(keys);
            _hidl_request.writeString(values);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(21, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void dialWithSipUri(int serial, String address) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(address);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(22, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void vtDialWithSipUri(int serial, String address) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(address);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(23, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void vtDial(int serial, Dial dialInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            dialInfo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(24, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void forceReleaseCall(int serial, int callId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(callId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(25, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void imsBearerStateConfirm(int serial, int aid, int action, int status) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(aid);
            _hidl_request.writeInt32(action);
            _hidl_request.writeInt32(status);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(26, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setImsRtpReport(int serial, int pdnId, int networkId, int timer) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(pdnId);
            _hidl_request.writeInt32(networkId);
            _hidl_request.writeInt32(timer);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(27, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void pullCall(int serial, String target, boolean isVideoCall) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(target);
            _hidl_request.writeBool(isVideoCall);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(28, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setImsRegistrationReport(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(29, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendEmbmsAtCommand(int serial, String data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(30, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setRoamingEnable(int serial, ArrayList<Integer> config) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32Vector(config);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(31, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getRoamingEnable(int serial, int phoneId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(phoneId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(32, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setBarringPasswordCheckedByNW(int serial, String facility, String oldPassword, String newPassword, String cfmPassword) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(facility);
            _hidl_request.writeString(oldPassword);
            _hidl_request.writeString(newPassword);
            _hidl_request.writeString(cfmPassword);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(33, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setClip(int serial, int clipEnable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(clipEnable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(34, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getColp(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(35, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getColr(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(36, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendCnap(int serial, String cnapssMessage) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(cnapssMessage);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(37, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setColp(int serial, int colpEnable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(colpEnable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(38, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setColr(int serial, int colrEnable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(colrEnable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(39, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void queryCallForwardInTimeSlotStatus(int serial, CallForwardInfoEx callInfoEx) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            callInfoEx.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(40, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setCallForwardInTimeSlot(int serial, CallForwardInfoEx callInfoEx) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            callInfoEx.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(41, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void runGbaAuthentication(int serial, String nafFqdn, String nafSecureProtocolId, boolean forceRun, int netId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(nafFqdn);
            _hidl_request.writeString(nafSecureProtocolId);
            _hidl_request.writeBool(forceRun);
            _hidl_request.writeInt32(netId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(42, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendUssi(int serial, String ussiString) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(ussiString);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(43, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void cancelUssi(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(44, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getXcapStatus(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(45, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void resetSuppServ(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(46, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setupXcapUserAgentString(int serial, String userAgent) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(userAgent);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(47, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void hangupAll(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(48, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setCallIndication(int serial, int mode, int callId, int seqNumber, int cause) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            _hidl_request.writeInt32(callId);
            _hidl_request.writeInt32(seqNumber);
            _hidl_request.writeInt32(cause);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(49, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setEccMode(int serial, String number, int enable, int airplaneMode, int imsReg) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(number);
            _hidl_request.writeInt32(enable);
            _hidl_request.writeInt32(airplaneMode);
            _hidl_request.writeInt32(imsReg);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(50, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setEccNum(int serial, String ecc_list_with_card, String ecc_list_no_card) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(ecc_list_with_card);
            _hidl_request.writeString(ecc_list_no_card);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(51, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getEccNum(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(52, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setCallSubAddress(int serial, boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(53, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getCallSubAddress(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(54, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void queryPhbStorageInfo(int serial, int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(55, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void writePhbEntry(int serial, PhbEntryStructure phbEntry) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            phbEntry.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(56, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void readPhbEntry(int serial, int type, int bIndex, int eIndex) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(bIndex);
            _hidl_request.writeInt32(eIndex);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(57, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void queryUPBCapability(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(58, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void editUPBEntry(int serial, ArrayList<String> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeStringVector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(59, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void deleteUPBEntry(int serial, int entryType, int adnIndex, int entryIndex) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(entryType);
            _hidl_request.writeInt32(adnIndex);
            _hidl_request.writeInt32(entryIndex);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(60, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void readUPBGasList(int serial, int startIndex, int endIndex) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(startIndex);
            _hidl_request.writeInt32(endIndex);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(61, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void readUPBGrpEntry(int serial, int adnIndex) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(adnIndex);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(62, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void writeUPBGrpEntry(int serial, int adnIndex, ArrayList<Integer> grpIds) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(adnIndex);
            _hidl_request.writeInt32Vector(grpIds);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(63, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getPhoneBookStringsLength(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(64, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getPhoneBookMemStorage(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(65, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setPhoneBookMemStorage(int serial, String storage, String password) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(storage);
            _hidl_request.writeString(password);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(66, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void readPhoneBookEntryExt(int serial, int index1, int index2) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(index1);
            _hidl_request.writeInt32(index2);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(67, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void writePhoneBookEntryExt(int serial, PhbEntryExt phbEntryExt) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            phbEntryExt.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(68, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void queryUPBAvailable(int serial, int eftype, int fileIndex) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(eftype);
            _hidl_request.writeInt32(fileIndex);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(69, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void readUPBEmailEntry(int serial, int adnIndex, int fileIndex) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(adnIndex);
            _hidl_request.writeInt32(fileIndex);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(70, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void readUPBSneEntry(int serial, int adnIndex, int fileIndex) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(adnIndex);
            _hidl_request.writeInt32(fileIndex);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(71, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void readUPBAnrEntry(int serial, int adnIndex, int fileIndex) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(adnIndex);
            _hidl_request.writeInt32(fileIndex);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(72, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void readUPBAasList(int serial, int startIndex, int endIndex) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(startIndex);
            _hidl_request.writeInt32(endIndex);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(73, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setPhonebookReady(int serial, int ready) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(ready);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(74, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setModemPower(int serial, boolean isOn) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(isOn);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(75, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void triggerModeSwitchByEcc(int serial, int mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(76, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getATR(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(77, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getIccid(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(78, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setSimPower(int serial, int mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(79, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void activateUiccCard(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(80, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void deactivateUiccCard(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(81, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getCurrentUiccCardProvisioningStatus(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(82, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void doGeneralSimAuthentication(int serial, SimAuthStructure simAuth) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            simAuth.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(83, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void queryNetworkLock(int serial, int category) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(category);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(84, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setNetworkLock(int serial, int category, int lockop, String password, String data_imsi, String gid1, String gid2) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(category);
            _hidl_request.writeInt32(lockop);
            _hidl_request.writeString(password);
            _hidl_request.writeString(data_imsi);
            _hidl_request.writeString(gid1);
            _hidl_request.writeString(gid2);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(85, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void supplyDepersonalization(int serial, String netPin, int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(netPin);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(86, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendVsimNotification(int serial, int transactionId, int eventId, int simType) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(transactionId);
            _hidl_request.writeInt32(eventId);
            _hidl_request.writeInt32(simType);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(87, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendVsimOperation(int serial, int transactionId, int eventId, int result, int dataLength, ArrayList<Byte> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(transactionId);
            _hidl_request.writeInt32(eventId);
            _hidl_request.writeInt32(result);
            _hidl_request.writeInt32(dataLength);
            _hidl_request.writeInt8Vector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(88, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getSmsParameters(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(89, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setSmsParameters(int serial, SmsParams message) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            message.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(90, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getSmsMemStatus(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(91, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setEtws(int serial, int mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(92, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void removeCbMsg(int serial, int channelId, int serialId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(channelId);
            _hidl_request.writeInt32(serialId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(93, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setGsmBroadcastLangs(int serial, String langs) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(langs);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(94, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getGsmBroadcastLangs(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(95, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getGsmBroadcastActivation(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(96, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendImsSmsEx(int serial, ImsSmsMessage message) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            message.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(97, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void acknowledgeLastIncomingGsmSmsEx(int serial, boolean success, int cause) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(success);
            _hidl_request.writeInt32(cause);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(98, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void acknowledgeLastIncomingCdmaSmsEx(int serial, CdmaSmsAck smsAck) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            smsAck.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(99, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendRequestRaw(int serial, ArrayList<Byte> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt8Vector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(100, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendRequestStrings(int serial, ArrayList<String> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeStringVector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(101, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setResumeRegistration(int serial, int sessionId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(sessionId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(102, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void modifyModemType(int serial, int applyType, int modemType) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(applyType);
            _hidl_request.writeInt32(modemType);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(UtranBands.BAND_C, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getSmsRuimMemoryStatus(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(UtranBands.BAND_D, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setNetworkSelectionModeManualWithAct(int serial, String operatorNumeric, String act, String mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(operatorNumeric);
            _hidl_request.writeString(act);
            _hidl_request.writeString(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(UtranBands.BAND_E, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getAvailableNetworksWithAct(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(UtranBands.BAND_F, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getSignalStrengthWithWcdmaEcio(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(107, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void cancelAvailableNetworks(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(108, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getFemtocellList(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(109, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void abortFemtocellList(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(110, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void selectFemtocell(int serial, String operatorNumeric, String act, String csgId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(operatorNumeric);
            _hidl_request.writeString(act);
            _hidl_request.writeString(csgId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(111, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void queryFemtoCellSystemSelectionMode(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(112, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setFemtoCellSystemSelectionMode(int serial, int mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(113, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setServiceStateToModem(int serial, int voiceRegState, int dataRegState, int voiceRoamingType, int dataRoamingType, int rilVoiceRegState, int rilDataRegState) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(voiceRegState);
            _hidl_request.writeInt32(dataRegState);
            _hidl_request.writeInt32(voiceRoamingType);
            _hidl_request.writeInt32(dataRoamingType);
            _hidl_request.writeInt32(rilVoiceRegState);
            _hidl_request.writeInt32(rilDataRegState);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(114, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void cfgA2offset(int serial, int offset, int threshBound) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(offset);
            _hidl_request.writeInt32(threshBound);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(115, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void cfgB1offset(int serial, int offset, int threshBound) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(offset);
            _hidl_request.writeInt32(threshBound);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(116, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void enableSCGfailure(int serial, boolean enable, int T1, int P1, int T2) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(enable);
            _hidl_request.writeInt32(T1);
            _hidl_request.writeInt32(P1);
            _hidl_request.writeInt32(T2);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(117, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setTxPower(int serial, int limitpower) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(limitpower);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(118, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setSearchStoredFreqInfo(int serial, int operation, int plmn_id, int rat, ArrayList<Integer> freq) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(operation);
            _hidl_request.writeInt32(plmn_id);
            _hidl_request.writeInt32(rat);
            _hidl_request.writeInt32Vector(freq);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(119, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setSearchRat(int serial, ArrayList<Integer> rat) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32Vector(rat);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(120, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setBgsrchDeltaSleepTimer(int serial, int sleepDuration) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(sleepDuration);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(121, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setRxTestConfig(int serial, int antType) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(antType);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(122, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getRxTestResult(int serial, int mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(DataCallFailCause.INVALID_DNS_ADDR, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getPOLCapability(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(DataCallFailCause.INVALID_PCSCF_OR_DNS_ADDRESS, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getCurrentPOLList(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(125, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setPOLEntry(int serial, int index, String numeric, int nAct) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(index);
            _hidl_request.writeString(numeric);
            _hidl_request.writeInt32(nAct);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(126, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setFdMode(int serial, int mode, int param1, int param2) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            _hidl_request.writeInt32(param1);
            _hidl_request.writeInt32(param2);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(127, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setTrm(int serial, int mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(128, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void handleStkCallSetupRequestFromSimWithResCode(int serial, int resultCode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(resultCode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(129, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setResponseFunctionsForAtci(IAtciResponse atciResponse, IAtciIndication atciIndication) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeStrongBinder(atciResponse == null ? null : atciResponse.asBinder());
            _hidl_request.writeStrongBinder(atciIndication != null ? atciIndication.asBinder() : null);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(130, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendAtciRequest(int serial, ArrayList<Byte> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt8Vector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(131, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void restartRILD(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(132, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void syncDataSettingsToMd(int serial, ArrayList<Integer> settings) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32Vector(settings);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(133, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void resetMdDataRetryCount(int serial, String apn) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(apn);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(RadioCdmaSmsConst.UDH_VAR_PIC_SIZE, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setRemoveRestrictEutranMode(int serial, int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(135, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setVoiceDomainPreference(int serial, int vdp) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(vdp);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(136, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setWifiEnabled(int serial, String ifName, int isWifiEnabled, int isFlightModeOn) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(ifName);
            _hidl_request.writeInt32(isWifiEnabled);
            _hidl_request.writeInt32(isFlightModeOn);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(137, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setWifiAssociated(int serial, ArrayList<String> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeStringVector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(138, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setWifiSignalLevel(int serial, int rssi, int snr) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(rssi);
            _hidl_request.writeInt32(snr);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(139, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setWifiIpAddress(int serial, ArrayList<String> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeStringVector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(140, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setWfcConfig(int serial, int setting, String ifName, String value) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(setting);
            _hidl_request.writeString(ifName);
            _hidl_request.writeString(value);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(141, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getWfcConfig(int serial, int setting) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(setting);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(142, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void querySsacStatus(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(143, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setLocationInfo(int serial, ArrayList<String> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeStringVector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(144, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setEmergencyAddressId(int serial, String aid) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(aid);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(145, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setNattKeepAliveStatus(int serial, String ifName, boolean enable, String srcIp, int srcPort, String dstIp, int dstPort) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(ifName);
            _hidl_request.writeBool(enable);
            _hidl_request.writeString(srcIp);
            _hidl_request.writeInt32(srcPort);
            _hidl_request.writeString(dstIp);
            _hidl_request.writeInt32(dstPort);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(146, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setWifiPingResult(int serial, int rat, int latency, int pktloss) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(rat);
            _hidl_request.writeInt32(latency);
            _hidl_request.writeInt32(pktloss);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(147, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setApcMode(int serial, int mode, int reportMode, int interval) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            _hidl_request.writeInt32(reportMode);
            _hidl_request.writeInt32(interval);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(148, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getApcInfo(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(149, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setImsBearerNotification(int serial, int enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(150, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setImsCfgFeatureValue(int serial, int featureId, int network, int value, int isLast) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(featureId);
            _hidl_request.writeInt32(network);
            _hidl_request.writeInt32(value);
            _hidl_request.writeInt32(isLast);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(151, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getImsCfgFeatureValue(int serial, int featureId, int network) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(featureId);
            _hidl_request.writeInt32(network);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(152, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setImsCfgProvisionValue(int serial, int configId, String value) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(configId);
            _hidl_request.writeString(value);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(153, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getImsCfgProvisionValue(int serial, int configId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(configId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(154, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getImsCfgResourceCapValue(int serial, int featureId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(featureId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(155, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void dataConnectionAttach(int serial, int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(156, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void dataConnectionDetach(int serial, int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(157, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void resetAllConnections(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(158, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setLteReleaseVersion(int serial, int mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(159, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getLteReleaseVersion(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(160, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setTxPowerStatus(int serial, int mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(161, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setSuppServProperty(int serial, String name, String value) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(name);
            _hidl_request.writeString(value);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(162, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void supplyDeviceNetworkDepersonalization(int serial, String pwd) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(pwd);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(163, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void notifyEPDGScreenState(int serial, int state) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(state);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(164, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void hangupWithReason(int serial, int callId, int reason) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(callId);
            _hidl_request.writeInt32(reason);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(165, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setResponseFunctionsSubsidyLock(ISubsidyLockResponse sublockResp, ISubsidyLockIndication sublockInd) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeStrongBinder(sublockResp == null ? null : sublockResp.asBinder());
            _hidl_request.writeStrongBinder(sublockInd != null ? sublockInd.asBinder() : null);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(166, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setResponseFunctionsRcs(IRcsRadioResponse radioResponse, IRcsRadioIndication radioIndication) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeStrongBinder(radioResponse == null ? null : radioResponse.asBinder());
            _hidl_request.writeStrongBinder(radioIndication != null ? radioIndication.asBinder() : null);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(167, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendSubsidyLockRequest(int serial, int reqType, ArrayList<Byte> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(reqType);
            _hidl_request.writeInt8Vector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(168, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setVendorSetting(int serial, int setting, String value) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(setting);
            _hidl_request.writeString(value);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(169, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setRttMode(int serial, int mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(170, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendRttModifyRequest(int serial, int callId, int newMode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(callId);
            _hidl_request.writeInt32(newMode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(171, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendRttText(int serial, int callId, int lenOfString, String text) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(callId);
            _hidl_request.writeInt32(lenOfString);
            _hidl_request.writeString(text);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(172, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void rttModifyRequestResponse(int serial, int callId, int result) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(callId);
            _hidl_request.writeInt32(result);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(173, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void toggleRttAudioIndication(int serial, int callId, int audio) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(callId);
            _hidl_request.writeInt32(audio);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(174, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void queryVopsStatus(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(175, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void notifyImsServiceReady() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(176, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getPlmnNameFromSE13Table(int serial, int mcc, int mnc) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mcc);
            _hidl_request.writeInt32(mnc);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(177, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void enableCAPlusBandWidthFilter(int serial, boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(178, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getVoiceDomainPreference(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(179, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setSipHeader(int serial, ArrayList<String> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeStringVector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(180, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setSipHeaderReport(int serial, ArrayList<String> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeStringVector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(181, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setImsCallMode(int serial, int mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(182, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setGwsdMode(int serial, ArrayList<String> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeStringVector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(183, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setCallValidTimer(int serial, int timer) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(timer);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(184, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setIgnoreSameNumberInterval(int serial, int interval) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(interval);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(185, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setKeepAliveByPDCPCtrlPDU(int serial, String config) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(config);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(186, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setKeepAliveByIpData(int serial, String config) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(config);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(187, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void enableDsdaIndication(int serial, boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(188, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getDsdaStatus(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(189, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void registerCellQltyReport(int serial, String registerQuality, String type, String thresholdValues, String triggerTime) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(registerQuality);
            _hidl_request.writeString(type);
            _hidl_request.writeString(thresholdValues);
            _hidl_request.writeString(triggerTime);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(190, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getSuggestedPlmnList(int serial, int rat, int num, int timer) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(rat);
            _hidl_request.writeInt32(num);
            _hidl_request.writeInt32(timer);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(191, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void routeCertificate(int serial, int uid, ArrayList<Byte> cert, ArrayList<Byte> msg) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(uid);
            _hidl_request.writeInt8Vector(cert);
            _hidl_request.writeInt8Vector(msg);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(192, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void routeAuthMessage(int serial, int uid, ArrayList<Byte> msg) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(uid);
            _hidl_request.writeInt8Vector(msg);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(193, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void enableCapabaility(int serial, String id, int uid, int toActive) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(id);
            _hidl_request.writeInt32(uid);
            _hidl_request.writeInt32(toActive);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(194, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void abortCertificate(int serial, int uid) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(uid);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(195, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void eccRedialApprove(int serial, int approve, int callId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(approve);
            _hidl_request.writeInt32(callId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(196, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void deactivateNrScgCommunication(int serial, boolean deactivate, boolean allowSCGAdd) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(deactivate);
            _hidl_request.writeBool(allowSCGAdd);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(197, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getDeactivateNrScgCommunication(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(198, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setMaxUlSpeed(int serial, int ulSpeed) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(ulSpeed);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(199, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setResponseFunctionsSmartRatSwitch(ISmartRatSwitchRadioResponse radioResponse, ISmartRatSwitchRadioIndication radioIndication) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeStrongBinder(radioResponse == null ? null : radioResponse.asBinder());
            _hidl_request.writeStrongBinder(radioIndication != null ? radioIndication.asBinder() : null);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(200, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void smartRatSwitch(int serial, int mode, int rat) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            _hidl_request.writeInt32(rat);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(201, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getSmartRatSwitch(int serial, int mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(202, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setSmartSceneSwitch(int serial, int mode, int tGear, int lGear) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            _hidl_request.writeInt32(tGear);
            _hidl_request.writeInt32(lGear);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(203, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendSarIndicator(int serial, int sar_cmd_type, String sar_parameter) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(sar_cmd_type);
            _hidl_request.writeString(sar_parameter);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(204, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setCallAdditionalInfo(int serial, ArrayList<String> info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeStringVector(info);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(205, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendRsuRequest(int serial, RsuRequestInfo rri) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            rri.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(206, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendWifiEnabled(int serial, String ifName, int isWifiEnabled) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(ifName);
            _hidl_request.writeInt32(isWifiEnabled);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(207, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendWifiAssociated(int serial, String ifName, int associated, String ssid, String apMac, int mtuSize) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(ifName);
            _hidl_request.writeInt32(associated);
            _hidl_request.writeString(ssid);
            _hidl_request.writeString(apMac);
            _hidl_request.writeInt32(mtuSize);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(208, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void sendWifiIpAddress(int serial, String ifName, String ipv4Addr, String ipv6Addr, int ipv4PrefixLen, int ipv6PrefixLen, String ipv4Gateway, String ipv6Gateway, int dnsCount, String dnsServers) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(ifName);
            _hidl_request.writeString(ipv4Addr);
            _hidl_request.writeString(ipv6Addr);
            _hidl_request.writeInt32(ipv4PrefixLen);
            _hidl_request.writeInt32(ipv6PrefixLen);
            _hidl_request.writeString(ipv4Gateway);
            _hidl_request.writeString(ipv6Gateway);
            _hidl_request.writeInt32(dnsCount);
            _hidl_request.writeString(dnsServers);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(209, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void videoRingtoneEventRequest(int serial, ArrayList<String> event) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeStringVector(event);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(210, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setNROption(int serial, int option) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(option);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(SmsAcknowledgeFailCause.MEMORY_CAPACITY_EXCEEDED, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getTOEInfo(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(212, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void disableAllCALinks(int serial, int linkType) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(linkType);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(213, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getCALinkEnableStatus(int serial, String bandsCombo, int linkType) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(bandsCombo);
            _hidl_request.writeInt32(linkType);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(214, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setCALinkEnableStatus(int serial, boolean status, String bandsCombo, int linkType) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(status);
            _hidl_request.writeString(bandsCombo);
            _hidl_request.writeInt32(linkType);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(215, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getCALinkCapabilityList(int serial, int linkType) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(linkType);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(216, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getLteData(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(217, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getLteRRCState(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(218, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setLteBandEnableStatus(int serial, int bandId, boolean status) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(bandId);
            _hidl_request.writeBool(status);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(219, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getBandPriorityList(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(220, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setBandPriorityList(int serial, ArrayList<Integer> bandPriList) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32Vector(bandPriList);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(221, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void set4x4MimoEnabled(int serial, int enabled_bitmask) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(enabled_bitmask);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(222, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void get4x4MimoEnabled(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(223, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setLteBsrTimer(int serial, int timer) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(timer);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(224, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getLteBsrTimer(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(225, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getLte1xRttCellList(int serial, boolean available) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(available);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(RadioCdmaSmsConst.UDH_OTHER_SIZE, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void clearLteAvailableFile(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(227, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getBandMode(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(228, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getCaBandMode(int serial, int primaryBandId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(primaryBandId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(RadioCdmaSmsConst.USER_DATA_MAX, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getCampedFemtoCellInfo(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(230, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setQamEnabled(int serial, boolean ulOrDl, boolean enabled) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(ulOrDl);
            _hidl_request.writeBool(enabled);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(231, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getQamEnabled(int serial, boolean ulOrDl) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(ulOrDl);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(232, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setTm9Enabled(int serial, boolean fddOrTdd, boolean enabled) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(fddOrTdd);
            _hidl_request.writeBool(enabled);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(233, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getTm9Enabled(int serial, boolean fddOrTdd) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(fddOrTdd);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(234, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setLteScanDuration(int serial, int duration) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(duration);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(235, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getLteScanDuration(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(236, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setDisable2G(int serial, boolean mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(237, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getDisable2G(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(238, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getEngineeringModeInfo(int serial, int index) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(index);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(239, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getIWlanRegistrationState(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(LastCallFailCause.CALL_BARRED, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void getAllBandMode(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(LastCallFailCause.FDN_BLOCKED, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx
        public void setNrBandMode(int serial, ArrayList<Integer> saEnable, ArrayList<Integer> saDisable, ArrayList<Integer> nsaEnable, ArrayList<Integer> nsaDisable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IMtkRadioEx.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32Vector(saEnable);
            _hidl_request.writeInt32Vector(saDisable);
            _hidl_request.writeInt32Vector(nsaEnable);
            _hidl_request.writeInt32Vector(nsaDisable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(LastCallFailCause.IMSI_UNKNOWN_IN_VLR, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public ArrayList<String> interfaceChain() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256067662, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ArrayList<String> _hidl_out_descriptors = _hidl_reply.readStringVector();
                return _hidl_out_descriptors;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            _hidl_request.writeNativeHandle(fd);
            _hidl_request.writeStringVector(options);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256131655, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public String interfaceDescriptor() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256136003, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                String _hidl_out_descriptor = _hidl_reply.readString();
                return _hidl_out_descriptor;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public ArrayList<byte[]> getHashChain() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256398152, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ArrayList<byte[]> _hidl_out_hashchain = new ArrayList<>();
                HwBlob _hidl_blob = _hidl_reply.readBuffer(16L);
                int _hidl_vec_size = _hidl_blob.getInt32(8L);
                HwBlob childBlob = _hidl_reply.readEmbeddedBuffer(_hidl_vec_size * 32, _hidl_blob.handle(), 0L, true);
                _hidl_out_hashchain.clear();
                for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                    byte[] _hidl_vec_element = new byte[32];
                    long _hidl_array_offset_1 = _hidl_index_0 * 32;
                    childBlob.copyToInt8Array(_hidl_array_offset_1, _hidl_vec_element, 32);
                    _hidl_out_hashchain.add(_hidl_vec_element);
                }
                _hidl_reply.release();
                return _hidl_out_hashchain;
            } catch (Throwable th) {
                _hidl_reply.release();
                throw th;
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public void setHALInstrumentation() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256462420, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public void ping() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256921159, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public DebugInfo getDebugInfo() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(257049926, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                DebugInfo _hidl_out_info = new DebugInfo();
                _hidl_out_info.readFromParcel(_hidl_reply);
                return _hidl_out_info;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public void notifySyspropsChanged() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(257120595, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    public static abstract class Stub extends HwBinder implements IMtkRadioEx {
        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this;
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IMtkRadioEx.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IMtkRadioEx.kInterfaceName;
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{-126, -23, -31, -3, 20, 82, -4, -101, -46, -60, -10, -99, -112, 0, -90, 23, -37, 60, 84, 28, -66, -62, 68, 52, -8, 26, -10, 78, -81, -117, 7, 39}, new byte[]{-20, 127, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IMtkRadioEx, android.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        public IHwInterface queryLocalInterface(String descriptor) {
            if (IMtkRadioEx.kInterfaceName.equals(descriptor)) {
                return this;
            }
            return null;
        }

        public void registerAsService(String serviceName) throws RemoteException {
            registerService(serviceName);
        }

        public String toString() {
            return interfaceDescriptor() + "@Stub";
        }

        public void onTransact(int _hidl_code, HwParcel _hidl_request, HwParcel _hidl_reply, int _hidl_flags) throws RemoteException {
            switch (_hidl_code) {
                case 1:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    responseAcknowledgementMtk();
                    return;
                case 2:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    IMtkRadioExResponse radioResponse = IMtkRadioExResponse.asInterface(_hidl_request.readStrongBinder());
                    IMtkRadioExIndication radioIndication = IMtkRadioExIndication.asInterface(_hidl_request.readStrongBinder());
                    setResponseFunctionsMtk(radioResponse, radioIndication);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 3:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    IImsRadioResponse radioResponse2 = IImsRadioResponse.asInterface(_hidl_request.readStrongBinder());
                    IImsRadioIndication radioIndication2 = IImsRadioIndication.asInterface(_hidl_request.readStrongBinder());
                    setResponseFunctionsIms(radioResponse2, radioIndication2);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 4:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    IMwiRadioResponse radioResponse3 = IMwiRadioResponse.asInterface(_hidl_request.readStrongBinder());
                    IMwiRadioIndication radioIndication3 = IMwiRadioIndication.asInterface(_hidl_request.readStrongBinder());
                    setResponseFunctionsMwi(radioResponse3, radioIndication3);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 5:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    ISERadioResponse radioResponse4 = ISERadioResponse.asInterface(_hidl_request.readStrongBinder());
                    ISERadioIndication radioIndication4 = ISERadioIndication.asInterface(_hidl_request.readStrongBinder());
                    setResponseFunctionsSE(radioResponse4, radioIndication4);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 6:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    IEmRadioResponse radioResponse5 = IEmRadioResponse.asInterface(_hidl_request.readStrongBinder());
                    IEmRadioIndication radioIndication5 = IEmRadioIndication.asInterface(_hidl_request.readStrongBinder());
                    setResponseFunctionsEm(radioResponse5, radioIndication5);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 7:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    IAssistRadioResponse radioResponse6 = IAssistRadioResponse.asInterface(_hidl_request.readStrongBinder());
                    setResponseFunctionsAssist(radioResponse6);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 8:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    ICapRadioResponse radioResponse7 = ICapRadioResponse.asInterface(_hidl_request.readStrongBinder());
                    setResponseFunctionsCap(radioResponse7);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 9:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    IRsuRadioResponse radioResponse8 = IRsuRadioResponse.asInterface(_hidl_request.readStrongBinder());
                    IRsuRadioIndication radioIndication6 = IRsuRadioIndication.asInterface(_hidl_request.readStrongBinder());
                    setResponseFunctionsRsu(radioResponse8, radioIndication6);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 10:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial = _hidl_request.readInt32();
                    int videoMode = _hidl_request.readInt32();
                    int callId = _hidl_request.readInt32();
                    videoCallAccept(serial, videoMode, callId);
                    return;
                case 11:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial2 = _hidl_request.readInt32();
                    String number = _hidl_request.readString();
                    int type = _hidl_request.readInt32();
                    imsEctCommand(serial2, number, type);
                    return;
                case 12:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial3 = _hidl_request.readInt32();
                    int controlType = _hidl_request.readInt32();
                    int callId2 = _hidl_request.readInt32();
                    controlCall(serial3, controlType, callId2);
                    return;
                case 13:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial4 = _hidl_request.readInt32();
                    int cause = _hidl_request.readInt32();
                    imsDeregNotification(serial4, cause);
                    return;
                case 14:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial5 = _hidl_request.readInt32();
                    boolean isOn = _hidl_request.readBool();
                    setImsEnable(serial5, isOn);
                    return;
                case 15:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial6 = _hidl_request.readInt32();
                    boolean volteEnable = _hidl_request.readBool();
                    boolean vilteEnable = _hidl_request.readBool();
                    boolean vowifiEnable = _hidl_request.readBool();
                    boolean viwifiEnable = _hidl_request.readBool();
                    boolean smsEnable = _hidl_request.readBool();
                    boolean eimsEnable = _hidl_request.readBool();
                    setImscfg(serial6, volteEnable, vilteEnable, vowifiEnable, viwifiEnable, smsEnable, eimsEnable);
                    return;
                case 16:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial7 = _hidl_request.readInt32();
                    String provisionstring = _hidl_request.readString();
                    getProvisionValue(serial7, provisionstring);
                    return;
                case 17:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial8 = _hidl_request.readInt32();
                    String provisionstring2 = _hidl_request.readString();
                    String provisionValue = _hidl_request.readString();
                    setProvisionValue(serial8, provisionstring2, provisionValue);
                    return;
                case 18:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial9 = _hidl_request.readInt32();
                    int controlType2 = _hidl_request.readInt32();
                    int confCallId = _hidl_request.readInt32();
                    String address = _hidl_request.readString();
                    int callId3 = _hidl_request.readInt32();
                    controlImsConferenceCallMember(serial9, controlType2, confCallId, address, callId3);
                    return;
                case 19:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial10 = _hidl_request.readInt32();
                    int wfcPreference = _hidl_request.readInt32();
                    setWfcProfile(serial10, wfcPreference);
                    return;
                case 20:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial11 = _hidl_request.readInt32();
                    ConferenceDial dailInfo = new ConferenceDial();
                    dailInfo.readFromParcel(_hidl_request);
                    conferenceDial(serial11, dailInfo);
                    return;
                case 21:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial12 = _hidl_request.readInt32();
                    String keys = _hidl_request.readString();
                    String values = _hidl_request.readString();
                    int type2 = _hidl_request.readInt32();
                    setModemImsCfg(serial12, keys, values, type2);
                    return;
                case 22:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial13 = _hidl_request.readInt32();
                    String address2 = _hidl_request.readString();
                    dialWithSipUri(serial13, address2);
                    return;
                case 23:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial14 = _hidl_request.readInt32();
                    String address3 = _hidl_request.readString();
                    vtDialWithSipUri(serial14, address3);
                    return;
                case 24:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial15 = _hidl_request.readInt32();
                    Dial dialInfo = new Dial();
                    dialInfo.readFromParcel(_hidl_request);
                    vtDial(serial15, dialInfo);
                    return;
                case 25:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial16 = _hidl_request.readInt32();
                    int callId4 = _hidl_request.readInt32();
                    forceReleaseCall(serial16, callId4);
                    return;
                case 26:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial17 = _hidl_request.readInt32();
                    int aid = _hidl_request.readInt32();
                    int action = _hidl_request.readInt32();
                    int status = _hidl_request.readInt32();
                    imsBearerStateConfirm(serial17, aid, action, status);
                    return;
                case 27:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial18 = _hidl_request.readInt32();
                    int pdnId = _hidl_request.readInt32();
                    int networkId = _hidl_request.readInt32();
                    int timer = _hidl_request.readInt32();
                    setImsRtpReport(serial18, pdnId, networkId, timer);
                    return;
                case 28:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial19 = _hidl_request.readInt32();
                    String target = _hidl_request.readString();
                    boolean isVideoCall = _hidl_request.readBool();
                    pullCall(serial19, target, isVideoCall);
                    return;
                case 29:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial20 = _hidl_request.readInt32();
                    setImsRegistrationReport(serial20);
                    return;
                case 30:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial21 = _hidl_request.readInt32();
                    String data = _hidl_request.readString();
                    sendEmbmsAtCommand(serial21, data);
                    return;
                case 31:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial22 = _hidl_request.readInt32();
                    ArrayList<Integer> config = _hidl_request.readInt32Vector();
                    setRoamingEnable(serial22, config);
                    return;
                case 32:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial23 = _hidl_request.readInt32();
                    int phoneId = _hidl_request.readInt32();
                    getRoamingEnable(serial23, phoneId);
                    return;
                case 33:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial24 = _hidl_request.readInt32();
                    String facility = _hidl_request.readString();
                    String oldPassword = _hidl_request.readString();
                    String newPassword = _hidl_request.readString();
                    String cfmPassword = _hidl_request.readString();
                    setBarringPasswordCheckedByNW(serial24, facility, oldPassword, newPassword, cfmPassword);
                    return;
                case 34:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial25 = _hidl_request.readInt32();
                    int clipEnable = _hidl_request.readInt32();
                    setClip(serial25, clipEnable);
                    return;
                case 35:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial26 = _hidl_request.readInt32();
                    getColp(serial26);
                    return;
                case 36:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial27 = _hidl_request.readInt32();
                    getColr(serial27);
                    return;
                case 37:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial28 = _hidl_request.readInt32();
                    String cnapssMessage = _hidl_request.readString();
                    sendCnap(serial28, cnapssMessage);
                    return;
                case 38:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial29 = _hidl_request.readInt32();
                    int colpEnable = _hidl_request.readInt32();
                    setColp(serial29, colpEnable);
                    return;
                case 39:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial30 = _hidl_request.readInt32();
                    int colrEnable = _hidl_request.readInt32();
                    setColr(serial30, colrEnable);
                    return;
                case 40:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial31 = _hidl_request.readInt32();
                    CallForwardInfoEx callInfoEx = new CallForwardInfoEx();
                    callInfoEx.readFromParcel(_hidl_request);
                    queryCallForwardInTimeSlotStatus(serial31, callInfoEx);
                    return;
                case 41:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial32 = _hidl_request.readInt32();
                    CallForwardInfoEx callInfoEx2 = new CallForwardInfoEx();
                    callInfoEx2.readFromParcel(_hidl_request);
                    setCallForwardInTimeSlot(serial32, callInfoEx2);
                    return;
                case 42:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial33 = _hidl_request.readInt32();
                    String nafFqdn = _hidl_request.readString();
                    String nafSecureProtocolId = _hidl_request.readString();
                    boolean forceRun = _hidl_request.readBool();
                    int netId = _hidl_request.readInt32();
                    runGbaAuthentication(serial33, nafFqdn, nafSecureProtocolId, forceRun, netId);
                    return;
                case 43:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial34 = _hidl_request.readInt32();
                    String ussiString = _hidl_request.readString();
                    sendUssi(serial34, ussiString);
                    return;
                case 44:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial35 = _hidl_request.readInt32();
                    cancelUssi(serial35);
                    return;
                case 45:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial36 = _hidl_request.readInt32();
                    getXcapStatus(serial36);
                    return;
                case 46:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial37 = _hidl_request.readInt32();
                    resetSuppServ(serial37);
                    return;
                case 47:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial38 = _hidl_request.readInt32();
                    String userAgent = _hidl_request.readString();
                    setupXcapUserAgentString(serial38, userAgent);
                    return;
                case 48:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial39 = _hidl_request.readInt32();
                    hangupAll(serial39);
                    return;
                case 49:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial40 = _hidl_request.readInt32();
                    int mode = _hidl_request.readInt32();
                    int callId5 = _hidl_request.readInt32();
                    int seqNumber = _hidl_request.readInt32();
                    int cause2 = _hidl_request.readInt32();
                    setCallIndication(serial40, mode, callId5, seqNumber, cause2);
                    return;
                case 50:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial41 = _hidl_request.readInt32();
                    String number2 = _hidl_request.readString();
                    int enable = _hidl_request.readInt32();
                    int airplaneMode = _hidl_request.readInt32();
                    int imsReg = _hidl_request.readInt32();
                    setEccMode(serial41, number2, enable, airplaneMode, imsReg);
                    return;
                case 51:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial42 = _hidl_request.readInt32();
                    String ecc_list_with_card = _hidl_request.readString();
                    String ecc_list_no_card = _hidl_request.readString();
                    setEccNum(serial42, ecc_list_with_card, ecc_list_no_card);
                    return;
                case 52:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial43 = _hidl_request.readInt32();
                    getEccNum(serial43);
                    return;
                case 53:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial44 = _hidl_request.readInt32();
                    boolean enable2 = _hidl_request.readBool();
                    setCallSubAddress(serial44, enable2);
                    return;
                case 54:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial45 = _hidl_request.readInt32();
                    getCallSubAddress(serial45);
                    return;
                case 55:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial46 = _hidl_request.readInt32();
                    int type3 = _hidl_request.readInt32();
                    queryPhbStorageInfo(serial46, type3);
                    return;
                case 56:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial47 = _hidl_request.readInt32();
                    PhbEntryStructure phbEntry = new PhbEntryStructure();
                    phbEntry.readFromParcel(_hidl_request);
                    writePhbEntry(serial47, phbEntry);
                    return;
                case 57:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial48 = _hidl_request.readInt32();
                    int type4 = _hidl_request.readInt32();
                    int bIndex = _hidl_request.readInt32();
                    int eIndex = _hidl_request.readInt32();
                    readPhbEntry(serial48, type4, bIndex, eIndex);
                    return;
                case 58:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial49 = _hidl_request.readInt32();
                    queryUPBCapability(serial49);
                    return;
                case 59:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial50 = _hidl_request.readInt32();
                    ArrayList<String> data2 = _hidl_request.readStringVector();
                    editUPBEntry(serial50, data2);
                    return;
                case 60:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial51 = _hidl_request.readInt32();
                    int entryType = _hidl_request.readInt32();
                    int adnIndex = _hidl_request.readInt32();
                    int entryIndex = _hidl_request.readInt32();
                    deleteUPBEntry(serial51, entryType, adnIndex, entryIndex);
                    return;
                case 61:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial52 = _hidl_request.readInt32();
                    int startIndex = _hidl_request.readInt32();
                    int endIndex = _hidl_request.readInt32();
                    readUPBGasList(serial52, startIndex, endIndex);
                    return;
                case 62:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial53 = _hidl_request.readInt32();
                    int adnIndex2 = _hidl_request.readInt32();
                    readUPBGrpEntry(serial53, adnIndex2);
                    return;
                case 63:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial54 = _hidl_request.readInt32();
                    int adnIndex3 = _hidl_request.readInt32();
                    ArrayList<Integer> grpIds = _hidl_request.readInt32Vector();
                    writeUPBGrpEntry(serial54, adnIndex3, grpIds);
                    return;
                case 64:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial55 = _hidl_request.readInt32();
                    getPhoneBookStringsLength(serial55);
                    return;
                case 65:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial56 = _hidl_request.readInt32();
                    getPhoneBookMemStorage(serial56);
                    return;
                case 66:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial57 = _hidl_request.readInt32();
                    String storage = _hidl_request.readString();
                    String password = _hidl_request.readString();
                    setPhoneBookMemStorage(serial57, storage, password);
                    return;
                case 67:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial58 = _hidl_request.readInt32();
                    int index1 = _hidl_request.readInt32();
                    int index2 = _hidl_request.readInt32();
                    readPhoneBookEntryExt(serial58, index1, index2);
                    return;
                case 68:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial59 = _hidl_request.readInt32();
                    PhbEntryExt phbEntryExt = new PhbEntryExt();
                    phbEntryExt.readFromParcel(_hidl_request);
                    writePhoneBookEntryExt(serial59, phbEntryExt);
                    return;
                case LastCallFailCause.REQUESTED_FACILITY_NOT_IMPLEMENTED /* 69 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial60 = _hidl_request.readInt32();
                    int eftype = _hidl_request.readInt32();
                    int fileIndex = _hidl_request.readInt32();
                    queryUPBAvailable(serial60, eftype, fileIndex);
                    return;
                case 70:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial61 = _hidl_request.readInt32();
                    int adnIndex4 = _hidl_request.readInt32();
                    int fileIndex2 = _hidl_request.readInt32();
                    readUPBEmailEntry(serial61, adnIndex4, fileIndex2);
                    return;
                case 71:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial62 = _hidl_request.readInt32();
                    int adnIndex5 = _hidl_request.readInt32();
                    int fileIndex3 = _hidl_request.readInt32();
                    readUPBSneEntry(serial62, adnIndex5, fileIndex3);
                    return;
                case 72:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial63 = _hidl_request.readInt32();
                    int adnIndex6 = _hidl_request.readInt32();
                    int fileIndex4 = _hidl_request.readInt32();
                    readUPBAnrEntry(serial63, adnIndex6, fileIndex4);
                    return;
                case 73:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial64 = _hidl_request.readInt32();
                    int startIndex2 = _hidl_request.readInt32();
                    int endIndex2 = _hidl_request.readInt32();
                    readUPBAasList(serial64, startIndex2, endIndex2);
                    return;
                case 74:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial65 = _hidl_request.readInt32();
                    int ready = _hidl_request.readInt32();
                    setPhonebookReady(serial65, ready);
                    return;
                case 75:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial66 = _hidl_request.readInt32();
                    boolean isOn2 = _hidl_request.readBool();
                    setModemPower(serial66, isOn2);
                    return;
                case 76:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial67 = _hidl_request.readInt32();
                    int mode2 = _hidl_request.readInt32();
                    triggerModeSwitchByEcc(serial67, mode2);
                    return;
                case 77:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial68 = _hidl_request.readInt32();
                    getATR(serial68);
                    return;
                case 78:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial69 = _hidl_request.readInt32();
                    getIccid(serial69);
                    return;
                case 79:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial70 = _hidl_request.readInt32();
                    int mode3 = _hidl_request.readInt32();
                    setSimPower(serial70, mode3);
                    return;
                case NgranBands.BAND_80 /* 80 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial71 = _hidl_request.readInt32();
                    activateUiccCard(serial71);
                    return;
                case 81:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial72 = _hidl_request.readInt32();
                    deactivateUiccCard(serial72);
                    return;
                case NgranBands.BAND_82 /* 82 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial73 = _hidl_request.readInt32();
                    getCurrentUiccCardProvisioningStatus(serial73);
                    return;
                case NgranBands.BAND_83 /* 83 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial74 = _hidl_request.readInt32();
                    SimAuthStructure simAuth = new SimAuthStructure();
                    simAuth.readFromParcel(_hidl_request);
                    doGeneralSimAuthentication(serial74, simAuth);
                    return;
                case NgranBands.BAND_84 /* 84 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial75 = _hidl_request.readInt32();
                    int category = _hidl_request.readInt32();
                    queryNetworkLock(serial75, category);
                    return;
                case EutranBands.BAND_85 /* 85 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial76 = _hidl_request.readInt32();
                    int category2 = _hidl_request.readInt32();
                    int lockop = _hidl_request.readInt32();
                    String password2 = _hidl_request.readString();
                    String data_imsi = _hidl_request.readString();
                    String gid1 = _hidl_request.readString();
                    String gid2 = _hidl_request.readString();
                    setNetworkLock(serial76, category2, lockop, password2, data_imsi, gid1, gid2);
                    return;
                case NgranBands.BAND_86 /* 86 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial77 = _hidl_request.readInt32();
                    String netPin = _hidl_request.readString();
                    int type5 = _hidl_request.readInt32();
                    supplyDepersonalization(serial77, netPin, type5);
                    return;
                case 87:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial78 = _hidl_request.readInt32();
                    int transactionId = _hidl_request.readInt32();
                    int eventId = _hidl_request.readInt32();
                    int simType = _hidl_request.readInt32();
                    sendVsimNotification(serial78, transactionId, eventId, simType);
                    return;
                case 88:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial79 = _hidl_request.readInt32();
                    int transactionId2 = _hidl_request.readInt32();
                    int eventId2 = _hidl_request.readInt32();
                    int result = _hidl_request.readInt32();
                    int dataLength = _hidl_request.readInt32();
                    ArrayList<Byte> data3 = _hidl_request.readInt8Vector();
                    sendVsimOperation(serial79, transactionId2, eventId2, result, dataLength, data3);
                    return;
                case NgranBands.BAND_89 /* 89 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial80 = _hidl_request.readInt32();
                    getSmsParameters(serial80);
                    return;
                case NgranBands.BAND_90 /* 90 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial81 = _hidl_request.readInt32();
                    SmsParams message = new SmsParams();
                    message.readFromParcel(_hidl_request);
                    setSmsParameters(serial81, message);
                    return;
                case 91:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial82 = _hidl_request.readInt32();
                    getSmsMemStatus(serial82);
                    return;
                case NgranBands.BAND_92 /* 92 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial83 = _hidl_request.readInt32();
                    int mode4 = _hidl_request.readInt32();
                    setEtws(serial83, mode4);
                    return;
                case NgranBands.BAND_93 /* 93 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial84 = _hidl_request.readInt32();
                    int channelId = _hidl_request.readInt32();
                    int serialId = _hidl_request.readInt32();
                    removeCbMsg(serial84, channelId, serialId);
                    return;
                case NgranBands.BAND_94 /* 94 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial85 = _hidl_request.readInt32();
                    String langs = _hidl_request.readString();
                    setGsmBroadcastLangs(serial85, langs);
                    return;
                case 95:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial86 = _hidl_request.readInt32();
                    getGsmBroadcastLangs(serial86);
                    return;
                case 96:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial87 = _hidl_request.readInt32();
                    getGsmBroadcastActivation(serial87);
                    return;
                case 97:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial88 = _hidl_request.readInt32();
                    ImsSmsMessage message2 = new ImsSmsMessage();
                    message2.readFromParcel(_hidl_request);
                    sendImsSmsEx(serial88, message2);
                    return;
                case 98:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial89 = _hidl_request.readInt32();
                    boolean success = _hidl_request.readBool();
                    int cause3 = _hidl_request.readInt32();
                    acknowledgeLastIncomingGsmSmsEx(serial89, success, cause3);
                    return;
                case 99:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial90 = _hidl_request.readInt32();
                    CdmaSmsAck smsAck = new CdmaSmsAck();
                    smsAck.readFromParcel(_hidl_request);
                    acknowledgeLastIncomingCdmaSmsEx(serial90, smsAck);
                    return;
                case 100:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial91 = _hidl_request.readInt32();
                    ArrayList<Byte> data4 = _hidl_request.readInt8Vector();
                    sendRequestRaw(serial91, data4);
                    return;
                case 101:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial92 = _hidl_request.readInt32();
                    ArrayList<String> data5 = _hidl_request.readStringVector();
                    sendRequestStrings(serial92, data5);
                    return;
                case 102:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial93 = _hidl_request.readInt32();
                    int sessionId = _hidl_request.readInt32();
                    setResumeRegistration(serial93, sessionId);
                    return;
                case UtranBands.BAND_C /* 103 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial94 = _hidl_request.readInt32();
                    int applyType = _hidl_request.readInt32();
                    int modemType = _hidl_request.readInt32();
                    modifyModemType(serial94, applyType, modemType);
                    return;
                case UtranBands.BAND_D /* 104 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial95 = _hidl_request.readInt32();
                    getSmsRuimMemoryStatus(serial95);
                    return;
                case UtranBands.BAND_E /* 105 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial96 = _hidl_request.readInt32();
                    String operatorNumeric = _hidl_request.readString();
                    String act = _hidl_request.readString();
                    String mode5 = _hidl_request.readString();
                    setNetworkSelectionModeManualWithAct(serial96, operatorNumeric, act, mode5);
                    return;
                case UtranBands.BAND_F /* 106 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial97 = _hidl_request.readInt32();
                    getAvailableNetworksWithAct(serial97);
                    return;
                case 107:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial98 = _hidl_request.readInt32();
                    getSignalStrengthWithWcdmaEcio(serial98);
                    return;
                case 108:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial99 = _hidl_request.readInt32();
                    cancelAvailableNetworks(serial99);
                    return;
                case 109:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial100 = _hidl_request.readInt32();
                    getFemtocellList(serial100);
                    return;
                case 110:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial101 = _hidl_request.readInt32();
                    abortFemtocellList(serial101);
                    return;
                case 111:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial102 = _hidl_request.readInt32();
                    String operatorNumeric2 = _hidl_request.readString();
                    String act2 = _hidl_request.readString();
                    String csgId = _hidl_request.readString();
                    selectFemtocell(serial102, operatorNumeric2, act2, csgId);
                    return;
                case 112:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial103 = _hidl_request.readInt32();
                    queryFemtoCellSystemSelectionMode(serial103);
                    return;
                case 113:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial104 = _hidl_request.readInt32();
                    int mode6 = _hidl_request.readInt32();
                    setFemtoCellSystemSelectionMode(serial104, mode6);
                    return;
                case 114:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial105 = _hidl_request.readInt32();
                    int voiceRegState = _hidl_request.readInt32();
                    int dataRegState = _hidl_request.readInt32();
                    int voiceRoamingType = _hidl_request.readInt32();
                    int dataRoamingType = _hidl_request.readInt32();
                    int rilVoiceRegState = _hidl_request.readInt32();
                    int rilDataRegState = _hidl_request.readInt32();
                    setServiceStateToModem(serial105, voiceRegState, dataRegState, voiceRoamingType, dataRoamingType, rilVoiceRegState, rilDataRegState);
                    return;
                case 115:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial106 = _hidl_request.readInt32();
                    int offset = _hidl_request.readInt32();
                    int threshBound = _hidl_request.readInt32();
                    cfgA2offset(serial106, offset, threshBound);
                    return;
                case 116:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial107 = _hidl_request.readInt32();
                    int offset2 = _hidl_request.readInt32();
                    int threshBound2 = _hidl_request.readInt32();
                    cfgB1offset(serial107, offset2, threshBound2);
                    return;
                case 117:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial108 = _hidl_request.readInt32();
                    boolean enable3 = _hidl_request.readBool();
                    int T1 = _hidl_request.readInt32();
                    int P1 = _hidl_request.readInt32();
                    int T2 = _hidl_request.readInt32();
                    enableSCGfailure(serial108, enable3, T1, P1, T2);
                    return;
                case 118:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial109 = _hidl_request.readInt32();
                    int limitpower = _hidl_request.readInt32();
                    setTxPower(serial109, limitpower);
                    return;
                case 119:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial110 = _hidl_request.readInt32();
                    int operation = _hidl_request.readInt32();
                    int plmn_id = _hidl_request.readInt32();
                    int rat = _hidl_request.readInt32();
                    ArrayList<Integer> freq = _hidl_request.readInt32Vector();
                    setSearchStoredFreqInfo(serial110, operation, plmn_id, rat, freq);
                    return;
                case 120:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial111 = _hidl_request.readInt32();
                    ArrayList<Integer> rat2 = _hidl_request.readInt32Vector();
                    setSearchRat(serial111, rat2);
                    return;
                case 121:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial112 = _hidl_request.readInt32();
                    int sleepDuration = _hidl_request.readInt32();
                    setBgsrchDeltaSleepTimer(serial112, sleepDuration);
                    return;
                case 122:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial113 = _hidl_request.readInt32();
                    int antType = _hidl_request.readInt32();
                    setRxTestConfig(serial113, antType);
                    return;
                case DataCallFailCause.INVALID_DNS_ADDR /* 123 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial114 = _hidl_request.readInt32();
                    int mode7 = _hidl_request.readInt32();
                    getRxTestResult(serial114, mode7);
                    return;
                case DataCallFailCause.INVALID_PCSCF_OR_DNS_ADDRESS /* 124 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial115 = _hidl_request.readInt32();
                    getPOLCapability(serial115);
                    return;
                case 125:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial116 = _hidl_request.readInt32();
                    getCurrentPOLList(serial116);
                    return;
                case 126:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial117 = _hidl_request.readInt32();
                    int index = _hidl_request.readInt32();
                    String numeric = _hidl_request.readString();
                    int nAct = _hidl_request.readInt32();
                    setPOLEntry(serial117, index, numeric, nAct);
                    return;
                case 127:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial118 = _hidl_request.readInt32();
                    int mode8 = _hidl_request.readInt32();
                    int param1 = _hidl_request.readInt32();
                    int param2 = _hidl_request.readInt32();
                    setFdMode(serial118, mode8, param1, param2);
                    return;
                case 128:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial119 = _hidl_request.readInt32();
                    int mode9 = _hidl_request.readInt32();
                    setTrm(serial119, mode9);
                    return;
                case 129:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial120 = _hidl_request.readInt32();
                    int resultCode = _hidl_request.readInt32();
                    handleStkCallSetupRequestFromSimWithResCode(serial120, resultCode);
                    return;
                case 130 /* 130 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    IAtciResponse atciResponse = IAtciResponse.asInterface(_hidl_request.readStrongBinder());
                    IAtciIndication atciIndication = IAtciIndication.asInterface(_hidl_request.readStrongBinder());
                    setResponseFunctionsForAtci(atciResponse, atciIndication);
                    return;
                case 131:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial121 = _hidl_request.readInt32();
                    ArrayList<Byte> data6 = _hidl_request.readInt8Vector();
                    sendAtciRequest(serial121, data6);
                    return;
                case 132 /* 132 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial122 = _hidl_request.readInt32();
                    restartRILD(serial122);
                    return;
                case 133 /* 133 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial123 = _hidl_request.readInt32();
                    ArrayList<Integer> settings = _hidl_request.readInt32Vector();
                    syncDataSettingsToMd(serial123, settings);
                    return;
                case RadioCdmaSmsConst.UDH_VAR_PIC_SIZE /* 134 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial124 = _hidl_request.readInt32();
                    String apn = _hidl_request.readString();
                    resetMdDataRetryCount(serial124, apn);
                    return;
                case 135 /* 135 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial125 = _hidl_request.readInt32();
                    int type6 = _hidl_request.readInt32();
                    setRemoveRestrictEutranMode(serial125, type6);
                    return;
                case 136 /* 136 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial126 = _hidl_request.readInt32();
                    int vdp = _hidl_request.readInt32();
                    setVoiceDomainPreference(serial126, vdp);
                    return;
                case 137:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial127 = _hidl_request.readInt32();
                    String ifName = _hidl_request.readString();
                    int isWifiEnabled = _hidl_request.readInt32();
                    int isFlightModeOn = _hidl_request.readInt32();
                    setWifiEnabled(serial127, ifName, isWifiEnabled, isFlightModeOn);
                    return;
                case 138:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial128 = _hidl_request.readInt32();
                    ArrayList<String> data7 = _hidl_request.readStringVector();
                    setWifiAssociated(serial128, data7);
                    return;
                case 139:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial129 = _hidl_request.readInt32();
                    int rssi = _hidl_request.readInt32();
                    int snr = _hidl_request.readInt32();
                    setWifiSignalLevel(serial129, rssi, snr);
                    return;
                case 140:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial130 = _hidl_request.readInt32();
                    ArrayList<String> data8 = _hidl_request.readStringVector();
                    setWifiIpAddress(serial130, data8);
                    return;
                case 141:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial131 = _hidl_request.readInt32();
                    int setting = _hidl_request.readInt32();
                    String ifName2 = _hidl_request.readString();
                    String value = _hidl_request.readString();
                    setWfcConfig(serial131, setting, ifName2, value);
                    return;
                case 142:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial132 = _hidl_request.readInt32();
                    int setting2 = _hidl_request.readInt32();
                    getWfcConfig(serial132, setting2);
                    return;
                case 143:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial133 = _hidl_request.readInt32();
                    querySsacStatus(serial133);
                    return;
                case 144:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial134 = _hidl_request.readInt32();
                    ArrayList<String> data9 = _hidl_request.readStringVector();
                    setLocationInfo(serial134, data9);
                    return;
                case 145:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial135 = _hidl_request.readInt32();
                    String aid2 = _hidl_request.readString();
                    setEmergencyAddressId(serial135, aid2);
                    return;
                case 146:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial136 = _hidl_request.readInt32();
                    String ifName3 = _hidl_request.readString();
                    boolean enable4 = _hidl_request.readBool();
                    String srcIp = _hidl_request.readString();
                    int srcPort = _hidl_request.readInt32();
                    String dstIp = _hidl_request.readString();
                    int dstPort = _hidl_request.readInt32();
                    setNattKeepAliveStatus(serial136, ifName3, enable4, srcIp, srcPort, dstIp, dstPort);
                    return;
                case 147:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial137 = _hidl_request.readInt32();
                    int rat3 = _hidl_request.readInt32();
                    int latency = _hidl_request.readInt32();
                    int pktloss = _hidl_request.readInt32();
                    setWifiPingResult(serial137, rat3, latency, pktloss);
                    return;
                case 148:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial138 = _hidl_request.readInt32();
                    int mode10 = _hidl_request.readInt32();
                    int reportMode = _hidl_request.readInt32();
                    int interval = _hidl_request.readInt32();
                    setApcMode(serial138, mode10, reportMode, interval);
                    return;
                case 149:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial139 = _hidl_request.readInt32();
                    getApcInfo(serial139);
                    return;
                case 150:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial140 = _hidl_request.readInt32();
                    int enable5 = _hidl_request.readInt32();
                    setImsBearerNotification(serial140, enable5);
                    return;
                case 151:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial141 = _hidl_request.readInt32();
                    int featureId = _hidl_request.readInt32();
                    int network = _hidl_request.readInt32();
                    int value2 = _hidl_request.readInt32();
                    int isLast = _hidl_request.readInt32();
                    setImsCfgFeatureValue(serial141, featureId, network, value2, isLast);
                    return;
                case 152:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial142 = _hidl_request.readInt32();
                    int featureId2 = _hidl_request.readInt32();
                    int network2 = _hidl_request.readInt32();
                    getImsCfgFeatureValue(serial142, featureId2, network2);
                    return;
                case 153:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial143 = _hidl_request.readInt32();
                    int configId = _hidl_request.readInt32();
                    String value3 = _hidl_request.readString();
                    setImsCfgProvisionValue(serial143, configId, value3);
                    return;
                case 154:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial144 = _hidl_request.readInt32();
                    int configId2 = _hidl_request.readInt32();
                    getImsCfgProvisionValue(serial144, configId2);
                    return;
                case 155:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial145 = _hidl_request.readInt32();
                    int featureId3 = _hidl_request.readInt32();
                    getImsCfgResourceCapValue(serial145, featureId3);
                    return;
                case 156:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial146 = _hidl_request.readInt32();
                    int type7 = _hidl_request.readInt32();
                    dataConnectionAttach(serial146, type7);
                    return;
                case 157:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial147 = _hidl_request.readInt32();
                    int type8 = _hidl_request.readInt32();
                    dataConnectionDetach(serial147, type8);
                    return;
                case 158:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial148 = _hidl_request.readInt32();
                    resetAllConnections(serial148);
                    return;
                case 159:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial149 = _hidl_request.readInt32();
                    int mode11 = _hidl_request.readInt32();
                    setLteReleaseVersion(serial149, mode11);
                    return;
                case 160:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial150 = _hidl_request.readInt32();
                    getLteReleaseVersion(serial150);
                    return;
                case 161:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial151 = _hidl_request.readInt32();
                    int mode12 = _hidl_request.readInt32();
                    setTxPowerStatus(serial151, mode12);
                    return;
                case 162:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial152 = _hidl_request.readInt32();
                    String name = _hidl_request.readString();
                    String value4 = _hidl_request.readString();
                    setSuppServProperty(serial152, name, value4);
                    return;
                case 163:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial153 = _hidl_request.readInt32();
                    String pwd = _hidl_request.readString();
                    supplyDeviceNetworkDepersonalization(serial153, pwd);
                    return;
                case 164:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial154 = _hidl_request.readInt32();
                    int state = _hidl_request.readInt32();
                    notifyEPDGScreenState(serial154, state);
                    return;
                case 165:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial155 = _hidl_request.readInt32();
                    int callId6 = _hidl_request.readInt32();
                    int reason = _hidl_request.readInt32();
                    hangupWithReason(serial155, callId6, reason);
                    return;
                case 166:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    ISubsidyLockResponse sublockResp = ISubsidyLockResponse.asInterface(_hidl_request.readStrongBinder());
                    ISubsidyLockIndication sublockInd = ISubsidyLockIndication.asInterface(_hidl_request.readStrongBinder());
                    setResponseFunctionsSubsidyLock(sublockResp, sublockInd);
                    return;
                case 167:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    IRcsRadioResponse radioResponse9 = IRcsRadioResponse.asInterface(_hidl_request.readStrongBinder());
                    IRcsRadioIndication radioIndication7 = IRcsRadioIndication.asInterface(_hidl_request.readStrongBinder());
                    setResponseFunctionsRcs(radioResponse9, radioIndication7);
                    return;
                case 168:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial156 = _hidl_request.readInt32();
                    int reqType = _hidl_request.readInt32();
                    ArrayList<Byte> data10 = _hidl_request.readInt8Vector();
                    sendSubsidyLockRequest(serial156, reqType, data10);
                    return;
                case 169:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial157 = _hidl_request.readInt32();
                    int setting3 = _hidl_request.readInt32();
                    String value5 = _hidl_request.readString();
                    setVendorSetting(serial157, setting3, value5);
                    return;
                case 170:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial158 = _hidl_request.readInt32();
                    int mode13 = _hidl_request.readInt32();
                    setRttMode(serial158, mode13);
                    return;
                case 171:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial159 = _hidl_request.readInt32();
                    int callId7 = _hidl_request.readInt32();
                    int newMode = _hidl_request.readInt32();
                    sendRttModifyRequest(serial159, callId7, newMode);
                    return;
                case 172:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial160 = _hidl_request.readInt32();
                    int callId8 = _hidl_request.readInt32();
                    int lenOfString = _hidl_request.readInt32();
                    String text = _hidl_request.readString();
                    sendRttText(serial160, callId8, lenOfString, text);
                    return;
                case 173:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial161 = _hidl_request.readInt32();
                    int callId9 = _hidl_request.readInt32();
                    int result2 = _hidl_request.readInt32();
                    rttModifyRequestResponse(serial161, callId9, result2);
                    return;
                case 174:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial162 = _hidl_request.readInt32();
                    int callId10 = _hidl_request.readInt32();
                    int audio = _hidl_request.readInt32();
                    toggleRttAudioIndication(serial162, callId10, audio);
                    return;
                case 175:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial163 = _hidl_request.readInt32();
                    queryVopsStatus(serial163);
                    return;
                case 176:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    notifyImsServiceReady();
                    return;
                case 177:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial164 = _hidl_request.readInt32();
                    int mcc = _hidl_request.readInt32();
                    int mnc = _hidl_request.readInt32();
                    getPlmnNameFromSE13Table(serial164, mcc, mnc);
                    return;
                case 178:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial165 = _hidl_request.readInt32();
                    boolean enable6 = _hidl_request.readBool();
                    enableCAPlusBandWidthFilter(serial165, enable6);
                    return;
                case 179:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial166 = _hidl_request.readInt32();
                    getVoiceDomainPreference(serial166);
                    return;
                case 180 /* 180 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial167 = _hidl_request.readInt32();
                    ArrayList<String> data11 = _hidl_request.readStringVector();
                    setSipHeader(serial167, data11);
                    return;
                case 181:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial168 = _hidl_request.readInt32();
                    ArrayList<String> data12 = _hidl_request.readStringVector();
                    setSipHeaderReport(serial168, data12);
                    return;
                case 182:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial169 = _hidl_request.readInt32();
                    int mode14 = _hidl_request.readInt32();
                    setImsCallMode(serial169, mode14);
                    return;
                case 183 /* 183 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial170 = _hidl_request.readInt32();
                    ArrayList<String> data13 = _hidl_request.readStringVector();
                    setGwsdMode(serial170, data13);
                    return;
                case 184:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial171 = _hidl_request.readInt32();
                    int timer2 = _hidl_request.readInt32();
                    setCallValidTimer(serial171, timer2);
                    return;
                case 185:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial172 = _hidl_request.readInt32();
                    int interval2 = _hidl_request.readInt32();
                    setIgnoreSameNumberInterval(serial172, interval2);
                    return;
                case 186:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial173 = _hidl_request.readInt32();
                    String config2 = _hidl_request.readString();
                    setKeepAliveByPDCPCtrlPDU(serial173, config2);
                    return;
                case 187:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial174 = _hidl_request.readInt32();
                    String config3 = _hidl_request.readString();
                    setKeepAliveByIpData(serial174, config3);
                    return;
                case 188:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial175 = _hidl_request.readInt32();
                    boolean enable7 = _hidl_request.readBool();
                    enableDsdaIndication(serial175, enable7);
                    return;
                case 189:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial176 = _hidl_request.readInt32();
                    getDsdaStatus(serial176);
                    return;
                case 190:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial177 = _hidl_request.readInt32();
                    String registerQuality = _hidl_request.readString();
                    String type9 = _hidl_request.readString();
                    String thresholdValues = _hidl_request.readString();
                    String triggerTime = _hidl_request.readString();
                    registerCellQltyReport(serial177, registerQuality, type9, thresholdValues, triggerTime);
                    return;
                case 191:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial178 = _hidl_request.readInt32();
                    int rat4 = _hidl_request.readInt32();
                    int num = _hidl_request.readInt32();
                    int timer3 = _hidl_request.readInt32();
                    getSuggestedPlmnList(serial178, rat4, num, timer3);
                    return;
                case 192:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial179 = _hidl_request.readInt32();
                    int uid = _hidl_request.readInt32();
                    ArrayList<Byte> cert = _hidl_request.readInt8Vector();
                    ArrayList<Byte> msg = _hidl_request.readInt8Vector();
                    routeCertificate(serial179, uid, cert, msg);
                    return;
                case 193:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial180 = _hidl_request.readInt32();
                    int uid2 = _hidl_request.readInt32();
                    ArrayList<Byte> msg2 = _hidl_request.readInt8Vector();
                    routeAuthMessage(serial180, uid2, msg2);
                    return;
                case 194:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial181 = _hidl_request.readInt32();
                    String id = _hidl_request.readString();
                    int uid3 = _hidl_request.readInt32();
                    int toActive = _hidl_request.readInt32();
                    enableCapabaility(serial181, id, uid3, toActive);
                    return;
                case 195:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial182 = _hidl_request.readInt32();
                    int uid4 = _hidl_request.readInt32();
                    abortCertificate(serial182, uid4);
                    return;
                case 196:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial183 = _hidl_request.readInt32();
                    int approve = _hidl_request.readInt32();
                    int callId11 = _hidl_request.readInt32();
                    eccRedialApprove(serial183, approve, callId11);
                    return;
                case 197:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial184 = _hidl_request.readInt32();
                    boolean deactivate = _hidl_request.readBool();
                    boolean allowSCGAdd = _hidl_request.readBool();
                    deactivateNrScgCommunication(serial184, deactivate, allowSCGAdd);
                    return;
                case 198:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial185 = _hidl_request.readInt32();
                    getDeactivateNrScgCommunication(serial185);
                    return;
                case 199:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial186 = _hidl_request.readInt32();
                    int ulSpeed = _hidl_request.readInt32();
                    setMaxUlSpeed(serial186, ulSpeed);
                    return;
                case 200 /* 200 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    ISmartRatSwitchRadioResponse radioResponse10 = ISmartRatSwitchRadioResponse.asInterface(_hidl_request.readStrongBinder());
                    ISmartRatSwitchRadioIndication radioIndication8 = ISmartRatSwitchRadioIndication.asInterface(_hidl_request.readStrongBinder());
                    setResponseFunctionsSmartRatSwitch(radioResponse10, radioIndication8);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 201 /* 201 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial187 = _hidl_request.readInt32();
                    int mode15 = _hidl_request.readInt32();
                    int rat5 = _hidl_request.readInt32();
                    smartRatSwitch(serial187, mode15, rat5);
                    return;
                case 202 /* 202 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial188 = _hidl_request.readInt32();
                    int mode16 = _hidl_request.readInt32();
                    getSmartRatSwitch(serial188, mode16);
                    return;
                case 203:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial189 = _hidl_request.readInt32();
                    int mode17 = _hidl_request.readInt32();
                    int tGear = _hidl_request.readInt32();
                    int lGear = _hidl_request.readInt32();
                    setSmartSceneSwitch(serial189, mode17, tGear, lGear);
                    return;
                case 204:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial190 = _hidl_request.readInt32();
                    int sar_cmd_type = _hidl_request.readInt32();
                    String sar_parameter = _hidl_request.readString();
                    sendSarIndicator(serial190, sar_cmd_type, sar_parameter);
                    return;
                case 205:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial191 = _hidl_request.readInt32();
                    ArrayList<String> info = _hidl_request.readStringVector();
                    setCallAdditionalInfo(serial191, info);
                    return;
                case 206:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial192 = _hidl_request.readInt32();
                    RsuRequestInfo rri = new RsuRequestInfo();
                    rri.readFromParcel(_hidl_request);
                    sendRsuRequest(serial192, rri);
                    return;
                case 207:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial193 = _hidl_request.readInt32();
                    String ifName4 = _hidl_request.readString();
                    int isWifiEnabled2 = _hidl_request.readInt32();
                    sendWifiEnabled(serial193, ifName4, isWifiEnabled2);
                    return;
                case 208:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial194 = _hidl_request.readInt32();
                    String ifName5 = _hidl_request.readString();
                    int associated = _hidl_request.readInt32();
                    String ssid = _hidl_request.readString();
                    String apMac = _hidl_request.readString();
                    int mtuSize = _hidl_request.readInt32();
                    sendWifiAssociated(serial194, ifName5, associated, ssid, apMac, mtuSize);
                    return;
                case 209:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial195 = _hidl_request.readInt32();
                    String ifName6 = _hidl_request.readString();
                    String ipv4Addr = _hidl_request.readString();
                    String ipv6Addr = _hidl_request.readString();
                    int ipv4PrefixLen = _hidl_request.readInt32();
                    int ipv6PrefixLen = _hidl_request.readInt32();
                    String ipv4Gateway = _hidl_request.readString();
                    String ipv6Gateway = _hidl_request.readString();
                    int dnsCount = _hidl_request.readInt32();
                    String dnsServers = _hidl_request.readString();
                    sendWifiIpAddress(serial195, ifName6, ipv4Addr, ipv6Addr, ipv4PrefixLen, ipv6PrefixLen, ipv4Gateway, ipv6Gateway, dnsCount, dnsServers);
                    return;
                case 210:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial196 = _hidl_request.readInt32();
                    ArrayList<String> event = _hidl_request.readStringVector();
                    videoRingtoneEventRequest(serial196, event);
                    return;
                case SmsAcknowledgeFailCause.MEMORY_CAPACITY_EXCEEDED /* 211 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial197 = _hidl_request.readInt32();
                    int option = _hidl_request.readInt32();
                    setNROption(serial197, option);
                    return;
                case 212:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial198 = _hidl_request.readInt32();
                    getTOEInfo(serial198);
                    return;
                case 213:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial199 = _hidl_request.readInt32();
                    int linkType = _hidl_request.readInt32();
                    disableAllCALinks(serial199, linkType);
                    return;
                case 214:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial200 = _hidl_request.readInt32();
                    String bandsCombo = _hidl_request.readString();
                    int linkType2 = _hidl_request.readInt32();
                    getCALinkEnableStatus(serial200, bandsCombo, linkType2);
                    return;
                case 215:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial201 = _hidl_request.readInt32();
                    boolean status2 = _hidl_request.readBool();
                    String bandsCombo2 = _hidl_request.readString();
                    int linkType3 = _hidl_request.readInt32();
                    setCALinkEnableStatus(serial201, status2, bandsCombo2, linkType3);
                    return;
                case 216:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial202 = _hidl_request.readInt32();
                    int linkType4 = _hidl_request.readInt32();
                    getCALinkCapabilityList(serial202, linkType4);
                    return;
                case 217:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial203 = _hidl_request.readInt32();
                    getLteData(serial203);
                    return;
                case 218:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial204 = _hidl_request.readInt32();
                    getLteRRCState(serial204);
                    return;
                case 219:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial205 = _hidl_request.readInt32();
                    int bandId = _hidl_request.readInt32();
                    boolean status3 = _hidl_request.readBool();
                    setLteBandEnableStatus(serial205, bandId, status3);
                    return;
                case 220:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial206 = _hidl_request.readInt32();
                    getBandPriorityList(serial206);
                    return;
                case 221:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial207 = _hidl_request.readInt32();
                    ArrayList<Integer> bandPriList = _hidl_request.readInt32Vector();
                    setBandPriorityList(serial207, bandPriList);
                    return;
                case 222:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial208 = _hidl_request.readInt32();
                    int enabled_bitmask = _hidl_request.readInt32();
                    set4x4MimoEnabled(serial208, enabled_bitmask);
                    return;
                case 223:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial209 = _hidl_request.readInt32();
                    get4x4MimoEnabled(serial209);
                    return;
                case 224:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial210 = _hidl_request.readInt32();
                    int timer4 = _hidl_request.readInt32();
                    setLteBsrTimer(serial210, timer4);
                    return;
                case 225:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial211 = _hidl_request.readInt32();
                    getLteBsrTimer(serial211);
                    return;
                case RadioCdmaSmsConst.UDH_OTHER_SIZE /* 226 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial212 = _hidl_request.readInt32();
                    boolean available = _hidl_request.readBool();
                    getLte1xRttCellList(serial212, available);
                    return;
                case 227:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial213 = _hidl_request.readInt32();
                    clearLteAvailableFile(serial213);
                    return;
                case 228:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial214 = _hidl_request.readInt32();
                    getBandMode(serial214);
                    return;
                case RadioCdmaSmsConst.USER_DATA_MAX /* 229 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial215 = _hidl_request.readInt32();
                    int primaryBandId = _hidl_request.readInt32();
                    getCaBandMode(serial215, primaryBandId);
                    return;
                case 230:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial216 = _hidl_request.readInt32();
                    getCampedFemtoCellInfo(serial216);
                    return;
                case 231:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial217 = _hidl_request.readInt32();
                    boolean ulOrDl = _hidl_request.readBool();
                    boolean enabled = _hidl_request.readBool();
                    setQamEnabled(serial217, ulOrDl, enabled);
                    return;
                case 232:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial218 = _hidl_request.readInt32();
                    boolean ulOrDl2 = _hidl_request.readBool();
                    getQamEnabled(serial218, ulOrDl2);
                    return;
                case 233:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial219 = _hidl_request.readInt32();
                    boolean fddOrTdd = _hidl_request.readBool();
                    boolean enabled2 = _hidl_request.readBool();
                    setTm9Enabled(serial219, fddOrTdd, enabled2);
                    return;
                case 234:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial220 = _hidl_request.readInt32();
                    boolean fddOrTdd2 = _hidl_request.readBool();
                    getTm9Enabled(serial220, fddOrTdd2);
                    return;
                case 235:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial221 = _hidl_request.readInt32();
                    int duration = _hidl_request.readInt32();
                    setLteScanDuration(serial221, duration);
                    return;
                case 236:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial222 = _hidl_request.readInt32();
                    getLteScanDuration(serial222);
                    return;
                case 237:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial223 = _hidl_request.readInt32();
                    boolean mode18 = _hidl_request.readBool();
                    setDisable2G(serial223, mode18);
                    return;
                case 238:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial224 = _hidl_request.readInt32();
                    getDisable2G(serial224);
                    return;
                case 239:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial225 = _hidl_request.readInt32();
                    int index3 = _hidl_request.readInt32();
                    getEngineeringModeInfo(serial225, index3);
                    return;
                case LastCallFailCause.CALL_BARRED /* 240 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial226 = _hidl_request.readInt32();
                    getIWlanRegistrationState(serial226);
                    return;
                case LastCallFailCause.FDN_BLOCKED /* 241 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial227 = _hidl_request.readInt32();
                    getAllBandMode(serial227);
                    return;
                case LastCallFailCause.IMSI_UNKNOWN_IN_VLR /* 242 */:
                    _hidl_request.enforceInterface(IMtkRadioEx.kInterfaceName);
                    int serial228 = _hidl_request.readInt32();
                    ArrayList<Integer> saEnable = _hidl_request.readInt32Vector();
                    ArrayList<Integer> saDisable = _hidl_request.readInt32Vector();
                    ArrayList<Integer> nsaEnable = _hidl_request.readInt32Vector();
                    ArrayList<Integer> nsaDisable = _hidl_request.readInt32Vector();
                    setNrBandMode(serial228, saEnable, saDisable, nsaEnable, nsaDisable);
                    return;
                case 256067662:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    ArrayList<String> _hidl_out_descriptors = interfaceChain();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStringVector(_hidl_out_descriptors);
                    _hidl_reply.send();
                    return;
                case 256131655:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    NativeHandle fd = _hidl_request.readNativeHandle();
                    ArrayList<String> options = _hidl_request.readStringVector();
                    debug(fd, options);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 256136003:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    String _hidl_out_descriptor = interfaceDescriptor();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeString(_hidl_out_descriptor);
                    _hidl_reply.send();
                    return;
                case 256398152:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    ArrayList<byte[]> _hidl_out_hashchain = getHashChain();
                    _hidl_reply.writeStatus(0);
                    HwBlob _hidl_blob = new HwBlob(16);
                    int _hidl_vec_size = _hidl_out_hashchain.size();
                    _hidl_blob.putInt32(8L, _hidl_vec_size);
                    _hidl_blob.putBool(12L, false);
                    HwBlob childBlob = new HwBlob(_hidl_vec_size * 32);
                    for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                        long _hidl_array_offset_1 = _hidl_index_0 * 32;
                        byte[] _hidl_array_item_1 = _hidl_out_hashchain.get(_hidl_index_0);
                        if (_hidl_array_item_1 == null || _hidl_array_item_1.length != 32) {
                            throw new IllegalArgumentException("Array element is not of the expected length");
                        }
                        childBlob.putInt8Array(_hidl_array_offset_1, _hidl_array_item_1);
                    }
                    _hidl_blob.putBlob(0L, childBlob);
                    _hidl_reply.writeBuffer(_hidl_blob);
                    _hidl_reply.send();
                    return;
                case 256462420:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    setHALInstrumentation();
                    return;
                case 256660548:
                default:
                    return;
                case 256921159:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    ping();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 257049926:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    DebugInfo _hidl_out_info = getDebugInfo();
                    _hidl_reply.writeStatus(0);
                    _hidl_out_info.writeToParcel(_hidl_reply);
                    _hidl_reply.send();
                    return;
                case 257120595:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    notifySyspropsChanged();
                    return;
            }
        }
    }
}
