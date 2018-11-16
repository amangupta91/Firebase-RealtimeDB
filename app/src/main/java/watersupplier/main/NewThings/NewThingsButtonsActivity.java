package watersupplier.main.NewThings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import watersupplier.main.Configuration.Configuration_Main_Activity;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Main.First_Menu;
import watersupplier.main.NewThings.FirebaseOTP.OTPLoginActivity;
import watersupplier.main.NewThings.InstagramFilters.FiltersListFragment;
import watersupplier.main.NewThings.InstagramFilters.FiltersMainActivity;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

public class NewThingsButtonsActivity extends Common_ActionBar_Abstract implements View.OnClickListener {

    private Button button1,button2,button3,button4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newthings_buttons);
        initialization();
        generateEvents();
    }

    private void initialization() {
        getSupportActionBar().setTitle("New Things");
         button1 = (Button) findViewById(R.id.button1);
         button2 = (Button) findViewById(R.id.button2);
         button3 = (Button) findViewById(R.id.button3);
         button4 = (Button) findViewById(R.id.button4);
    }


    private void generateEvents() {
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtills.givefinishEffect(this);
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_back, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_back:
                this.finish();
                AppUtills.givefinishEffect(this);
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button1:

                Intent intent = new Intent(this,FiltersMainActivity.class);
                startActivity(intent);
                AppUtills.giveIntentEffect(this);
                break;

            case R.id.button2:
                Intent intent2 = new Intent(this,OTPLoginActivity.class);
                startActivity(intent2);
                AppUtills.giveIntentEffect(this);
                break;
        }
    }
}
