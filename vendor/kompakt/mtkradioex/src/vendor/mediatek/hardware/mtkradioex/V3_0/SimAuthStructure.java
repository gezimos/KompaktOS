package vendor.mediatek.hardware.mtkradioex.V3_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class SimAuthStructure {
    public int sessionId = 0;
    public int mode = 0;
    public String param1 = new String();
    public String param2 = new String();
    public int tag = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != SimAuthStructure.class) {
            return false;
        }
        SimAuthStructure other = (SimAuthStructure) otherObject;
        if (this.sessionId == other.sessionId && this.mode == other.mode && HidlSupport.deepEquals(this.param1, other.param1) && HidlSupport.deepEquals(this.param2, other.param2) && this.tag == other.tag) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.sessionId))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.mode))), Integer.valueOf(HidlSupport.deepHashCode(this.param1)), Integer.valueOf(HidlSupport.deepHashCode(this.param2)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.tag))));
    }

    public final String toString() {
        return "{.sessionId = " + this.sessionId + ", .mode = " + this.mode + ", .param1 = " + this.param1 + ", .param2 = " + this.param2 + ", .tag = " + this.tag + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(48L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<SimAuthStructure> readVectorFromParcel(HwParcel parcel) {
        ArrayList<SimAuthStructure> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 48, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            SimAuthStructure _hidl_vec_element = new SimAuthStructure();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 48);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.sessionId = _hidl_blob.getInt32(_hidl_offset + 0);
        this.mode = _hidl_blob.getInt32(_hidl_offset + 4);
        String string = _hidl_blob.getString(_hidl_offset + 8);
        this.param1 = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 8 + 0, false);
        String string2 = _hidl_blob.getString(_hidl_offset + 24);
        this.param2 = string2;
        parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 24 + 0, false);
        this.tag = _hidl_blob.getInt32(_hidl_offset + 40);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(48);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<SimAuthStructure> _hidl_vec) {
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
        _hidl_blob.putInt32(0 + _hidl_offset, this.sessionId);
        _hidl_blob.putInt32(4 + _hidl_offset, this.mode);
        _hidl_blob.putString(8 + _hidl_offset, this.param1);
        _hidl_blob.putString(24 + _hidl_offset, this.param2);
        _hidl_blob.putInt32(40 + _hidl_offset, this.tag);
    }
}
