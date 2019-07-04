package ru.redandspring.config;

import org.apache.commons.lang3.StringUtils;

public class Artist {

    private final int position;
    private final String artistName;
    private final String countListen;
    private final String avatar;
    private final String stylePercent;
    private final String artistLink;

    public Artist(final String position, final String artistName, final String countListen, final String avatar, final String stylePercent, final String artistLink) {
        this.position = Integer.parseInt(position);
        this.artistName = StringUtils.trimToEmpty(artistName);
        this.countListen = countListen;
        this.avatar = avatar;
        this.stylePercent = stylePercent;
        this.artistLink = artistLink;
    }

    public int getPosition() {
        return position;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getCountListen() {
        return countListen;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getStylePercent() {
        return stylePercent;
    }

    public String getArtistLink() {
        return artistLink;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "position='" + position + '\'' +
                ", artistName='" + artistName + '\'' +
                ", countListen='" + countListen + '\'' +
                '}';
    }
}
