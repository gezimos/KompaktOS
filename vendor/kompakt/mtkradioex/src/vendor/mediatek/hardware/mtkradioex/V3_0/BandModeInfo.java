package vendor.mediatek.hardware.mtkradioex.V3_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class BandModeInfo {
    public int gsm = 0;
    public int umts = 0;
    public ArrayList<Integer> lte = new ArrayList<>();
    public ArrayList<Integer> sa = new ArrayList<>();
    public ArrayList<Integer> nsa = new ArrayList<>();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != BandModeInfo.class) {
            return false;
        }
        BandModeInfo other = (BandModeInfo) otherObject;
        if (this.gsm == other.gsm && this.umts == other.umts && HidlSupport.deepEquals(this.lte, other.lte) && HidlSupport.deepEquals(this.sa, other.sa) && HidlSupport.deepEquals(this.nsa, other.nsa)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.gsm))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.umts))), Integer.valueOf(HidlSupport.deepHashCode(this.lte)), Integer.valueOf(HidlSupport.deepHashCode(this.sa)), Integer.valueOf(HidlSupport.deepHashCode(this.nsa)));
    }

    public final String toString() {
        return "{.gsm = " + this.gsm + ", .umts = " + this.umts + ", .lte = " + this.lte + ", .sa = " + this.sa + ", .nsa = " + this.nsa + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(56L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<BandModeInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<BandModeInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 56, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            BandModeInfo _hidl_vec_element = new BandModeInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 56);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.gsm = _hidl_blob.getInt32(_hidl_offset + 0);
        this.umts = _hidl_blob.getInt32(_hidl_offset + 4);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 8 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 4, _hidl_blob.handle(), _hidl_offset + 8 + 0, true);
        this.lte.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            int _hidl_vec_element = childBlob.getInt32(_hidl_index_0 * 4);
            this.lte.add(Integer.valueOf(_hidl_vec_element));
        }
        int _hidl_vec_size2 = _hidl_blob.getInt32(_hidl_offset + 24 + 8);
        HwBlob childBlob2 = parcel.readEmbeddedBuffer(_hidl_vec_size2 * 4, _hidl_blob.handle(), _hidl_offset + 24 + 0, true);
        this.sa.clear();
        for (int _hidl_index_1 = 0; _hidl_index_1 < _hidl_vec_size2; _hidl_index_1++) {
            int _hidl_vec_element2 = childBlob2.getInt32(_hidl_index_1 * 4);
            this.sa.add(Integer.valueOf(_hidl_vec_element2));
        }
        int _hidl_vec_size3 = _hidl_blob.getInt32(_hidl_offset + 40 + 8);
        HwBlob childBlob3 = parcel.readEmbeddedBuffer(_hidl_vec_size3 * 4, _hidl_blob.handle(), _hidl_offset + 40 + 0, true);
        this.nsa.clear();
        for (int _hidl_index_2 = 0; _hidl_index_2 < _hidl_vec_size3; _hidl_index_2++) {
            int _hidl_vec_element3 = childBlob3.getInt32(_hidl_index_2 * 4);
            this.nsa.add(Integer.valueOf(_hidl_vec_element3));
        }
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(56);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<BandModeInfo> _hidl_vec) {
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
        _hidl_blob.putInt32(_hidl_offset + 0, this.gsm);
        _hidl_blob.putInt32(_hidl_offset + 4, this.umts);
        int _hidl_vec_size = this.lte.size();
        _hidl_blob.putInt32(_hidl_offset + 8 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 8 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 4);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            childBlob.putInt32(_hidl_index_0 * 4, this.lte.get(_hidl_index_0).intValue());
        }
        _hidl_blob.putBlob(_hidl_offset + 8 + 0, childBlob);
        int _hidl_vec_size2 = this.sa.size();
        _hidl_blob.putInt32(_hidl_offset + 24 + 8, _hidl_vec_size2);
        _hidl_blob.putBool(_hidl_offset + 24 + 12, false);
        HwBlob childBlob2 = new HwBlob(_hidl_vec_size2 * 4);
        for (int _hidl_index_1 = 0; _hidl_index_1 < _hidl_vec_size2; _hidl_index_1++) {
            childBlob2.putInt32(_hidl_index_1 * 4, this.sa.get(_hidl_index_1).intValue());
        }
        _hidl_blob.putBlob(_hidl_offset + 24 + 0, childBlob2);
        int _hidl_vec_size3 = this.nsa.size();
        _hidl_blob.putInt32(_hidl_offset + 40 + 8, _hidl_vec_size3);
        _hidl_blob.putBool(_hidl_offset + 40 + 12, false);
        HwBlob childBlob3 = new HwBlob(_hidl_vec_size3 * 4);
        for (int _hidl_index_2 = 0; _hidl_index_2 < _hidl_vec_size3; _hidl_index_2++) {
            childBlob3.putInt32(_hidl_index_2 * 4, this.nsa.get(_hidl_index_2).intValue());
        }
        _hidl_blob.putBlob(_hidl_offset + 40 + 0, childBlob3);
    }
}
