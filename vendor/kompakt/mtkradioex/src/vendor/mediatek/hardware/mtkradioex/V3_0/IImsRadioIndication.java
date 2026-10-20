package vendor.mediatek.hardware.mtkradioex.V3_0;

import android.hardware.radio.V1_0.CdmaSmsMessage;
import android.hardware.radio.V1_0.SuppSvcNotification;
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
public interface IImsRadioIndication extends IBase {
    public static final String kInterfaceName = "vendor.mediatek.hardware.mtkradioex@3.0::IImsRadioIndication";

    @Override // android.hidl.base.V1_0.IBase
    IHwBinder asBinder();

    void audioIndication(int i, int i2, int i3) throws RemoteException;

    void callAdditionalInfoInd(int i, int i2, ArrayList<String> arrayList) throws RemoteException;

    void callInfoIndication(int i, ArrayList<String> arrayList) throws RemoteException;

    void callRatIndication(int i, int i2, int i3) throws RemoteException;

    void callmodChangeIndicator(int i, String str, String str2, String str3, String str4, String str5) throws RemoteException;

    void cdmaNewSmsEx(int i, CdmaSmsMessage cdmaSmsMessage) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    void econfResultIndication(int i, String str, String str2, String str3, String str4, String str5, String str6) throws RemoteException;

    void ectIndication(int i, int i2, int i3, int i4) throws RemoteException;

    void eregrtInfoInd(int i, ArrayList<Integer> arrayList) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    void getProvisionDone(int i, String str, String str2) throws RemoteException;

    void imsBearerInit(int i) throws RemoteException;

    void imsBearerStateNotify(int i, int i2, int i3, String str) throws RemoteException;

    void imsCfgConfigChanged(int i, int i2, String str, String str2) throws RemoteException;

    void imsCfgConfigLoaded(int i) throws RemoteException;

    void imsCfgDynamicImsSwitchComplete(int i) throws RemoteException;

    void imsCfgFeatureChanged(int i, int i2, int i3, int i4) throws RemoteException;

    void imsConferenceInfoIndication(int i, ArrayList<ImsConfParticipant> arrayList) throws RemoteException;

    void imsDataInfoNotify(int i, String str, String str2, String str3) throws RemoteException;

    void imsDeregDone(int i) throws RemoteException;

    void imsDialogIndication(int i, ArrayList<Dialog> arrayList) throws RemoteException;

    void imsDisableDone(int i) throws RemoteException;

    void imsDisableStart(int i) throws RemoteException;

    void imsEnableDone(int i) throws RemoteException;

    void imsEnableStart(int i) throws RemoteException;

    void imsEventPackageIndication(int i, String str, String str2, String str3, String str4, String str5) throws RemoteException;

    void imsRadioInfoChange(int i, String str, String str2) throws RemoteException;

    void imsRedialEmergencyIndication(int i, String str) throws RemoteException;

    void imsRegFlagInd(int i, int i2) throws RemoteException;

    void imsRegInfoInd(int i, ArrayList<Integer> arrayList) throws RemoteException;

    void imsRegStatusReport(int i, ImsRegStatusInfo imsRegStatusInfo) throws RemoteException;

    void imsRegistrationInfo(int i, int i2, int i3) throws RemoteException;

    void imsRtpInfo(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7) throws RemoteException;

    void imsSupportEcc(int i, int i2) throws RemoteException;

    void incomingCallIndication(int i, IncomingCallNotification incomingCallNotification) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    void lteMessageWaitingIndication(int i, String str, String str2, String str3, String str4, String str5) throws RemoteException;

    void newSmsEx(int i, ArrayList<Byte> arrayList) throws RemoteException;

    void newSmsStatusReportEx(int i, ArrayList<Byte> arrayList) throws RemoteException;

    void noEmergencyCallbackMode(int i) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    void onMDInternetUsageInd(int i, ArrayList<Integer> arrayList) throws RemoteException;

    void onSsacStatus(int i, ArrayList<Integer> arrayList) throws RemoteException;

    void onUssi(int i, int i2, String str) throws RemoteException;

    void onVolteSubscription(int i, int i2) throws RemoteException;

    void onXui(int i, String str, String str2, String str3) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    void rttCapabilityIndication(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException;

    void rttModifyRequestReceive(int i, int i2, int i3) throws RemoteException;

    void rttModifyResponse(int i, int i2, int i3) throws RemoteException;

    void rttTextReceive(int i, int i2, int i3, String str) throws RemoteException;

    void sendVopsIndication(int i, int i2) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    void sipCallProgressIndicator(int i, String str, String str2, String str3, String str4, String str5, String str6) throws RemoteException;

    void sipHeaderReport(int i, ArrayList<String> arrayList) throws RemoteException;

    void sipRegInfoInd(int i, int i2, int i3, ArrayList<String> arrayList) throws RemoteException;

    void speechCodecInfoIndication(int i, int i2) throws RemoteException;

    void suppSvcNotify(int i, SuppSvcNotification suppSvcNotification) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    void videoCapabilityIndicator(int i, String str, String str2, String str3) throws RemoteException;

    void videoRingtoneEventInd(int i, ArrayList<String> arrayList) throws RemoteException;

    void volteSetting(int i, boolean z) throws RemoteException;

    static IImsRadioIndication asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IImsRadioIndication)) {
            return (IImsRadioIndication) iface;
        }
        IImsRadioIndication proxy = new Proxy(binder);
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

    static IImsRadioIndication castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IImsRadioIndication getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IImsRadioIndication getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IImsRadioIndication getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IImsRadioIndication getService() throws RemoteException {
        return getService("default");
    }

    public static final class Proxy implements IImsRadioIndication {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            Objects.requireNonNull(remote);
            this.mRemote = remote;
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of vendor.mediatek.hardware.mtkradioex@3.0::IImsRadioIndication]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void incomingCallIndication(int type, IncomingCallNotification inCallNotify) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            inCallNotify.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void callInfoIndication(int type, ArrayList<String> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeStringVector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(2, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void econfResultIndication(int type, String confCallId, String op, String num, String result, String cause, String joinedCallId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(confCallId);
            _hidl_request.writeString(op);
            _hidl_request.writeString(num);
            _hidl_request.writeString(result);
            _hidl_request.writeString(cause);
            _hidl_request.writeString(joinedCallId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(3, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void sipCallProgressIndicator(int type, String callId, String dir, String sipMsgType, String method, String responseCode, String reasonText) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(callId);
            _hidl_request.writeString(dir);
            _hidl_request.writeString(sipMsgType);
            _hidl_request.writeString(method);
            _hidl_request.writeString(responseCode);
            _hidl_request.writeString(reasonText);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(4, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void callmodChangeIndicator(int type, String callId, String callMode, String videoState, String audioDirection, String pau) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(callId);
            _hidl_request.writeString(callMode);
            _hidl_request.writeString(videoState);
            _hidl_request.writeString(audioDirection);
            _hidl_request.writeString(pau);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(5, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void videoCapabilityIndicator(int type, String callId, String localVideoCap, String remoteVideoCap) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(callId);
            _hidl_request.writeString(localVideoCap);
            _hidl_request.writeString(remoteVideoCap);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(6, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void onUssi(int type, int modeType, String msg) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(modeType);
            _hidl_request.writeString(msg);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(7, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void getProvisionDone(int type, String result1, String result2) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(result1);
            _hidl_request.writeString(result2);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(8, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void onXui(int type, String accountId, String broadcastFlag, String xuiInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(accountId);
            _hidl_request.writeString(broadcastFlag);
            _hidl_request.writeString(xuiInfo);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(9, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void onVolteSubscription(int type, int status) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(status);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(10, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void suppSvcNotify(int type, SuppSvcNotification suppSvc) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            suppSvc.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(11, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsEventPackageIndication(int type, String callId, String ptype, String urcIdx, String totalUrcCount, String rawData) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(callId);
            _hidl_request.writeString(ptype);
            _hidl_request.writeString(urcIdx);
            _hidl_request.writeString(totalUrcCount);
            _hidl_request.writeString(rawData);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(12, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsRegistrationInfo(int type, int registerState, int capability) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(registerState);
            _hidl_request.writeInt32(capability);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(13, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsEnableDone(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(14, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsDisableDone(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(15, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsEnableStart(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(16, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsDisableStart(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(17, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void ectIndication(int type, int call_id, int ectResult, int cause) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(call_id);
            _hidl_request.writeInt32(ectResult);
            _hidl_request.writeInt32(cause);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(18, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void volteSetting(int type, boolean isEnable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeBool(isEnable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(19, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsBearerStateNotify(int type, int aid, int action, String capability) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(aid);
            _hidl_request.writeInt32(action);
            _hidl_request.writeString(capability);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(20, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsBearerInit(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(21, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsDeregDone(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(22, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsSupportEcc(int type, int supportLteEcc) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(supportLteEcc);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(23, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsRadioInfoChange(int type, String iid, String info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(iid);
            _hidl_request.writeString(info);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(24, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void speechCodecInfoIndication(int type, int info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(info);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(25, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsConferenceInfoIndication(int type, ArrayList<ImsConfParticipant> participants) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            ImsConfParticipant.writeVectorToParcel(_hidl_request, participants);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(26, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void lteMessageWaitingIndication(int type, String callId, String ptype, String urcIdx, String totalUrcCount, String rawData) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(callId);
            _hidl_request.writeString(ptype);
            _hidl_request.writeString(urcIdx);
            _hidl_request.writeString(totalUrcCount);
            _hidl_request.writeString(rawData);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(27, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsDialogIndication(int type, ArrayList<Dialog> dialogList) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            Dialog.writeVectorToParcel(_hidl_request, dialogList);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(28, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsCfgDynamicImsSwitchComplete(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(29, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsCfgFeatureChanged(int type, int phoneId, int featureId, int value) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(phoneId);
            _hidl_request.writeInt32(featureId);
            _hidl_request.writeInt32(value);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(30, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsCfgConfigChanged(int type, int phoneId, String configId, String value) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(phoneId);
            _hidl_request.writeString(configId);
            _hidl_request.writeString(value);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(31, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsCfgConfigLoaded(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(32, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsDataInfoNotify(int type, String capability, String event, String extra) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(capability);
            _hidl_request.writeString(event);
            _hidl_request.writeString(extra);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(33, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void newSmsEx(int type, ArrayList<Byte> pdu) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt8Vector(pdu);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(34, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void newSmsStatusReportEx(int type, ArrayList<Byte> pdu) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt8Vector(pdu);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(35, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void cdmaNewSmsEx(int type, CdmaSmsMessage msg) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            msg.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(36, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void noEmergencyCallbackMode(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(37, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsRedialEmergencyIndication(int type, String callId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(callId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(38, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsRtpInfo(int type, String pdnId, String networkId, String timer, String sendPktLost, String recvPktLost, String jitter, String delay) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(pdnId);
            _hidl_request.writeString(networkId);
            _hidl_request.writeString(timer);
            _hidl_request.writeString(sendPktLost);
            _hidl_request.writeString(recvPktLost);
            _hidl_request.writeString(jitter);
            _hidl_request.writeString(delay);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(39, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void rttCapabilityIndication(int type, int callId, int localCap, int remoteCap, int localStatus, int remoteStatus) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(callId);
            _hidl_request.writeInt32(localCap);
            _hidl_request.writeInt32(remoteCap);
            _hidl_request.writeInt32(localStatus);
            _hidl_request.writeInt32(remoteStatus);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(40, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void rttModifyResponse(int type, int callId, int result) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(callId);
            _hidl_request.writeInt32(result);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(41, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void rttTextReceive(int type, int callId, int lenOfString, String text) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(callId);
            _hidl_request.writeInt32(lenOfString);
            _hidl_request.writeString(text);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(42, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void rttModifyRequestReceive(int type, int callId, int rttType) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(callId);
            _hidl_request.writeInt32(rttType);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(43, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void audioIndication(int type, int callId, int audio) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(callId);
            _hidl_request.writeInt32(audio);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(44, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void sendVopsIndication(int type, int vops) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(vops);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(45, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void callAdditionalInfoInd(int type, int ciType, ArrayList<String> info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(ciType);
            _hidl_request.writeStringVector(info);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(46, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void sipHeaderReport(int type, ArrayList<String> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeStringVector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(47, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void callRatIndication(int type, int domain, int rat) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(domain);
            _hidl_request.writeInt32(rat);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(48, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void sipRegInfoInd(int type, int account_id, int response_code, ArrayList<String> info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(account_id);
            _hidl_request.writeInt32(response_code);
            _hidl_request.writeStringVector(info);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(49, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsRegStatusReport(int type, ImsRegStatusInfo report) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            report.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(50, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsRegInfoInd(int type, ArrayList<Integer> info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32Vector(info);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(51, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void onSsacStatus(int type, ArrayList<Integer> status) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32Vector(status);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(52, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void eregrtInfoInd(int type, ArrayList<Integer> info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32Vector(info);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(53, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void videoRingtoneEventInd(int type, ArrayList<String> event) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeStringVector(event);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(54, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void onMDInternetUsageInd(int type, ArrayList<Integer> info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32Vector(info);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(55, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication
        public void imsRegFlagInd(int type, int flag) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IImsRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(flag);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(56, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
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

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
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

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
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

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
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

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
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

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
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

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
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

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
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

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    public static abstract class Stub extends HwBinder implements IImsRadioIndication {
        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this;
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IImsRadioIndication.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IImsRadioIndication.kInterfaceName;
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{-71, -63, -20, 4, -106, 55, -102, -35, -114, -111, 88, -10, -112, 62, 79, -124, -102, -19, 15, -103, -101, -103, 82, 47, -19, 23, 83, -37, -26, -52, -122, 35}, new byte[]{-20, 127, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // vendor.mediatek.hardware.mtkradioex.V3_0.IImsRadioIndication, android.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        public IHwInterface queryLocalInterface(String descriptor) {
            if (IImsRadioIndication.kInterfaceName.equals(descriptor)) {
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
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type = _hidl_request.readInt32();
                    IncomingCallNotification inCallNotify = new IncomingCallNotification();
                    inCallNotify.readFromParcel(_hidl_request);
                    incomingCallIndication(type, inCallNotify);
                    return;
                case 2:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type2 = _hidl_request.readInt32();
                    ArrayList<String> data = _hidl_request.readStringVector();
                    callInfoIndication(type2, data);
                    return;
                case 3:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type3 = _hidl_request.readInt32();
                    String confCallId = _hidl_request.readString();
                    String op = _hidl_request.readString();
                    String num = _hidl_request.readString();
                    String result = _hidl_request.readString();
                    String cause = _hidl_request.readString();
                    String joinedCallId = _hidl_request.readString();
                    econfResultIndication(type3, confCallId, op, num, result, cause, joinedCallId);
                    return;
                case 4:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type4 = _hidl_request.readInt32();
                    String callId = _hidl_request.readString();
                    String dir = _hidl_request.readString();
                    String sipMsgType = _hidl_request.readString();
                    String method = _hidl_request.readString();
                    String responseCode = _hidl_request.readString();
                    String reasonText = _hidl_request.readString();
                    sipCallProgressIndicator(type4, callId, dir, sipMsgType, method, responseCode, reasonText);
                    return;
                case 5:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type5 = _hidl_request.readInt32();
                    String callId2 = _hidl_request.readString();
                    String callMode = _hidl_request.readString();
                    String videoState = _hidl_request.readString();
                    String audioDirection = _hidl_request.readString();
                    String pau = _hidl_request.readString();
                    callmodChangeIndicator(type5, callId2, callMode, videoState, audioDirection, pau);
                    return;
                case 6:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type6 = _hidl_request.readInt32();
                    String callId3 = _hidl_request.readString();
                    String localVideoCap = _hidl_request.readString();
                    String remoteVideoCap = _hidl_request.readString();
                    videoCapabilityIndicator(type6, callId3, localVideoCap, remoteVideoCap);
                    return;
                case 7:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type7 = _hidl_request.readInt32();
                    int modeType = _hidl_request.readInt32();
                    onUssi(type7, modeType, _hidl_request.readString());
                    return;
                case 8:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type8 = _hidl_request.readInt32();
                    String result1 = _hidl_request.readString();
                    String result2 = _hidl_request.readString();
                    getProvisionDone(type8, result1, result2);
                    return;
                case 9:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type9 = _hidl_request.readInt32();
                    String accountId = _hidl_request.readString();
                    String broadcastFlag = _hidl_request.readString();
                    String xuiInfo = _hidl_request.readString();
                    onXui(type9, accountId, broadcastFlag, xuiInfo);
                    return;
                case 10:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type10 = _hidl_request.readInt32();
                    int status = _hidl_request.readInt32();
                    onVolteSubscription(type10, status);
                    return;
                case 11:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type11 = _hidl_request.readInt32();
                    SuppSvcNotification suppSvc = new SuppSvcNotification();
                    suppSvc.readFromParcel(_hidl_request);
                    suppSvcNotify(type11, suppSvc);
                    return;
                case 12:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type12 = _hidl_request.readInt32();
                    String callId4 = _hidl_request.readString();
                    String ptype = _hidl_request.readString();
                    String urcIdx = _hidl_request.readString();
                    String totalUrcCount = _hidl_request.readString();
                    String rawData = _hidl_request.readString();
                    imsEventPackageIndication(type12, callId4, ptype, urcIdx, totalUrcCount, rawData);
                    return;
                case 13:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type13 = _hidl_request.readInt32();
                    int registerState = _hidl_request.readInt32();
                    int capability = _hidl_request.readInt32();
                    imsRegistrationInfo(type13, registerState, capability);
                    return;
                case 14:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type14 = _hidl_request.readInt32();
                    imsEnableDone(type14);
                    return;
                case 15:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type15 = _hidl_request.readInt32();
                    imsDisableDone(type15);
                    return;
                case 16:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type16 = _hidl_request.readInt32();
                    imsEnableStart(type16);
                    return;
                case 17:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type17 = _hidl_request.readInt32();
                    imsDisableStart(type17);
                    return;
                case 18:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type18 = _hidl_request.readInt32();
                    int call_id = _hidl_request.readInt32();
                    int ectResult = _hidl_request.readInt32();
                    int cause2 = _hidl_request.readInt32();
                    ectIndication(type18, call_id, ectResult, cause2);
                    return;
                case 19:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type19 = _hidl_request.readInt32();
                    boolean isEnable = _hidl_request.readBool();
                    volteSetting(type19, isEnable);
                    return;
                case 20:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type20 = _hidl_request.readInt32();
                    int aid = _hidl_request.readInt32();
                    int action = _hidl_request.readInt32();
                    String capability2 = _hidl_request.readString();
                    imsBearerStateNotify(type20, aid, action, capability2);
                    return;
                case 21:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type21 = _hidl_request.readInt32();
                    imsBearerInit(type21);
                    return;
                case 22:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type22 = _hidl_request.readInt32();
                    imsDeregDone(type22);
                    return;
                case 23:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type23 = _hidl_request.readInt32();
                    int supportLteEcc = _hidl_request.readInt32();
                    imsSupportEcc(type23, supportLteEcc);
                    return;
                case 24:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type24 = _hidl_request.readInt32();
                    String iid = _hidl_request.readString();
                    String info = _hidl_request.readString();
                    imsRadioInfoChange(type24, iid, info);
                    return;
                case 25:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type25 = _hidl_request.readInt32();
                    int info2 = _hidl_request.readInt32();
                    speechCodecInfoIndication(type25, info2);
                    return;
                case 26:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type26 = _hidl_request.readInt32();
                    ArrayList<ImsConfParticipant> participants = ImsConfParticipant.readVectorFromParcel(_hidl_request);
                    imsConferenceInfoIndication(type26, participants);
                    return;
                case 27:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type27 = _hidl_request.readInt32();
                    String callId5 = _hidl_request.readString();
                    String ptype2 = _hidl_request.readString();
                    String urcIdx2 = _hidl_request.readString();
                    String totalUrcCount2 = _hidl_request.readString();
                    String rawData2 = _hidl_request.readString();
                    lteMessageWaitingIndication(type27, callId5, ptype2, urcIdx2, totalUrcCount2, rawData2);
                    return;
                case 28:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type28 = _hidl_request.readInt32();
                    ArrayList<Dialog> dialogList = Dialog.readVectorFromParcel(_hidl_request);
                    imsDialogIndication(type28, dialogList);
                    return;
                case 29:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type29 = _hidl_request.readInt32();
                    imsCfgDynamicImsSwitchComplete(type29);
                    return;
                case 30:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type30 = _hidl_request.readInt32();
                    int phoneId = _hidl_request.readInt32();
                    int featureId = _hidl_request.readInt32();
                    int value = _hidl_request.readInt32();
                    imsCfgFeatureChanged(type30, phoneId, featureId, value);
                    return;
                case 31:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type31 = _hidl_request.readInt32();
                    int phoneId2 = _hidl_request.readInt32();
                    String configId = _hidl_request.readString();
                    String value2 = _hidl_request.readString();
                    imsCfgConfigChanged(type31, phoneId2, configId, value2);
                    return;
                case 32:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type32 = _hidl_request.readInt32();
                    imsCfgConfigLoaded(type32);
                    return;
                case 33:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type33 = _hidl_request.readInt32();
                    String capability3 = _hidl_request.readString();
                    String event = _hidl_request.readString();
                    String extra = _hidl_request.readString();
                    imsDataInfoNotify(type33, capability3, event, extra);
                    return;
                case 34:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type34 = _hidl_request.readInt32();
                    ArrayList<Byte> pdu = _hidl_request.readInt8Vector();
                    newSmsEx(type34, pdu);
                    return;
                case 35:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type35 = _hidl_request.readInt32();
                    ArrayList<Byte> pdu2 = _hidl_request.readInt8Vector();
                    newSmsStatusReportEx(type35, pdu2);
                    return;
                case 36:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type36 = _hidl_request.readInt32();
                    CdmaSmsMessage msg = new CdmaSmsMessage();
                    msg.readFromParcel(_hidl_request);
                    cdmaNewSmsEx(type36, msg);
                    return;
                case 37:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type37 = _hidl_request.readInt32();
                    noEmergencyCallbackMode(type37);
                    return;
                case 38:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type38 = _hidl_request.readInt32();
                    String callId6 = _hidl_request.readString();
                    imsRedialEmergencyIndication(type38, callId6);
                    return;
                case 39:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type39 = _hidl_request.readInt32();
                    String pdnId = _hidl_request.readString();
                    String networkId = _hidl_request.readString();
                    String timer = _hidl_request.readString();
                    String sendPktLost = _hidl_request.readString();
                    String recvPktLost = _hidl_request.readString();
                    String jitter = _hidl_request.readString();
                    String delay = _hidl_request.readString();
                    imsRtpInfo(type39, pdnId, networkId, timer, sendPktLost, recvPktLost, jitter, delay);
                    return;
                case 40:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type40 = _hidl_request.readInt32();
                    int callId7 = _hidl_request.readInt32();
                    int localCap = _hidl_request.readInt32();
                    int remoteCap = _hidl_request.readInt32();
                    int localStatus = _hidl_request.readInt32();
                    int remoteStatus = _hidl_request.readInt32();
                    rttCapabilityIndication(type40, callId7, localCap, remoteCap, localStatus, remoteStatus);
                    return;
                case 41:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type41 = _hidl_request.readInt32();
                    int callId8 = _hidl_request.readInt32();
                    int result3 = _hidl_request.readInt32();
                    rttModifyResponse(type41, callId8, result3);
                    return;
                case 42:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type42 = _hidl_request.readInt32();
                    int callId9 = _hidl_request.readInt32();
                    int lenOfString = _hidl_request.readInt32();
                    String text = _hidl_request.readString();
                    rttTextReceive(type42, callId9, lenOfString, text);
                    return;
                case 43:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type43 = _hidl_request.readInt32();
                    int callId10 = _hidl_request.readInt32();
                    int rttType = _hidl_request.readInt32();
                    rttModifyRequestReceive(type43, callId10, rttType);
                    return;
                case 44:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type44 = _hidl_request.readInt32();
                    int callId11 = _hidl_request.readInt32();
                    int audio = _hidl_request.readInt32();
                    audioIndication(type44, callId11, audio);
                    return;
                case 45:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type45 = _hidl_request.readInt32();
                    int vops = _hidl_request.readInt32();
                    sendVopsIndication(type45, vops);
                    return;
                case 46:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type46 = _hidl_request.readInt32();
                    int ciType = _hidl_request.readInt32();
                    ArrayList<String> info3 = _hidl_request.readStringVector();
                    callAdditionalInfoInd(type46, ciType, info3);
                    return;
                case 47:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type47 = _hidl_request.readInt32();
                    ArrayList<String> data2 = _hidl_request.readStringVector();
                    sipHeaderReport(type47, data2);
                    return;
                case 48:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type48 = _hidl_request.readInt32();
                    int domain = _hidl_request.readInt32();
                    int rat = _hidl_request.readInt32();
                    callRatIndication(type48, domain, rat);
                    return;
                case 49:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type49 = _hidl_request.readInt32();
                    int account_id = _hidl_request.readInt32();
                    int response_code = _hidl_request.readInt32();
                    ArrayList<String> info4 = _hidl_request.readStringVector();
                    sipRegInfoInd(type49, account_id, response_code, info4);
                    return;
                case 50:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type50 = _hidl_request.readInt32();
                    ImsRegStatusInfo report = new ImsRegStatusInfo();
                    report.readFromParcel(_hidl_request);
                    imsRegStatusReport(type50, report);
                    return;
                case 51:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type51 = _hidl_request.readInt32();
                    ArrayList<Integer> info5 = _hidl_request.readInt32Vector();
                    imsRegInfoInd(type51, info5);
                    return;
                case 52:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type52 = _hidl_request.readInt32();
                    ArrayList<Integer> status2 = _hidl_request.readInt32Vector();
                    onSsacStatus(type52, status2);
                    return;
                case 53:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type53 = _hidl_request.readInt32();
                    ArrayList<Integer> info6 = _hidl_request.readInt32Vector();
                    eregrtInfoInd(type53, info6);
                    return;
                case 54:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type54 = _hidl_request.readInt32();
                    ArrayList<String> event2 = _hidl_request.readStringVector();
                    videoRingtoneEventInd(type54, event2);
                    return;
                case 55:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type55 = _hidl_request.readInt32();
                    ArrayList<Integer> info7 = _hidl_request.readInt32Vector();
                    onMDInternetUsageInd(type55, info7);
                    return;
                case 56:
                    _hidl_request.enforceInterface(IImsRadioIndication.kInterfaceName);
                    int type56 = _hidl_request.readInt32();
                    int flag = _hidl_request.readInt32();
                    imsRegFlagInd(type56, flag);
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
