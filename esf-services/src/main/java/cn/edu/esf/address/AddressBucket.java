package cn.edu.esf.address;

import cn.edu.esf.weighting.WeightingRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/14
 */
public class AddressBucket {
    public final String serviceUniqueName;
    public final AddressPool addressPool;

    public final List<String> allAddress = new ArrayList<>();
    public final List<String> availableAddress = new ArrayList<>();
    public final List<String> failedAddress = new ArrayList<>();
    public volatile WeightingRule weightingRule = null;

    public AddressBucket(String serviceUniqueName,AddressPool addressPool) {
        this.serviceUniqueName = serviceUniqueName;
        this.addressPool = addressPool;
    }

    public synchronized List<String> getAvailableAddress() {
        return new ArrayList<>(availableAddress);
    }

    public synchronized WeightingRule getWeightingRule() {
        return weightingRule;
    }


}
