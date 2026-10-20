package vendor.mediatek.hardware.mtkradioex.V3_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class PhbEntryExt {
    public int index = 0;
    public String number = new String();
    public int type = 0;
    public String text = new String();
    public int hidden = 0;
    public String group = new String();
    public String adnumber = new String();
    public String adnumber_a = new String();
    public String adnumber_b = new String();
    public int adtype = 0;
    public String secondtext = new String();
    public String email = new String();
    public int secondtext_dcs = 0;
    public int email_dcs = 0;
    public int text_dcs = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != PhbEntryExt.class) {
            return false;
        }
        PhbEntryExt other = (PhbEntryExt) otherObject;
        if (this.index == other.index && HidlSupport.deepEquals(this.number, other.number) && this.type == other.type && HidlSupport.deepEquals(this.text, other.text) && this.hidden == other.hidden && HidlSupport.deepEquals(this.group, other.group) && HidlSupport.deepEquals(this.adnumber, other.adnumber) && HidlSupport.deepEquals(this.adnumber_a, other.adnumber_a) && HidlSupport.deepEquals(this.adnumber_b, other.adnumber_b) && this.adtype == other.adtype && HidlSupport.deepEquals(this.secondtext, other.secondtext) && HidlSupport.deepEquals(this.email, other.email) && this.secondtext_dcs == other.secondtext_dcs && this.email_dcs == other.email_dcs && this.text_dcs == other.text_dcs) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.index))), Integer.valueOf(HidlSupport.deepHashCode(this.number)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.type))), Integer.valueOf(HidlSupport.deepHashCode(this.text)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.hidden))), Integer.valueOf(HidlSupport.deepHashCode(this.group)), Integer.valueOf(HidlSupport.deepHashCode(this.adnumber)), Integer.valueOf(HidlSupport.deepHashCode(this.adnumber_a)), Integer.valueOf(HidlSupport.deepHashCode(this.adnumber_b)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.adtype))), Integer.valueOf(HidlSupport.deepHashCode(this.secondtext)), Integer.valueOf(HidlSupport.deepHashCode(this.email)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.secondtext_dcs))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.email_dcs))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.text_dcs))));
    }

    public final String toString() {
        return "{.index = " + this.index + ", .number = " + this.number + ", .type = " + this.type + ", .text = " + this.text + ", .hidden = " + this.hidden + ", .group = " + this.group + ", .adnumber = " + this.adnumber + ", .adnumber_a = " + this.adnumber_a + ", .adnumber_b = " + this.adnumber_b + ", .adtype = " + this.adtype + ", .secondtext = " + this.secondtext + ", .email = " + this.email + ", .secondtext_dcs = " + this.secondtext_dcs + ", .email_dcs = " + this.email_dcs + ", .text_dcs = " + this.text_dcs + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(176L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<PhbEntryExt> readVectorFromParcel(HwParcel parcel) {
        ArrayList<PhbEntryExt> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 176, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            PhbEntryExt _hidl_vec_element = new PhbEntryExt();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 176);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.index = _hidl_blob.getInt32(_hidl_offset + 0);
        String string = _hidl_blob.getString(_hidl_offset + 8);
        this.number = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 8 + 0, false);
        this.type = _hidl_blob.getInt32(_hidl_offset + 24);
        String string2 = _hidl_blob.getString(_hidl_offset + 32);
        this.text = string2;
        parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 32 + 0, false);
        this.hidden = _hidl_blob.getInt32(_hidl_offset + 48);
        String string3 = _hidl_blob.getString(_hidl_offset + 56);
        this.group = string3;
        parcel.readEmbeddedBuffer(string3.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 56 + 0, false);
        String string4 = _hidl_blob.getString(_hidl_offset + 72);
        this.adnumber = string4;
        parcel.readEmbeddedBuffer(string4.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 72 + 0, false);
        String string5 = _hidl_blob.getString(_hidl_offset + 88);
        this.adnumber_a = string5;
        parcel.readEmbeddedBuffer(string5.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 88 + 0, false);
        String string6 = _hidl_blob.getString(_hidl_offset + 104);
        this.adnumber_b = string6;
        parcel.readEmbeddedBuffer(string6.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 104 + 0, false);
        this.adtype = _hidl_blob.getInt32(_hidl_offset + 120);
        String string7 = _hidl_blob.getString(_hidl_offset + 128);
        this.secondtext = string7;
        parcel.readEmbeddedBuffer(string7.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 128 + 0, false);
        String string8 = _hidl_blob.getString(_hidl_offset + 144);
        this.email = string8;
        parcel.readEmbeddedBuffer(string8.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 144 + 0, false);
        this.secondtext_dcs = _hidl_blob.getInt32(_hidl_offset + 160);
        this.email_dcs = _hidl_blob.getInt32(_hidl_offset + 164);
        this.text_dcs = _hidl_blob.getInt32(_hidl_offset + 168);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(176);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<PhbEntryExt> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 176);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 176);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(0 + _hidl_offset, this.index);
        _hidl_blob.putString(8 + _hidl_offset, this.number);
        _hidl_blob.putInt32(24 + _hidl_offset, this.type);
        _hidl_blob.putString(32 + _hidl_offset, this.text);
        _hidl_blob.putInt32(48 + _hidl_offset, this.hidden);
        _hidl_blob.putString(56 + _hidl_offset, this.group);
        _hidl_blob.putString(72 + _hidl_offset, this.adnumber);
        _hidl_blob.putString(88 + _hidl_offset, this.adnumber_a);
        _hidl_blob.putString(104 + _hidl_offset, this.adnumber_b);
        _hidl_blob.putInt32(120 + _hidl_offset, this.adtype);
        _hidl_blob.putString(128 + _hidl_offset, this.secondtext);
        _hidl_blob.putString(144 + _hidl_offset, this.email);
        _hidl_blob.putInt32(160 + _hidl_offset, this.secondtext_dcs);
        _hidl_blob.putInt32(164 + _hidl_offset, this.email_dcs);
        _hidl_blob.putInt32(168 + _hidl_offset, this.text_dcs);
    }
}
