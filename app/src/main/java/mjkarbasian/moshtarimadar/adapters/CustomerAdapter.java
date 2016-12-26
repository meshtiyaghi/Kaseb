package mjkarbasian.moshtarimadar.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import mjkarbasian.moshtarimadar.Data.KasebContract;
import mjkarbasian.moshtarimadar.R;
import mjkarbasian.moshtarimadar.Helpers.Utility;


/**
 * Created by family on 6/23/2016.
 */
public class CustomerAdapter extends CursorAdapter {

    String name;
    String stateId;
    private LayoutInflater cursorInflater;

    public CustomerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.list_item_customers, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewName = (TextView) view.findViewById(R.id.item_list_customer_name);
        TextView textViewAmount = (TextView) view.findViewById(R.id.item_list_purchase_amount);
        ImageView imageViewState = (ImageView) view.findViewById(R.id.item_list_customer_state);

        String selection = KasebContract.State._ID + " = ?";
        String[] selecArg = new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(KasebContract.Customers.COLUMN_STATE_ID)))};
        Cursor colorCursor = context.getContentResolver().query(KasebContract.State.CONTENT_URI,
                new String[]{KasebContract.State._ID ,KasebContract.State.COLUMN_STATE_COLOR},selection,selecArg,null);
        if(colorCursor.moveToFirst())
        imageViewState.setColorFilter(colorCursor.getInt(colorCursor.getColumnIndex(KasebContract.State.COLUMN_STATE_COLOR)));

        stateId = cursor.getString(cursor.getColumnIndex(KasebContract.Customers.COLUMN_STATE_ID));
        name = cursor.getString(cursor.getColumnIndex(KasebContract.Customers.COLUMN_FIRST_NAME)) + "   " +
                cursor.getString(cursor.getColumnIndex(KasebContract.Customers.COLUMN_LAST_NAME));

        Cursor mCursor = context.getContentResolver().query(
                KasebContract.Sales.customerSales(
                        cursor.getLong(cursor.getColumnIndex(KasebContract.Customers._ID))),
                new String[]{
                        KasebContract.Sales._ID},
                null,
                null,
                null);

        Long totalDue = 0l;
        if (mCursor != null)
            if (mCursor.moveToFirst())
                for (int i = 0; i < mCursor.getCount(); i++) {
                    Cursor mCursor1 = context.getContentResolver().query(
                            KasebContract.DetailSale.saleDetailSale(
                                    mCursor.getLong(cursor.getColumnIndex(KasebContract.Sales._ID))),
                            new String[]{
                                    KasebContract.DetailSale._ID,
                                    KasebContract.DetailSale.COLUMN_TOTAL_DUE},
                            null,
                            null,
                            null);

                    if (mCursor1 != null)
                        if (mCursor1.moveToFirst())
                            totalDue += mCursor1.getLong(mCursor1.getColumnIndex(KasebContract.DetailSale.COLUMN_TOTAL_DUE));

                    mCursor.moveToNext();
                }
        textViewName.setText(name);
        textViewAmount.setText(Utility.formatPurchase(
                context,
                Utility.DecimalSeperation(context, Long.valueOf(String.format("%.0f", (float) totalDue)))));
    }
}
