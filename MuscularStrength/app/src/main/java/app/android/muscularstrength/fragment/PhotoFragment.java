package app.android.muscularstrength.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.android.muscularstrength.R;
import app.android.muscularstrength.Util.Util;
import app.android.muscularstrength.activity.AddPhotoActivity;
import app.android.muscularstrength.activity.DashBoardActivity;
import app.android.muscularstrength.adapter.AlbumAdapter;
import app.android.muscularstrength.model.Album;
import app.android.muscularstrength.model.PhotoParser;
import app.android.muscularstrength.model.User;
import app.android.muscularstrength.network.JSONParser;
import app.android.muscularstrength.session.SessionManager;
import app.android.muscularstrength.webservice.WebServices;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sa on 8/27/2015.
 */
public class PhotoFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "PhotoFragment";
    View rootView;
    int from;
    float density;
    private int page_no = 1;
    private GridView gridAlbum;
    private Button addPhotos, createAlbum;
    ProgressDialog pDialog;
    CircleImageView userProfileImg;
    TextView user, account_type, level;
    SessionManager session;
    User userObj;
    AlbumAdapter adapter;
    List<Album> album;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DashBoardActivity.actionBar.show();
        DashBoardActivity.menuView.setVisibility(View.GONE);
        DashBoardActivity.mainView.setBackground(null);
        DashBoardActivity.actiontitle.setText("PHOTOS");
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.photo_fragment, container, false);
            density = Util.getDensity(getActivity());
            session = new SessionManager(getActivity());
            gridAlbum = (GridView) rootView.findViewById(R.id.albumGrid);
            createAlbum = (Button) rootView.findViewById(R.id.createAlbum);
            addPhotos = (Button) rootView.findViewById(R.id.addPhotos);
            createAlbum.setOnClickListener(this);
            addPhotos.setOnClickListener(this);
            Gson gson = new Gson();
            adapter = new AlbumAdapter(getActivity());
            gridAlbum.setAdapter(adapter);
            userObj = gson.fromJson(session.getSession(), User.class);
            //header View
            View headerlayout = rootView.findViewById(R.id.header);
            userProfileImg = (CircleImageView) headerlayout.findViewById(R.id.profileImg);
            user = (TextView) headerlayout.findViewById(R.id.user);
            account_type = (TextView) headerlayout.findViewById(R.id.account_type);
            level = (TextView) headerlayout.findViewById(R.id.level);
            Glide.with(getActivity()).load(userObj.getFullImage()).into(userProfileImg);
            user.setText(userObj.getFirstName() + "" + userObj.getLastName());
            account_type.setText(userObj.getAccountType());
            level.setText(userObj.getUserLevel());
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("loading...");
            Bundle args = getArguments();
            from = args.getInt("from");
            Log.i(TAG, "called From=" + from);
            getPhoto();
            gridAlbum.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            //getNewsfeed();
        }


        return rootView;
    }

    //get photos
    private void getPhoto() {
        pDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("userid", "" + 118495);
                 /* params.put("display","15");*/
                JSONParser parser = new JSONParser();
                JSONObject json = parser.makeHttpRequest(WebServices.photos, "GET", params);
                Gson gson = new Gson();
                PhotoParser data = gson.fromJson(json.toString(), PhotoParser.class);
                if (data.getResult().equalsIgnoreCase("SUCCESS")) {
                    //datanewsFeed.addAll(data.getData().getNewsfeed());
                    album = new ArrayList<Album>();
                    album.addAll(data.getData().getData());
                    mainHandler.sendMessage(mainHandler.obtainMessage(1));
                } else {
                    mainHandler.sendMessage(mainHandler.obtainMessage(0));
                }
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
                            setGridAdapter();
                            // storyTxt.setText(Html.fromHtml(story));
                            break;

                    }
                }
            } catch (Resources.NotFoundException e) {

            }
        }
    };

    private void setGridAdapter() {
        for (int i = 0; i < album.size(); i++) {
            adapter.add(album.get(i));
        }
        // Toast.makeText(getActivity(),"COUNT A="+adapter.getCount(),Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createAlbum:
                break;
            case R.id.addPhotos:
                Intent it=new Intent(getActivity(), AddPhotoActivity.class);
                startActivity(it);
                break;
            default:
                break;
        }
    }
}
