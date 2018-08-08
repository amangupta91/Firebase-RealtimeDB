package watersupplier.main.Generic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

import watersupplier.main.Utils.Debug;


public class FacebookLogin {

    private CallbackManager callbackManager;
    private Context mContext;
    private Handler mFBHandler;

    public FacebookLogin(Context mContext) {
        this.mContext = mContext;
    }

    public void onFbLogin(CallbackManager callbackManager, final Handler fbLoginHandler) {
        this.callbackManager = callbackManager;
        this.mFBHandler = fbLoginHandler;
        LoginManager.getInstance().logInWithReadPermissions((Activity) mContext, Arrays.asList("email,publish_actions"));

        LoginManager.getInstance().registerCallback(this.callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                // code for success login
                getUserInfo(login_result);
            }

            @Override
            public void onCancel() {
                // code for cancellation
                if (Debug.isDebug) {
                    Debug.PrintLogError("FB cancle", "onCancel");
                }
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
                if (Debug.isDebug) {
                    Debug.PrintLogError("FB exception", "" + exception);
                }
            }
        });
    }

    /**
     * get name email picture id from facebook
     *
     * @param login_result pass parameter toGrapthRequest Async and get user Logindetails
     */
    protected void getUserInfo(LoginResult login_result) {
        GraphRequest data_request = GraphRequest.newMeRequest(login_result.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json_object, GraphResponse response) {
                        Message msg = new Message();
                        msg.obj = json_object;
                        mFBHandler.sendMessage(msg);

                    }
                });

        Bundle permission_param = new Bundle();
//        parameters.putString("fields", "id, first_name, last_name, email, birthday, gender");
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120),birthday, gender");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

    /**
     * Facebook signout
     */
    public void signOut() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
            }
        }).executeAsync();
    }
}
