package campaigns;

import utils.ArrayMap;
import vouchers.Voucher;

import java.util.ArrayList;

public class CampaignVoucherMap {

    /* creates a map of the vouchers that each user has received */

    private ArrayMap<String, ArrayList<Voucher>> campaignVoucherMap;

    CampaignVoucherMap() {
        this.campaignVoucherMap = new ArrayMap<>();
    }

    /* adds a new voucher to the entry value corresponding to the email key */
    public void addVoucher(Voucher voucher) {
        if (campaignVoucherMap.get(voucher.getUserEmail()) == null) {
            ArrayList<Voucher> campaignVouchers = new ArrayList<>();
            campaignVouchers.add(voucher);
            campaignVoucherMap.put(voucher.getUserEmail(), campaignVouchers);
        } else {
            campaignVoucherMap.get(voucher.getUserEmail()).add(voucher);
        }
    }
}