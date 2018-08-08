package watersupplier.main.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman on 18/1/17.
 */
public class Common_ActionBar_Abstract extends AppCompatActivity{

    private Object object=null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getSupportActionBar().setIcon(R.drawable.drops2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher1);
//        getSupportActionBar().setElevation(0);

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
//                Log.d("", getApplicationContext())
                if(object==null){
                    Intent intent=new Intent(Common_ActionBar_Abstract.this,First_Menu.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
//                    AppUtills.givefinishEffect(this);
                    finish();
                }


                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setHomeScreenInstance(Object instance){
        this.object=instance;
    }
}
