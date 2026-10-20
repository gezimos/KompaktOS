package vendor.mediatek.hardware.mtkradioex.V3_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class Tft {
    public int operation = 0;
    public int pfNumber = 0;
    public ArrayList<PktFilter> pfList = new ArrayList<>();
    public TftParameter tftParameter = new TftParameter();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != Tft.class) {
            return false;
        }
        Tft other = (Tft) otherObject;
        if (this.operation == other.operation && this.pfNumber == other.pfNumber && HidlSupport.deepEquals(this.pfList, other.pfList) && HidlSupport.deepEquals(this.tftParameter, other.tftParameter)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.operation))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.pfNumber))), Integer.valueOf(HidlSupport.deepHashCode(this.pfList)), Integer.valueOf(HidlSupport.deepHashCode(this.tftParameter)));
    }

    public final String toString() {
        return "{.operation = " + this.operation + ", .pfNumber = " + this.pfNumber + ", .pfList = " + this.pfList + ", .tftParameter = " + this.tftParameter + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(48L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<Tft> readVectorFromParcel(HwParcel parcel) {
        ArrayList<Tft> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 48, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            Tft _hidl_vec_element = new Tft();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 48);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.operation = _hidl_blob.getInt32(_hidl_offset + 0);
        this.pfNumber = _hidl_blob.getInt32(_hidl_offset + 4);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 8 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 96, _hidl_blob.handle(), _hidl_offset + 8 + 0, true);
        this.pfList.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            PktFilter _hidl_vec_element = new PktFilter();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 96);
            this.pfList.add(_hidl_vec_element);
        }
        this.tftParameter.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 24);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(48);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<Tft> _hidl_vec) {
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
        _hidl_blob.putInt32(_hidl_offset + 0, this.operation);
        _hidl_blob.putInt32(4 + _hidl_offset, this.pfNumber);
        int _hidl_vec_size = this.pfList.size();
        _hidl_blob.putInt32(_hidl_offset + 8 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 8 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 96);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            this.pfList.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 96);
        }
        _hidl_blob.putBlob(8 + _hidl_offset + 0, childBlob);
        this.tftParameter.writeEmbeddedToBlob(_hidl_blob, 24 + _hidl_offset);
    }
}
