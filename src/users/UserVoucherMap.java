package users;

import utils.ArrayMap;
import vouchers.Voucher;

import java.util.ArrayList;

public class UserVoucherMap extends ArrayMap {

    /* implements an ArrayMap which maps the campaign ID to the vouchers distributed within
     * the campaign */

    public ArrayMap<Integer, ArrayList<Voucher>> userVoucherMap;

    UserVoucherMap() {
        this.userVoucherMap = new ArrayMap<>();
    }

    /* adds a voucher to the ArrayList of vouchers from the map */
    public void addVoucher(Voucher voucher) {
        if (userVoucherMap.get(voucher.getCampaignID()) == null) {
            ArrayList<Voucher> userVouchers = new ArrayList<>();
            userVouchers.add(voucher);
            userVoucherMap.put(voucher.getCampaignID(), userVouchers);
        } else {
            userVoucherMap.get(voucher.getCampaignID()).add(voucher);
        }
    }
}
