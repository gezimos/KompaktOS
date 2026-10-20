package vendor.mediatek.hardware.mtkradioex.V3_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class ImsConfParticipant {
    public String user_addr = new String();
    public String end_point = new String();
    public String entity = new String();
    public String display_text = new String();
    public String status = new String();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != ImsConfParticipant.class) {
            return false;
        }
        ImsConfParticipant other = (ImsConfParticipant) otherObject;
        if (HidlSupport.deepEquals(this.user_addr, other.user_addr) && HidlSupport.deepEquals(this.end_point, other.end_point) && HidlSupport.deepEquals(this.entity, other.entity) && HidlSupport.deepEquals(this.display_text, other.display_text) && HidlSupport.deepEquals(this.status, other.status)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.user_addr)), Integer.valueOf(HidlSupport.deepHashCode(this.end_point)), Integer.valueOf(HidlSupport.deepHashCode(this.entity)), Integer.valueOf(HidlSupport.deepHashCode(this.display_text)), Integer.valueOf(HidlSupport.deepHashCode(this.status)));
    }

    public final String toString() {
        return "{.user_addr = " + this.user_addr + ", .end_point = " + this.end_point + ", .entity = " + this.entity + ", .display_text = " + this.display_text + ", .status = " + this.status + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(80L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<ImsConfParticipant> readVectorFromParcel(HwParcel parcel) {
        ArrayList<ImsConfParticipant> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 80, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            ImsConfParticipant _hidl_vec_element = new ImsConfParticipant();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 80);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        String string = _hidl_blob.getString(_hidl_offset + 0);
        this.user_addr = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 0 + 0, false);
        String string2 = _hidl_blob.getString(_hidl_offset + 16);
        this.end_point = string2;
        parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 16 + 0, false);
        String string3 = _hidl_blob.getString(_hidl_offset + 32);
        this.entity = string3;
        parcel.readEmbeddedBuffer(string3.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 32 + 0, false);
        String string4 = _hidl_blob.getString(_hidl_offset + 48);
        this.display_text = string4;
        parcel.readEmbeddedBuffer(string4.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 48 + 0, false);
        String string5 = _hidl_blob.getString(_hidl_offset + 64);
        this.status = string5;
        parcel.readEmbeddedBuffer(string5.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 64 + 0, false);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(80);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<ImsConfParticipant> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 80);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 80);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putString(0 + _hidl_offset, this.user_addr);
        _hidl_blob.putString(16 + _hidl_offset, this.end_point);
        _hidl_blob.putString(32 + _hidl_offset, this.entity);
        _hidl_blob.putString(48 + _hidl_offset, this.display_text);
        _hidl_blob.putString(64 + _hidl_offset, this.status);
    }
}
