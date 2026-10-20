package vendor.mediatek.hardware.mtkradioex.V3_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class Dialog {
    public int dialogId = 0;
    public int callState = 0;
    public int callType = 0;
    public boolean isPullable = false;
    public boolean isCallHeld = false;
    public boolean isMt = false;
    public String address = new String();
    public String remoteAddress = new String();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != Dialog.class) {
            return false;
        }
        Dialog other = (Dialog) otherObject;
        if (this.dialogId == other.dialogId && this.callState == other.callState && this.callType == other.callType && this.isPullable == other.isPullable && this.isCallHeld == other.isCallHeld && this.isMt == other.isMt && HidlSupport.deepEquals(this.address, other.address) && HidlSupport.deepEquals(this.remoteAddress, other.remoteAddress)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.dialogId))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.callState))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.callType))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isPullable))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isCallHeld))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isMt))), Integer.valueOf(HidlSupport.deepHashCode(this.address)), Integer.valueOf(HidlSupport.deepHashCode(this.remoteAddress)));
    }

    public final String toString() {
        return "{.dialogId = " + this.dialogId + ", .callState = " + this.callState + ", .callType = " + this.callType + ", .isPullable = " + this.isPullable + ", .isCallHeld = " + this.isCallHeld + ", .isMt = " + this.isMt + ", .address = " + this.address + ", .remoteAddress = " + this.remoteAddress + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(48L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<Dialog> readVectorFromParcel(HwParcel parcel) {
        ArrayList<Dialog> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 48, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            Dialog _hidl_vec_element = new Dialog();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 48);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.dialogId = _hidl_blob.getInt32(_hidl_offset + 0);
        this.callState = _hidl_blob.getInt32(_hidl_offset + 4);
        this.callType = _hidl_blob.getInt32(_hidl_offset + 8);
        this.isPullable = _hidl_blob.getBool(_hidl_offset + 12);
        this.isCallHeld = _hidl_blob.getBool(_hidl_offset + 13);
        this.isMt = _hidl_blob.getBool(_hidl_offset + 14);
        String string = _hidl_blob.getString(_hidl_offset + 16);
        this.address = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 16 + 0, false);
        String string2 = _hidl_blob.getString(_hidl_offset + 32);
        this.remoteAddress = string2;
        parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 32 + 0, false);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(48);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<Dialog> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 48);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 48);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(0 + _hidl_offset, this.dialogId);
        _hidl_blob.putInt32(4 + _hidl_offset, this.callState);
        _hidl_blob.putInt32(8 + _hidl_offset, this.callType);
        _hidl_blob.putBool(12 + _hidl_offset, this.isPullable);
        _hidl_blob.putBool(13 + _hidl_offset, this.isCallHeld);
        _hidl_blob.putBool(14 + _hidl_offset, this.isMt);
        _hidl_blob.putString(16 + _hidl_offset, this.address);
        _hidl_blob.putString(32 + _hidl_offset, this.remoteAddress);
    }
}
