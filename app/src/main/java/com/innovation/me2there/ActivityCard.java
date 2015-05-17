package com.innovation.me2there;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

/**
 * Created by ashley on 3/11/15.
 */
public class ActivityCard  extends Card {


    protected TextView mTitle;
    protected TextView mSecondaryTitle;
    protected RatingBar mRatingBar;
    protected EventDetailVO mActivity;
    protected ImageView mImage;
    int listImages[] = new int[]{R.drawable.meetup_1, R.drawable.meetup_2,
            R.drawable.meetup_3, R.drawable.meetup_4, R.drawable.meetup_5};
    /**
     * Constructor with a custom inner layout
     * @param context
     */
    public ActivityCard(Context context) {
        this(context, R.layout.row_card);
    }
    /**
     *
     * @param context
     * @param innerLayout
     */
    public ActivityCard(Context context, int innerLayout) {
        super(context, innerLayout);
        init();
    }

    /**
     *
     * @param context
     * @param innerLayout
     */
    public ActivityCard(Context context, int innerLayout,EventDetailVO inActivity) {
        super(context, innerLayout);
        mActivity = inActivity;
        init();

    }

    /**
     * Init
     */
    private void init(){

        //No Header

        //Set a OnClickListener listener
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                //Log.i("FragmentList", "Item clicked: " + id);
                Intent intent = new Intent(getContext(), EventDetail.class);
                Bundle b = new Bundle();
                b.putParcelable("event_detail",mActivity);
                //b.putLong("selectedID", id); //Your id
                intent.putExtras(b); //Put your id to your next Intent

                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        //Retrieve elements
        mTitle = (TextView) parent.findViewById(R.id.card_main_inner_simple_title);
        mSecondaryTitle = (TextView) parent.findViewById(R.id.card_main_inner_secondary_title);
        mRatingBar = (RatingBar) parent.findViewById(R.id.card_main_inner_ratingBar);
        mImage = (ImageView) parent.findViewById(R.id.card_main_inner_image);

        if (mTitle!=null)
            mTitle.setText(mActivity.getEventDesc());

        if (mSecondaryTitle!=null)
            mSecondaryTitle.setText(mActivity.getEventTime().toString());
        int rating = ( mActivity.getEventID()%5);

        if (mRatingBar!=null) {
            mRatingBar.setNumStars(rating);
            mRatingBar.setMax(5);
            mRatingBar.setRating(4.7f);
        }
        // Create a CardHeader
        CardHeader headerItem = new CardHeader(getContext());
        // Add Header to card
        headerItem.setTitle(mActivity.getEventName());

        this.addCardHeader(headerItem);

        CardThumbnail thumbItem = new CardThumbnail(getContext());
        Bitmap eventImage = mActivity.getEventImage();
        if (eventImage == null) {
            thumbItem.setDrawableResource(listImages[rating]);
        } else {
            //thumbItem.applyBitmap(view,eventImage);
            mImage.setImageBitmap(eventImage);

        }

        //thumbItem.setDrawableResource(listImages[rating]);
        //this.addCardThumbnail(thumbItem);
        Log.i("Activity Card",""+rating);
    }
}
