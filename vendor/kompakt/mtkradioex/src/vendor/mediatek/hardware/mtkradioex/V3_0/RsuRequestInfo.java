package vendor.mediatek.hardware.mtkradioex.V3_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class RsuRequestInfo {
    public int opId = 0;
    public int requestId = 0;
    public int requestType = 0;
    public String data = new String();
    public int reserveInt1 = 0;
    public int reserveInt2 = 0;
    public String reserveString1 = new String();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != RsuRequestInfo.class) {
            return false;
        }
        RsuRequestInfo other = (RsuRequestInfo) otherObject;
        if (this.opId == other.opId && this.requestId == other.requestId && this.requestType == other.requestType && HidlSupport.deepEquals(this.data, other.data) && this.reserveInt1 == other.reserveInt1 && this.reserveInt2 == other.reserveInt2 && HidlSupport.deepEquals(this.reserveString1, other.reserveString1)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.opId))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.requestId))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.requestType))), Integer.valueOf(HidlSupport.deepHashCode(this.data)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.reserveInt1))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.reserveInt2))), Integer.valueOf(HidlSupport.deepHashCode(this.reserveString1)));
    }

    public final String toString() {
        return "{.opId = " + this.opId + ", .requestId = " + this.requestId + ", .requestType = " + this.requestType + ", .data = " + this.data + ", .reserveInt1 = " + this.reserveInt1 + ", .reserveInt2 = " + this.reserveInt2 + ", .reserveString1 = " + this.reserveString1 + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(56L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<RsuRequestInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<RsuRequestInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 56, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            RsuRequestInfo _hidl_vec_element = new RsuRequestInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 56);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.opId = _hidl_blob.getInt32(_hidl_offset + 0);
        this.requestId = _hidl_blob.getInt32(_hidl_offset + 4);
        this.requestType = _hidl_blob.getInt32(_hidl_offset + 8);
        String string = _hidl_blob.getString(_hidl_offset + 16);
        this.data = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 16 + 0, false);
        this.reserveInt1 = _hidl_blob.getInt32(_hidl_offset + 32);
        this.reserveInt2 = _hidl_blob.getInt32(_hidl_offset + 36);
        String string2 = _hidl_blob.getString(_hidl_offset + 40);
        this.reserveString1 = string2;
        parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 40 + 0, false);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(56);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<RsuRequestInfo> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 56);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 56);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(0 + _hidl_offset, this.opId);
        _hidl_blob.putInt32(4 + _hidl_offset, this.requestId);
        _hidl_blob.putInt32(8 + _hidl_offset, this.requestType);
        _hidl_blob.putString(16 + _hidl_offset, this.data);
        _hidl_blob.putInt32(32 + _hidl_offset, this.reserveInt1);
        _hidl_blob.putInt32(36 + _hidl_offset, this.reserveInt2);
        _hidl_blob.putString(40 + _hidl_offset, this.reserveString1);
    }
}
