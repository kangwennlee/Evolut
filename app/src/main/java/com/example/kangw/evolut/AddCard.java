package com.example.kangw.evolut;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.example.kangw.evolut.R;
import com.example.kangw.evolut.TopUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCard extends AppCompatActivity implements OnCardFormSubmitListener, CardEditText.OnCardTypeChangedListener{
    protected CardForm cardForm;
    private static final CardType[] SUPPORTED_CARD_TYPES = {CardType.AMEX, CardType.MASTERCARD, CardType.VISA, CardType.DINERS_CLUB};
    private SupportedCardTypesView mSupportedCardTypesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        mSupportedCardTypesView = findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);

        cardForm = findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("SMS is required on this number")
                .actionLabel("Top Up")
                .setup(this);
        cardForm.setOnCardFormSubmitListener(this);
        cardForm.setOnCardTypeChangedListener(this);

    }

    @Override
    public void onCardFormSubmit() {
        if(cardForm.isValid()){
            Toast.makeText(this, R.string.valid, Toast.LENGTH_SHORT).show();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Card").child(FirebaseAuth.getInstance().getUid());
            mDatabase.child(cardForm.getCardNumber()).child("ExpirationMonth").setValue(cardForm.getExpirationMonth());
            mDatabase.child(cardForm.getCardNumber()).child("ExpirationYear").setValue(cardForm.getExpirationYear());
            finish();
        }else{
            cardForm.validate();
            Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCardTypeChanged(CardType cardType) {
        if(cardType == CardType.EMPTY){
            mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        }
        else{
            mSupportedCardTypesView.setSelected(cardType);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.card_io_item){
            cardForm.scanCard(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (cardForm.isCardScanningAvailable()) {
            getMenuInflater().inflate(R.menu.card_io, menu);
        }
        return true;

    }
}
