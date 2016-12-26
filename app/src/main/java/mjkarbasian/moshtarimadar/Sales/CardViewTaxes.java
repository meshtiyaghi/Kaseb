package mjkarbasian.moshtarimadar.Sales;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import mjkarbasian.moshtarimadar.R;
import mjkarbasian.moshtarimadar.Adapters.TaxAdapter;
import mjkarbasian.moshtarimadar.Helpers.Utility;

/**
 * Created by Unique on 20/12/2016.
 */
public class CardViewTaxes extends Fragment {
    ListView taxListView;
    TaxAdapter mTaxAdapter;
    ArrayList<Map<String, String>> mTaxListHashMap;
    View view;
    String activity;
    String _id;
    String _amount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_card_view_taxes, container, false);
        activity = getArguments().getString("activity").toString();

        mTaxAdapter = new TaxAdapter(getActivity(), mTaxListHashMap);
        taxListView = (ListView) view.findViewById(R.id.list_view_fragment_card_view_taxes);
        taxListView.setAdapter(mTaxAdapter);

        //region TaxListView OnItemClickListener
        taxListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        //endregion TaxListView OnItemClickListener

        //region TaxListView OnItemLongClickListener
        taxListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> cursor = (Map<String, String>) parent.getItemAtPosition(position);
                if (cursor != null) {
                    _id = cursor.get("id");
                    _amount = cursor.get("amount");

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Confirmation ...")
                            .setMessage("Do You Really Want to Delete This TAX?\n\nTax Amount : " + _amount)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mTaxListHashMap.remove(Utility.indexOfRowsInMap(mTaxListHashMap, "id", _id));
                                    taxListView.setAdapter(mTaxAdapter);

                                    if (activity.equals("insert"))
                                        ((DetailSaleInsert) getActivity()).setValuesOfFactor();
                                    else if (activity.equals("view"))
                                        ((DetailSaleView) getActivity()).setValuesOfFactor();

                                    Utility.setHeightOfListView(taxListView);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            }).show();
                }

                return true;
            }
        });
        //endregion TaxListView OnItemLongClickListener

        return view;
    }

    public void getTaxAdapter(ArrayList<Map<String, String>> list) {
        mTaxListHashMap = list;
        mTaxAdapter = new TaxAdapter(getActivity(), mTaxListHashMap);
        taxListView.setAdapter(mTaxAdapter);

        if (activity.equals("insert"))
            ((DetailSaleInsert) getActivity()).setValuesOfFactor();
        else if (activity.equals("view"))
            ((DetailSaleView) getActivity()).setValuesOfFactor();

        Utility.setHeightOfListView(taxListView);
    }
}
