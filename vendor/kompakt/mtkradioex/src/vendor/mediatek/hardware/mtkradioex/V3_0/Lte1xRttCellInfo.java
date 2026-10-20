package vendor.mediatek.hardware.mtkradioex.V3_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class Lte1xRttCellInfo {
    public int sid = 0;
    public int nid = 0;
    public int bsid = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != Lte1xRttCellInfo.class) {
            return false;
        }
        Lte1xRttCellInfo other = (Lte1xRttCellInfo) otherObject;
        if (this.sid == other.sid && this.nid == other.nid && this.bsid == other.bsid) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.sid))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.nid))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.bsid))));
    }

    public final String toString() {
        return "{.sid = " + this.sid + ", .nid = " + this.nid + ", .bsid = " + this.bsid + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(12L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<Lte1xRttCellInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<Lte1xRttCellInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 12, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            Lte1xRttCellInfo _hidl_vec_element = new Lte1xRttCellInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 12);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.sid = _hidl_blob.getInt32(0 + _hidl_offset);
        this.nid = _hidl_blob.getInt32(4 + _hidl_offset);
        this.bsid = _hidl_blob.getInt32(8 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(12);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<Lte1xRttCellInfo> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 12);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 12);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(0 + _hidl_offset, this.sid);
        _hidl_blob.putInt32(4 + _hidl_offset, this.nid);
        _hidl_blob.putInt32(8 + _hidl_offset, this.bsid);
    }
}
