package app.android.muscularstrength.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import app.android.muscularstrength.R;
import app.android.muscularstrength.Util.Util;
import app.android.muscularstrength.activity.DashBoardActivity;
import app.android.muscularstrength.model.User;
import app.android.muscularstrength.session.SessionManager;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bisht Bhawna on 8/26/2015.
 */
public class RoutineFragment extends Fragment {
    public static final String TAG = "RoutineFragment";
    View rootView;
    int from;
    float density;
    private int page_no = 1;
    ProgressDialog pDialog;
    CircleImageView userProfileImg;
    TextView user, account_type, level;
    SessionManager session;
    User userObj;



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DashBoardActivity.actionBar.show();
        DashBoardActivity.menuView.setVisibility(View.GONE);
        DashBoardActivity.mainView.setBackground(null);
        DashBoardActivity.actiontitle.setText("VIDEOS");
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.routine_fragment, container, false);
            density = Util.getDensity(getActivity());
            session=new SessionManager(getActivity());
            Gson gson=new Gson();
            userObj=gson.fromJson(session.getSession(),User.class);
            //header View
            View headerlayout= rootView.findViewById(R.id.header);
            userProfileImg=(CircleImageView)headerlayout.findViewById(R.id.profileImg);
            user = (TextView)headerlayout.findViewById(R.id.user);
            account_type = (TextView)headerlayout.findViewById(R.id.account_type);
            level = (TextView)headerlayout.findViewById(R.id.level);
            Glide.with(getActivity()).load(userObj.getFullImage()).into(userProfileImg);
            user.setText(userObj.getFirstName() + "" + userObj.getLastName());
            account_type.setText(userObj.getAccountType());
            level.setText(userObj.getUserLevel());
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("loading...");
            Bundle args = getArguments();
            from = args.getInt("from");
            Log.i(TAG, "called From=" + from);
            getRoutine();

            //getNewsfeed();
        }


        return rootView;
    }
    private void getRoutine() {
        pDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mainHandler.sendMessage(mainHandler.obtainMessage(1));
               /* HashMap<String, String> params = new HashMap<String, String>();
                params.put("userid", "" + 2);
                 *//* params.put("display","15");*//*
                JSONParser parser = new JSONParser();
                JSONObject json = parser.makeHttpRequest(WebServices.meal_plan, "GET", params);
                Gson gson = new Gson();
                UserVideoParser data = gson.fromJson(json.toString(), UserVideoParser.class);
                if (data.getResult().equalsIgnoreCase("SUCCESS")) {
                    //datanewsFeed.addAll(data.getData().getNewsfeed());
                    mainHandler.sendMessage(mainHandler.obtainMessage(from));
                } else {
                    mainHandler.sendMessage(mainHandler.obtainMessage(0));
                }*/
            }
        }).start();
    }

    private Handler mainHandler = new Handler() {
        public void handleMessage(Message message) {
            try {

                if (isAdded()) {
                    pDialog.dismiss();
                    pDialog.cancel();
                    switch (message.what) {
                        case 0:
                            break;
                        case 1:
                           // storyTxt.setText(Html.fromHtml(story));
                            break;

                    }
                }
            } catch (Resources.NotFoundException e) {

            }
        }
    };
}
