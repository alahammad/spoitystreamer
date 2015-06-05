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

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by alahammad on 6/4/15.
 */
public class ArtistAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArtistsPager artistsPager;
    private Context context;

    public ArtistAdapter(ArtistsPager artistsPager, Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.artistsPager = artistsPager;
        this.context = context;
    }

    @Override
    public int getCount() {
        return artistsPager.artists.items.size();
    }

    @Override
    public Artist getItem(int position) {
        return artistsPager.artists.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView==null){

            convertView = inflater.inflate(R.layout.artist_row,null,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else holder = (ViewHolder) convertView.getTag();
        Artist artist =artistsPager.artists.items.get(position);
        holder.mName.setText(artist.name);
        List<Image> images= artist.images;
        if (images!=null && images.size()>0){
            Picasso.with(context).load(artist.images.get(2).url).placeholder(R.drawable.ph).error(R.drawable.ph).into(holder.mArtistImage);
        }
        return convertView;
    }

    class ViewHolder{
        private ImageView mArtistImage;
        private TextView mName;

        public ViewHolder(View itemView) {
            mArtistImage = (ImageView) itemView.findViewById(R.id.iv_artist);
            mName=(TextView)itemView.findViewById(R.id.tv_artist_name);
        }
    }
}
