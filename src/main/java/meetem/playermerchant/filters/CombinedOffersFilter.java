package meetem.playermerchant.filters;

import meetem.playermerchant.MerchantOffer;

import java.util.ArrayList;

public class CombinedOffersFilter extends MerchantOffersFilter {
    private ArrayList<MerchantOffersFilter> filters = new ArrayList<>();
    private boolean ordered = false;

    /**
     * @param ordered If true each filter will pass output to next filter
     *                instead of each filter get all items and then uniques selected
     */
    public CombinedOffersFilter(boolean ordered){

    }

    public boolean isOrdered(){
        return ordered;
    }

    public CombinedOffersFilter setOrdered(boolean ordered){
        this.ordered = ordered;
        return this;
    }

    public CombinedOffersFilter addFilter(MerchantOffersFilter filter){
        if(filter != null)
            this.filters.add(filter);

        return this;
    }

    @Override
    public ArrayList<MerchantOffer> filterOffers(ArrayList<MerchantOffer> offersList) {
        if(ordered)
            return filterOffersOrdered(offersList);

        return filterOffersParallel(offersList);
    }

    private ArrayList<MerchantOffer> filterOffersParallel(ArrayList<MerchantOffer> offersList){
        ArrayList<MerchantOffer> offers = new ArrayList<>();

        for(MerchantOffersFilter filter : filters){
            ArrayList<MerchantOffer> output = filter.filterOffers(offersList);
            for(MerchantOffer outputOffer : output){
                if(offers.contains(outputOffer))
                    continue;

                offers.add(outputOffer);
            }
        }

        return offers;
    }

    private ArrayList<MerchantOffer> filterOffersOrdered(ArrayList<MerchantOffer> offersList){
        ArrayList<MerchantOffer> input = offersList;
        for(MerchantOffersFilter filter : filters){
            input = filter.filterOffers(input);
        }

        return input;
    }
}
