package net.ahammad.myportfolio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.ahammad.myportfolio.R;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by alahammad on 6/4/15.
 */
public class TracksAdapter extends BaseAdapter{

    private Tracks tracks;
    private Context context;
    private LayoutInflater inflater;

    public TracksAdapter(Tracks tracks, Context context) {
        this.tracks = tracks;
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tracks.tracks.size();
    }

    @Override
    public Track getItem(int position) {
        return tracks.tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder= null;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.track_row,null,false);
            holder= new ViewHolder(convertView);
            convertView.setTag(holder);
        }else holder = (ViewHolder) convertView.getTag();
        Track track = tracks.tracks.get(position);
        holder.albumName.setText(track.album.name);
        holder.trackName.setText(track.name);
        List<Image> images = track.album.images;
        if (images!=null && images.size()>0){
            Picasso.with(context).load(images.get(1).url).placeholder(R.drawable.ph).error(R.drawable.ph).into(holder.trackImage);
        }
        return convertView;
    }

    class ViewHolder {
        private ImageView trackImage;
        private TextView albumName, trackName;

        public ViewHolder(View itemView) {
            trackImage = (ImageView)itemView.findViewById(R.id.iv_track);
            albumName = (TextView)itemView.findViewById(R.id.tv_track);
            trackName = (TextView)itemView.findViewById(R.id.tv_album);
        }
    }
}
